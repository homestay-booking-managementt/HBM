package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopHomestayDTO {
    private Long homestayId;
    private String homestayName;
    private Long bookings;
    private BigDecimal revenue;
    private BigDecimal percentage;
}
