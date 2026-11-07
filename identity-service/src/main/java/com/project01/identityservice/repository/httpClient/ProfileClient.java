package com.project01.identityservice.repository.httpClient;
import com.project01.common.core.dto.response.ResponseData;
import com.project01.identityservice.dto.response.RegisterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "profile-service")
public interface ProfileClient {
    @PostMapping(value = "/profile/internal/create-profile",produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseData<Void> createProfile(@RequestBody RegisterDTO registerDTO);
}
