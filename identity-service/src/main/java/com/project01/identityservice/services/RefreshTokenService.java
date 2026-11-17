package com.project01.identityservice.services;

import com.project01.identityservice.entity.RefreshTokenEntity;

import java.util.List;

public interface RefreshTokenService {

    IssuedToken issue(String accountId, String userAgent, String ipAddress);

    Rotation rotate(String rawRefreshToken, String userAgent, String ipAddress);

    void revoke(String rawRefreshToken);

    /** Các phiên đang hoạt động (chưa thu hồi, chưa hết hạn), mới nhất trước */
    List<RefreshTokenEntity> findActiveSessions(String accountId);

    /** Đăng xuất 1 phiên của chính tài khoản đó (vd thiết bị lạ). Phiên không thuộc tài khoản thì báo không tìm thấy */
    void revokeSession(String accountId, Long sessionId);

    record IssuedToken(Long sessionId, String rawToken) {
    }

    record Rotation(String accountId, IssuedToken newToken) {
    }
}
