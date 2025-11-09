package hbm.bookingservice.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailSummaryDto {
    Long userId;
    String name;
    String email;
    String phone;
}
