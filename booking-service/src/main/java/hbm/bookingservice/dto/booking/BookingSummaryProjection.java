package hbm.bookingservice.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookingSummaryProjection {
    Long getBookingId();
    LocalDate getCheckIn();
    LocalDate getCheckOut();
    Integer getNights();
    BigDecimal getTotalPrice();
    String getStatus();
    LocalDateTime getCreatedAt();
    // --- Homestay
    Long getHomestayId();
    String getHomestayName();
    String getHomestayCity();

    // --- Homestay Image (Primary Image URL) ---
    String getPrimaryImageUrl();
}
