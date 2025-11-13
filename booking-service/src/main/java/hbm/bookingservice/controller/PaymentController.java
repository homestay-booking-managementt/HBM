package hbm.bookingservice.controller;

import hbm.bookingservice.dto.payment.CreateMomoResponse;
import hbm.bookingservice.service.MomoService;
import hbm.bookingservice.service.PaymentCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final MomoService momoService;
    private final PaymentCallbackService paymentCallbackService;

    @PostMapping("/momo/initiate/{bookingId}")
    public ResponseEntity<CreateMomoResponse> initiate(@PathVariable Long bookingId) {
        return ResponseEntity.ok().body(momoService.createPaymentUrl(bookingId));
    }

    @PostMapping("/momo/callback")
    @Transactional
    public ResponseEntity<String> callback(@RequestBody Map<String, Object> payload) {
//        log.info("Callback payload: {}", payload);
//
//        try {
//            // === LẤY CÁC GIÁ TRỊ GỐC ===
//            String momoSignature = (String) payload.get("signature");
//            String orderId = String.valueOf(payload.get("orderId"));
//            String partnerCode = String.valueOf(payload.get("partnerCode"));
//            String requestId = String.valueOf(payload.get("requestId"));
//            String extraData = payload.get("extraData") == null ? "" : String.valueOf(payload.get("extraData"));
//            String orderInfo = String.valueOf(payload.get("orderInfo"));
//            String amount = String.valueOf(payload.get("amount"));
//            String resultCode = String.valueOf(payload.get("resultCode"));
//            String transId = String.valueOf(payload.get("transId"));
//            String responseTime = String.valueOf(payload.get("responseTime"));
//
//            // === TÌM PAYMENT ===
//            Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
//            if (payment == null) {
//                log.error("Payment not found for orderId: {}", orderId);
//                return ResponseEntity.ok("OK - Payment Not Found");
//            }
//
//            // === XÁC THỰC CHỮ KÝ ===
//            String accessKey = config.getAccessKey();
//            String secretKey = config.getSecretKey();
//
//            String rawSignature = String.format(
//                    "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
//                    accessKey, amount, extraData, payload.get("message"), orderId, payload.get("orderInfo"),
//                    payload.get("orderType"), partnerCode, payload.get("payType"), requestId,
//                    payload.get("responseTime"), payload.get("resultCode"), payload.get("transId")
//            );
//
//
//            String calculatedSignature = momoService.signHmacSHA256(rawSignature, secretKey);
//
//            if (!Objects.equals(calculatedSignature, momoSignature)) {
//                log.error("Invalid signature for orderId: {}", orderId);
//                log.error("Raw: {}", rawSignature);
//                log.error("Calculated: {}", calculatedSignature);
//                log.error("MoMo: {}", momoSignature);
//                return ResponseEntity.ok("OK - Invalid Signature");
//            }
//
//            // === TRÁNH DOUBLE CALLBACK ===
//            if ("success".equals(payment.getStatus())) {
//                log.warn("Callback already processed for orderId: {}", orderId);
//                return ResponseEntity.ok("OK - Already Processed");
//            }
//
//            // === CẬP NHẬT TRẠNG THÁI ===
//            if ("0".equals(resultCode)) {
//                // Thanh toán thành công
//                payment.setStatus("success");
//                payment.setTransactionCode(transId);
//                payment.setPaidAt(LocalDateTime.now());
//                paymentRepository.save(payment);
//
//                Booking booking = bookingRepository.findById(payment.getBookingId())
//                        .orElseThrow(() -> new IllegalStateException("Booking must exist for successful payment"));
//                booking.setStatus("confirmed");
//                bookingRepository.save(booking);
//
//                log.info(" Payment success, booking {} confirmed", booking.getId());
//
//            } else if (isPermanentFailure(resultCode)) {
//                // Lỗi vĩnh viễn -> hủy booking luôn
//                payment.setStatus("failed");
//                paymentRepository.save(payment);
//
//                Booking booking = bookingRepository.findById(payment.getBookingId())
//                        .orElseThrow(() -> new IllegalStateException("Booking must exist for payment fail"));
//                booking.setStatus("cancelled");
//                booking.setCancelledAt(LocalDateTime.now());
//                bookingRepository.save(booking);
//
//                log.warn(" Payment failed permanently for orderId: {} - resultCode: {}", orderId, resultCode);
//
//            } else {
//                //  Lỗi tạm thời hoặc đang xử lý -> chờ auto-cancel
//                payment.setStatus("pending");
//                paymentRepository.save(payment);
//                log.warn(" Payment pending for orderId: {} - resultCode: {}", orderId, resultCode);
//            }
//
//
//            return ResponseEntity.ok("OK");
//
//        } catch (Exception e) {
//            log.error("Error processing MoMo callback: ", e);
//            return ResponseEntity.ok("OK - Internal Error");
//        }
        log.info("🔔 Received MoMo callback");
        try {
            paymentCallbackService.handleCallback(payload);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("❌ Error while processing MoMo callback", e);
            // MoMo chỉ cần HTTP 200, không cần biết nội dung
            return ResponseEntity.ok("OK - Internal Error");
        }
    }

    private boolean isPermanentFailure(String resultCode) {
        return List.of("1001", "1002", "1004").contains(resultCode);
    }

}

