package hbm.bookingservice.service.impl;

import hbm.bookingservice.client.MomoClient;
import hbm.bookingservice.config.MomoConfig;
import hbm.bookingservice.dto.payment.CreateMomoRequest;
import hbm.bookingservice.dto.payment.CreateMomoResponse;
import hbm.bookingservice.entity.Booking;
import hbm.bookingservice.entity.Payment;
import hbm.bookingservice.repository.BookingRepository;
import hbm.bookingservice.repository.PaymentRepository;
import hbm.bookingservice.service.MomoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
}
