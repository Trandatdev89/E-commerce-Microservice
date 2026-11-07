package com.project01.identityservice.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KafkaProducerConfig {
    @Bean
    public NewTopic identityTopic() {
        return new NewTopic("identity-topic", 3, (short) 1);
    }
}
