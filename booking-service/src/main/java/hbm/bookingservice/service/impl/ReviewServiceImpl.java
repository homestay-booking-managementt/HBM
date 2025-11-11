package hbm.bookingservice.service.impl;

import hbm.bookingservice.dto.review.ReviewCreationRequestDto;
import hbm.bookingservice.dto.review.ReviewDto;
import hbm.bookingservice.dto.user.UserDetailSummaryDto;
import hbm.bookingservice.entity.Booking;
import hbm.bookingservice.entity.Review;
import hbm.bookingservice.entity.User;
import hbm.bookingservice.exception.AccessForbiddenException;
import hbm.bookingservice.mapper.ReviewMapper;
import hbm.bookingservice.mapper.UserMapper;
import hbm.bookingservice.repository.BookingRepository;
import hbm.bookingservice.repository.ReviewRepository;
import hbm.bookingservice.repository.UserRepository;
import hbm.bookingservice.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewDto createReview(Long customerId, ReviewCreationRequestDto requestDto) {

        // 1. Kiểm tra Booking
        Booking booking = bookingRepository.findById(requestDto.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        // 2. Kiểm tra quyền và trạng thái
        if (!booking.getUserId().equals(customerId)) {
            throw new AccessForbiddenException("You can only review your own bookings.");
        }

        if (!"completed".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Booking status must be 'completed' to submit a review.");
        }

        // 3. Kiểm tra đã có Review chưa
        if (reviewRepository.existsByBookingIdAndIsDeletedFalse(requestDto.getBookingId())) {
            throw new IllegalArgumentException("A review already exists for this booking.");
        }

        // 4. Tạo Review Entity
        Review review = new Review();
        review.setBookingId(requestDto.getBookingId());
        review.setHomestayId(booking.getHomestayId());
        review.setCustomerId(customerId);
        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setStatus((short) 1); // 1: Active

        Review savedReview = reviewRepository.save(review);

        // 5. [TODO] Trigger cập nhật điểm trung bình của Homestay (Cần gọi Homestay Service)
        // homestayService.updateAverageRating(review.getHomestayId());

        // 6. Map và trả về
        return reviewMapper.convertToDto(savedReview); // Giả định có ReviewMapper
    }

    @Override
    public List<ReviewDto> getReviewsByHomestayId(Long homestayId) {

        List<Review> reviews = reviewRepository.findByHomestayIdAndIsDeletedFalse(homestayId);

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        // Tải thông tin User cho tất cả reviews
        Set<Long> customerIds = reviews.stream().map(Review::getCustomerId).collect(Collectors.toSet());
        List<User> customers = userRepository.findAllById(customerIds); // Giả định hàm findAllById
        Map<Long, UserDetailSummaryDto> customerMap = customers.stream()
                .collect(Collectors.toMap(User::getUserId, userMapper::toSummaryDto)); // Giả định userMapper

        return reviews.stream()
                .map(review -> {
                    ReviewDto dto = reviewMapper.convertToDto(review);
                    UserDetailSummaryDto customerDto = customerMap.get(review.getCustomerId());
                    dto.setCustomer(customerDto);
                    return dto;
                })
                .toList();
    }
}
