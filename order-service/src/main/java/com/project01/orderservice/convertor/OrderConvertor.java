package com.project01.orderservice.convertor;

import com.project01.orderservice.dto.request.OrderRequest;
import com.project01.orderservice.dto.response.OrderItemResponse;
import com.project01.orderservice.dto.response.OrderResponse;
import com.project01.orderservice.dto.response.PurchaseResponse;
import com.project01.orderservice.entity.Order;
import com.project01.orderservice.entity.OrderItem;
import com.project01.orderservice.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderConvertor {

    public Order toOrder(String orderCode, OrderRequest request, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal shippingFee = BigDecimal.ZERO;
        return Order.builder()
                .orderCode(orderCode)
                .customerId(request.getCustomerId())
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .discountAmount(discount)
                .shippingFee(shippingFee)
                .totalAmount(subtotal.subtract(discount).add(shippingFee))
                .paymentMethod(request.getPaymentMethod())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .provinceCode(request.getProvinceCode())
                .provinceName(request.getProvinceName())
                .wardCode(request.getWardCode())
                .wardName(request.getWardName())
                .addressDetail(request.getAddressDetail())
                .note(request.getNote())
                .placedAt(LocalDateTime.now())
                .build();
    }

    public OrderItem toOrderItem(Long orderId, PurchaseResponse purchase) {
        return OrderItem.builder()
                .orderId(orderId)
                .skuId(purchase.getSkuId())
                .skuCode(purchase.getSkuCode())
                .productName(purchase.getProductName())
                .skuName(purchase.getSkuName())
                .imageUrl(purchase.getImageUrl())
                .unitPrice(purchase.getUnitPrice())
                .quantity(purchase.getQuantity())
                .lineTotal(purchase.getLineTotal())
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {
        return toOrderResponse(order, null);
    }

    public OrderResponse toOrderResponse(Order order, List<OrderItem> items) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus())
                .customerId(order.getCustomerId())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(order.getShippingFee())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .provinceName(order.getProvinceName())
                .wardName(order.getWardName())
                .addressDetail(order.getAddressDetail())
                .note(order.getNote())
                .cancelReasonCode(order.getCancelReasonCode())
                .placedAt(order.getPlacedAt())
                .updatedAt(order.getUpdatedAt())
                .items(items == null ? null : items.stream().map(this::toOrderItemResponse).toList())
                .build();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .skuId(item.getSkuId())
                .skuCode(item.getSkuCode())
                .productName(item.getProductName())
                .skuName(item.getSkuName())
                .imageUrl(item.getImageUrl())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
