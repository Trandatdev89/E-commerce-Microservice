package com.project01.orderservice.repository.httpClient;


import com.project01.common.core.dto.response.ResponseData;
import com.project01.common.security.jwt.ResourceServerAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.project01.common.feign.AuthenticationRequestInterceptor;

import java.util.List;

@FeignClient(name = "cart-service"
        ,configuration = {AuthenticationRequestInterceptor.class, ResourceServerAutoConfiguration.class})

public interface CartClient {
    @DeleteMapping(value = "/cart/{skuIds}",produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseData<Void> deleteItemFromCart(@PathVariable("skuIds") List<Long> skuIds, @RequestParam String userId);
}
