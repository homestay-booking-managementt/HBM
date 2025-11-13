package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTrendDTO {
    private String date; // YYYY-MM-DD
    private BigDecimal revenue;
    private Long bookings;
}
