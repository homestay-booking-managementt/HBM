package hbm.bookingservice.dto.host;

import java.math.BigDecimal;

public interface PeriodRevenueProjection {
    Integer getWeekNumber();
    Integer getMonthNumber();
    BigDecimal getRevenue();
    Long getBookings();
}
