package com.project01.common.core.dto;

/**
 * DTO lồng nhau (vd địa chỉ trong đơn hàng) implements interface này để message lỗi
 * validate có thêm tên đối tượng phía trước, vd "Địa chỉ nhận hàng: không được để trống".
 */
public interface BaseObjectInfo {
    String getObjectName();
}
