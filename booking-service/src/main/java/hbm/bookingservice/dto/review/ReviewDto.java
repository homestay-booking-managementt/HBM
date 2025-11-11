package hbm.bookingservice.dto.review;

import hbm.bookingservice.dto.user.UserDetailSummaryDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {
    private Long id;
    private Long bookingId;
    private Short rating;
    private String comment;
    private LocalDateTime createdAt;

    // Thông tin người đánh giá (cần fetch User Entity)
    private UserDetailSummaryDto customer;
}
