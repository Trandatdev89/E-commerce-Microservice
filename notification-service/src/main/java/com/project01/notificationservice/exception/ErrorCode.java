package com.project01.notificationservice.exception;

import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseErrorCode {
    NOTIFICATION_NOTFOUND("NTF_ER_001", "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    TEMPLATE_NOTFOUND("NTF_ER_002", "Chưa cấu hình mẫu thông báo", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
