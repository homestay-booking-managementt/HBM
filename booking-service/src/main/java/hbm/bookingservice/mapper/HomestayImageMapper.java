package hbm.bookingservice.mapper;

import hbm.bookingservice.dto.homestay.HomestayImageDto;
import hbm.bookingservice.entity.HomestayImage;

import java.util.List;

public interface HomestayImageMapper {
    HomestayImageDto convertToDto(HomestayImage image);

    List<HomestayImageDto> convertToDtoList(List<HomestayImage> images);
}
