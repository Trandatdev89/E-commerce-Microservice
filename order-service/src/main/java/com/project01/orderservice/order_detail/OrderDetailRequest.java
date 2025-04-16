package com.project01.orderservice.order_detail;
import com.project01.orderservice.entity.Order;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailRequest {
    private Order order;
    private Long productId;
    private Long quantity;
}
