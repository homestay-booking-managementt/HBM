package hbm.homestayservice.dto;

import lombok.Data;

@Data
public class UpdateHomestayStatusRequest {
    private Byte status; // 2: công khai, 3: tạm ẩn, 4: bị khóa
}
