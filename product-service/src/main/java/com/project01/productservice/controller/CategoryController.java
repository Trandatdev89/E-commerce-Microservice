package com.project01.productservice.controller;

import com.project01.productservice.category.CategoryDTO;
import com.project01.productservice.category.CategoryServiceImpl;
import com.project01.productservice.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/category/private")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API tạo mới 1 danh mục",
            description = "API tạo mới 1 danh mục")
    public ApiResponse createCategory(@RequestBody CategoryDTO category) {
        categoryService.createCategory(category);
        return ApiResponse.builder()
              .code(200)
              .message("Success")
              .build();
    }
}
