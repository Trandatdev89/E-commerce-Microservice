package com.project01.event.dto;

import com.project01.orderservice.dto.response.PurchaseResponse;
import com.project01.orderservice.enums.PaymentMethod;
import com.project01.orderservice.dto.profile.ProfileDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderConfirm {
    private String orderReference;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private ProfileDTO customer;
    private List<PurchaseResponse> products;
}
