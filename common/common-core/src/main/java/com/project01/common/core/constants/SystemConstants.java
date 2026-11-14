package com.project01.common.core.constants;

public class SystemConstants {
    public static final String PATH = "path";
    public static final String CLIENT_MESSAGE_ID = "clientMessageId";
    public static final String START_TIME = "startTime";
    public static final String LOG_REQUEST = "logRequest";
    public static final String LOG_RESPONSE = "logResponse";
    public static final String TRANSACTION_ID = "transactionId";

    /** Key do Micrometer Tracing tự đặt vào MDC, dùng làm transactionId trả về cho client */
    public static final String TRACE_ID = "traceId";

    /** Các key GlobalExceptionHandler ghi vào MDC khi trả lỗi */
    public static final String LOG_TYPE = "logType";
    public static final String STATUS = "status";
    public static final String RESPONSE_CODE = "responseCode";
    public static final String RESPONSE_MSG = "responseMsg";

    /** Header client gửi kèm để tự đối soát request của mình */
    public static final String HEADER_CLIENT_MESSAGE_ID = "X-Client-Message-Id";
    /** Header trả về cho client chứa traceId, dùng để tra log / Zipkin khi báo lỗi */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
}
