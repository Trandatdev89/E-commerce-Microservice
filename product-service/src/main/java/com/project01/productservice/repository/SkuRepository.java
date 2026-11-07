package com.project01.productservice.repository;

import com.project01.productservice.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SkuRepository extends JpaRepository<Sku, Long> {
    List<Sku> findAllByProductIdIn(Collection<Long> productIds);
    boolean existsBySkuCode(String skuCode);
    boolean existsBySkuCodeAndIdNot(String skuCode, Long id);
}
