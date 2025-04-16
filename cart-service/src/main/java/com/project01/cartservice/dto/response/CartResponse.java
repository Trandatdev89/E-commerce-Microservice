package com.project01.cartservice.dto.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private String userId;
    private String title;
    private double price;
    private String imageUrl;
    private Long productId;
    private int quantityOrder;
}
