package com.project01.productservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.productservice.category.CategoryDTO;
import com.project01.productservice.category.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/category/private")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API tạo mới 1 danh mục",
            description = "API tạo mới 1 danh mục")
    public ResponseData<Void> createCategory(@RequestBody CategoryDTO category) {
        categoryService.createCategory(category);
        return new ResponseData<Void>().success();
    }
}
