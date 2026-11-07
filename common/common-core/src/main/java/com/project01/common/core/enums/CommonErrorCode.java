package com.project01.common.core.enums;

import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements BaseErrorCode {

    BAD_REQUEST("GL_ER_400", "Server cannot or will not process the request due to something that is to be a client error.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("GL_ER_401", "There was an error during authentication, requires login again.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("GL_ER_403", "You do not have permission to perform this operation.", HttpStatus.FORBIDDEN),
    NOT_FOUND("GL_ER_004", "No handler found for %s %s", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("GL_ER_405", "Request method is known by the server but is not supported by the target resource", HttpStatus.METHOD_NOT_ALLOWED),
    DATA_INTEGRITY_VIOLATION("GL_ER_006", "An attempt to insert or update data results in violation of an integrity constraint.", HttpStatus.CONFLICT),
    MISSING_REQUEST_PARAMETER("GL_ER_007", "Required %s parameter '%s' is not present", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE("GL_ER_008", "The server refuses to accept the request because the payload format is in an unsupported format", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    MEDIA_TYPE_NOT_ACCEPTABLE("GL_ER_009", "The request handler cannot generate a response that is acceptable by the client", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    ARGUMENT_TYPE_MISMATCH("GL_ER_010", "Required type %s parameter '%s' is not match", HttpStatus.BAD_REQUEST),
    ARGUMENT_NOT_VALID("GL_ER_011", "Validation failed", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND("GL_ER_012", "Object no longer exists in the database", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("GL_ER_013", "There was an error processing the request, please contact the administrator!", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_TOKEN_INVALID("GL_ER_014", "Access token invalid, create a new access token or login again!", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("GL_ER_015", "Access token expired, create a new access token or login again!", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("GL_ER_016", "The refresh token invalid, requires login again!", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("GL_ER_017", "The refresh token expired, requires login again!", HttpStatus.UNAUTHORIZED),
    WRONG_PASSWORD("GL_ER_018", "Wrong password", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("GL_ER_019", "User not found", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REQUIRED("GL_ER_020", "Refresh token is required", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_EXIST("GL_ER_021", "Refresh token not exist", HttpStatus.UNAUTHORIZED),
    GRANT_TYPE_NOT_SUPPORTED("GL_ER_022", "Grant type not support", HttpStatus.UNAUTHORIZED),
    ROLE_EXISTED("GL_ER_023", "Role existed", HttpStatus.UNPROCESSABLE_ENTITY),
    ROLE_NOT_FOUND("GL_ER_024", "Role existed", HttpStatus.NOT_FOUND),
    RESET_TOKEN_NOT_FOUND("GL_ER_025", "Reset token not found", HttpStatus.NOT_FOUND),
    RESET_TOKEN_EXPIRED("GL_ER_026", "Reset token expired", HttpStatus.UNPROCESSABLE_ENTITY),
    INSUFFICIENT_SCOPE("GL_ER_027", "Insufficient scope", HttpStatus.FORBIDDEN),
    INTEGRATION_DATA_REQUEST_INVALID("GL_ER_028", "Integration data request invalid", HttpStatus.BAD_REQUEST),
    INTEGRATION_VERSION_INVALID("GL_ER_029", "Integration version invalid", HttpStatus.BAD_REQUEST),
    INTEGRATION_SOURCE_APP_INVALID("GL_ER_030", "Integration source app invalid", HttpStatus.BAD_REQUEST),
    INTEGRATION_DESTINATION_INVALID("GL_ER_031", "Integration destination invalid", HttpStatus.BAD_REQUEST),
    INTEGRATION_MESSAGE_INVALID("GL_ER_032", "Integration message invalid", HttpStatus.BAD_REQUEST),
    INTEGRATION_DESTINATION_UNSUPPORTED("GL_ER_033", "Integration destination unsupported", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    CommonErrorCode(String code, String message, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

}
