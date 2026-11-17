package com.project01.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/** Event payment-service gửi lên topic "payment-topic" khi tạo thanh toán. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentConfirm {
    String customerId;
    String orderReference;
    BigDecimal amount;
    PaymentMethod paymentMethod;
    String customerFirstname;
    String customerLastname;
    String customerEmail;
}
