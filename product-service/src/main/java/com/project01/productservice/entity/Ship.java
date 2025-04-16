package com.project01.productservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ship")
public class Ship extends AbstractEntity<Long> {

    @Column(length = 50)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "ships")
    List<Product> products = new ArrayList<>();
}
