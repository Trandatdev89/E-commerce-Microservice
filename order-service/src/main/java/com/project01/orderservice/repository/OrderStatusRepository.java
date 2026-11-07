package com.project01.orderservice.repository;

import com.project01.orderservice.entity.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatusEntity, String> {
}
