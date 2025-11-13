package hbm.bookingservice.service.payment;

import hbm.bookingservice.dto.payment.PaymentHistoryDto;

import java.util.List;

public interface PaymentService {

    List<PaymentHistoryDto> getPaymentHistory(Long userId);
}
