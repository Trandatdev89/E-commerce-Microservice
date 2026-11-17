package com.project01.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cấu hình Mailjet (prefix "mailjet" trong notification-service.yml).
 * Key thật nên truyền qua biến môi trường, không commit vào git.
 */
@ConfigurationProperties(prefix = "mailjet")
public record MailjetProperties(String apiKey, String apiSecretKey, String fromEmail, String fromName) {
}
