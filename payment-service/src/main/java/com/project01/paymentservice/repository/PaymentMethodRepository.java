package com.project01.paymentservice.repository;

import com.project01.paymentservice.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, String> {
}
