package com.project01.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Phát lên topic "new-device-login-topic" khi tài khoản đăng nhập từ thiết bị chưa từng dùng.
 * notification-service gửi email cảnh báo cho chủ tài khoản.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewDeviceLoginEvent {
    private String userId;
    private String username;
    private String email;
    /** id phiên mới (idt_refresh_token.id), cũng dùng làm khóa chống gửi trùng */
    private Long sessionId;
    private String browser;
    private String os;
    private String ipAddress;
    /** Thời điểm đăng nhập, đã định dạng để hiển thị, vd "21:10 16/11/2026" */
    private String loginAt;
    /** Số thiết bị khác đang đăng nhập tại thời điểm này */
    private int otherActiveSessions;
    /** Link tới trang quản lý phiên trên FE để đăng xuất thiết bị lạ */
    private String manageUrl;
}
