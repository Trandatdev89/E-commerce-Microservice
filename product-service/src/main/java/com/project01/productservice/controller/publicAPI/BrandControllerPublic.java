package com.project01.productservice.controller.publicAPI;


import com.project01.common.core.dto.response.ResponseData;
import com.project01.productservice.brand.BrandDTO;
import com.project01.productservice.brand.BrandServiceImpl;
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

    @GetMapping(value = "/all")
    public ResponseData<List<BrandDTO>> getAllBrands() {
        return new ResponseData<List<BrandDTO>>().success(BrandServiceImpl.getAllBrands());
    }
}
