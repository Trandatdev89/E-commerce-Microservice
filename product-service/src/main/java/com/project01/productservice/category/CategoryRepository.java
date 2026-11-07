package com.project01.productservice.category;


import com.project01.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlug(String slug);
    List<Category> findAllByIsActiveTrueOrderBySortOrder();
}
