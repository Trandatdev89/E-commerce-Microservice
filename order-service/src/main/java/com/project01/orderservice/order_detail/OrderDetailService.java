package com.project01.orderservice.order_detail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    public void saveOrderDetail(OrderDetailRequest request) {
        orderDetailRepository.save(OrderDetail.builder()
                        .order(request.getOrder())
                        .productId(request.getProductId())
                        .quantity(request.getQuantity())
                .build());
    }
}
