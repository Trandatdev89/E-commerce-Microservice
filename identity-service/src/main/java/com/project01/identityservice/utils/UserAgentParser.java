package com.project01.identityservice.utils;

import java.util.Locale;

/**
 * Đọc trình duyệt + hệ điều hành từ header User-Agent (đủ dùng để hiển thị và so thiết bị,
 * không cần thư viện ngoài). User-Agent có thể bị làm giả nên chỉ dùng làm tín hiệu tham khảo,
 * không dùng để xác thực.
 */
public final class UserAgentParser {

    private UserAgentParser() {
    }

    public record DeviceInfo(String browser, String os) {
        /** Khóa so sánh thiết bị: chỉ trình duyệt + HĐH, bỏ số phiên bản (Chrome 131 -> 132 vẫn là cùng thiết bị) */
        public String deviceKey() {
            return browser + "|" + os;
        }

        public String label() {
            return browser + " trên " + os;
        }
    }

    public static DeviceInfo parse(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new DeviceInfo("Không rõ", "Không rõ");
        }
        String ua = userAgent.toLowerCase(Locale.ROOT);
        return new DeviceInfo(browser(ua), os(ua));
    }

    // Thứ tự kiểm tra quan trọng: UA của Edge/Opera cũng chứa "chrome", UA của Chrome cũng chứa "safari"
    private static String browser(String ua) {
        if (ua.contains("postmanruntime")) return "Postman";
        if (ua.startsWith("curl/")) return "curl";
        if (ua.contains("edg/")) return "Edge";
        if (ua.contains("opr/") || ua.contains("opera")) return "Opera";
        if (ua.contains("coc_coc_browser")) return "Cốc Cốc";
        if (ua.contains("firefox/")) return "Firefox";
        if (ua.contains("chrome/") || ua.contains("crios/")) return "Chrome";
        if (ua.contains("safari/")) return "Safari";
        return "Trình duyệt khác";
    }

    private static String os(String ua) {
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("android")) return "Android";
        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        if (ua.contains("mac os x") || ua.contains("macintosh")) return "macOS";
        if (ua.contains("linux")) return "Linux";
        return "Không rõ";
    }
}
