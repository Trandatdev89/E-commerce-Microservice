package com.project01.cartservice.exception.custom;


public enum ErrorCode {
    UNAUTHOUCATED(401,"Tài khoản chưa được xác thực,Yêu cầu đăng nhập!"),
    BAD_REQUEST(400,"Dữ liệu chưa hợp lệ!"),
    CART_EMPTY(400,"Giỏ hàng trống"),
    INVALID_TOKEN(401,"Token đã hết hiệu lực hoặc sai!"),
    UNAUTHOUZATED(403,"Bạn không có quyền truy cập vào tài nguyên này"),
    ITEM_NOT_FOUND(404,"Item Not Exits in Cart");
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
