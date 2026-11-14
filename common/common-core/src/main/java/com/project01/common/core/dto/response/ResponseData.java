package com.project01.common.core.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project01.common.core.constants.SystemConstants;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseData<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;
    private String error;
    private String path;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processDate;
    private String transactionId;
    private String clientMessageId;
    private String soaErrorCode;
    private String soaErrorDesc;
    private T data;

    public ResponseData(){}

    @NonNull
    private ResponseData<T> getResponseDataSuccess() {
        this.status = 200;
        this.error = null;
        this.path = MDC.get(SystemConstants.PATH);
        this.processDate = LocalDateTime.now();
        this.transactionId = MDC.get(SystemConstants.TRACE_ID);
        this.clientMessageId = MDC.get(SystemConstants.CLIENT_MESSAGE_ID);
        return this;
    }

    @NonNull
    private ResponseData<T> getResponseDataError(int status, String error, String soaErrorCode, String soaErrorDesc) {
        this.status = status;
        this.error = error;
        this.path = MDC.get(SystemConstants.PATH);
        this.processDate = LocalDateTime.now();
        this.transactionId = MDC.get(SystemConstants.TRACE_ID);
        this.clientMessageId = MDC.get(SystemConstants.CLIENT_MESSAGE_ID);
        this.soaErrorCode = soaErrorCode;
        this.soaErrorDesc = soaErrorDesc;
        return this;
    }

    public ResponseData<T> success(){
        return getResponseDataSuccess();
    }

    public ResponseData<T> success(T data){
        this.data = data;
        return getResponseDataSuccess();
    }

    public ResponseData<T> error(int status, String error, String soaErrorCode, String soaErrorDesc){
        return getResponseDataError(status, error, soaErrorCode, soaErrorDesc);
    }

    public ResponseData<T> error(int status, String error, String soaErrorCode, String soaErrorDesc,T data){
        this.data = data;
        return getResponseDataError(status, error, soaErrorCode, soaErrorDesc);
    }

}
