package com.project01.paymentservice.kafka;

import com.project01.event.dto.PaymentConfirm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void sendNotification(PaymentConfirm notification) {
        kafkaTemplate.send("payment-topic", notification);
    }
}
