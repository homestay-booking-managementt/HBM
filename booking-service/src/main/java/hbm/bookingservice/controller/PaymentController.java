package hbm.bookingservice.controller;

import hbm.bookingservice.dto.payment.CreateMomoResponse;
import hbm.bookingservice.dto.payment.PaymentHistoryDto;
import hbm.bookingservice.service.payment.MomoService;
import hbm.bookingservice.service.payment.PaymentService;
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
    private final PaymentService paymentService;

//    @PostMapping("/momo/initiate/{bookingId}")
//    public ResponseEntity<CreateMomoResponse> initiate(@PathVariable Long bookingId) {
//        return ResponseEntity.ok().body(momoService.createPaymentUrl(bookingId));
//    }

    @PostMapping("/momo/callback")
    @Transactional
    public ResponseEntity<String> callback(@RequestBody Map<String, Object> payload) {
        log.info("🔔 Received MoMo callback");
        try {
            momoService.handleCallback(payload);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("❌ Error while processing MoMo callback", e);
            // MoMo chỉ cần HTTP 200, không cần biết nội dung
            return ResponseEntity.ok("OK - Internal Error");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentHistoryDto>> getPaymentHistory(@RequestParam Long userId) {
        return ResponseEntity.ok().body(paymentService.getPaymentHistory(userId));
    }
}

