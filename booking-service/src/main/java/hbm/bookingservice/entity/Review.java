package hbm.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "booking_id")
    Long bookingId;

    @Column(name = "homestay_id")
    Long homestayId;

    @Column(name = "customer_id")
    Long customerId;

    @Column(name = "rating", nullable = false)
    Short rating; // Dùng Short cho tinyint (1-5)

    @Column(name = "comment")
    String comment;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    // THÊM: status (tinyint, default 1)
    @Column(name = "status")
    Short status;

    // THÊM: is_deleted (tinyint(1), default 0)
    @Column(name = "is_deleted")
    Boolean isDeleted = false; // Mặc định là false theo DDL (0)
}
