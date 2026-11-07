package com.project01.common.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final BaseErrorCode baseErrorCode;
    private final Object[] messageArg;

    public BusinessException(BaseErrorCode baseErrorCode) {
        this(null,baseErrorCode,null);
    }

    public BusinessException(BaseErrorCode baseErrorCode,Throwable cause){
        this(null,baseErrorCode,cause);
    }

    public BusinessException(String message, BaseErrorCode baseErrorCode) {
        super(message);
        this.messageArg = null;
        this.baseErrorCode = baseErrorCode;
    }

    public BusinessException(Object[] messageArg, BaseErrorCode baseErrorCode, Throwable cause) {
        super(baseErrorCode.getMessage(), cause);
        this.messageArg = messageArg;
        this.baseErrorCode = baseErrorCode;
    }

    public String getCode() {
        return baseErrorCode.getCode();
    }

    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        }
        return messageArg == null ? baseErrorCode.getMessage() : String.format(baseErrorCode.getMessage(), messageArg);
    }

    public String getLocalizedMessage() {
        return BusinessException.class.getSimpleName() + "[" + baseErrorCode.getCode() + "-" + getMessage() + "-" + baseErrorCode.getHttpStatus() + "]";
    }

    public HttpStatus getHttpStatus() {
        return baseErrorCode.getHttpStatus();
    }
}
