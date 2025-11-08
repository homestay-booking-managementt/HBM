package hbm.adminservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "homestay_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomestayImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "homestay_id", nullable = false)
    private Long homestayId;
    
    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;
    
    @Column(name = "alt", columnDefinition = "TEXT")
    private String alt;
    
    @Column(name = "is_primary")
    private Boolean isPrimary;
    
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
