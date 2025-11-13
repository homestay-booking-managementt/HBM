package hbm.adminservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private Boolean isUpdate; // true nếu có yêu cầu cập nhật đang chờ duyệt
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Danh sách ảnh của homestay
    private List<HomestayImageDTO> images;
    
    // Thông tin chủ nhà
    private HostInfo host;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HostInfo {
        private Long id;
        private String name;
        private String email;
    }
}
