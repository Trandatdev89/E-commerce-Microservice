package com.project01.productservice.dto.request;

import com.project01.productservice.entity.SkuStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuRequest {
    @NotBlank(message = "SKU code is not null or empty")
    private String skuCode;
    @NotBlank(message = "SKU name is not null or empty")
    private String name;
    @NotNull(message = "Price is not null")
    @PositiveOrZero(message = "Price must be >= 0")
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Integer weightGram;
    private SkuStatus status;
    /** id trong prd_attribute_value, vd [Đỏ, L] */
    @Builder.Default
    private List<Long> attributeValueIds = new ArrayList<>();
}
