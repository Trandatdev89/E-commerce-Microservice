package com.project01.cartservice.convertor;

import com.project01.cartservice.dto.request.CartRequest;
import com.project01.cartservice.dto.response.ApiResponse;
import com.project01.cartservice.dto.response.CartResponse;
import com.project01.cartservice.dto.response.ProductResponse;
import com.project01.cartservice.entity.Cart;
import com.project01.cartservice.repository.CartRepository;
import com.project01.cartservice.repository.httpClient.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartConvertor {

    private final ProductClient productClient;

    public Cart toCart(CartRequest cartRequest){
        return Cart.builder()
                .productId(cartRequest.productId())
                .quantityOrder(cartRequest.quantityOrder())
                .totalAmount(cartRequest.totalAmount())
                .userId(cartRequest.userId())
                .build();
    }

    public CartResponse toCartResponse(Cart cart){

        ApiResponse<ProductResponse> productResponse = productClient.getProductById(cart.getProductId());
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .productId(productResponse.getData().getId())
                .price(cart.getTotalAmount())
                .title(productResponse.getData().getName())
                .imageUrl(productResponse.getData().getImage())
                .quantityOrder(cart.getQuantityOrder())
                .build();
    }
}
