package com.project01.orderservice.order_detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
