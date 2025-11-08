package hbm.adminservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    
    private Long id;
    private Long userId;
    private Long homestayId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;
    
    private Integer nights;
    private BigDecimal totalPrice;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Thông tin bổ sung từ các bảng khác
    private String userName;      // Tên khách hàng
    private String userEmail;     // Email khách hàng
    private String userPhone;     // Số điện thoại khách hàng
    private String homestayName;  // Tên homestay
    private String homestayCity;  // Thành phố của homestay
}
