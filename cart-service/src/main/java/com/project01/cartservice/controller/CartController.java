package com.project01.cartservice.controller;

import com.project01.cartservice.dto.request.CartRequest;
import com.project01.cartservice.dto.response.ApiResponse;
import com.project01.cartservice.dto.response.CartResponse;
import com.project01.cartservice.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/add")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Thêm sản phẩm vào giỏ hàng",
            description = "API này là sẽ lý 2 chức năng thêm hoặc câp nhật giỏ hàng trong DB đồng thời xóa cache để get ra list item trong db cart")
    public ApiResponse addItemCart(@RequestBody CartRequest cartRequest) {
        cartService.addItemCart(cartRequest);
       return ApiResponse.builder()
               .code(200)
               .message("Success")
               .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Lấy toàn bo sản phẩm trong giỏ",
            description = "API này sẽ lấy toàn bộ sản phẩm trong giỏ cache (Redis) ")
    public ApiResponse<List<CartResponse>> getItemFromCart(@RequestParam String userId) {
        return ApiResponse.<List<CartResponse>>builder()
                .code(200)
                .data(cartService.getItemFromCart(userId))
                .message("Success")
                .build();
    }

    @DeleteMapping(value = "/{idProduct}")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    @Operation(summary = "Xóa sản phẩm trong giỏ",
            description = "API này sẽ xóa sản phẩm trong giỏ theo list ID")
    public ApiResponse deleteItemFromCart(@PathVariable List<Long> idProduct,@RequestParam String userId) {
        cartService.deleteItemFromCart(idProduct,userId);
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .build();
    }
}
