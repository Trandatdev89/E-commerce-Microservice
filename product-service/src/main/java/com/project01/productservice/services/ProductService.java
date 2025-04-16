package com.project01.productservice.services;

import com.project01.productservice.dto.request.ProductRequest;
import com.project01.productservice.dto.request.PurchaseRequest;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.PurchaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductResponse>  getAllProducts();
    ProductResponse createProduct(ProductRequest request,MultipartFile image) throws IOException;
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductRequest productRequest,MultipartFile image) throws IOException;
    void deleteProduct(List<Long> id);
    List<ProductResponse> getProductByCategory(Long categoryId);
    List<PurchaseResponse> purchaseProduct(List<PurchaseRequest> request) ;
    List<ProductResponse> searchSpecificationProduct(String[] search);
}
