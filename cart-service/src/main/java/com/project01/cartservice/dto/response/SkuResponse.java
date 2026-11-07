package com.project01.cartservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Dữ liệu SKU nhận từ product-service (GET /product/public/skus). */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String skuCode;
    private String name;
    private BigDecimal price;
    private String status;
    private String imageUrl;
}
