package hbm.bookingservice.dto.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookingStatusUpdateDto {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "CONFIRMED|REJECTED|CANCELLED", message = "Invalid status. Must be CONFIRMED, REJECTED, or CANCELLED.")
    private String newStatus;
}
