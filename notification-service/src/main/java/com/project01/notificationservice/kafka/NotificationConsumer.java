package com.project01.notificationservice.kafka;

import com.project01.event.dto.OrderConfirm;
import com.project01.event.dto.PaymentConfirm;
import com.project01.event.dto.VerifyAccountRequest;
import com.project01.notificationservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic", containerFactory = "kafkaListenerContainerFactory")
    public void paymentNotification(PaymentConfirm paymentConfirm) {
        emailService.sendEmailForPayment(paymentConfirm);
    }

    @KafkaListener(topics = "order-topic", containerFactory = "kafkaListenerContainerFactory")
    public void orderNotification(OrderConfirm orderConfirm) {
        emailService.sendEmailForOrder(orderConfirm);
    }

    @KafkaListener(topics = "identity-topic", containerFactory = "kafkaListenerContainerFactory")
    public void verifyAccount(VerifyAccountRequest verifyAccountRequest) {
        emailService.verifyAccount(verifyAccountRequest);
    }
}
