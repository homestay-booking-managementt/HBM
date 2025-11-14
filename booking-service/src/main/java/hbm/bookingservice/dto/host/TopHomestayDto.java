package hbm.bookingservice.dto.host;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopHomestayDto {
    private Long homestayId;
    private String homestayName;
    private BigDecimal totalRevenue;
    private Long totalBookings;
}
