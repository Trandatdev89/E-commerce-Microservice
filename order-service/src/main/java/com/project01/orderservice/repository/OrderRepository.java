package com.project01.orderservice.repository;

import com.project01.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByPlacedAtDesc();
    List<Order> findAllByCustomerIdOrderByPlacedAtDesc(String customerId);
    Optional<Order> findByOrderCode(String orderCode);
    List<Order> findAllByOrderCodeIn(Collection<String> orderCodes);
    boolean existsByOrderCode(String orderCode);
}
