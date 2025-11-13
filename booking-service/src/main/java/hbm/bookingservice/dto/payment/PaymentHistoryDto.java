package hbm.bookingservice.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDto {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String method;
    private String paymentStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;
    private String orderId;
    private String homestayName;
    private Long userId;
}
