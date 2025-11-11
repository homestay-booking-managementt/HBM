package hbm.adminservice.dto;

import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    private Integer status; // 0:chờ duyệt, 1:hoạt động, 2:tạm khóa, 3:bị chặn
    private String reason; // Lý do thay đổi trạng thái (optional)
}
