package hbm.bookingservice.dto.homestay;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomestaySummaryDto {
    Long id;
    String name;
    String city;
    String address;
    String primaryImageUrl;
}
