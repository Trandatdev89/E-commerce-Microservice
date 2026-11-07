package com.project01.orderservice.dto.payment;

import com.project01.orderservice.enums.PaymentMethod;
import com.project01.orderservice.dto.profile.ProfileDTO;
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
    private ProfileDTO customer;
}
