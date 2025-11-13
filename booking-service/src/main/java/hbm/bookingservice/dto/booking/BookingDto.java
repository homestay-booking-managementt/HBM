package hbm.bookingservice.dto.booking;

import hbm.bookingservice.dto.homestay.HomestaySummaryDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long bookingId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer nights;
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private HomestaySummaryDto homestay;
}
