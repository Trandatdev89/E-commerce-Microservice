package com.project01.notificationservice.entity;

import com.project01.notificationservice.enums.NotificationChannel;
import com.project01.notificationservice.enums.NotificationStatus;
import com.project01.notificationservice.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Mỗi document là 1 thông báo đã gửi (hoặc đang gửi) tới 1 người nhận qua 1 kênh.
 * <p>
 * Thay cho thiết kế cũ (1 document chứa 3 field payload OrderConfirm / PaymentConfirm /
 * VerifyAccountRequest, phần lớn null): giờ cấu trúc giống nhau cho mọi loại, dữ liệu riêng
 * của từng loại để trong {@link #metadata}, và không bao giờ lưu OTP.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ntf_notifications")
@CompoundIndex(name = "idx_user_created", def = "{'userId': 1, 'createdAt': -1}")
public class Notification {

    @Id
    private String id;

    @Indexed(unique = true)
    private String dedupKey;

    private String userId;

    private NotificationType type;
    private NotificationChannel channel;

    private String recipient;
    private String subject;

    private String content;

    @Indexed
    private String referenceId;

    private Map<String, Object> metadata;

    @Indexed
    private NotificationStatus status;

    /**
     * Mã message phía nhà cung cấp (Mailjet MessageID)
     */
    private String providerMessageId;
    private String errorMessage;
    private int retryCount;

    private boolean read;

    private Instant createdAt;
    private Instant sentAt;
}
