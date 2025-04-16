package com.project01.productservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "band")
public class Brand extends AbstractEntity<Long>{
    @Column(name = "name",unique = true)
    private String name;

    @OneToMany(fetch = FetchType.LAZY,orphanRemoval = true,mappedBy = "brand")
    private List<Product> products;
}
