package com.project01.paymentservice.repository;

import com.project01.paymentservice.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findAllByPaymentIdOrderByCreatedAt(Long paymentId);
}
