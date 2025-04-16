package com.project01.orderservice.repository.httpClient;

import com.project01.orderservice.config.AuthenticationRequestIntercepter;
import com.project01.orderservice.config.WebSecurityConfig;
import com.project01.orderservice.dto.payment.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",configuration = {AuthenticationRequestIntercepter.class, WebSecurityConfig.class})
public interface PaymentClient {

    @PostMapping(value = "/payment/create",produces = MediaType.APPLICATION_JSON_VALUE)
    Long createPayment(@RequestBody PaymentDTO payment);
}
