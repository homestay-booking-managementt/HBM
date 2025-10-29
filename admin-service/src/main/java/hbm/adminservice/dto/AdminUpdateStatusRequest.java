package hbm.adminservice.dto;

import lombok.Data;

@Data
public class AdminUpdateStatusRequest {
    private Byte status; // 2: duyệt & công khai, 3: tạm ẩn, 4: bị khóa
    private String reason; // Lý do khóa/ẩn (optional)
}
