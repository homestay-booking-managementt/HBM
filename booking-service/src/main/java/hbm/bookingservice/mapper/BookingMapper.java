package hbm.bookingservice.mapper;

import hbm.bookingservice.dto.booking.BookingDetailDto;
import hbm.bookingservice.entity.Booking;
import hbm.bookingservice.entity.Homestay;
import hbm.bookingservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {HomestayMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "booking.checkIn", target = "checkIn")
    @Mapping(source = "homestay", target = "homestay")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "booking.status", target = "status")
    @Mapping(source = "booking.createdAt", target = "createdAt")
    BookingDetailDto toDetailDto(Booking booking, Homestay homestay, User user);
}
