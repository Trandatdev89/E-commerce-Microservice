package com.project01.orderservice.exception;


import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ của service, dạng ORD_ER_xxx. Lỗi chung (400, 401, 403, 500...)
 * dùng CommonErrorCode trong common-core.
 */
public enum ErrorCode implements BaseErrorCode {
    UNAUTHOUCATED("ORD_ER_001", "Tài khoản chưa được xác thực,Yêu cầu đăng nhập!", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("ORD_ER_002", "Dữ liệu chưa hợp lệ!", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("ORD_ER_003", "Token đã hết hiệu lực hoặc sai!", HttpStatus.UNAUTHORIZED),
    UNAUTHOUZATED("ORD_ER_004", "Bạn không có quyền truy cập vào tài nguyên này", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_MATCH("ORD_ER_005", "San pham khong phu hop", HttpStatus.BAD_REQUEST),
    INVENTORY_NOT_ENOUGH("ORD_ER_006", "The quantity of products in stock is not enough", HttpStatus.BAD_REQUEST),
    REFERENCE_NOTFOUND("ORD_ER_007", "Reference order not found", HttpStatus.NOT_FOUND),
    CANCEL_REASON_NOTFOUND("ORD_ER_008", "Lý do hủy đơn không hợp lệ", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_CANCEL("ORD_ER_009", "Đơn hàng đang giao hoặc đã kết thúc, không thể hủy", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOTFOUND("ORD_ER_010", "Not found customer in Order", HttpStatus.NOT_FOUND);

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
