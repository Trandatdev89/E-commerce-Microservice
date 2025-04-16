package com.project01.notificationservice.kafka.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse {
    private Long productId;
    private String name;
    private String description;
    private Long price;
    private Long quantity;
}
