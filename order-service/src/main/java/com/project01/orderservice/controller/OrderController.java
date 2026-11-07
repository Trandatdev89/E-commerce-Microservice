package com.project01.orderservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.orderservice.dto.request.OrderRequest;
import com.project01.orderservice.dto.response.OrderResponse;
import com.project01.orderservice.service.OrderService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "API tạo đơn hàng",
            description = "API tạo đơn hàng sau khi thanh toán thành công, nó sẽ gọi vào api của payment-service dể tạo mới 1 record thanh toán và notification-service để gửi thông cho khách qua mail")
    public ResponseData<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return new ResponseData<OrderResponse>().success(orderService.createOrder(orderRequest));
    }

    @GetMapping(value = "/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API lấy ra tất cả đơn đặt hàng  ",
            description = "Lấy ra tất cả đơn hàng")
    public ResponseData<List<OrderResponse>> getAllOrders() {
        return new ResponseData<List<OrderResponse>>().success(orderService.getAllOrders());
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "API lấy ra tất cả đơn đặt hàng của từng khách hàng",
            description = "Lấy ra tất cả đơn hàng  của từng khách hàng thông qua ID")
    public ResponseData<List<OrderResponse>> getOrderByCustomerId(@PathVariable String id) {
        return new ResponseData<List<OrderResponse>>().success(orderService.getOrderByCustomerId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "API lấy ra đơn hàng thông qua mã đơn hàng",
            description = "API lấy ra đơn hàng thông qua mã đơn hàng")
    public ResponseData<OrderResponse> getOrderByReferenceOrder(@RequestParam String referenceOrder) {
        return new ResponseData<OrderResponse>().success(orderService.getOrderByReferenceOrder(referenceOrder));
    }

    @DeleteMapping(value = "/{reference}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "API hủy đơn hàng thông qua mã đơn hàng",
            description = "Chuyển các đơn sang CANCELLED (không xóa dữ liệu). reasonCode lấy từ ord_cancel_reason, mặc định CUSTOMER_CHANGED_MIND")
    public ResponseData<Void> cancelOrderByReference(@PathVariable List<String> reference,
                                              @RequestParam(required = false) String reasonCode) {
        orderService.cancelOrders(reference, reasonCode);
        return new ResponseData<Void>().success();
    }
}
