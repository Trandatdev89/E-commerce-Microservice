package com.project01.apigateway.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Bản sao cấu trúc ResponseData của common-core. Gateway chạy WebFlux nên không dùng được
 * common-core (servlet), nhưng phải trả lỗi cùng format JSON với các service phía sau.
 */
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
