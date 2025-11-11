package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer status; // 0:chưa kích hoạt,1:hoạt động,2:tạm khóa,3:bị chặn
    private Boolean isDeleted;
    private List<String> roles;
}
