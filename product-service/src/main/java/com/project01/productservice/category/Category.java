package com.project01.productservice.category;

import com.project01.productservice.entity.AbstractEntity;
import com.project01.productservice.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "category")
public class Category extends AbstractEntity<Long> {

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String description;

    @OneToMany(fetch = FetchType.LAZY,orphanRemoval = true,mappedBy = "category")
    private List<Product> products;
}
