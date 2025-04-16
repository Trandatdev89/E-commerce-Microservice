package com.project01.orderservice.exception.custom;


public enum ErrorCode {
    UNAUTHOUCATED(401,"Tài khoản chưa được xác thực,Yêu cầu đăng nhập!"),
    BAD_REQUEST(400,"Dữ liệu chưa hợp lệ!"),
    INVALID_TOKEN(401,"Token đã hết hiệu lực hoặc sai!"),
    UNAUTHOUZATED(403,"Bạn không có quyền truy cập vào tài nguyên này"),
    PRODUCT_NOT_MATCH(400,"San pham khong phu hop"),
    INVENTORY_NOT_ENOUGH(400,"The quantity of products in stock is not enough"),
    REFERENCE_NOTFOUND(404,"Reference order not found"),
    CUSTOMER_NOTFOUND(404,"Not found customer in Order");
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
