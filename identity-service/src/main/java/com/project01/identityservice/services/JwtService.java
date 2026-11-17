package com.project01.identityservice.services;

import com.nimbusds.jwt.SignedJWT;
import com.project01.common.security.jwt.CustomUserDetail;

public interface JwtService {

    /** Access token (JWT) ngắn hạn, mang username, userId và role. */
    String generateAccessToken(CustomUserDetail customUserDetail, Long sessionId);

    /** Thời gian sống của access token (giây), trả cho client biết khi nào cần refresh. */
    long getAccessTokenTtlSeconds();

    /** Kiểm tra chữ ký, hạn dùng và blacklist. Không hợp lệ thì ném BusinessException(INVALID_TOKEN). */
    SignedJWT verifyAccessToken(String token);

    /** Giống verifyAccessToken nhưng trả true/false thay vì ném lỗi (dùng cho gateway introspect). */
    boolean introspectToken(String token);

    /** Đưa access token vào blacklist tới khi nó hết hạn (dùng khi logout). */
    void blacklist(SignedJWT signedJWT);
}
