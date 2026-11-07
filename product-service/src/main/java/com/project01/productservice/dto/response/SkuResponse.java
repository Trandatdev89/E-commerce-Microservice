package com.project01.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    private BigDecimal compareAtPrice;
    private Integer weightGram;
    private String status;
    private String imageUrl;
    private List<Long> attributeValueIds;
}
