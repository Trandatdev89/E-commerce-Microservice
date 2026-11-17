package com.project01.identityservice.services;

import com.project01.identityservice.entity.RefreshTokenEntity;
import com.project01.identityservice.repository.RefreshTokenRepository;
import com.project01.identityservice.utils.UserAgentParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Xác định lần đăng nhập có đến từ thiết bị lạ không, dựa trên lịch sử idt_refresh_token.
 * <p>
 * Thiết bị được nhận diện bằng trình duyệt + hệ điều hành đọc từ user_agent. Không so ip_address,
 * vì IP của nhà mạng / 4G / Wi-Fi đổi liên tục: so IP sẽ cảnh báo nhầm gần như mỗi ngày.
 * IP chỉ được đưa vào email để người dùng tự nhận biết.
 */
@Component
@RequiredArgsConstructor
public class NewDeviceDetector {

    /** Chỉ xét lịch sử 90 ngày gần nhất: thiết bị lâu không dùng coi như thiết bị mới */
    private static final int HISTORY_DAYS = 90;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Gọi TRƯỚC khi lưu phiên mới.
     *
     * @return true nếu tài khoản đã từng đăng nhập nhưng chưa từng dùng thiết bị này.
     * Lần đăng nhập đầu tiên của tài khoản trả false (chưa có gì để so, không cảnh báo).
     */
    public boolean isNewDevice(String accountId, String userAgent) {
        List<RefreshTokenEntity> history = refreshTokenRepository
                .findAllByAccountIdAndCreatedAtAfter(accountId, LocalDateTime.now().minusDays(HISTORY_DAYS));
        if (history.isEmpty()) {
            return false;
        }
        String currentDevice = UserAgentParser.parse(userAgent).deviceKey();
        return history.stream()
                .noneMatch(session -> UserAgentParser.parse(session.getUserAgent()).deviceKey().equals(currentDevice));
    }
}
