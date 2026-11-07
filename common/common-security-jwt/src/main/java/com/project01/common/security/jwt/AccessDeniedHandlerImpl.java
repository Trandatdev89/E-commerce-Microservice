package com.project01.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project01.common.core.dto.response.ResponseData;
import com.project01.common.core.enums.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Bị chặn quyền ở tầng filter -> 403. (@PreAuthorize trong controller thì do GlobalExceptionHandler xử lý.)
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    // findAndRegisterModules để ghi được LocalDateTime (processDate) trong ResponseData
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();
    private static final String MESSAGE = "Bạn không có quyền truy cập vào tài nguyên này";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        CommonErrorCode errorCode = CommonErrorCode.FORBIDDEN;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ResponseData<Object> body = new ResponseData<>()
                .error(errorCode.getHttpStatus().value(), MESSAGE, errorCode.getCode(), MESSAGE);
        OBJECT_MAPPER.writeValue(response.getWriter(), body);
        response.flushBuffer();
    }
}
