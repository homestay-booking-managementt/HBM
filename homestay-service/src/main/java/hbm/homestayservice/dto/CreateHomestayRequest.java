package hbm.homestayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    
    // Danh sách URL ảnh kèm thông tin
    private List<ImageRequest> images;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageRequest {
        private String url;
        private String alt;
        private Boolean isPrimary; // true nếu là ảnh chính
    }
}
