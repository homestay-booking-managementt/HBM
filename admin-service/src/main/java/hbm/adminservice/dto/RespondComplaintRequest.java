package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespondComplaintRequest {
    
    private String status;         // resolved, closed, in_progress
    private String adminResponse;  // Phản hồi của admin
}
