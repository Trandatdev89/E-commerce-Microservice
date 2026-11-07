package com.project01.orderservice.service;

import com.project01.orderservice.dto.request.OrderRequest;
import com.project01.orderservice.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrderByCustomerId(String id);
    OrderResponse getOrderByReferenceOrder(String referenceOrder);
    void cancelOrders(List<String> orderCodes, String reasonCode);
}
