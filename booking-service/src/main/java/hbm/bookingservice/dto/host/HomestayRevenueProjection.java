package hbm.bookingservice.dto.host;

import java.math.BigDecimal;

public interface HomestayRevenueProjection {
    Long getHomestayId();
    String getHomestayName();
    BigDecimal getTotalRevenue();
    Long getTotalBookings();
}
