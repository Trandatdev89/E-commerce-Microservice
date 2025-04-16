package com.project01.productservice.convertor;

import com.project01.productservice.brand.BrandRepository;
import com.project01.productservice.category.Category;
import com.project01.productservice.category.CategoryRepository;
import com.project01.productservice.dto.request.ProductRequest;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.PurchaseResponse;
import com.project01.productservice.entity.Product;
import com.project01.productservice.exception.custom.AppException;
import com.project01.productservice.exception.custom.ErrorCode;
import com.project01.productservice.ship.ShipRepository;
import com.project01.productservice.utils.UploadFileUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;

@Component
public class ProductConvertor {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UploadFileUtil uploadFileUtil;
    @Autowired
    private ShipRepository shipRepository;

    public ProductResponse toProductResponse(Product product) {
        ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
        Category category = categoryRepository
                .findById(product.getCategory().getId())
                .orElseThrow(()-> new RuntimeException("Category not found"));
        productResponse.setCategoryName(category.getName());
        productResponse.setCategoryId(category.getId());
        return productResponse;
    }

    public Product toProductEntity(ProductRequest request,MultipartFile image) throws IOException {

        if(request.getCategoryId() == null || request.getBrandId() == null || request.getShipId()==null){
            throw new AppException(ErrorCode.FIELD_NOTNULL);
        }

        Product product = Product.builder()
                .category(categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(()-> new RuntimeException("Category not found")))
                .brand(brandRepository
                        .findById(request.getBrandId())
                        .orElseThrow(()-> new RuntimeException("Brand not found")))
                .address(request.getAddress())
                .ships(shipRepository
                        .findAllById((request.getShipId())))
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .availableQuantity(request.getAvailableQuantity())
                .build();
        if(image != null) {
            String imageUrl = uploadFileUtil.getImageToCloudinary(image);
            product.setImage(imageUrl);
        }
        return product;
    }



    public PurchaseResponse toPurchaseResponse(Long quantity, Product product){
        return PurchaseResponse.builder()
                .productId(product.getId())
                .quantity(quantity)
                .price(product.getPrice())
                .description(product.getDescription())
                .name(product.getName())
                .build();
    }
}
