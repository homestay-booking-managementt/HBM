package hbm.bookingservice.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMomoResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private long responseTime;
    private String message;
    private int resultCode;
    private String payUrl;
    private String orderInfo;
    private String orderType;
    private String transId;
}
