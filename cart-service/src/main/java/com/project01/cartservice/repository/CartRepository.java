package com.project01.cartservice.repository;

import com.project01.cartservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndProductId(String userId, Long productId);
    List<Cart> findAllByUserId(String userId);
    void deleteByProductIdInAndUserId(List<Long> ids,String userId);
}
