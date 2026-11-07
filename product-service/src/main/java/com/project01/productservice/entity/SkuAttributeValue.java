package com.project01.productservice.entity;

import com.project01.productservice.entity.id.SkuAttributeValueId;
import jakarta.persistence.*;
import lombok.*;

/** Bảng nối SKU - giá trị thuộc tính, map thủ công bằng 2 cột id. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(SkuAttributeValueId.class)
@Table(name = "prd_sku_attribute_value")
public class SkuAttributeValue {

    @Id
    @Column(name = "sku_id")
    private Long skuId;

    @Id
    @Column(name = "attribute_value_id")
    private Long attributeValueId;
}
