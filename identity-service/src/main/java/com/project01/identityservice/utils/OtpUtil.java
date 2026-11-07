package com.project01.identityservice.utils;

import java.security.SecureRandom;

public class OtpUtil {

    public static String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }
}
