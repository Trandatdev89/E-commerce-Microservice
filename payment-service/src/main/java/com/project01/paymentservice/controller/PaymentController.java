package com.project01.paymentservice.controller;

import com.project01.common.core.dto.response.ResponseData;
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
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    @Operation(summary = "API tạo mới 1 thanh toán",
            description = "Tạo mới 1 thanh toán sau khi đặt hàng")
    public Long createPayment(@RequestBody PaymentDTO payment) {
        return paymentService.createPayment(payment);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    @Operation(summary = "API lấy đơn thanh toán thông qua mã đơn hàng",
            description = "Trả về lần thanh toán gần nhất của đơn theo order_code, vd ORD-20261101-8F3K2A")
    public ResponseData<PaymentDTOResponse> getPaymentByOrderCode(@RequestParam String orderCode) {
        return new ResponseData<PaymentDTOResponse>().success(paymentService.getPaymentByOrderCode(orderCode));
    }


}
