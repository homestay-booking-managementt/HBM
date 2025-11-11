package hbm.bookingservice.service.impl;

import hbm.bookingservice.constant.BookingStatus;
import hbm.bookingservice.dto.booking.*;
import hbm.bookingservice.dto.homestay.HomestayDetailDto;
import hbm.bookingservice.dto.homestay.HomestayImageDto;
import hbm.bookingservice.dto.homestay.HomestaySummaryDto;
import hbm.bookingservice.dto.user.UserDetailSummaryDto;
import hbm.bookingservice.entity.Booking;
import hbm.bookingservice.entity.Homestay;
import hbm.bookingservice.entity.User;
import hbm.bookingservice.exception.AccessForbiddenException;
import hbm.bookingservice.mapper.BookingMapper;
import hbm.bookingservice.repository.BookingRepository;
import hbm.bookingservice.repository.HomestayRepository;
import hbm.bookingservice.repository.UserRepository;
import hbm.bookingservice.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final HomestayRepository homestayRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookingDto> getMyBookings(Long userId) {
        List<BookingSummaryProjection> projections = bookingRepository.findMyBookingsByUserId(userId);

        if (projections.isEmpty()) {
            return Collections.emptyList();
        }

        return projections.stream()
                .map(this::mapSummaryProjectionToDto)
                .toList();
    }

    @Override
    public BookingDetailDto getBookingDetails(Long bookingId, Long userId) {
        List<BookingDetailProjection> projections = bookingRepository.findBookingDetails(bookingId, userId);

        if (projections.isEmpty()) {
            return null;
        }

        return mapDetailProjectionsToDto(projections);
    }

    @Override
    @Transactional
    public BookingDetailDto createBooking(BookingCreationRequestDto requestDto) {
        if (requestDto.getCheckIn().isAfter(requestDto.getCheckOut())
                || requestDto.getCheckIn().isEqual(requestDto.getCheckOut())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        // 1. Tải Homestay và kiểm tra tồn tại
        Homestay homestay = homestayRepository.findById(requestDto.getHomestayId())
                .orElseThrow(() -> new IllegalArgumentException("Homestay not found."));

        // 2. Tải User (Customer) để chuẩn bị cho mapping trả về
        User customer = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        // 3. Tính toán Tổng giá và Nights
        long nights = ChronoUnit.DAYS.between(requestDto.getCheckIn(), requestDto.getCheckOut());
        if (nights <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than zero.");
        }

        BigDecimal basePrice = homestay.getBasePrice(); // Nên dùng giá từ Entity
        BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(nights));

        // 4. KIỂM TRA TRÙNG LỊCH
        Long conflictingBookingsCount = bookingRepository.countConflictingConfirmedBookings(
                requestDto.getHomestayId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut(),
                null); // null vì đây là booking mới

        if (conflictingBookingsCount > 0) {
            throw new IllegalArgumentException("Homestay is already booked and confirmed for the requested period.");
        }

        // 5. Tạo và Lưu Entity Booking
        Booking newBooking = new Booking();
        newBooking.setUserId(requestDto.getUserId());
        newBooking.setHomestayId(requestDto.getHomestayId());
        newBooking.setCheckIn(requestDto.getCheckIn());
        newBooking.setCheckOut(requestDto.getCheckOut());
        newBooking.setTotalPrice(totalPrice);
        newBooking.setStatus("pending"); // Trạng thái ban đầu
        newBooking.setCreatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(newBooking);

        // 6. Ánh xạ sang BookingDetailDto đầy đủ (Booking, Homestay Entity, User
        // Entity)
        return bookingMapper.toDetailDto(savedBooking, homestay, customer);
    }

    @Override
    public BookingDetailDto updateBookingStatusByHost(Long bookingId, Long hostId, String newStatusStr) {

        // 1. Tải Booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        // 2. Tải Entities phụ để kiểm tra và mapping
        Homestay homestay = homestayRepository.findById(booking.getHomestayId())
                .orElseThrow(() -> new IllegalArgumentException("Homestay not found for this booking."));
        User customer = userRepository.findById(booking.getUserId()) // Tải User (Customer)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found for this booking."));

        // 2a. Kiểm tra quyền sở hữu (Security Check)
        if (!homestay.getUserId().equals(hostId)) {
            throw new AccessForbiddenException("Host does not own this homestay or booking.");
        }

        // 3. Kiểm tra chuyển đổi trạng thái hợp lệ (Business Rule Check)
        BookingStatus oldStatus = BookingStatus.valueOf(booking.getStatus().toUpperCase());
        BookingStatus newStatus = BookingStatus.valueOf(newStatusStr.toUpperCase());

        // * Chỉ xử lý khi đang ở PENDING *
        if (oldStatus != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Booking is not in PENDING status. Current status: " + oldStatus);
        }

        // * Host chỉ được chuyển sang CONFIRMED, REJECTED *
        if (newStatus == BookingStatus.COMPLETED || newStatus == BookingStatus.CANCELLED) {
            // Host chỉ nên REJECT (Từ chối) yêu cầu PENDING, không phải CANCEL (Hủy)
            throw new IllegalArgumentException("Host can only set status to CONFIRMED or REJECTED from PENDING.");
        }

        // * Kiểm tra Trùng lịch *
        if (newStatus == BookingStatus.CONFIRMED) {
            Long conflictingBookingsCount = bookingRepository.countConflictingConfirmedBookings(
                    booking.getHomestayId(), booking.getCheckIn(), booking.getCheckOut(), bookingId);

            if (conflictingBookingsCount > 0) {
                throw new IllegalArgumentException("Homestay is already booked and confirmed for part of this period.");
            }
        }

        // 4. Cập nhật và lưu
        booking.setStatus(newStatus.name().toLowerCase());
        // Ghi lại lý do nếu có
        // if (newStatus == BookingStatus.REJECTED || newStatus ==
        // BookingStatus.CANCELLED) {
        // booking.setHostReason(reason);
        // }

        Booking updatedBooking = bookingRepository.save(booking);

        // 5. Ánh xạ và trả về DTO hoàn chỉnh
        // Sử dụng Mapper đa tham số: (Booking, Homestay, User)
        return bookingMapper.toDetailDto(updatedBooking, homestay, customer);
    }

    @Override
    @Transactional
    public BookingDetailDto cancelBookingByCustomer(Long bookingId, Long customerId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (!booking.getUserId().equals(customerId)) {
            throw new AccessForbiddenException("Access denied. Customer does not own this booking.");
        }

        BookingStatus currentStatus = BookingStatus.valueOf(booking.getStatus().toUpperCase());

        if (currentStatus == BookingStatus.REJECTED ||
                currentStatus == BookingStatus.CANCELLED ||
                currentStatus == BookingStatus.COMPLETED) {

            throw new IllegalArgumentException("Cannot cancel booking with current status: " + currentStatus);
        }

        // 3. Kiểm tra Chính sách Hủy (Ví dụ: Không được hủy trong vòng 2 ngày trước
        // Check-in)
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), booking.getCheckIn());

        if (daysUntilCheckIn < 2 && currentStatus == BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Cancellation must be made at least 48 hours before check-in.");
            // Tùy theo logic nghiệp vụ, ở đây có thể áp dụng chính sách hoàn tiền 0%
        }

        // 5. Xử lý Hoàn tiền (MOCK Logic)
        if (currentStatus == BookingStatus.CONFIRMED) {

            // Giả lập kiểm tra thanh toán (chỉ các CONFIRMED booking mới cần hoàn tiền)
            // Thay thế bằng logic PaymentService khi có
            boolean isPaid = true; // GIẢ LẬP ĐÃ THANH TOÁN

            if (isPaid) {
                BigDecimal refundAmount = booking.getTotalPrice(); // Giả sử hoàn 100%

                // *** GHI LOG YÊU CẦU HOÀN TIỀN ***
                logRefundRequest(bookingId, refundAmount, customerId, reason);
                // **********************************
            }
        }

        booking.setStatus(BookingStatus.CANCELLED.name().toLowerCase());
        Booking updatedBooking = bookingRepository.save(booking);

        Homestay homestay = homestayRepository.findById(updatedBooking.getHomestayId())
                .orElseThrow(() -> new RuntimeException("Homestay not found for mapping."));

        // 5b. Tải User Entity từ ID
        User user = userRepository.findById(updatedBooking.getUserId())
                .orElseThrow(() -> new RuntimeException("User (Customer) not found for mapping."));

        // 5c. Gọi Mapper với 3 Entity (Sửa lỗi cú pháp book.toDto ->
        // bookingMapper.toDetailDto)
        return bookingMapper.toDetailDto(updatedBooking, homestay, user);

    }

    // Phương thức giả lập
    private void logRefundRequest(Long bookingId, BigDecimal amount, Long userId, String reason) {
        System.out.println("--- REFUND LOG ---");
        System.out.println("REQUEST: Refund for Booking ID: " + bookingId);
        System.out.println("Amount: " + amount);
        System.out.println("Requested by User ID: " + userId);
        System.out.println("Reason: " + reason);
        System.out.println("------------------");
    }

    /**
     * Phương thức ánh xạ và nhóm (grouping) từ List<BookingDetailProjection> sang
     * BookingDetailDto
     * 
     * @param projections danh sách kết quả phẳng từ DB
     * @return BookingDetailDto đã được ánh xạ
     */
    private BookingDetailDto mapDetailProjectionsToDto(List<BookingDetailProjection> projections) {

        BookingDetailProjection firstProjection = projections.get(0);

        // --- 1. Ánh xạ User Detail Summary DTO ---
        UserDetailSummaryDto userDto = new UserDetailSummaryDto();
        userDto.setUserId(firstProjection.getUserId());
        userDto.setName(firstProjection.getUserName());
        userDto.setEmail(firstProjection.getUserEmail());
        userDto.setPhone(firstProjection.getUserPhone());

        // --- 2. Ánh xạ Homestay Detail DTO và Hình ảnh ---
        HomestayDetailDto homestayDto = new HomestayDetailDto();
        homestayDto.setId(firstProjection.getHomestayId());
        homestayDto.setName(firstProjection.getHomestayName());
        homestayDto.setDescription(firstProjection.getDescription());
        homestayDto.setAddress(firstProjection.getAddress());
        homestayDto.setCity(firstProjection.getCity());
        homestayDto.setLat(firstProjection.getLat());
        homestayDto.setLongVal(firstProjection.getLongVal()); // Sử dụng LongVal
        homestayDto.setCapacity(firstProjection.getCapacity());
        homestayDto.setNumRooms(firstProjection.getNumRooms());
        homestayDto.setBathroomCount(firstProjection.getBathroomCount());
        homestayDto.setBasePrice(firstProjection.getBasePrice());
        // Có thể cần xử lý JSON string của amenities ở đây nếu cần
        homestayDto.setAmenities(firstProjection.getAmenities());

        // Ánh xạ danh sách hình ảnh (Xử lý các dòng lặp lại)
        List<HomestayImageDto> images = projections.stream()
                // Loại bỏ các dòng mà không có hình ảnh (nếu left join trả về null cho ảnh)
                .filter(p -> p.getHomestayImageId() != null)
                .map(p -> {
                    HomestayImageDto imageDto = new HomestayImageDto();
                    imageDto.setUrl(p.getImageUrl());
                    // Alt không có trong projection hiện tại, có thể bổ sung nếu cần
                    imageDto.setIsPrimary(p.getIsPrimary());
                    return imageDto;
                })
                .collect(Collectors.toList());
        homestayDto.setImages(images);

        // --- 3. Ánh xạ Booking Detail DTO ---
        BookingDetailDto detailDto = new BookingDetailDto();
        detailDto.setBookingId(firstProjection.getBookingId());
        detailDto.setCheckIn(firstProjection.getCheckIn());
        detailDto.setCheckOut(firstProjection.getCheckOut());
        detailDto.setNights(firstProjection.getNights());
        detailDto.setTotalPrice(firstProjection.getTotalPrice());
        detailDto.setStatus(firstProjection.getStatus());
        detailDto.setCreatedAt(firstProjection.getCreatedAt());

        // Gán các DTO lồng nhau
        detailDto.setUser(userDto);
        detailDto.setHomestay(homestayDto);

        return detailDto;
    }

    private BookingDto mapSummaryProjectionToDto(BookingSummaryProjection p) {
        // Ánh xạ Homestay Summary
        HomestaySummaryDto homestayDto = new HomestaySummaryDto();
        homestayDto.setId(p.getHomestayId());
        homestayDto.setName(p.getHomestayName());
        homestayDto.setCity(p.getHomestayCity());
        // Lấy địa chỉ nếu bạn có thêm trong projection, nếu không thì bỏ qua
        // homestayDto.setAddress(...);
        homestayDto.setPrimaryImageUrl(p.getPrimaryImageUrl());

        // Ánh xạ Booking DTO
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBookingId(p.getBookingId());
        bookingDto.setCheckIn(p.getCheckIn());
        bookingDto.setCheckOut(p.getCheckOut());
        bookingDto.setNights(p.getNights());
        bookingDto.setTotalPrice(p.getTotalPrice());
        bookingDto.setStatus(p.getStatus());
        bookingDto.setCreatedAt(p.getCreatedAt());

        // Gán Homestay Summary DTO
        bookingDto.setHomestay(homestayDto);

        return bookingDto;
    }
}
