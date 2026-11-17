package com.project01.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

/**
 * Event order-service gửi lên topic "order-topic" khi đặt hàng thành công.
 * Giữ nguyên package com.project01.event.dto vì JsonSerializer của bên gửi ghi tên class vào header.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderConfirm {
    String customerId;
    String orderReference;
    BigDecimal totalAmount;
    PaymentMethod paymentMethod;
    ProfileDTO customer;
    List<PurchaseResponse> products;
}
