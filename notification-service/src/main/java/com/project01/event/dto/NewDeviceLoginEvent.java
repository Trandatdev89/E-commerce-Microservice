package com.project01.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event identity-service phát khi tài khoản đăng nhập từ thiết bị chưa từng dùng (topic "new-device-login-topic").
 * Giữ đúng package com.project01.event.dto vì bên gửi ghi tên class vào header Kafka.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewDeviceLoginEvent {
    private String userId;
    private String username;
    private String email;
    private Long sessionId;
    private String browser;
    private String os;
    private String ipAddress;
    private String loginAt;
    private int otherActiveSessions;
    private String manageUrl;
}
