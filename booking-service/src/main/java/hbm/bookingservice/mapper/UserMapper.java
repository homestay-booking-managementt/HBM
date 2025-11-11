package hbm.bookingservice.mapper;

import hbm.bookingservice.dto.user.UserDetailSummaryDto;
import hbm.bookingservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "userId", target = "userId")
    UserDetailSummaryDto toSummaryDto(User user);
}
