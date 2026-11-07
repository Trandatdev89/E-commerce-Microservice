package com.project01.cartservice.dto.response;


import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    /** id của crt_cart_item */
    private Long id;
    private String userId;
    private Long skuId;
    private String skuCode;
    private Long productId;
    private String productName;
    private String skuName;
    private String imageUrl;
    /** Giá hiện tại lấy từ product-service */
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal lineTotal;
    /** false nếu SKU đã ngừng bán hoặc không còn tồn tại */
    private boolean available;
}
