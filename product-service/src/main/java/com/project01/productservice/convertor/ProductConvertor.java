package com.project01.productservice.convertor;

import com.project01.productservice.brand.BrandRepository;
import com.project01.productservice.category.CategoryRepository;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.PurchaseResponse;
import com.project01.productservice.dto.response.SkuResponse;
import com.project01.productservice.entity.*;
import com.project01.productservice.repository.ProductImageRepository;
import com.project01.productservice.repository.ProductRepository;
import com.project01.productservice.repository.SkuAttributeValueRepository;
import com.project01.productservice.repository.SkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Ghép dữ liệu từ nhiều bảng thành response. Vì entity không có quan hệ JPA,
 * mỗi bảng liên quan được lấy 1 lần theo danh sách id (tránh N+1) rồi ghép trong bộ nhớ.
 */
@Component
@RequiredArgsConstructor
public class ProductConvertor {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;
    private final ProductImageRepository productImageRepository;
    private final SkuAttributeValueRepository skuAttributeValueRepository;

    public ProductResponse toProductResponse(Product product) {
        return toProductResponses(List.of(product)).get(0);
    }

    public List<ProductResponse> toProductResponses(List<Product> products) {
        if (products.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = products.stream().map(Product::getId).toList();

        Map<Long, String> categoryNames = categoryRepository
                .findAllById(products.stream().map(Product::getCategoryId).distinct().toList())
                .stream().collect(Collectors.toMap(Category::getId, Category::getName));
        Map<Long, String> brandNames = brandRepository
                .findAllById(products.stream().map(Product::getBrandId).filter(Objects::nonNull).distinct().toList())
                .stream().collect(Collectors.toMap(Brand::getId, Brand::getName));

        List<Sku> skus = skuRepository.findAllByProductIdIn(productIds);
        Map<Long, List<Sku>> skusByProduct = skus.stream().collect(Collectors.groupingBy(Sku::getProductId));
        Map<Long, List<ProductImage>> imagesByProduct = imagesByProduct(productIds);
        Map<Long, List<Long>> attributeValuesBySku = attributeValuesBySku(skus);
        Map<Long, Product> productById = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        return products.stream().map(product -> {
            List<ProductImage> images = imagesByProduct.getOrDefault(product.getId(), List.of());
            List<Sku> productSkus = skusByProduct.getOrDefault(product.getId(), List.of());
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .slug(product.getSlug())
                    .description(product.getDescription())
                    .status(product.getStatus().name())
                    .categoryId(product.getCategoryId())
                    .categoryName(categoryNames.get(product.getCategoryId()))
                    .brandId(product.getBrandId())
                    .brandName(product.getBrandId() == null ? null : brandNames.get(product.getBrandId()))
                    .imageUrl(images.isEmpty() ? null : images.get(0).getUrl())
                    .images(images.stream().map(ProductImage::getUrl).toList())
                    .minPrice(productSkus.stream()
                            .filter(sku -> sku.getStatus() == SkuStatus.ACTIVE)
                            .map(Sku::getPrice)
                            .min(BigDecimal::compareTo)
                            .orElse(null))
                    .skus(productSkus.stream()
                            .map(sku -> toSkuResponse(sku, productById.get(sku.getProductId()), images, attributeValuesBySku))
                            .toList())
                    .build();
        }).toList();
    }

    /** Chi tiết danh sách SKU kèm tên sản phẩm và ảnh, dùng cho giỏ hàng. */
    public List<SkuResponse> toSkuResponses(List<Sku> skus) {
        if (skus.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = skus.stream().map(Sku::getProductId).distinct().toList();
        Map<Long, Product> productById = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        Map<Long, List<ProductImage>> imagesByProduct = imagesByProduct(productIds);
        Map<Long, List<Long>> attributeValuesBySku = attributeValuesBySku(skus);

        return skus.stream()
                .map(sku -> toSkuResponse(sku, productById.get(sku.getProductId()),
                        imagesByProduct.getOrDefault(sku.getProductId(), List.of()), attributeValuesBySku))
                .toList();
    }

    public PurchaseResponse toPurchaseResponse(Sku sku, Product product, String imageUrl, Integer quantity) {
        return PurchaseResponse.builder()
                .skuId(sku.getId())
                .skuCode(sku.getSkuCode())
                .productId(product.getId())
                .productName(product.getName())
                .skuName(sku.getName())
                .imageUrl(imageUrl)
                .unitPrice(sku.getPrice())
                .quantity(quantity)
                .lineTotal(sku.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    public Map<Long, List<ProductImage>> imagesByProduct(Collection<Long> productIds) {
        return productImageRepository.findAllByProductIdInOrderBySortOrder(productIds).stream()
                .collect(Collectors.groupingBy(ProductImage::getProductId, LinkedHashMap::new, Collectors.toList()));
    }

    /** Ảnh riêng của SKU nếu có, không thì lấy ảnh đại diện của sản phẩm. */
    public String imageOfSku(Sku sku, List<ProductImage> productImages) {
        return productImages.stream()
                .filter(image -> sku.getId().equals(image.getSkuId()))
                .findFirst()
                .or(() -> productImages.stream().findFirst())
                .map(ProductImage::getUrl)
                .orElse(null);
    }

    private SkuResponse toSkuResponse(Sku sku, Product product, List<ProductImage> productImages,
                                      Map<Long, List<Long>> attributeValuesBySku) {
        return SkuResponse.builder()
                .id(sku.getId())
                .productId(sku.getProductId())
                .productName(product == null ? null : product.getName())
                .skuCode(sku.getSkuCode())
                .name(sku.getName())
                .price(sku.getPrice())
                .compareAtPrice(sku.getCompareAtPrice())
                .weightGram(sku.getWeightGram())
                .status(sku.getStatus().name())
                .imageUrl(imageOfSku(sku, productImages))
                .attributeValueIds(attributeValuesBySku.getOrDefault(sku.getId(), List.of()))
                .build();
    }

    private Map<Long, List<Long>> attributeValuesBySku(List<Sku> skus) {
        return skuAttributeValueRepository.findAllBySkuIdIn(skus.stream().map(Sku::getId).toList()).stream()
                .collect(Collectors.groupingBy(SkuAttributeValue::getSkuId,
                        Collectors.mapping(SkuAttributeValue::getAttributeValueId, Collectors.toList())));
    }
}
