package com.project01.orderservice.dto.request;

import com.project01.orderservice.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Không có field tổng tiền: server tự tính từ giá hiện tại của product-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    @NotBlank(message = "Customer should be present")
    private String customerId;
    @Valid
    @NotEmpty(message = "You should at least purchase one product")
    private List<PurchaseRequest> purchaseRequests;
    @NotNull(message = "Payment method should be precised")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Receiver name is required")
    private String receiverName;
    @NotBlank(message = "Receiver phone is required")
    private String receiverPhone;
    @NotBlank(message = "Province is required")
    private String provinceCode;
    @NotBlank(message = "Province is required")
    private String provinceName;
    @NotBlank(message = "Ward is required")
    private String wardCode;
    @NotBlank(message = "Ward is required")
    private String wardName;
    @NotBlank(message = "Address detail is required")
    private String addressDetail;
    private String note;
}
