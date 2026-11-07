package com.project01.productservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.productservice.dto.request.ProductRequest;
import com.project01.productservice.dto.request.PurchaseRequest;
import com.project01.productservice.dto.request.SkuRequest;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.PurchaseResponse;
import com.project01.productservice.dto.response.SkuResponse;
import com.project01.productservice.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(summary = "API tạo mới 1 sản phẩm",
            description = "Tạo sản phẩm gốc (mặc định DRAFT) kèm ảnh đại diện. Giá và biến thể tạo bằng API /{productId}/skus")
    public ResponseData<ProductResponse> createProduct(
            @Valid @ModelAttribute ProductRequest productRequest,
            @RequestPart(required = false) MultipartFile image) throws IOException {
        ProductResponse productResponse = productService.createProduct(productRequest, image);
        return new ResponseData<ProductResponse>().success(productResponse);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(summary = "API cập nhập 1  sản phẩm",
            description = "Cập nhật thông tin sản phẩm, nếu gửi kèm ảnh thì thêm ảnh mới lên cloudinary")
    public ResponseData<ProductResponse> updateProduct(@PathVariable Long id,
                                                      @Valid @ModelAttribute ProductRequest productRequest,
                                                      @RequestPart(required = false) MultipartFile image) throws IOException {
        ProductResponse productResponse = productService.updateProduct(id, productRequest, image);
        return new ResponseData<ProductResponse>().success(productResponse);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(summary = "API ẩn nhiều sản phẩm",
            description = "Chuyển sản phẩm sang HIDDEN (không xóa cứng vì đơn hàng cũ còn tham chiếu)")
    public ResponseData<Void> deleteProduct(@PathVariable List<Long> id) {
        productService.deleteProduct(id);
        return new ResponseData<Void>().success();
    }

    @PostMapping(value = "/{productId}/skus")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(summary = "API tạo biến thể (SKU) cho sản phẩm",
            description = "Mỗi SKU có giá riêng và gắn với các giá trị thuộc tính, vd Đỏ / L")
    public ResponseData<SkuResponse> createSku(@PathVariable Long productId, @Valid @RequestBody SkuRequest skuRequest) {
        return new ResponseData<SkuResponse>().success(productService.createSku(productId, skuRequest));
    }

    @PutMapping(value = "/skus/{skuId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(summary = "API cập nhật biến thể (SKU)",
            description = "Cập nhật giá, trạng thái, thuộc tính của SKU")
    public ResponseData<SkuResponse> updateSku(@PathVariable Long skuId, @Valid @RequestBody SkuRequest skuRequest) {
        return new ResponseData<SkuResponse>().success(productService.updateSku(skuId, skuRequest));
    }

    @PostMapping("/purchase")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "API chốt giá sản phẩm khi đặt hàng",
            description = "order-service gọi API này để kiểm tra SKU còn bán và lấy giá hiện tại từ DB (không tin giá client gửi)")
    public ResponseData<List<PurchaseResponse>> purchaseProduct(@Valid @RequestBody List<PurchaseRequest> purchaseRequest) {
        return new ResponseData<List<PurchaseResponse>>().success(productService.purchaseProduct(purchaseRequest));
    }

}
