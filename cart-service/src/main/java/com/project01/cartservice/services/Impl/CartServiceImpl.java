package com.project01.cartservice.services.Impl;

import com.project01.cartservice.convertor.CartConvertor;
import com.project01.cartservice.dto.request.CartRequest;
import com.project01.cartservice.dto.response.CartResponse;
import com.project01.cartservice.entity.Cart;
import com.project01.cartservice.entity.CartItem;
import com.project01.cartservice.exception.ErrorCode;
import com.project01.cartservice.repository.CartItemRepository;
import com.project01.cartservice.repository.CartRepository;
import com.project01.cartservice.services.CartService;
import com.project01.cartservice.services.RedisService;
import com.project01.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RedisService redisService;
    private final CartConvertor cartConvertor;

    @Override
    @Transactional
    public void addItemCart(CartRequest cartRequest) {
        if (cartRequest.quantity() == null || cartRequest.quantity() <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        Cart cart = cartRepository.findByUserId(cartRequest.userId())
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(cartRequest.userId()).build()));

        CartItem item = cartItemRepository.findByCartIdAndSkuId(cart.getId(), cartRequest.skuId())
                .orElseGet(() -> CartItem.builder().cartId(cart.getId()).skuId(cartRequest.skuId()).build());
        item.setQuantity(cartRequest.quantity());
        cartItemRepository.save(item);

        redisService.deleteData(cacheKey(cartRequest.userId()));
    }

    @Override
    public List<CartResponse> getItemFromCart(String userId) {
        String key = cacheKey(userId);
        if (redisService.existsKey(key)) {
            return redisService.getData(key);
        }
        List<CartItem> items = cartRepository.findByUserId(userId)
                .map(cart -> cartItemRepository.findAllByCartIdOrderByAddedAtDesc(cart.getId()))
                .orElse(List.of());
        if (items.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }
        List<CartResponse> cartResponse = cartConvertor.toCartResponses(userId, items);
        redisService.saveData(key, cartResponse);
        return cartResponse;
    }

    @Override
    @Transactional
    public void deleteItemFromCart(List<Long> skuIds, String userId) {
        cartRepository.findByUserId(userId)
                .ifPresent(cart -> cartItemRepository.deleteByCartIdAndSkuIdIn(cart.getId(), skuIds));
        redisService.deleteData(cacheKey(userId));
    }

    private String cacheKey(String userId) {
        return "user:" + userId;
    }
}
