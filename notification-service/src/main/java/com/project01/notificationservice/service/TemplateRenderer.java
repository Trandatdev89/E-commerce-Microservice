package com.project01.notificationservice.service;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Thay {{bien}} trong mẫu bằng giá trị. Giá trị được escape HTML (tên sản phẩm có ký tự "<" không phá giao diện
 * và không chèn được mã độc), trừ biến có hậu tố "Html" là đoạn HTML do code tự dựng.
 */
@Component
public class TemplateRenderer {

    private static final Pattern VARIABLE = Pattern.compile("\\{\\{\\s*(\\w+)\\s*}}");

    public String render(String template, Map<String, Object> variables) {
        if (template == null) {
            return null;
        }
        Matcher matcher = VARIABLE.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            Object value = variables.get(name);
            String text = value == null ? "" : value.toString();
            String replacement = name.endsWith("Html") ? text : HtmlUtils.htmlEscape(text);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
