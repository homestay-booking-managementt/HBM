package hbm.adminservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "old_status")
    private Integer oldStatus;

    @Column(name = "new_status")
    private Integer newStatus;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    // Transient fields for additional info
    @Transient
    private String changedByName;

    @Transient
    private String changedByEmail;
}
