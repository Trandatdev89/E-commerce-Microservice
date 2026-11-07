package com.project01.paymentservice.dto;

import com.project01.paymentservice.entity.PaymentMethod;
import com.project01.paymentservice.entity.PaymentStatus;
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
    private String paymentCode;
    private String orderCode;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
