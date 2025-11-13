package hbm.bookingservice.service.payment;

import hbm.bookingservice.dto.payment.CreateMomoResponse;

import java.util.Map;

public interface MomoService {
    CreateMomoResponse createPaymentUrl(Long bookingId);

    String signHmacSHA256(String rawSignature, String secretKey);

    void handleCallback(Map<String, Object> payload);
}
