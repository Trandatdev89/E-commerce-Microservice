package com.project01.orderservice.repository.httpClient;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.common.security.jwt.ResourceServerAutoConfiguration;
import com.project01.orderservice.dto.profile.ProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service",configuration = {ResourceServerAutoConfiguration.class, ResourceServerAutoConfiguration.class})
public interface ProfileClient {

    @GetMapping(value = "/profile/external/private/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseData<ProfileDTO> getProfileByUserId(@PathVariable String id);
}
