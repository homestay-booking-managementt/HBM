package hbm.bookingservice.dto.booking;

import hbm.bookingservice.dto.homestay.HomestayDetailDto;
import hbm.bookingservice.dto.user.UserDetailSummaryDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDetailDto {
    private Long bookingId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer nights;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private HomestayDetailDto homestay;
    private UserDetailSummaryDto user;
}
