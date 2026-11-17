package com.project01.notificationservice.enums;

/** Loại thông báo, trùng với ntf_templates.code */
public enum NotificationType {
    VERIFY_ACCOUNT,
    ORDER_CONFIRMATION,
    PAYMENT_CONFIRMATION,
    /** Cảnh báo đăng nhập từ thiết bị mới */
    NEW_DEVICE_LOGIN
}
