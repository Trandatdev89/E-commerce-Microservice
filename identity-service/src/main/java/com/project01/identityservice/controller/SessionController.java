package com.project01.identityservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.identityservice.dto.response.SessionResponse;
import com.project01.identityservice.entity.RefreshTokenEntity;
import com.project01.identityservice.services.RefreshTokenService;
import com.project01.identityservice.utils.UserAgentParser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project01.identityservice.services.impl.JwtServiceImpl.CLAIM_SESSION_ID;
import static com.project01.identityservice.services.impl.JwtServiceImpl.CLAIM_USER_ID;

/**
 * Quản lý các phiên đăng nhập của chính người dùng. userId lấy từ JWT, không nhận từ request,
 * nên không xem / đăng xuất được phiên của người khác.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {

    private final RefreshTokenService refreshTokenService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Danh sách thiết bị đang đăng nhập",
            description = "Các phiên chưa đăng xuất và chưa hết hạn, kèm thiết bị (đọc từ User-Agent), IP, lần hoạt động gần nhất")
    public ResponseData<List<SessionResponse>> getMySessions(@AuthenticationPrincipal Jwt jwt) {
        Long currentSessionId = currentSessionId(jwt);
        List<SessionResponse> sessions = refreshTokenService.findActiveSessions(jwt.getClaimAsString(CLAIM_USER_ID))
                .stream()
                .map(session -> toResponse(session, currentSessionId))
                .toList();
        return new ResponseData<List<SessionResponse>>().success(sessions);
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Đăng xuất một thiết bị",
            description = "Thu hồi refresh token của phiên đó: thiết bị không làm mới token được nữa và bị đăng xuất khi access token hết hạn")
    public ResponseData<Void> revokeSession(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable(name = "sessionId") Long sessionId) {
        refreshTokenService.revokeSession(jwt.getClaimAsString(CLAIM_USER_ID), sessionId);
        return new ResponseData<Void>().success();
    }

    private static Long currentSessionId(Jwt jwt) {
        Object sid = jwt.getClaim(CLAIM_SESSION_ID);
        return sid instanceof Number number ? number.longValue() : null;
    }

    private static SessionResponse toResponse(RefreshTokenEntity session, Long currentSessionId) {
        UserAgentParser.DeviceInfo device = UserAgentParser.parse(session.getUserAgent());
        return SessionResponse.builder()
                .sessionId(session.getId())
                .browser(device.browser())
                .os(device.os())
                .device(device.label())
                .ipAddress(session.getIpAddress())
                .lastActiveAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .current(session.getId().equals(currentSessionId))
                .build();
    }
}
