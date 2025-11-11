package hbm.bookingservice.service;

import hbm.bookingservice.dto.review.ReviewCreationRequestDto;
import hbm.bookingservice.dto.review.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto createReview(Long customerId, ReviewCreationRequestDto requestDto);

    List<ReviewDto> getReviewsByHomestayId(Long homestayId);

}
