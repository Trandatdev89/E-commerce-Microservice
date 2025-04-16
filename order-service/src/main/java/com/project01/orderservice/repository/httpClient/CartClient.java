package com.project01.orderservice.repository.httpClient;


import com.project01.orderservice.config.AuthenticationRequestIntercepter;
import com.project01.orderservice.config.WebSecurityConfig;
import com.project01.orderservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "cart-service",configuration = {AuthenticationRequestIntercepter.class, WebSecurityConfig.class})

public interface CartClient {
    @DeleteMapping(value = "/cart/{idProduct}",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse deleteItemFromCart(@PathVariable List<Long> idProduct, @RequestParam String userId);
}
