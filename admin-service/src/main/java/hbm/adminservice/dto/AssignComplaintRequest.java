package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignComplaintRequest {
    
    private Long adminId;  // ID của admin được giao xử lý
}
