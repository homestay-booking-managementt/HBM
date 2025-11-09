package hbm.bookingservice.dto.homestay;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomestayDetailDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private Double lat;
    private Double longVal;
    private Integer capacity;
    private Integer numRooms;
    private Integer bathroomCount;
    private BigDecimal basePrice;
    private Object amenities;
    List<HomestayImageDto> images;
}
