package hbm.bookingservice.service.payment.impl;

import hbm.bookingservice.dto.payment.PaymentHistoryDto;
import hbm.bookingservice.repository.PaymentRepository;
import hbm.bookingservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentHistoryDto> getPaymentHistory(Long userId) {
        return paymentRepository.getPaymentHistoryByUserId(userId);
    }
}
