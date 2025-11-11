package hbm.bookingservice.mapper;

import hbm.bookingservice.dto.review.ReviewDto;
import hbm.bookingservice.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDto convertToDto(Review review);
}
