package hbm.adminservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDTO {
    
    private Long id;
    private Long userId;
    private Long bookingId;
    private Long homestayId;
    private String subject;
    private String content;
    private String status;
    private Long assignedAdminId;
    private String adminResponse;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Thông tin bổ sung
    private String userName;           // Tên người khiếu nại
    private String userEmail;          // Email người khiếu nại
    private String userPhone;          // SĐT người khiếu nại
    private String homestayName;       // Tên homestay (nếu có)
    private String assignedAdminName;  // Tên admin được giao (nếu có)
}
