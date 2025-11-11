package hbm.bookingservice.mapper;

import hbm.bookingservice.dto.homestay.HomestayDTO;
import hbm.bookingservice.dto.homestay.HomestayDetailDto;
import hbm.bookingservice.entity.Homestay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {HomestayImageMapper.class})
public interface HomestayMapper {

    @Mapping(source = "longitude", target = "longVal")
    HomestayDetailDto convertToDetailDto(Homestay homestay);

    HomestayDTO convertToDto(Homestay homestay);
}
