package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonDTO {
    private BigDecimal currentRevenue;
    private BigDecimal previousRevenue;
    private BigDecimal revenueChange;
    private Double revenueChangePercentage;
    
    private Long currentBookings;
    private Long previousBookings;
    private Long bookingsChange;
    private Double bookingsChangePercentage;
}
