package com.project01.productservice.controller;

import com.project01.productservice.dto.ApiResponse;
import com.project01.productservice.dto.request.ProductRequest;
import com.project01.productservice.dto.request.PurchaseRequest;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.PurchaseResponse;
import com.project01.productservice.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/private")
public class ProductController {

    private final ProductService productService;

    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API tạo mới 1 sản phẩm",
            description = "API tạo mới 1 sản phẩm")
    public ApiResponse<ProductResponse> createProduct(
            @Valid @ModelAttribute ProductRequest productRequest,
            @RequestPart MultipartFile image) throws IOException {
        ProductResponse productResponse = productService.createProduct(productRequest,image);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Product created")
                .data(productResponse)
                .build();
    }

    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API cập nhập 1  sản phẩm",
            description = "API cập nhập 1 sản phẩm lưu ảnh trên cloudinary")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,@ModelAttribute ProductRequest productRequest,@RequestPart MultipartFile image) throws IOException {
        ProductResponse productResponse = productService.updateProduct(id,productRequest,image);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Product created")
                .data(productResponse)
                .build();
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    @Operation(summary = "API xóa nhiều sản phẩm",
            description = "API xóa nhiều sản phẩm qua ID")
    public ApiResponse deleteProduct(@PathVariable List<Long> id){
        productService.deleteProduct(id);
        return ApiResponse.builder()
                .code(200)
                .message("Product deleted")
                .build();
    }


    @PostMapping("/purchase")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "API mua sản phẩm",
            description = "API này sẽ được order-service gọi để cập nhập lạ số lượng trong table product và trả ra những sản phẩm được mua")
    public ApiResponse<List<PurchaseResponse>> purchaseProduct(@Valid @RequestBody List<PurchaseRequest> purchaseRequest){
        return ApiResponse.<List<PurchaseResponse>>builder()
                .code(200)
                .message("OK")
                .data(productService.purchaseProduct(purchaseRequest))
                .build();
    }

}
