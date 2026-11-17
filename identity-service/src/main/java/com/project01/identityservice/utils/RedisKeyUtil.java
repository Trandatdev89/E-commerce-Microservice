package com.project01.identityservice.utils;

public class RedisKeyUtil {

    public static final String OTP_CODE = "Otp:";
    /** Số lần nhập sai OTP, quá giới hạn thì hủy OTP */
    public static final String OTP_ATTEMPT = "OtpAttempt:";
    /** Chặn gửi lại OTP liên tục (spam email) */
    public static final String OTP_RESEND_COOLDOWN = "OtpResendCooldown:";
    public static final String PENDING_PROFILE = "PendingProfile:";
    public static final String JWT_BLACKLIST = "JwtBlackList:";

    public static String generateKey(String prefix, String key) {
        return prefix + key;
    }

}
