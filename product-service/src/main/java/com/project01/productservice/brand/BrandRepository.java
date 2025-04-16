package com.project01.productservice.brand;

import com.project01.productservice.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BrandRepository extends JpaRepository<Brand, Long> {

}
