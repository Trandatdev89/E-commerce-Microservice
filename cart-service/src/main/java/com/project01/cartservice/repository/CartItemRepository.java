package com.project01.cartservice.repository;

import com.project01.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndSkuId(Long cartId, Long skuId);
    List<CartItem> findAllByCartIdOrderByAddedAtDesc(Long cartId);
    void deleteByCartIdAndSkuIdIn(Long cartId, Collection<Long> skuIds);
}
