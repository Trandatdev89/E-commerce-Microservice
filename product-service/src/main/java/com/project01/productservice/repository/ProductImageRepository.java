package com.project01.productservice.repository;

import com.project01.productservice.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findAllByProductIdInOrderBySortOrder(Collection<Long> productIds);
    int countByProductId(Long productId);
}
