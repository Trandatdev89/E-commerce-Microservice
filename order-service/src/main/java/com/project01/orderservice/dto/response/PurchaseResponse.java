package com.project01.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Giá đã chốt từ product-service (POST /product/private/purchase). */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse {
    private Long skuId;
    private String skuCode;
    private Long productId;
    private String productName;
    private String skuName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
}
