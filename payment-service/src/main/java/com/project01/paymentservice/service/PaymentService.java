package com.project01.paymentservice.service;

import com.project01.paymentservice.dto.PaymentDTO;
import com.project01.paymentservice.dto.PaymentDTOResponse;
import com.project01.paymentservice.entity.Payment;
import com.project01.paymentservice.kafka.NotificationProducer;
import com.project01.event.dto.PaymentConfirm;
import com.project01.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationProducer notificationProducer;


    public Long createPayment(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return null;
        }

        Payment payment = paymentRepository.save(Payment
                .builder()
                .paymentMethod(paymentDTO.getPaymentMethod())
                .amount(paymentDTO.getAmount())
                .orderId(paymentDTO.getOrderId())
                .build());

        notificationProducer.sendNotification(PaymentConfirm.builder()
                        .orderReference(paymentDTO.getOrderReference())
                        .paymentMethod(paymentDTO.getPaymentMethod())
                        .amount(paymentDTO.getAmount())
                        .customerEmail(paymentDTO.getCustomer().email())
                        .customerFirstname(paymentDTO.getCustomer().firstName())
                        .customerLastname(paymentDTO.getCustomer().lastName())
                .build());


        return payment.getId();
    }

    public PaymentDTOResponse getPaymentByOrderId(Long orderId){
        Payment payment = paymentRepository.findByOrderId(orderId);
        return PaymentDTOResponse.builder()
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .orderId(payment.getOrderId())
                .id(payment.getId())
                .createdDate(payment.getCreatedDate())
                .lastModifiedDate(payment.getLastModifiedDate())
                .build();
    }
}
