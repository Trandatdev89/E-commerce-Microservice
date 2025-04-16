package com.project01.productservice.controller.publicAPI;


import com.project01.productservice.dto.ApiResponse;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/public")
public class ProductControllerPublic {


    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .code(200)
                .message("OK")
                .data(productService.getAllProducts())
                .build();
    }

    @GetMapping(value = "/search")
    @Operation(summary = "API dùng để tìm kiếm và lọc sản phẩm theo nhiều tiêu chí (Criteria) vi du search:[title~Ao khoac,price:200000]",
            description = "Ví dụ param gửi xuống sẽ có dạng search:[title~Ao khoac,price:200000]  và tìm kiếm theo and .Chi tiet xem source code")
    public ApiResponse<List<ProductResponse>> searchSpecificationProduct(@RequestParam(required = false) String[] search){
        List<ProductResponse> productResponse = productService.searchSpecificationProduct(search);
        return ApiResponse.<List<ProductResponse>>builder()
                .code(200)
                .message("Search product created")
                .data(productResponse)
                .build();
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "API dùng để tìm kiếm sản phẩm theo ID",
            description = "API dùng để tìm kiếm sản phẩm theo ID")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id){
        ProductResponse productResponse = productService.getProductById(id);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Product created")
                .data(productResponse)
                .build();
    }

    @GetMapping(value = "/list/category")
    @Operation(summary = "API dùng để tìm kiếm sản phẩm theo danh mục",
            description = "API dùng để tìm kiếm sản phẩm theo danh mục")
    public ApiResponse<List<ProductResponse>> getProductByCategory(@RequestParam Long categoryId) {
        return ApiResponse.<List<ProductResponse>>builder()
                .code(200)
                .message("OK")
                .data(productService.getProductByCategory(categoryId))
                .build();
    }
}
