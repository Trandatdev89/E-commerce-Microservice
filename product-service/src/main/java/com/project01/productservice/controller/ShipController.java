package com.project01.productservice.controller;

import com.project01.productservice.category.CategoryDTO;
import com.project01.productservice.dto.ApiResponse;
import com.project01.productservice.ship.ShipDTO;
import com.project01.productservice.ship.ShipServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ship/private")
@RequiredArgsConstructor
public class ShipController {

    private final ShipServiceImpl shipService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API tạo mới shipper",
            description = "API tạo mới shipper")
    public ApiResponse createShip(@RequestBody ShipDTO shipDTO) {
        shipService.createShip(shipDTO);
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .build();
    }
}
