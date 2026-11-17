package com.project01.identityservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Logout cần cả 2 token: access token bị đưa vào blacklist tới khi hết hạn,
 * refresh token bị thu hồi để không lấy được access token mới.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest {
    @NotBlank(message = "Access token không được để trống")
    private String accessToken;
    private String refreshToken;
}
