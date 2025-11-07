package hbm.adminservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "homestay_pending")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class HomestayPending {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "homestay_id", nullable = false)
    private Long homestayId;
    
    @Column(name = "pending_data", nullable = false, columnDefinition = "JSON")
    private String pendingData;
    
    @Column(name = "submitted_at", insertable = false, updatable = false)
    private LocalDateTime submittedAt;
    
    @Column(name = "status", columnDefinition = "ENUM('waiting','approved','rejected')")
    private String status;
    
    @Column(name = "reviewed_by")
    private Long reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
}
