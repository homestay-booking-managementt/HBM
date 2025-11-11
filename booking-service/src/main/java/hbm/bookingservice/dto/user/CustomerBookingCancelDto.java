package hbm.bookingservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerBookingCancelDto {

    @NotBlank(message = "Reason for cancellation is required")
    private String cancellationReason;
}
