package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusHistoryDTO {
    private Long id;
    private Long userId;
    private Integer oldStatus;
    private Integer newStatus;
    private String reason;
    private Long changedBy;
    private String changedByName;
    private String changedByEmail;
    private LocalDateTime changedAt;
}
