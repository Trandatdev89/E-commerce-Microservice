package com.project01.productservice.controller.publicAPI;


import com.project01.common.core.dto.response.ResponseData;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.SkuResponse;
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
    public ResponseData<List<ProductResponse>> getAllProducts() {
        return new ResponseData<List<ProductResponse>>().success(productService.getAllProducts());
    }

    @GetMapping(value = "/search")
    @Operation(summary = "API dùng để tìm kiếm và lọc sản phẩm theo nhiều tiêu chí (Criteria) vi du search:[title~Ao khoac,price:200000]",
            description = "Ví dụ param gửi xuống sẽ có dạng search:[title~Ao khoac,price:200000]  và tìm kiếm theo and .Chi tiet xem source code")
    public ResponseData<List<ProductResponse>> searchSpecificationProduct(@RequestParam(required = false) String[] search) {
        List<ProductResponse> productResponse = productService.searchSpecificationProduct(search);
        return new ResponseData<List<ProductResponse>>().success(productResponse);
    }

    @GetMapping(value = "/skus")
    @Operation(summary = "API lấy chi tiết nhiều SKU",
            description = "Dùng cho giỏ hàng: trả tên sản phẩm, ảnh, giá hiện tại theo danh sách sku id, vd ?ids=1,2,3")
    public ResponseData<List<SkuResponse>> getSkusByIds(@RequestParam List<Long> ids) {
        return new ResponseData<List<SkuResponse>>().success(productService.getSkusByIds(ids));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "API dùng để tìm kiếm sản phẩm theo ID",
            description = "API dùng để tìm kiếm sản phẩm theo ID")
    public ResponseData<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        return new ResponseData<ProductResponse>().success(productResponse);
    }

    @GetMapping(value = "/list/category")
    @Operation(summary = "API dùng để tìm kiếm sản phẩm theo danh mục",
            description = "API dùng để tìm kiếm sản phẩm theo danh mục")
    public ResponseData<List<ProductResponse>> getProductByCategory(@RequestParam Long categoryId) {
        return new ResponseData<List<ProductResponse>>().success(productService.getProductByCategory(categoryId));
    }
}
