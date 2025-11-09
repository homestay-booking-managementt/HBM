package hbm.bookingservice.dto.homestay;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomestayImageDto {
    String url;
    String alt;
    Boolean isPrimary;
}
