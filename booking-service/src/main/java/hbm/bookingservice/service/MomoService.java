package hbm.bookingservice.service;

import hbm.bookingservice.dto.payment.CreateMomoResponse;

public interface MomoService {
    CreateMomoResponse createPaymentUrl(Long bookingId);

    String signHmacSHA256(String rawSignature, String secretKey);
}
