package com.project01.orderservice.repository.httpClient;

import com.project01.orderservice.config.AuthenticationRequestIntercepter;
import com.project01.orderservice.config.WebSecurityConfig;
import com.project01.orderservice.dto.ApiResponse;
import com.project01.orderservice.dto.profile.ProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service",configuration = {AuthenticationRequestIntercepter.class, WebSecurityConfig.class})
public interface ProfileClient {

    @GetMapping(value = "/profile/external/private/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<ProfileDTO> getProfileByUserId(@PathVariable String id);
}
