package com.project01.productservice.services.impl;

import com.project01.common.core.exception.BusinessException;
import com.project01.productservice.brand.BrandRepository;
import com.project01.productservice.category.CategoryRepository;
import com.project01.productservice.convertor.ProductConvertor;
import com.project01.productservice.dto.request.ProductRequest;
import com.project01.productservice.dto.request.PurchaseRequest;
import com.project01.productservice.dto.request.SkuRequest;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.PurchaseResponse;
import com.project01.productservice.dto.response.SkuResponse;
import com.project01.productservice.entity.*;
import com.project01.productservice.exception.ErrorCode;
import com.project01.productservice.repository.*;
import com.project01.productservice.repository.criteria.SearchCriteria;
import com.project01.productservice.repository.criteria.SpecSearchCriteria;
import com.project01.productservice.services.ProductService;
import com.project01.productservice.utils.SlugUtil;
import com.project01.productservice.utils.UploadFileUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final Pattern SEARCH_PATTERN = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");

    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;
    private final SkuAttributeValueRepository skuAttributeValueRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductConvertor productConvertor;
    private final UploadFileUtil uploadFileUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productConvertor.toProductResponses(productRepository.findAllByStatus(ProductStatus.ACTIVE));
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile image) throws IOException {
        validateCategoryAndBrand(request);
        Product product = productRepository.save(Product.builder()
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .name(request.getName())
                .slug(SlugUtil.toUniqueSlug(request.getName(), productRepository::existsBySlug))
                .description(request.getDescription())
                .status(request.getStatus() == null ? ProductStatus.DRAFT : request.getStatus())
                .build());
        saveImage(product.getId(), image);
        return productConvertor.toProductResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productConvertor.toProductResponse(findProduct(id));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, MultipartFile image) throws IOException {
        Product product = findProduct(id);
        validateCategoryAndBrand(request);
        if (!product.getName().equals(request.getName())) {
            product.setSlug(SlugUtil.toUniqueSlug(request.getName(), productRepository::existsBySlug));
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setBrandId(request.getBrandId());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        productRepository.save(product);
        saveImage(product.getId(), image);
        return productConvertor.toProductResponse(product);
    }

    /** Không xóa cứng vì đơn hàng cũ còn tham chiếu SKU, chỉ chuyển sang HIDDEN. */
    @Override
    @Transactional
    public void deleteProduct(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        products.forEach(product -> product.setStatus(ProductStatus.HIDDEN));
        productRepository.saveAll(products);
    }

    @Override
    public List<ProductResponse> getProductByCategory(Long categoryId) {
        return productConvertor.toProductResponses(
                productRepository.findAllByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE));
    }

    @Override
    @Transactional
    public SkuResponse createSku(Long productId, SkuRequest request) {
        findProduct(productId);
        if (skuRepository.existsBySkuCode(request.getSkuCode())) {
            throw new BusinessException(ErrorCode.SKU_CODE_EXISTED);
        }
        Sku sku = Sku.builder().productId(productId).build();
        applySkuRequest(sku, request);
        sku = skuRepository.save(sku);
        saveAttributeValues(sku.getId(), request.getAttributeValueIds());
        return productConvertor.toSkuResponses(List.of(sku)).get(0);
    }

    @Override
    @Transactional
    public SkuResponse updateSku(Long skuId, SkuRequest request) {
        Sku sku = skuRepository.findById(skuId).orElseThrow(() -> new BusinessException(ErrorCode.SKU_NOTFOUND));
        if (skuRepository.existsBySkuCodeAndIdNot(request.getSkuCode(), skuId)) {
            throw new BusinessException(ErrorCode.SKU_CODE_EXISTED);
        }
        applySkuRequest(sku, request);
        skuRepository.save(sku);
        skuAttributeValueRepository.deleteAllBySkuId(skuId);
        saveAttributeValues(skuId, request.getAttributeValueIds());
        return productConvertor.toSkuResponses(List.of(sku)).get(0);
    }

    @Override
    public List<SkuResponse> getSkusByIds(List<Long> skuIds) {
        return productConvertor.toSkuResponses(skuRepository.findAllById(skuIds));
    }

    /**
     * Chốt giá cho order-service: kiểm tra SKU còn bán và trả giá hiện tại lấy từ DB.
     * Chưa trừ tồn kho - phần giữ hàng sẽ nằm ở inventory-service (inv_reservation).
     */
    @Override
    public List<PurchaseResponse> purchaseProduct(List<PurchaseRequest> request) {
        Map<Long, Integer> quantityBySku = request.stream()
                .collect(Collectors.toMap(PurchaseRequest::getSkuId, PurchaseRequest::getQuantity, Integer::sum, LinkedHashMap::new));

        Map<Long, Sku> skuById = skuRepository.findAllById(quantityBySku.keySet()).stream()
                .collect(Collectors.toMap(Sku::getId, Function.identity()));
        if (skuById.size() != quantityBySku.size()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_MATCH);
        }
        List<Long> productIds = skuById.values().stream().map(Sku::getProductId).distinct().toList();
        Map<Long, Product> productById = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        Map<Long, List<ProductImage>> imagesByProduct = productConvertor.imagesByProduct(productIds);

        List<PurchaseResponse> responses = new ArrayList<>();
        quantityBySku.forEach((skuId, quantity) -> {
            Sku sku = skuById.get(skuId);
            Product product = productById.get(sku.getProductId());
            if (sku.getStatus() != SkuStatus.ACTIVE || product.getStatus() != ProductStatus.ACTIVE) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_AVAILABLE);
            }
            String imageUrl = productConvertor.imageOfSku(sku, imagesByProduct.getOrDefault(product.getId(), List.of()));
            responses.add(productConvertor.toPurchaseResponse(sku, product, imageUrl, quantity));
        });
        return responses;
    }

    @Override
    public List<ProductResponse> searchSpecificationProduct(String[] search) {
        if (search == null) {
            return getAllProducts();
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.equal(root.get("status"), ProductStatus.ACTIVE);
        for (String s : search) {
            Matcher matcher = SEARCH_PATTERN.matcher(s);
            if (!matcher.find()) {
                continue;
            }
            SearchCriteria criteria = new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
            Predicate condition = switch (criteria.getKey()) {
                case "category_id" -> cb.equal(root.get("categoryId"), Long.valueOf(criteria.getValue().toString()));
                case "brand_id" -> cb.equal(root.get("brandId"), Long.valueOf(criteria.getValue().toString()));
                case "price" -> skuPricePredicate(criteria, root, query, cb);
                default -> new SpecSearchCriteria(criteria).toPredicate(root, query, cb);
            };
            predicate = cb.and(predicate, condition);
        }
        query.where(predicate);
        List<Product> products = entityManager.createQuery(query).getResultList();
        log.info("Total Element: {}", products.size());
        return productConvertor.toProductResponses(products);
    }

    /** Giá nằm ở prd_sku nên lọc bằng EXISTS (sku đang bán của sản phẩm có giá thỏa điều kiện). */
    private Predicate skuPricePredicate(SearchCriteria criteria, Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Sku> sku = subquery.from(Sku.class);
        BigDecimal value = new BigDecimal(criteria.getValue().toString());
        Expression<BigDecimal> price = sku.get("price");
        Predicate priceCondition = switch (criteria.getOperator()) {
            case GREATER_THAN -> cb.greaterThanOrEqualTo(price, value);
            case LESS_THAN -> cb.lessThanOrEqualTo(price, value);
            case NEGATION -> cb.notEqual(price, value);
            default -> cb.equal(price, value);
        };
        subquery.select(sku.get("id")).where(
                cb.equal(sku.get("productId"), root.get("id")),
                cb.equal(sku.get("status"), SkuStatus.ACTIVE),
                priceCondition);
        return cb.exists(subquery);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOTFOUND));
    }

    private void validateCategoryAndBrand(ProductRequest request) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new BusinessException(ErrorCode.CATEGORY_NOTFOUND);
        }
        if (request.getBrandId() != null && !brandRepository.existsById(request.getBrandId())) {
            throw new BusinessException(ErrorCode.BRAND_NOTFOUND);
        }
    }

    private void saveImage(Long productId, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return;
        }
        productImageRepository.save(ProductImage.builder()
                .productId(productId)
                .url(uploadFileUtil.getImageToCloudinary(image))
                .sortOrder(productImageRepository.countByProductId(productId))
                .build());
    }

    private void applySkuRequest(Sku sku, SkuRequest request) {
        sku.setSkuCode(request.getSkuCode());
        sku.setName(request.getName());
        sku.setPrice(request.getPrice());
        sku.setCompareAtPrice(request.getCompareAtPrice());
        sku.setWeightGram(request.getWeightGram());
        if (request.getStatus() != null) {
            sku.setStatus(request.getStatus());
        }
    }

    private void saveAttributeValues(Long skuId, List<Long> attributeValueIds) {
        if (attributeValueIds == null || attributeValueIds.isEmpty()) {
            return;
        }
        Set<Long> distinctIds = new LinkedHashSet<>(attributeValueIds);
        if (attributeValueRepository.findAllById(distinctIds).size() != distinctIds.size()) {
            throw new BusinessException(ErrorCode.ATTRIBUTE_VALUE_NOTFOUND);
        }
        skuAttributeValueRepository.saveAll(distinctIds.stream()
                .map(valueId -> SkuAttributeValue.builder().skuId(skuId).attributeValueId(valueId).build())
                .toList());
    }
}
