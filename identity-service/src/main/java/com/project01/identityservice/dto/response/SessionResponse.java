package com.project01.identityservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    private Long sessionId;
    private String browser;
    private String os;
    private String device;
    private String ipAddress;
    private LocalDateTime lastActiveAt;
    private LocalDateTime expiresAt;
    /** true nếu là phiên đang gọi API (thiết bị hiện tại) */
    private boolean current;
}
