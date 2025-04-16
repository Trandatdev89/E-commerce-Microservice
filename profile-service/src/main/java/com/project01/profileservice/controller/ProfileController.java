package com.project01.profileservice.controller;

import com.project01.profileservice.dto.response.ApiResponse;
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
    public ApiResponse<List<ProfileDTO>> getAllProfiles() {
        return ApiResponse.<List<ProfileDTO>>builder()
                .code(200)
                .message("OK")
                .data(profileService.getAllProfiles())
                .build();
    }

    @GetMapping(value = "/private/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Lây thông tin cá nhân của người dùng theo ID",
            description = "Lây thông tin cá nhân của người dùng theo ID")
    public ApiResponse<ProfileDTO> getProfileByUserId(@PathVariable String id) {
        return ApiResponse.<ProfileDTO>builder()
                .code(200)
                .message("OK")
                .data(profileService.getProfileByUserId(id))
                .build();
    }

    @PatchMapping(value = "/private/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "API dùng để cập nhập người dùng theo ID",
            description = "API dùng để tìm kiếm người dùng theo ID")
    public ApiResponse<ProfileDTO> updateProfile(@PathVariable String userId,@RequestBody ProfileDTO profileDTO) {
        return ApiResponse.<ProfileDTO>builder()
                .code(200)
                .message("OK")
                .data(profileService.updateProfile(userId,profileDTO))
                .build();
    }

}
