package com.project01.identityservice.kafka;

import com.project01.event.dto.VerifyAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class IdentityProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendVerifyAccount(VerifyAccountRequest verifyAccountRequest) {
        kafkaTemplate.send("identity-topic", verifyAccountRequest);
    }
}
