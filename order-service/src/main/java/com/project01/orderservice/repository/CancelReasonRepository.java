package com.project01.orderservice.repository;

import com.project01.orderservice.entity.CancelReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelReasonRepository extends JpaRepository<CancelReason, String> {
}
