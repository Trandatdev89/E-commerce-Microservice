package com.project01.orderservice.controller;

import com.project01.orderservice.dto.ApiResponse;
import com.project01.orderservice.dto.request.OrderRequest;
import com.project01.orderservice.dto.response.OrderResponse;
import com.project01.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "API tạo đơn hàng",
            description = "API tạo đơn hàng sau khi thanh toán thành công, nó sẽ gọi vào api của payment-service dể tạo mới 1 record thanh toán và notification-service để gửi thông cho khách qua mail")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
         return ApiResponse.<OrderResponse>builder()
                 .code(200)
                 .message("Order created")
                 .data(orderService.createOrder(orderRequest))
                 .build();
    }

    @GetMapping(value = "/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API lấy ra tất cả đơn đặt hàng  ",
            description = "Lấy ra tất cả đơn hàng")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .message("List order success")
                .data(orderService.getAllOrders())
                .build();
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "API lấy ra tất cả đơn đặt hàng của từng khách hàng",
            description = "Lấy ra tất cả đơn hàng  của từng khách hàng thông qua ID")
    public ApiResponse<List<OrderResponse>> getOrderByCustomerId(@PathVariable String id) {
        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .message("List order success")
                .data(orderService.getOrderByCustomerId(id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "API lấy ra đơn hàng thông qua mã đơn hàng",
            description = "API lấy ra đơn hàng thông qua mã đơn hàng")
    public ApiResponse<OrderResponse> getOrderByReferenceOrder(@RequestParam String referenceOrder) {
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("List order success")
                .data(orderService.getOrderByReferenceOrder(referenceOrder))
                .build();
    }

    @DeleteMapping(value = "/{reference}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Transactional
    @Operation(summary = "API xóa đơn hàng thông qua mã đơn hàng",
            description = "API xóa những đơn hàng thông qua mã đơn hàng list mã đơn hàng")
    public ApiResponse deleteOrderByReference(@PathVariable List<String> reference) {
        orderService.deleteOrderByReference(reference);
        return ApiResponse.builder()
                .code(200)
                .message("Delete order success")
                .build();
    }
}
