package com.project01.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Phát lên topic "account-verified-topic" khi tài khoản xác thực OTP thành công.
 * profile-service nghe event này để tạo hồ sơ. Không chứa mật khẩu.
 * Bên nhận phải có class cùng tên đầy đủ (com.project01.event.dto.AccountVerifiedEvent).
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
