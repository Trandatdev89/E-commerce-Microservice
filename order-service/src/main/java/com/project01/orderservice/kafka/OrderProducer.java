package com.project01.orderservice.kafka;

import com.project01.event.dto.OrderConfirm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderConfirm(OrderConfirm orderConfirm) {
        kafkaTemplate.send("order-topic", orderConfirm);
    }
}
