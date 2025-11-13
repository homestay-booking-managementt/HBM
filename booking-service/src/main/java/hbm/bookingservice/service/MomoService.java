package hbm.bookingservice.service;

import hbm.bookingservice.dto.payment.CreateMomoRequest;
import hbm.bookingservice.dto.payment.CreateMomoResponse;
import hbm.bookingservice.entity.Payment;

public interface MomoService {
    CreateMomoResponse createPaymentUrl(Long bookingId);

    String signHmacSHA256(String rawSignature, String secretKey);
}
