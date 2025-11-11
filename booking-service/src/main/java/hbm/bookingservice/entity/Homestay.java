package hbm.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "homestay")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Homestay {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "lat")
    private Double lat;
    
    // Column name `long` is reserved keyword, use backticks so Hibernate quotes it.
    @Column(name = "`long`")
    private Double longitude;
    
    @Column(name = "capacity")
    private Short capacity;
    
    @Column(name = "num_rooms")
    private Short numRooms;
    
    @Column(name = "bathroom_count")
    private Short bathroomCount;
    
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "amenities", columnDefinition = "JSON")
    private String amenities;
    
    @Column(name = "status")
    private Byte status;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "is_deleted", insertable = false)
    private Boolean isDeleted;
}
