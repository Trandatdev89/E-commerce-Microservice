package com.project01.paymentservice.service;

import com.project01.common.core.exception.BusinessException;
import com.project01.paymentservice.dto.PaymentDTO;
import com.project01.paymentservice.dto.PaymentDTOResponse;
import com.project01.paymentservice.entity.Payment;
import com.project01.paymentservice.entity.PaymentStatus;
import com.project01.paymentservice.exception.ErrorCode;
import com.project01.paymentservice.kafka.NotificationProducer;
import com.project01.event.dto.PaymentConfirm;
import com.project01.paymentservice.repository.PaymentMethodRepository;
import com.project01.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final NotificationProducer notificationProducer;


    /**
     * Tạo thanh toán cho đơn. idempotency_key = order_code nên order-service gọi lại
     * (retry do timeout...) sẽ nhận lại đúng payment cũ thay vì tạo bản ghi trùng.
     */
    @Transactional
    public Long createPayment(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return null;
        }
        var existing = paymentRepository.findByIdempotencyKey(paymentDTO.getOrderReference());
        if (existing.isPresent()) {
            return existing.get().getId();
        }
        if (paymentDTO.getPaymentMethod() == null
                || !paymentMethodRepository.existsById(paymentDTO.getPaymentMethod().name())) {
            throw new BusinessException(ErrorCode.PAYMENT_METHOD_INVALID);
        }

        Payment payment = paymentRepository.save(Payment
                .builder()
                .paymentCode("PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(Locale.ROOT))
                .orderCode(paymentDTO.getOrderReference())
                .customerId(paymentDTO.getCustomerId())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .amount(paymentDTO.getAmount())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(paymentDTO.getOrderReference())
                .build());

        notificationProducer.sendNotification(PaymentConfirm.builder()
                        .customerId(paymentDTO.getCustomerId())
                        .orderReference(paymentDTO.getOrderReference())
                        .paymentMethod(paymentDTO.getPaymentMethod())
                        .amount(paymentDTO.getAmount())
                        .customerEmail(paymentDTO.getCustomer().email())
                        .customerFirstname(paymentDTO.getCustomer().firstName())
                        .customerLastname(paymentDTO.getCustomer().lastName())
                .build());


        return payment.getId();
    }

    public PaymentDTOResponse getPaymentByOrderCode(String orderCode){
        Payment payment = paymentRepository.findTopByOrderCodeOrderByCreatedAtDesc(orderCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOTFOUND));
        return PaymentDTOResponse.builder()
                .id(payment.getId())
                .paymentCode(payment.getPaymentCode())
                .orderCode(payment.getOrderCode())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .createdDate(payment.getCreatedAt())
                .lastModifiedDate(payment.getUpdatedAt())
                .build();
    }
}
