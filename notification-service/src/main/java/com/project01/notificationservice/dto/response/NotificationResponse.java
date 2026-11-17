package com.project01.notificationservice.dto.response;

import com.project01.notificationservice.enums.NotificationChannel;
import com.project01.notificationservice.enums.NotificationStatus;
import com.project01.notificationservice.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private String id;
    private NotificationType type;
    private NotificationChannel channel;
    private String subject;
    private String referenceId;
    private Map<String, Object> metadata;
    private NotificationStatus status;
    private boolean read;
    private Instant createdAt;
    private Instant sentAt;
}
