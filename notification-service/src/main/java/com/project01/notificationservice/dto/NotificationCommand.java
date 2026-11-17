package com.project01.notificationservice.dto;

import com.project01.notificationservice.enums.NotificationType;
import lombok.Builder;

import java.util.Map;

/**
 * Yêu cầu gửi 1 thông báo, được consumer dựng từ event Kafka.
 *
 * @param variables biến để render mẫu (có thể chứa dữ liệu nhạy cảm như OTP, không lưu DB)
 * @param metadata  dữ liệu lưu kèm thông báo để tra cứu (không được chứa dữ liệu nhạy cảm)
 */
@Builder
public record NotificationCommand(
        NotificationType type,
        String dedupKey,
        String userId,
        String recipient,
        String recipientName,
        String referenceId,
        Map<String, Object> variables,
        Map<String, Object> metadata) {
}
