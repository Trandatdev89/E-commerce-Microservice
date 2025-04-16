package com.project01.orderservice.convertor;

import com.project01.orderservice.dto.account.AccountDTO;
import com.project01.orderservice.dto.request.OrderRequest;
import com.project01.orderservice.dto.response.OrderResponse;
import com.project01.orderservice.entity.Order;
import com.project01.orderservice.utils.ReferenceOrder;
import org.springframework.stereotype.Component;

@Component
public class OrderConvertor {

    public Order toOrder(AccountDTO accountDTO, OrderRequest orderRequest){
        return Order.builder()
                .totalAmount(orderRequest.getTotalAmount())
                .customerId(accountDTO.getId())
                .paymentMethod(orderRequest.getPaymentMethod())
                .reference(ReferenceOrder.getReferenceOrder(accountDTO))
                .build();
    }

    public OrderResponse toOrderResponse(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .paymentMethod(order.getPaymentMethod())
                .reference(order.getReference())
                .customerId(order.getCustomerId())
                .amount(order.getTotalAmount())
                .lastModifiedDate(order.getLastModifiedDate())
                .createdDate(order.getCreatedDate())
                .build();
    }
}
