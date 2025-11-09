package hbm.bookingservice.service.impl;

import hbm.bookingservice.dto.booking.*;
import hbm.bookingservice.dto.homestay.HomestayDTO;
import hbm.bookingservice.dto.homestay.HomestayDetailDto;
import hbm.bookingservice.dto.homestay.HomestayImageDto;
import hbm.bookingservice.dto.homestay.HomestaySummaryDto;
import hbm.bookingservice.dto.user.UserDetailSummaryDto;
import hbm.bookingservice.entity.Booking;
import hbm.bookingservice.repository.BookingRepository;
import hbm.bookingservice.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final WebClient homestayWebClient;

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
    @Transactional
    public BookingDetailDto getBookingDetails(Long bookingId, Long userId) {
        List<BookingDetailProjection> projections = bookingRepository.findBookingDetails(bookingId, userId);

        if (projections.isEmpty()) {
            return null;
        }

        return mapDetailProjectionsToDto(projections);
    }

    @Override
    public BookingDetailDto createBooking(BookingCreationRequestDto requestDto) {
        if (requestDto.getCheckIn().isAfter(requestDto.getCheckOut()) || requestDto.getCheckIn().isEqual(requestDto.getCheckOut())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        HomestayDTO homestay;
        try {
            // Lấy HomestayDTO từ API /homestays/{id}
            Map<String, Object> responseMap = homestayWebClient.get()
                    .uri("/homestays/{id}", requestDto.getHomestayId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (responseMap == null || !(Boolean) responseMap.getOrDefault("success", false)) {
                throw new RuntimeException("Failed to retrieve homestay details or homestay not found.");
            }

            // Cần ánh xạ Map.get("data") sang HomestayDTO.
            // Trong thực tế, bạn sẽ dùng ObjectMapper hoặc thư viện khác.
            // Ở đây tôi giả định có thể lấy được giá cơ bản.
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
            homestay = mapToHomestayDTO(data); // Hàm giả định

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Homestay Service is unavailable or returned an error: " + e.getMessage());
        }

        // 3. Tính toán Tổng giá
        long nights = ChronoUnit.DAYS.between(requestDto.getCheckIn(), requestDto.getCheckOut());
        if (nights <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than zero.");
        }

        BigDecimal basePrice = homestay.getBasePrice();
        BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(nights));


        // 5. Tạo và Lưu Entity Booking
        Booking newBooking = new Booking();
        newBooking.setUserId(requestDto.getUserId());
        newBooking.setHomestayId(requestDto.getHomestayId());
        newBooking.setCheckIn(requestDto.getCheckIn());
        newBooking.setCheckOut(requestDto.getCheckOut());
        // nights được tính trong DB hoặc tự set
        newBooking.setTotalPrice(totalPrice);
        newBooking.setStatus("pending"); // Trạng thái ban đầu
        newBooking.setCreatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(newBooking);

        // 6. Ánh xạ sang BookingDetailDto để trả về (đã bao gồm thông tin Homestay)
        // Cần logic ánh xạ từ savedBooking và homestay sang BookingDetailDto
        return mapBookingAndHomestayToDetailDto(savedBooking, homestay);

    }

    private BookingDetailDto mapBookingAndHomestayToDetailDto(Booking booking, HomestayDTO homestay) {

        // --- 1. Lấy thông tin User ---
        // Giả sử userId đã được xác thực
        UserDetailSummaryDto userDto =new UserDetailSummaryDto(booking.getUserId(), "null", "null", "0214213231");

        // --- 2. Ánh xạ Homestay Detail DTO ---
        HomestayDetailDto homestayDetailDto = mapHomestayDtoToDetailDto(homestay);

        // --- 3. Ánh xạ Booking Detail DTO ---
        BookingDetailDto detailDto = new BookingDetailDto();
        detailDto.setBookingId(booking.getId());
        detailDto.setCheckIn(booking.getCheckIn());
        detailDto.setCheckOut(booking.getCheckOut());

        // Tính nights (Nếu không được Entity Booking tự tính)
        long nights = java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut());
        detailDto.setNights((int) nights);

        detailDto.setTotalPrice(booking.getTotalPrice());
        detailDto.setStatus(booking.getStatus());
        detailDto.setCreatedAt(booking.getCreatedAt());

        // Gán các DTO lồng nhau
        detailDto.setUser(userDto);
        detailDto.setHomestay(homestayDetailDto);

        return detailDto;
    }

    private HomestayDetailDto mapHomestayDtoToDetailDto(HomestayDTO homestay) {
        HomestayDetailDto dto = new HomestayDetailDto();
        dto.setId(homestay.getId());
        dto.setName(homestay.getName());
        dto.setDescription(homestay.getDescription());
        dto.setAddress(homestay.getAddress());
        dto.setCity(homestay.getCity());
        dto.setLat(homestay.getLat());
        dto.setLongVal(homestay.getLongitude()); // Mapping từ longitude sang longVal
        dto.setCapacity(homestay.getCapacity() != null ? homestay.getCapacity().intValue() : null);
        dto.setNumRooms(homestay.getNumRooms() != null ? homestay.getNumRooms().intValue() : null);
        dto.setBathroomCount(homestay.getBathroomCount() != null ? homestay.getBathroomCount().intValue() : null);
        dto.setBasePrice(homestay.getBasePrice());

        // Amenities: giữ nguyên JSON string nếu cần
        dto.setAmenities(homestay.getAmenities());

        // Hình ảnh (List<HomestayImageDto>):
        // LƯU Ý QUAN TRỌNG: HomestayDTO lấy từ service khác chưa có List<HomestayImageDto>.
        // Bạn cần gọi thêm API để lấy danh sách ảnh chi tiết HOẶC Homestay Service
        // cần trả về List<HomestayImageDto> ngay trong HomestayDTO.
        // Tạm thời để trống hoặc lấy ảnh chính nếu có.
        dto.setImages(java.util.Collections.emptyList());

        return dto;
    }

    private HomestayDTO mapToHomestayDTO(Map<String, Object> data) {
        HomestayDTO dto = new HomestayDTO();
        dto.setId(Long.valueOf(data.get("id").toString()));
        dto.setUserId(Long.valueOf(data.get("userId").toString()));
        dto.setName(data.get("name").toString());
        dto.setDescription(data.get("description").toString());
        dto.setAddress(data.get("address").toString());
        dto.setCity(data.get("city").toString());
        dto.setLat(Double.valueOf(data.get("lat").toString()));
        dto.setLongitude(Double.valueOf(data.get("longitude").toString()));
        dto.setCapacity(Short.valueOf(data.get("capacity").toString()));
        dto.setNumRooms(Short.valueOf(data.get("numRooms").toString()));
        dto.setBathroomCount(Short.valueOf(data.get("bathroomCount").toString()));
        dto.setStatus(Byte.valueOf(data.get("status").toString()));
        dto.setAmenities(data.get("amenities").toString());
        dto.setCreatedAt(LocalDateTime.parse(data.get("createdAt").toString()));
        dto.setUpdatedAt(LocalDateTime.parse(data.get("updatedAt").toString()));
        dto.setBasePrice(new BigDecimal(data.get("basePrice").toString()));

        return dto;
    }

    /**
     * Phương thức ánh xạ và nhóm (grouping) từ List<BookingDetailProjection> sang BookingDetailDto
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
