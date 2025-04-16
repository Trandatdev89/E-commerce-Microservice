package com.project01.cartservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cart")
public class Cart extends AbstractEntity<Long>{
    private String userId;
    private Long productId;
    private double totalAmount;
    private int quantityOrder;
}
