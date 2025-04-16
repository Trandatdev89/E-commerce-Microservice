package com.project01.orderservice.repository.httpClient;

import com.project01.orderservice.config.AuthenticationRequestIntercepter;
import com.project01.orderservice.config.WebSecurityConfig;
import com.project01.orderservice.dto.account.AccountDTO;
import com.project01.orderservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service",configuration = {AuthenticationRequestIntercepter.class, WebSecurityConfig.class})
public interface AccountClient {
    @GetMapping(value = "/identity/account/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<AccountDTO> getAccount(@PathVariable String id);
}
