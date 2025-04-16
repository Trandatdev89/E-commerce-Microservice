package com.project01.productservice.controller.publicAPI;


import com.project01.productservice.brand.BrandDTO;
import com.project01.productservice.brand.BrandServiceImpl;
import com.project01.productservice.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/brand/public")
@RequiredArgsConstructor
public class BrandControllerPublic {

    private final BrandServiceImpl BrandServiceImpl;

    @GetMapping(value= "/all")
    public ApiResponse<List<BrandDTO>> getAllBrands() {
        return ApiResponse.<List<BrandDTO>>builder()
                .code(200)
                .data(BrandServiceImpl.getAllBrands())
                .message("Success")
                .build();
    }
}
