package com.project01.productservice.exception;


import com.project01.common.core.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ của service, dạng PRD_ER_xxx. Lỗi chung (400, 401, 403, 500...)
 * dùng CommonErrorCode trong common-core.
 */
public enum ErrorCode implements BaseErrorCode {
    UNAUTHOUCATED("PRD_ER_001", "Tài khoản chưa được xác thực,Yêu cầu đăng nhập!", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("PRD_ER_002", "Dữ liệu chưa hợp lệ!", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("PRD_ER_003", "Token đã hết hiệu lực hoặc sai!", HttpStatus.UNAUTHORIZED),
    UNAUTHOUZATED("PRD_ER_004", "Bạn không có quyền truy cập vào tài nguyên này", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_MATCH("PRD_ER_005", "San pham khong phu hop", HttpStatus.BAD_REQUEST),
    CATEGORY_NOTFOUND("PRD_ER_006", "Khong co danh muc san pham", HttpStatus.NOT_FOUND),
    FIELD_NOTNULL("PRD_ER_007", "cac truong khong duoc null", HttpStatus.BAD_REQUEST),
    PRODUCT_NOTFOUND("PRD_ER_008", "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_AVAILABLE("PRD_ER_009", "Sản phẩm đã ngừng bán", HttpStatus.BAD_REQUEST),
    BRAND_NOTFOUND("PRD_ER_010", "Không tìm thấy thương hiệu", HttpStatus.NOT_FOUND),
    SKU_NOTFOUND("PRD_ER_011", "Không tìm thấy biến thể sản phẩm", HttpStatus.NOT_FOUND),
    SKU_CODE_EXISTED("PRD_ER_012", "Mã SKU đã tồn tại", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_VALUE_NOTFOUND("PRD_ER_013", "Giá trị thuộc tính không tồn tại", HttpStatus.NOT_FOUND),
    INVENTORY_NOT_ENOUGH("PRD_ER_014", "The quantity of products in stock is not enough", HttpStatus.BAD_REQUEST);

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
