package hbm.homestayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomestayDTO {
    
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String address;
    private String city;
    private Double lat;
    private Double longitude;
    private Short capacity;
    private Short numRooms;
    private Short bathroomCount;
    private BigDecimal basePrice;
    private String amenities;
    private Byte status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
