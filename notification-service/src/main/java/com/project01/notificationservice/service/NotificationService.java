package com.project01.notificationservice.service;

import com.project01.notificationservice.dto.NotificationCommand;
import com.project01.notificationservice.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationService {

    /**
     * Render mẫu, gửi và lưu lịch sử. Gọi lại với cùng dedupKey sẽ không gửi trùng.
     * Gửi lỗi thì ném exception để Kafka giao lại message và thử lại.
     */
    void send(NotificationCommand command);

    Page<NotificationResponse> getMyNotifications(String userId, int page, int size);

    long countUnread(String userId);

    void markAsRead(String notificationId, String userId);
}
