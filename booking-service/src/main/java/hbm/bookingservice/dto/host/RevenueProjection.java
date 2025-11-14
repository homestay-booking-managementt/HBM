package hbm.bookingservice.dto.host;

import java.math.BigDecimal;

public interface RevenueProjection {
    BigDecimal getTotalRevenue();
    Long getTotalBookings();
}
