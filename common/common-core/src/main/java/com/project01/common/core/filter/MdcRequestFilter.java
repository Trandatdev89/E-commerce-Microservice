package com.project01.common.core.filter;

import com.project01.common.core.constants.SystemConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Đặt thông tin của request vào MDC để log và ResponseData dùng, và xóa sau khi request kết thúc.
 * <p>
 * traceId / spanId do Micrometer Tracing tự đặt (filter observation chạy trước filter này),
 * ở đây chỉ bổ sung path, clientMessageId và trả traceId về header X-Trace-Id.
 * Bắt buộc xóa trong finally vì Tomcat dùng lại thread cho request sau.
 */
public class MdcRequestFilter extends OncePerRequestFilter {

    private static final List<String> OWNED_KEYS = List.of(
            SystemConstants.PATH,
            SystemConstants.CLIENT_MESSAGE_ID,
            SystemConstants.LOG_TYPE,
            SystemConstants.STATUS,
            SystemConstants.RESPONSE_CODE,
            SystemConstants.RESPONSE_MSG);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            MDC.put(SystemConstants.PATH, request.getRequestURI());
            String clientMessageId = request.getHeader(SystemConstants.HEADER_CLIENT_MESSAGE_ID);
            if (StringUtils.hasText(clientMessageId)) {
                MDC.put(SystemConstants.CLIENT_MESSAGE_ID, clientMessageId);
            }
            String traceId = MDC.get(SystemConstants.TRACE_ID);
            if (StringUtils.hasText(traceId)) {
                response.setHeader(SystemConstants.HEADER_TRACE_ID, traceId);
            }
            chain.doFilter(request, response);
        } finally {
            // Chỉ xóa key do mình đặt; traceId/spanId do Micrometer Tracing tự dọn khi đóng span
            OWNED_KEYS.forEach(MDC::remove);
        }
    }
}
