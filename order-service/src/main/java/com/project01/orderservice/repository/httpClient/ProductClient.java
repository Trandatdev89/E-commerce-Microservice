package com.project01.orderservice.repository.httpClient;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.common.feign.AuthenticationRequestInterceptor;
import com.project01.common.security.jwt.ResourceServerAutoConfiguration;
import com.project01.orderservice.dto.request.PurchaseRequest;
import com.project01.orderservice.dto.response.PurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", configuration = {AuthenticationRequestInterceptor.class, ResourceServerAutoConfiguration.class})
public interface ProductClient {

    @PostMapping(value = "/product/private/purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseData<List<PurchaseResponse>> purchaseProduct(@Valid @RequestBody List<PurchaseRequest> purchaseRequest);
}