package com.project01.profileservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.profileservice.dto.response.RegisterDTO;
import com.project01.profileservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/internal")
public class InternalProfileController {

    private final ProfileService profileService;

    @PostMapping(value = "/create-profile")
    @Operation(summary = "Tạo thông tin cá nhân của người sau khi register thành công",
            description = "Tạo thông tin cá nhân của người sau khi register thành công")
    public ResponseData<Void> createProfile(@RequestBody RegisterDTO registerDTO) {
        profileService.createProfile(registerDTO);
        return new ResponseData<Void>().success();
    }

}
