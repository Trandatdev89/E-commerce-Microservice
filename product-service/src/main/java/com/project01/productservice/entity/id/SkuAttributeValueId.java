package com.project01.productservice.entity.id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SkuAttributeValueId implements Serializable {
    private Long skuId;
    private Long attributeValueId;
}
