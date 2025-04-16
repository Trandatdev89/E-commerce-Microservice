package com.project01.cartservice.services.Impl;

import com.project01.cartservice.convertor.CartConvertor;
import com.project01.cartservice.dto.request.CartRequest;
import com.project01.cartservice.dto.response.CartResponse;
import com.project01.cartservice.entity.Cart;
import com.project01.cartservice.exception.custom.AppException;
import com.project01.cartservice.exception.custom.ErrorCode;
import com.project01.cartservice.repository.CartRepository;
import com.project01.cartservice.services.CartService;
import com.project01.cartservice.services.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final RedisService redisService;
    private final CartConvertor cartConvertor;

    @Override
    public void addItemCart(CartRequest cartRequest) {

        Optional<Cart> cart = cartRepository.findByUserIdAndProductId
                (cartRequest.userId(),cartRequest.productId());

        if(cart.isPresent()){
            Cart updateCart = cart.get();
            updateCart.setQuantityOrder(cartRequest.quantityOrder());
            updateCart.setTotalAmount(cartRequest.totalAmount());
            cartRepository.save(updateCart);
        }else{
            cartRepository.save(cartConvertor.toCart(cartRequest));
        }

        redisService.deleteData("user:"+cartRequest.userId());
    }

    @Override
    public List<CartResponse> getItemFromCart(String userId) {
        String key = "user:"+userId;
        if(redisService.existsKey(key)){
            return (List<CartResponse>) redisService.getData(key);
        }
        List<Cart> cart = cartRepository.findAllByUserId(userId);
        if(cart.isEmpty()){
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        List<CartResponse> cartResponse = new ArrayList<>();
        for (Cart item : cart) {
            cartResponse.add(cartConvertor.toCartResponse(item));
        }
        redisService.saveData(key, cartResponse);
        return cartResponse;
    }

    @Override
    public void deleteItemFromCart(List<Long> idProduct,String userId) {
        cartRepository.deleteByProductIdInAndUserId(idProduct,userId);
        redisService.deleteData("user:"+userId);
    }
}
