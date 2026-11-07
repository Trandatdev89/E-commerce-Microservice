package com.project01.productservice.dto.request;

import com.project01.productservice.entity.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Thông tin sản phẩm gốc. Giá / biến thể tạo riêng qua SkuRequest. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Name product is not null or empty")
    private String name;
    private String description;
    @NotNull(message = "Category is not null")
    private Long categoryId;
    private Long brandId;
    /** Mặc định DRAFT nếu không truyền */
    private ProductStatus status;
}
