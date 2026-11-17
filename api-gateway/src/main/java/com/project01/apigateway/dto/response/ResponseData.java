package com.project01.apigateway.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData<T> {
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
}
