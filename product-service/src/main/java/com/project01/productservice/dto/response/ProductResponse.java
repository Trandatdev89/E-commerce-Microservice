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
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String status;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    /** Ảnh đại diện (sort_order nhỏ nhất) */
    private String imageUrl;
    private List<String> images;
    /** Giá thấp nhất trong các SKU đang bán, để hiển thị "từ ...đ" */
    private BigDecimal minPrice;
    private List<SkuResponse> skus;
}
