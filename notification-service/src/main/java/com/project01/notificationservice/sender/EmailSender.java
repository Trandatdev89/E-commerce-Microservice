package com.project01.notificationservice.sender;

/**
 * Gửi email qua nhà cung cấp bên ngoài. Tách interface để đổi Mailjet sang SendGrid / AWS SES / SMTP
 * mà không phải sửa nghiệp vụ.
 */
public interface EmailSender {

    /** @return mã message phía nhà cung cấp, dùng để đối soát */
    String send(String toEmail, String toName, String subject, String htmlBody, String customId);
}
