package com.project01.orderservice.repository;

import com.project01.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomerId(String customerId);
    Optional<Order> findByReference(String reference);
    void deleteByReferenceIn(List<String> reference);
}
