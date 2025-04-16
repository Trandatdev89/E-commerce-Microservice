package com.project01.paymentservice.controller;

import com.project01.paymentservice.dto.ApiResponse;
import com.project01.paymentservice.dto.PaymentDTO;
import com.project01.paymentservice.dto.PaymentDTOResponse;
import com.project01.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "API tạo mới 1 thanh toán",
            description = "Tạo mới 1 thanh toán sau khi đặt hàng")
    public Long createPayment(@RequestBody PaymentDTO payment) {
        return paymentService.createPayment(payment);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "API lấy đơn thanh toán thông qua mã đơn hàng",
            description = "API lấy đơn thanh toán thông qua mã đơn hàng (Lưu ý orderId là mã đơn ,còn referenceOrder là mã tham chiếu đơn)")
    public ApiResponse<PaymentDTOResponse> getPaymentByOrderId(@RequestParam Long orderId) {
        return ApiResponse.<PaymentDTOResponse>builder()
                .code(200)
                .message("Success")
                .data(paymentService.getPaymentByOrderId(orderId))
                .build();
    }


}
