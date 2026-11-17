package com.project01.identityservice.kafka;

import com.project01.event.dto.AccountVerifiedEvent;
import com.project01.event.dto.NewDeviceLoginEvent;
import com.project01.event.dto.VerifyAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class IdentityProducer {

    public static final String IDENTITY_TOPIC = "identity-topic";
    public static final String ACCOUNT_VERIFIED_TOPIC = "account-verified-topic";
    public static final String NEW_DEVICE_LOGIN_TOPIC = "new-device-login-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendVerifyAccount(VerifyAccountRequest verifyAccountRequest) {
        kafkaTemplate.send(IDENTITY_TOPIC, verifyAccountRequest.getUserId(), verifyAccountRequest);
    }

    public void sendAccountVerified(AccountVerifiedEvent event) {
        kafkaTemplate.send(ACCOUNT_VERIFIED_TOPIC, event.getUserId(), event);
    }

    public void sendNewDeviceLogin(NewDeviceLoginEvent event) {
        kafkaTemplate.send(NEW_DEVICE_LOGIN_TOPIC, event.getUserId(), event);
    }
}
