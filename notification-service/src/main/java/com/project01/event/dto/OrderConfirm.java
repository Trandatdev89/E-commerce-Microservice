package com.project01.event.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project01.notificationservice.kafka.order.ProfileDTO;
import com.project01.notificationservice.kafka.order.PurchaseResponse;
import com.project01.notificationservice.kafka.payment.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderConfirm  {
    private String orderReference;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private ProfileDTO customer;
    private List<PurchaseResponse> products;
}
