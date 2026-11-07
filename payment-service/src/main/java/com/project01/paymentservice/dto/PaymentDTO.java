package com.project01.paymentservice.dto;

import com.project01.paymentservice.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDTO {
    private Long id;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    /** order_code */
    private String orderReference;
    private Long orderId;
    private String customerId;
    private Customer customer;
}
