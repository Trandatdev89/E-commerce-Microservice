package com.project01.productservice.exception.custom;


public enum ErrorCode {
    UNAUTHOUCATED(401,"Tài khoản chưa được xác thực,Yêu cầu đăng nhập!"),
    BAD_REQUEST(400,"Dữ liệu chưa hợp lệ!"),
    INVALID_TOKEN(401,"Token đã hết hiệu lực hoặc sai!"),
    UNAUTHOUZATED(403,"Bạn không có quyền truy cập vào tài nguyên này"),
    PRODUCT_NOT_MATCH(400,"San pham khong phu hop"),
    CATEGORY_NOTFOUND(404,"Khong co danh muc san pham"),
    FIELD_NOTNULL(400,"cac truong khong duoc null"),
    INVENTORY_NOT_ENOUGH(400,"The quantity of products in stock is not enough");
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
