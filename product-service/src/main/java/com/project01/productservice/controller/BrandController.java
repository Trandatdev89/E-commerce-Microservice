package com.project01.productservice.controller;


import com.project01.productservice.brand.BrandDTO;
import com.project01.productservice.brand.BrandServiceImpl;
import com.project01.productservice.category.CategoryDTO;
import com.project01.productservice.category.CategoryServiceImpl;
import com.project01.productservice.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/brand/private")
@RequiredArgsConstructor
public class BrandController {

    private final BrandServiceImpl brandService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API tạo mới 1 hãng",
            description = "API tạo mới 1 hãng")
    public ApiResponse createBrand(@RequestBody BrandDTO brandDTO) {
        brandService.createBrand(brandDTO);
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .build();
    }
}
