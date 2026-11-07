package com.project01.identityservice.utils;

public class RedisKeyUtil {

    public static final String OTP_CODE = "Otp:";
    public static final String PENDING_PROFILE = "PendingProfile:";
    public static final String JWT_BLACKLIST = "JwtBlackList:";

    public static String generateKey(String prefix, String key) {
        return prefix + key;
    }

}
