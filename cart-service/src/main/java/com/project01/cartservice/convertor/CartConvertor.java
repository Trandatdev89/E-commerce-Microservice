package com.project01.cartservice.convertor;

import com.project01.cartservice.dto.response.CartResponse;
import com.project01.cartservice.dto.response.SkuResponse;
import com.project01.cartservice.entity.CartItem;
import com.project01.cartservice.repository.httpClient.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartConvertor {

    private final ProductClient productClient;

    /** Gọi product-service 1 lần cho cả giỏ để lấy tên, ảnh, giá hiện tại. */
    public List<CartResponse> toCartResponses(String userId, List<CartItem> items) {
        List<Long> skuIds = items.stream().map(CartItem::getSkuId).toList();
        Map<Long, SkuResponse> skuById = productClient.getSkusByIds(skuIds).getData().stream()
                .collect(Collectors.toMap(SkuResponse::getId, Function.identity()));

        return items.stream().map(item -> {
            SkuResponse sku = skuById.get(item.getSkuId());
            CartResponse.CartResponseBuilder builder = CartResponse.builder()
                    .id(item.getId())
                    .userId(userId)
                    .skuId(item.getSkuId())
                    .quantity(item.getQuantity())
                    .available(sku != null && "ACTIVE".equals(sku.getStatus()));
            if (sku != null) {
                builder.skuCode(sku.getSkuCode())
                        .productId(sku.getProductId())
                        .productName(sku.getProductName())
                        .skuName(sku.getName())
                        .imageUrl(sku.getImageUrl())
                        .price(sku.getPrice())
                        .lineTotal(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            return builder.build();
        }).toList();
    }
}
