package com.project01.notificationservice.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailjetProperties.class)
public class MailjetConfig {

    /** Tạo 1 client dùng chung, thay vì code cũ tạo MailjetClient mới cho mỗi email */
    @Bean
    public MailjetClient mailjetClient(MailjetProperties properties) {
        return new MailjetClient(ClientOptions.builder()
                .apiKey(properties.apiKey())
                .apiSecretKey(properties.apiSecretKey())
                .build());
    }
}
