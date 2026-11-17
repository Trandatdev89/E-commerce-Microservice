package com.project01.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event identity-service phát khi tài khoản xác thực OTP thành công (topic "account-verified-topic").
 * Giữ đúng package com.project01.event.dto vì bên gửi ghi tên class vào header Kafka.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountVerifiedEvent {
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
