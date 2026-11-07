package com.project01.cartservice.dto.request;


/**
 * Thêm / cập nhật sản phẩm trong giỏ. quantity là số lượng mới (ghi đè), không phải cộng thêm.
 */
public record CartRequest(String userId, Long skuId, Integer quantity) {
}
