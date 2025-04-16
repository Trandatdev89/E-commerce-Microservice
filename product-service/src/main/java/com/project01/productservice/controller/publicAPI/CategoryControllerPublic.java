package com.project01.productservice.controller.publicAPI;

import com.project01.productservice.category.CategoryDTO;
import com.project01.productservice.category.CategoryServiceImpl;
import com.project01.productservice.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/category/public")
@RequiredArgsConstructor
public class CategoryControllerPublic {

    private final CategoryServiceImpl categoryService;

    @GetMapping(value= "/all")
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        return ApiResponse.<List<CategoryDTO>>builder()
                .code(200)
                .data(categoryService.getAllCategories())
                .message("Success")
                .build();
    }
}
