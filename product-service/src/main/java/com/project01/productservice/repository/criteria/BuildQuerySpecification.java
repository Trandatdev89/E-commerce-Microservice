package com.project01.productservice.repository.criteria;

import com.project01.productservice.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class BuildQuerySpecification {

    public static Specification<Product> build(SearchCriteria criteria) {
        Specification<Product> spec = new SpecSearchCriteria(criteria);
        return spec;
    }

}
