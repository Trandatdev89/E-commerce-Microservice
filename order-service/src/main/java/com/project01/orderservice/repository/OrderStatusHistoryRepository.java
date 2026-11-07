package com.project01.orderservice.repository;

import com.project01.orderservice.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findAllByOrderIdOrderByCreatedAt(Long orderId);
}
