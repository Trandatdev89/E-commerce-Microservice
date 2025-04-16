package com.project01.paymentservice.dto;

import com.project01.paymentservice.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTOResponse {
    private Long id;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private Long orderId;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
