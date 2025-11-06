package hbm.homestayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHomestayRequest {
    
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
    private String amenities; // JSON string
}
