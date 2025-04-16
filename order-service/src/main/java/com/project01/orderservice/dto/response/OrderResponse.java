package com.project01.orderservice.dto.response;

import com.project01.orderservice.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    String reference;
    BigDecimal amount;
    PaymentMethod paymentMethod;
    String customerId;
    LocalDateTime createdDate;
    LocalDateTime lastModifiedDate;
}
