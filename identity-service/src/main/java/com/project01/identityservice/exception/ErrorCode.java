package com.project01.identityservice.exception;


import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ của service, dạng IDT_ER_xxx. Lỗi chung (400, 401, 403, 500...)
 * dùng CommonErrorCode trong common-core.
 */
public enum ErrorCode implements BaseErrorCode {
    UNAUTHOUCATED("IDT_ER_001", "Tài khoản chưa được xác thực,Yêu cầu đăng nhập!", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("IDT_ER_002", "Dữ liệu chưa hợp lệ!", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND("IDT_ER_003", "User không tồn tại!", HttpStatus.NOT_FOUND),
    USER_DISABLE("IDT_ER_004", "User chua kich hoat account!", HttpStatus.UNAUTHORIZED),
    PHONE_EXITSED("IDT_ER_005", "SDT này đã tồn tại!", HttpStatus.BAD_REQUEST),
    PASSWORD_WRONG("IDT_ER_006", "Mật khẩu sai.Hãy nhập lại!", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("IDT_ER_007", "Token đã hết hiệu lực hoặc sai!", HttpStatus.UNAUTHORIZED),
    USER_EXITSED("IDT_ER_008", "Username này đã tồn tại.Vui lòng chọn tên đăng nhập khác", HttpStatus.BAD_REQUEST),
    EMAIL_EXITSED("IDT_ER_009", "Email này đã được sử dụng", HttpStatus.BAD_REQUEST),
    USER_LOCKED("IDT_ER_010", "Tài khoản đã bị khóa", HttpStatus.FORBIDDEN),
    ROLE_NOTFOUND("IDT_ER_011", "Chưa khởi tạo dữ liệu role, hãy chạy script seed", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHOUZATED("IDT_ER_012", "Bạn không có quyền truy cập vào tài nguyên này", HttpStatus.FORBIDDEN),
    SEARCH_USERNAME("IDT_ER_013", "Field nay chi duoc tim kiem bang username", HttpStatus.BAD_REQUEST);

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
