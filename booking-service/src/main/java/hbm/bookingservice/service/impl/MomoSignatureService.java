package hbm.bookingservice.service.impl;

import hbm.bookingservice.service.MomoService;
import hbm.bookingservice.service.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MomoSignatureService implements SignatureService {

    private final MomoService momoService; // service có hàm signHmacSHA256

    public boolean isValidSignature(Map<String, Object> payload, String secretKey, String accessKey) {
        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey, payload.get("amount"), payload.get("extraData"), payload.get("message"),
                payload.get("orderId"), payload.get("orderInfo"), payload.get("orderType"),
                payload.get("partnerCode"), payload.get("payType"), payload.get("requestId"),
                payload.get("responseTime"), payload.get("resultCode"), payload.get("transId")
        );

        String calculated = momoService.signHmacSHA256(rawSignature, secretKey);
        return calculated.equals(payload.get("signature"));
    }
}

