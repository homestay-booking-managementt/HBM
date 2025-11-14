package hbm.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    private BigDecimal amount;
    private String method;
    private String status;
    private String transactionCode;
    private String orderId;
    private String requestId;
    private LocalDateTime paidAt;

    @Column(columnDefinition = "LONGTEXT")
    private String callbackPayload;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private String payUrl;
}
