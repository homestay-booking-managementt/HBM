package hbm.bookingservice.controller;

import hbm.bookingservice.dto.review.ReviewCreationRequestDto;
import hbm.bookingservice.dto.review.ReviewDto;
import hbm.bookingservice.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @RequestParam("userId") Long customerId,
            @Valid @RequestBody ReviewCreationRequestDto requestDto) {

        ReviewDto newReview = reviewService.createReview(customerId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @GetMapping("/homestay/{homestayId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByHomestayId(@PathVariable Long homestayId) {

        List<ReviewDto> reviews = reviewService.getReviewsByHomestayId(homestayId);
        return ResponseEntity.ok(reviews);
    }
}
