package com.project01.productservice.repository;

import com.project01.productservice.entity.Product;
import com.project01.productservice.repository.criteria.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public interface SearchRepository {
    Predicate joinCateAndProduct(SearchCriteria criteria, Root<Product> root, CriteriaBuilder builder);
    Predicate joinBrandAndProduct(SearchCriteria criteria, Root<Product> root, CriteriaBuilder builder);
    Predicate joinShipAndProduct(SearchCriteria criteria, Root<Product> root, CriteriaBuilder builder);
}
