package com.project01.profileservice.exception;


import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ của service, dạng PRF_ER_xxx. Lỗi chung (400, 401, 403, 500...)
 * dùng CommonErrorCode trong common-core.
 */
public enum ErrorCode implements BaseErrorCode {
    UNAUTHOUCATED("PRF_ER_001", "Tài khoản chưa được xác thực,Yêu cầu đăng nhập!", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("PRF_ER_002", "Dữ liệu chưa hợp lệ!", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND("PRF_ER_003", "User không tồn tại!", HttpStatus.NOT_FOUND),
    PHONE_EXITSED("PRF_ER_004", "SDT này đã tồn tại!", HttpStatus.BAD_REQUEST),
    PASSWORD_WRONG("PRF_ER_005", "Mật khẩu sai.Hãy nhập lại!", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("PRF_ER_006", "Token đã hết hiệu lực hoặc sai!", HttpStatus.UNAUTHORIZED),
    USER_EXITSED("PRF_ER_007", "Username này đã tồn tại.Vui lòng chọn tên đăng nhập khác", HttpStatus.BAD_REQUEST),
    UNAUTHOUZATED("PRF_ER_008", "Bạn không có quyền truy cập vào tài nguyên này", HttpStatus.FORBIDDEN),
    SEARCH_USERNAME("PRF_ER_009", "Field nay chi duoc tim kiem bang username", HttpStatus.BAD_REQUEST);

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
