package com.project01.identityservice.services.impl;

import com.project01.common.core.exception.BusinessException;
import com.project01.identityservice.entity.RefreshTokenEntity;
import com.project01.identityservice.exception.ErrorCode;
import com.project01.identityservice.repository.RefreshTokenRepository;
import com.project01.identityservice.services.RefreshTokenService;
import com.project01.identityservice.utils.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-token-expiration:604800}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final ReuseRevoker reuseRevoker;

    @Override
    @Transactional
    public IssuedToken issue(String accountId, String userAgent, String ipAddress) {
        String rawToken = TokenHashUtil.generateOpaqueToken();
        RefreshTokenEntity saved = refreshTokenRepository.save(RefreshTokenEntity.builder()
                .accountId(accountId)
                .tokenHash(TokenHashUtil.sha256(rawToken))
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                .revoked(false)
                .userAgent(truncate(userAgent, 255))
                .ipAddress(truncate(ipAddress, 45))
                .build());
        return new IssuedToken(saved.getId(), rawToken);
    }

    @Override
    @Transactional
    public Rotation rotate(String rawRefreshToken, String userAgent, String ipAddress) {
        RefreshTokenEntity current = refreshTokenRepository.findByTokenHash(TokenHashUtil.sha256(rawRefreshToken))
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID));

        if (Boolean.TRUE.equals(current.getRevoked())) {
            log.warn("Refresh token reuse detected, revoking all sessions of account {}", current.getAccountId());
            // Chạy ở transaction riêng để lệnh thu hồi không bị rollback theo exception ném ra bên dưới
            reuseRevoker.revokeAll(current.getAccountId());
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        if (!current.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        current.setRevoked(true);
        refreshTokenRepository.save(current);
        return new Rotation(current.getAccountId(), issue(current.getAccountId(), userAgent, ipAddress));
    }

    @Override
    @Transactional
    public void revoke(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(TokenHashUtil.sha256(rawRefreshToken))
                .filter(token -> !Boolean.TRUE.equals(token.getRevoked()))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    public List<RefreshTokenEntity> findActiveSessions(String accountId) {
        return refreshTokenRepository.findAllByAccountIdAndRevokedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                accountId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void revokeSession(String accountId, Long sessionId) {
        // Tìm theo cả accountId: không đăng xuất được phiên của người khác dù đoán đúng id
        RefreshTokenEntity session = refreshTokenRepository.findByIdAndAccountId(sessionId, accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOTFOUND));
        if (!Boolean.TRUE.equals(session.getRevoked())) {
            session.setRevoked(true);
            refreshTokenRepository.save(session);
        }
    }

    private static String truncate(String value, int maxLength) {
        return value == null || value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    /** Bean riêng để @Transactional(REQUIRES_NEW) có hiệu lực (gọi qua proxy, không phải this). */
    @Service
    @RequiredArgsConstructor
    public static class ReuseRevoker {
        private final RefreshTokenRepository refreshTokenRepository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void revokeAll(String accountId) {
            refreshTokenRepository.revokeAllByAccountId(accountId);
        }
    }
}
