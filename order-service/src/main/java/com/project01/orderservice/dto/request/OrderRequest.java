package com.project01.orderservice.dto.request;

import com.project01.orderservice.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Long id;
    @NotBlank(message = "Customer should be present")
    private String customerId;
    @NotEmpty(message = "You should at least purchase one product")
    private List<PurchaseRequest> purchaseRequests;
    private String reference;
    @NotEmpty(message = "Payment method should be precised")
    private PaymentMethod paymentMethod;
    @Positive(message = "Order amount should be positive")
    private BigDecimal totalAmount;
}
