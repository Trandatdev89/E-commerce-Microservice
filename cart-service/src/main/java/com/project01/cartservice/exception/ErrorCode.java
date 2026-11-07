package com.project01.cartservice.exception;


import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ của service, dạng CRT_ER_xxx. Lỗi chung (400, 401, 403, 500...)
 * dùng CommonErrorCode trong common-core.
 */
public enum ErrorCode implements BaseErrorCode {
    UNAUTHOUCATED("CRT_ER_001", "Tài khoản chưa được xác thực,Yêu cầu đăng nhập!", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("CRT_ER_002", "Dữ liệu chưa hợp lệ!", HttpStatus.BAD_REQUEST),
    CART_EMPTY("CRT_ER_003", "Giỏ hàng trống", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("CRT_ER_004", "Token đã hết hiệu lực hoặc sai!", HttpStatus.UNAUTHORIZED),
    UNAUTHOUZATED("CRT_ER_005", "Bạn không có quyền truy cập vào tài nguyên này", HttpStatus.FORBIDDEN),
    ITEM_NOT_FOUND("CRT_ER_006", "Item Not Exits in Cart", HttpStatus.NOT_FOUND);

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
