package hbm.bookingservice.service.payment.impl;

import hbm.bookingservice.client.MomoClient;
import hbm.bookingservice.config.MomoConfig;
import hbm.bookingservice.dto.payment.CreateMomoRequest;
import hbm.bookingservice.dto.payment.CreateMomoResponse;
import hbm.bookingservice.entity.Booking;
import hbm.bookingservice.entity.Payment;
import hbm.bookingservice.repository.BookingRepository;
import hbm.bookingservice.repository.PaymentRepository;
import hbm.bookingservice.service.payment.MomoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoServiceImpl implements MomoService {

    private final MomoConfig config;
    private final MomoClient momoClient;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public CreateMomoResponse createPaymentUrl(Long bookingId) {

        // 1. Tải Booking để lấy thông tin cần thiết
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        // 1. Chuẩn bị TẤT CẢ dữ liệu GỐC chỉ MỘT LẦN
        String orderId = "BKG-" + bookingId + "-" + UUID.randomUUID().toString().substring(0, 8); // Kèm bookingId để dễ lấy ra lúc callback
        String requestIdValue = UUID.randomUUID().toString();
        String extraDataValue = Base64.getEncoder().encodeToString("extra data".getBytes(StandardCharsets.UTF_8));
        String orderInfoValue = "Thanh toán đơn hàng: " + bookingId;
        String langValue = "vi"; // Giá trị cố định
        Long amountValue = 10000L;

        // Đảm bảo không sử dụng các giá trị hardcode khác
        String accessKey = config.getAccessKey();
        String partnerCode = config.getPartnerCode();
        String ipnUrl = config.getIpnUrl();
        String redirectUrl = config.getRedirectUrl();
        String requestType = config.getRequestType();

        // SỬA ĐỔI: TẠO VÀ LƯU PAYMENT RECORD (Trạng thái Pending)
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(BigDecimal.valueOf(amountValue));
        payment.setOrderId(orderId);             // <-- LƯU orderId
        payment.setRequestId(requestIdValue);
        // <-- LƯU requestId
        payment.setStatus("pending");
        payment.setMethod("momo");
        paymentRepository.save(payment);

        // Cập nhật booking status (nếu cần)
        booking.setStatus("pending_payment");
        bookingRepository.save(booking);

        // 2. TẠO RAW SIGNATURE: Thêm 'lang' và đảm bảo thứ tự
        // Lưu ý: Thứ tự các trường là TUYỆT ĐỐI quan trọng theo quy định của MoMo.
        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                accessKey, amountValue, extraDataValue, ipnUrl, orderId, orderInfoValue, partnerCode, redirectUrl, requestIdValue, requestType);

        // 3. TẠO SIGNATURE
        String signature = signHmacSHA256(rawSignature, config.getSecretKey());

        // 4. TẠO REQUEST: Sử dụng lại TẤT CẢ các biến đã dùng ở bước 1 và 2
        CreateMomoRequest request = CreateMomoRequest.builder()
                .partnerCode(partnerCode)
                .requestType(requestType)
                .ipnUrl(ipnUrl)
                .redirectUrl(redirectUrl)
                .orderId(orderId)
                .amount(amountValue)
                .orderInfo(orderInfoValue) // <-- Dùng biến đã tạo ở trên
                .requestId(requestIdValue)   // <-- Dùng biến đã tạo ở trên
                .extraData(extraDataValue)
                .signature(signature)
                .lang(langValue)             // <-- Thêm lang
                .build();

        return momoClient.createMomoQR(request);
    }

    public String signHmacSHA256(String rawSignature, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hashBytes = mac.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8));

            // Chuyển byte[] → hex string, lowercase
            return HexFormat.of().formatHex(hashBytes).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC SHA256 signature", e);
        }
    }

    @Override
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
        if (!isValidSignature(payload, config.getSecretKey(), config.getAccessKey())) {
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

    public boolean isValidSignature(Map<String, Object> payload, String secretKey, String accessKey) {
        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey, payload.get("amount"), payload.get("extraData"), payload.get("message"),
                payload.get("orderId"), payload.get("orderInfo"), payload.get("orderType"),
                payload.get("partnerCode"), payload.get("payType"), payload.get("requestId"),
                payload.get("responseTime"), payload.get("resultCode"), payload.get("transId")
        );

        String calculated = signHmacSHA256(rawSignature, secretKey);
        return calculated.equals(payload.get("signature"));
    }
}
