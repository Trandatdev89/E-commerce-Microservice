package com.project01.identityservice.config;


import com.project01.identityservice.kafka.IdentityProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KafkaProducerConfig {
    @Bean
    public NewTopic identityTopic() {
        return new NewTopic(IdentityProducer.IDENTITY_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic accountVerifiedTopic() {
        return new NewTopic(IdentityProducer.ACCOUNT_VERIFIED_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic newDeviceLoginTopic() {
        return new NewTopic(IdentityProducer.NEW_DEVICE_LOGIN_TOPIC, 3, (short) 1);
    }
}
