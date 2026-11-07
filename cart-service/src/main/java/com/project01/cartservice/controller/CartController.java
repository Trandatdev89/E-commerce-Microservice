package com.project01.cartservice.controller;

import com.project01.cartservice.dto.request.CartRequest;
import com.project01.cartservice.dto.response.CartResponse;
import com.project01.cartservice.services.CartService;
import com.project01.common.core.dto.response.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/add")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Thêm sản phẩm vào giỏ hàng",
            description = "API này là sẽ lý 2 chức năng thêm hoặc câp nhật giỏ hàng trong DB đồng thời xóa cache để get ra list item trong db cart")
    public ResponseData<Void> addItemCart(@RequestBody CartRequest cartRequest) {
        cartService.addItemCart(cartRequest);
       return new ResponseData<Void>().success();
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Lấy toàn bo sản phẩm trong giỏ",
            description = "API này sẽ lấy toàn bộ sản phẩm trong giỏ cache (Redis) ")
    public ResponseData<List<CartResponse>> getItemFromCart(@RequestParam String userId) {
        return new ResponseData<List<CartResponse>>().success(cartService.getItemFromCart(userId));
    }

    @DeleteMapping(value = "/{skuIds}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Xóa sản phẩm trong giỏ",
            description = "API này sẽ xóa sản phẩm trong giỏ theo list sku id")
    public ResponseData<Void> deleteItemFromCart(@PathVariable List<Long> skuIds,@RequestParam String userId) {
        cartService.deleteItemFromCart(skuIds,userId);
        return new ResponseData<Void>().success();
    }
}
