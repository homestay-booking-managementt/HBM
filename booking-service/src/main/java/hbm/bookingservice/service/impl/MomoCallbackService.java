package hbm.bookingservice.service.impl;

import hbm.bookingservice.config.MomoConfig;
import hbm.bookingservice.entity.Booking;
import hbm.bookingservice.entity.Payment;
import hbm.bookingservice.repository.BookingRepository;
import hbm.bookingservice.repository.PaymentRepository;
import hbm.bookingservice.service.PaymentCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoCallbackService implements PaymentCallbackService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final MomoSignatureService momoSignatureService;
    private final MomoConfig config;

    @Transactional
    public void handleCallback(Map<String, Object> payload) {
        log.info("Callback payload: {}", payload);

        String orderId = String.valueOf(payload.get("orderId"));
        String resultCode = String.valueOf(payload.get("resultCode"));
        String signature = String.valueOf(payload.get("signature"));
        log.info("Signature: {}", signature);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for orderId: " + orderId));

        // 1️⃣ Verify signature
        if (!momoSignatureService.isValidSignature(payload, config.getSecretKey(), config.getAccessKey())) {
            log.error("Invalid signature for orderId {}", orderId);
            return;
        }

        // 2️⃣ Avoid double processing
        if ("success".equals(payment.getStatus())) {
            log.warn("Callback already processed for orderId: {}", orderId);
            return;
        }

        // 3️⃣ Process payment
        processPaymentResult(payment, resultCode, payload);
    }

    private void processPaymentResult(Payment payment, String resultCode, Map<String, Object> payload) {
        if ("0".equals(resultCode)) {
            handleSuccess(payment, payload);
        } else if (isPermanentFailure(resultCode)) {
            handlePermanentFailure(payment);
        } else {
            handlePending(payment);
        }
    }

    private void handleSuccess(Payment payment, Map<String, Object> payload) {
        payment.setStatus("success");
        payment.setTransactionCode(String.valueOf(payload.get("transId")));
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new IllegalStateException("Booking must exist for successful payment"));
        booking.setStatus("confirmed");
        bookingRepository.save(booking);

        log.info("✅ Payment success, booking {} confirmed", booking.getId());
    }

    private void handlePermanentFailure(Payment payment) {
        payment.setStatus("failed");
        paymentRepository.save(payment);

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new IllegalStateException("Booking must exist for payment fail"));
        booking.setStatus("cancelled");
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        log.warn("❌ Payment failed permanently for booking {}", booking.getId());
    }

    private void handlePending(Payment payment) {
        payment.setStatus("pending");
        paymentRepository.save(payment);
        log.warn("⚠️ Payment pending for orderId: {}", payment.getOrderId());
    }

    private boolean isPermanentFailure(String resultCode) {
        return List.of("1001", "1002", "1004").contains(resultCode);
    }
}
