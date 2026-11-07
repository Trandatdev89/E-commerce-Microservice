package com.project01.productservice.controller.publicAPI;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.productservice.category.CategoryDTO;
import com.project01.productservice.category.CategoryServiceImpl;
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

    @GetMapping(value = "/all")
    public ResponseData<List<CategoryDTO>> getAllCategories() {
        return new ResponseData<List<CategoryDTO>>().success(categoryService.getAllCategories());
    }
}
