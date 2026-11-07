package com.project01.paymentservice.exception;


import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ của service, dạng PAY_ER_xxx. Lỗi chung (400, 401, 403, 500...)
 * dùng CommonErrorCode trong common-core.
 */
public enum ErrorCode implements BaseErrorCode {
    UNAUTHOUCATED("PAY_ER_001", "Tài khoản chưa được xác thực,Yêu cầu đăng nhập!", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("PAY_ER_002", "Dữ liệu chưa hợp lệ!", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("PAY_ER_003", "Token đã hết hiệu lực hoặc sai!", HttpStatus.UNAUTHORIZED),
    UNAUTHOUZATED("PAY_ER_004", "Bạn không có quyền truy cập vào tài nguyên này", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_MATCH("PAY_ER_005", "San pham khong phu hop", HttpStatus.BAD_REQUEST),
    INVENTORY_NOT_ENOUGH("PAY_ER_006", "The quantity of products in stock is not enough", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOTFOUND("PAY_ER_007", "No find Customer with that id", HttpStatus.BAD_REQUEST),
    PAYMENT_NOTFOUND("PAY_ER_008", "Không tìm thấy thanh toán của đơn hàng", HttpStatus.NOT_FOUND),
    PAYMENT_METHOD_INVALID("PAY_ER_009", "Phương thức thanh toán không hợp lệ hoặc đang tạm tắt", HttpStatus.BAD_REQUEST);

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
