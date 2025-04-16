package com.project01.cartservice.services;

import com.project01.cartservice.dto.request.CartRequest;
import com.project01.cartservice.dto.response.CartResponse;
import com.project01.cartservice.entity.Cart;

import java.util.List;

public interface CartService {
    void addItemCart(CartRequest cart);
    List<CartResponse>  getItemFromCart(String userId);
    void deleteItemFromCart(List<Long> id,String userId);
}
