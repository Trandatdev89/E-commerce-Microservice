package com.project01.identityservice.exception;


import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

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
    SEARCH_USERNAME("IDT_ER_013", "Field nay chi duoc tim kiem bang username", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("IDT_ER_014", "Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    OTP_INVALID("IDT_ER_015", "Mã xác thực không đúng", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED("IDT_ER_016", "Mã xác thực đã hết hạn, vui lòng yêu cầu gửi lại mã", HttpStatus.BAD_REQUEST),
    OTP_TOO_MANY_ATTEMPTS("IDT_ER_017", "Nhập sai mã quá nhiều lần, vui lòng yêu cầu gửi lại mã", HttpStatus.TOO_MANY_REQUESTS),
    ACCOUNT_ALREADY_VERIFIED("IDT_ER_018", "Tài khoản đã được xác thực trước đó", HttpStatus.BAD_REQUEST),
    REGISTRATION_EXPIRED("IDT_ER_019", "Phiên đăng ký đã hết hạn, vui lòng đăng ký lại", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID("IDT_ER_020", "Refresh token không hợp lệ, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("IDT_ER_021", "Refresh token đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    OTP_RESEND_TOO_SOON("IDT_ER_022", "Vui lòng đợi 60 giây trước khi yêu cầu gửi lại mã", HttpStatus.TOO_MANY_REQUESTS),
    SESSION_NOTFOUND("IDT_ER_023", "Không tìm thấy phiên đăng nhập", HttpStatus.NOT_FOUND);

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
