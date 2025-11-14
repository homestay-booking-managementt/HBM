package hbm.bookingservice.dto.host;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsDto {
    private BigDecimal totalRevenue;
    private Long totalBookings;
    private BigDecimal averageBookingValue;
    private String period;
    private List<PeriodDataDto> periodData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodDataDto {
        private String period;
        private BigDecimal revenue;
        private Long bookings;
    }
}
