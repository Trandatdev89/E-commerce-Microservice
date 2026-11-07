package com.project01.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project01.common.core.dto.response.ResponseData;
import com.project01.common.core.enums.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Thiếu token / token sai -> 401. Chạy trong filter chain nên không đi qua GlobalExceptionHandler,
 * tự ghi ResponseData cùng format với các lỗi khác.
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // findAndRegisterModules để ghi được LocalDateTime (processDate) trong ResponseData
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();
    private static final String MESSAGE = "Tài khoản chưa được xác thực, yêu cầu đăng nhập!";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        CommonErrorCode errorCode = CommonErrorCode.UNAUTHORIZED;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ResponseData<Object> body = new ResponseData<>()
                .error(errorCode.getHttpStatus().value(), MESSAGE, errorCode.getCode(), MESSAGE);
        OBJECT_MAPPER.writeValue(response.getWriter(), body);
        response.flushBuffer();
    }
}
