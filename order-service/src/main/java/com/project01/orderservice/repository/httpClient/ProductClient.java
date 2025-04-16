package com.project01.orderservice.repository.httpClient;

import com.project01.orderservice.config.AuthenticationRequestIntercepter;
import com.project01.orderservice.config.WebSecurityConfig;
import com.project01.orderservice.dto.ApiResponse;
import com.project01.orderservice.dto.request.PurchaseRequest;
import com.project01.orderservice.dto.response.PurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service",configuration = {AuthenticationRequestIntercepter.class, WebSecurityConfig.class})
public interface ProductClient {

    @PostMapping(value = "/product/private/purchase",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<PurchaseResponse>> purchaseProduct(@Valid @RequestBody List<PurchaseRequest> purchaseRequest);
}