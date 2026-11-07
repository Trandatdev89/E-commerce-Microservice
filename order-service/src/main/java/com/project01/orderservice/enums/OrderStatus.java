package com.project01.orderservice.enums;

/** Trùng với ord_status.code */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PAID,
    SHIPPING,
    DELIVERED,
    CANCELLED,
    RETURNED;

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED || this == RETURNED;
    }
}
