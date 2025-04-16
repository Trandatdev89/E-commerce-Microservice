package com.project01.productservice.repository.criteria;

import com.project01.productservice.category.Category;
import com.project01.productservice.entity.Brand;
import com.project01.productservice.entity.Product;
import com.project01.productservice.entity.Ship;
import com.project01.productservice.repository.SearchRepository;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.springframework.stereotype.Repository;



@Repository
public class SearchCriteriaRepository implements SearchRepository {

    @Override
    public Predicate joinCateAndProduct(SearchCriteria criteria,Root<Product> root,CriteriaBuilder cb) {
        Join<Product, Category> joinCategory = root.join("category", JoinType.INNER);
        String key = extractFieldName(criteria.getKey());
        Predicate predicate = toPredicate(joinCategory,cb,criteria,key);
        return predicate;
    }

    @Override
    public Predicate joinBrandAndProduct(SearchCriteria criteria, Root<Product> root, CriteriaBuilder cb) {
        Join<Product, Brand> joinBrand = root.join("brand", JoinType.INNER);
        String key = extractFieldName(criteria.getKey());
        Predicate predicate = toPredicate(joinBrand,cb,criteria,key);
        return predicate;
    }

    @Override
    public Predicate joinShipAndProduct(SearchCriteria criteria, Root<Product> root, CriteriaBuilder cb) {
        Join<Product, Ship> joinShip = root.join("ships", JoinType.INNER);
        String key = extractFieldName(criteria.getKey());
        Predicate predicate = toPredicate(joinShip,cb,criteria,key);
        return predicate;
    }

    public Predicate toPredicate(@NonNull final Join<?, ?>  root, @NonNull final CriteriaBuilder builder,SearchCriteria criteria,String key) {
        return switch (criteria.getOperator()) {
            case EQUALITY -> builder.equal(root.get(key), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(key), criteria.getValue());
            case GREATER_THAN -> builder.greaterThanOrEqualTo(root.get(key), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThanOrEqualTo(root.get(key), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(key), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(key), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(key), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(key), criteria.getValue().toString());
        };
    }

    private String extractFieldName(String key) {
        return key.split("_")[1].toLowerCase(); // "category_id" -> "id"
    }

}
