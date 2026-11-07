package com.project01.orderservice.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReferenceOrder {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ReferenceOrder() {
    }

    /** Mã đơn dạng ORD-20261101-8F3K2A: ngày đặt + 6 ký tự ngẫu nhiên (bỏ 0/O/1/I dễ nhầm). */
    public static String generate() {
        StringBuilder suffix = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            suffix.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return "ORD-" + LocalDate.now().format(DATE) + "-" + suffix;
    }
}
