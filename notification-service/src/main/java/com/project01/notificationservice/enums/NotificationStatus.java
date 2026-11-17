package com.project01.notificationservice.enums;

public enum NotificationStatus {
    /** Đã ghi nhận, đang gửi */
    PENDING,
    SENT,
    /** Gửi lỗi; Kafka sẽ giao lại message để thử lại */
    FAILED
}
