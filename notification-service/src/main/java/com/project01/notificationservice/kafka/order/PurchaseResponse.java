package com.project01.notificationservice.kafka.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Sản phẩm trong event OrderConfirm, cùng cấu trúc với PurchaseResponse của order-service. */
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
