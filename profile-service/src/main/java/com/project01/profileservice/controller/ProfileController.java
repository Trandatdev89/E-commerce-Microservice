package com.project01.profileservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.profileservice.dto.response.ProfileDTO;
import com.project01.profileservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/external")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping(value = "/private/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Lây thông tin cá nhân của nhiều người dùng",
            description = "Lây thông tin cá nhân của nhiều người dùng")
    public ResponseData<List<ProfileDTO>> getAllProfiles() {
        return new ResponseData<List<ProfileDTO>>().success(profileService.getAllProfiles());
    }

    @GetMapping(value = "/private/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "Lây thông tin cá nhân của người dùng theo ID",
            description = "Lây thông tin cá nhân của người dùng theo ID")
    public ResponseData<ProfileDTO> getProfileByUserId(@PathVariable String id) {
        return new ResponseData<ProfileDTO>().success(profileService.getProfileByUserId(id));
    }

    @PatchMapping(value = "/private/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "API dùng để cập nhập người dùng theo ID",
            description = "API dùng để tìm kiếm người dùng theo ID")
    public ResponseData<ProfileDTO> updateProfile(@PathVariable String userId,@RequestBody ProfileDTO profileDTO) {
        return new ResponseData<ProfileDTO>().success(profileService.updateProfile(userId,profileDTO));
    }

}
