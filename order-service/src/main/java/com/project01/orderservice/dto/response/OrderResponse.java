package com.project01.orderservice.dto.response;

import com.project01.orderservice.enums.OrderStatus;
import com.project01.orderservice.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderCode;
    private OrderStatus status;
    private String customerId;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String wardName;
    private String addressDetail;
    private String note;
    private String cancelReasonCode;
    private LocalDateTime placedAt;
    private LocalDateTime updatedAt;
    /** Chỉ có khi xem chi tiết 1 đơn, danh sách đơn để null */
    private List<OrderItemResponse> items;
}
