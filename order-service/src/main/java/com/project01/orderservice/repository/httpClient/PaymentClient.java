package com.project01.orderservice.repository.httpClient;

import com.project01.common.feign.AuthenticationRequestInterceptor;
import com.project01.common.security.jwt.ResourceServerAutoConfiguration;
import com.project01.orderservice.dto.payment.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",configuration = {AuthenticationRequestInterceptor.class, ResourceServerAutoConfiguration.class})
public interface PaymentClient {

    @PostMapping(value = "/payment/create",produces = MediaType.APPLICATION_JSON_VALUE)
    Long createPayment(@RequestBody PaymentDTO payment);
}
