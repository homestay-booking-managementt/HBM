package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPendingRequest {
    private String action; // "approve" hoặc "reject"
    private String reason; // Lý do từ chối (nếu action = "reject")
}
