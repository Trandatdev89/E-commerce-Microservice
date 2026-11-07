package com.project01.productservice.repository;

import com.project01.productservice.entity.SkuAttributeValue;
import com.project01.productservice.entity.id.SkuAttributeValueId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SkuAttributeValueRepository extends JpaRepository<SkuAttributeValue, SkuAttributeValueId> {
    List<SkuAttributeValue> findAllBySkuIdIn(Collection<Long> skuIds);
    void deleteAllBySkuId(Long skuId);
}
