package com.project01.productservice.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public final class SlugUtil {

    private SlugUtil() {
    }

    /** "Áo Polo Nam Đỏ" -> "ao-polo-nam-do" */
    public static String toSlug(String text) {
        String noAccent = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return noAccent.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    /** Tạo slug, nếu đã tồn tại thì thêm hậu tố ngẫu nhiên cho tới khi không trùng. */
    public static String toUniqueSlug(String text, Predicate<String> exists) {
        String base = toSlug(text);
        String slug = base;
        while (exists.test(slug)) {
            slug = base + "-" + Integer.toString(ThreadLocalRandom.current().nextInt(36 * 36 * 36 * 36), 36);
        }
        return slug;
    }
}
