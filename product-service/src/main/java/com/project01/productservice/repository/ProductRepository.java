package com.project01.productservice.repository;

import com.project01.productservice.entity.Product;
import com.project01.productservice.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findAllByStatus(ProductStatus status);
    List<Product> findAllByCategoryIdAndStatus(Long categoryId, ProductStatus status);
    boolean existsBySlug(String slug);
}
