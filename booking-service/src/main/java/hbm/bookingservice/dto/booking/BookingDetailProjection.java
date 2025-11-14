package hbm.bookingservice.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookingDetailProjection {
    // --- Booking Fields (Đã bổ sung nights) ---
    Long getBookingId();
    LocalDate getCheckIn();
    LocalDate getCheckOut();
    BigDecimal getTotalPrice();
    String getStatus();
    String getPaymentStatus();
    String getPayUrl();
    Integer getNights();        // Đã thêm
    LocalDateTime getCreatedAt();

    // --- Homestay Fields (Đã bổ sung chi tiết homestay) ---
    Long getHomestayId();
    String getHomestayName();
    String getDescription();     // Đã thêm
    String getAddress();
    String getCity();
    Double getLat();
    Double getLongVal();         // Đã đổi tên để ánh xạ tốt hơn
    Integer getCapacity();       // Đã thêm
    Integer getNumRooms();       // Đã thêm
    Integer getBathroomCount();  // Đã thêm
    BigDecimal getBasePrice();
    String getAmenities();

    // --- User Fields ---
    Long getUserId();
    String getUserName();
    String getUserEmail();
    String getUserPhone();

    // --- Homestay Image Fields ---
    Long getHomestayImageId();
    String getImageUrl();
    Boolean getIsPrimary();
}
