package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomestayPendingDTO {
    private Long id;
    private Long homestayId;
    private String pendingData;
    private LocalDateTime submittedAt;
    private String status;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String reason;
}
