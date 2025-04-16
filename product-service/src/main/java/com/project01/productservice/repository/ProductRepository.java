package com.project01.productservice.repository;

import com.project01.productservice.category.Category;
import com.project01.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{
    void deleteByIdIn(List<Long> id);
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findAllByIdInOrderById(List<Long> id);
    boolean existsByCategory_Id(Long categoryId);
    boolean existsByBrand_Id(Long brandId);
    boolean existsByShips_Id(Long shipId);
}
