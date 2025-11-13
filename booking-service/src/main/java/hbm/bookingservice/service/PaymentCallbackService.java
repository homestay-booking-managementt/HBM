package hbm.bookingservice.service;

import java.util.Map;

public interface PaymentCallbackService {
    void handleCallback(Map<String, Object> payload);
}
