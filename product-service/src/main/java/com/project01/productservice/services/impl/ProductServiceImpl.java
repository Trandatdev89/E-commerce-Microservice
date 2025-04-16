package com.project01.productservice.services.impl;

import com.project01.productservice.convertor.ProductConvertor;
import com.project01.productservice.dto.request.ProductRequest;
import com.project01.productservice.dto.request.PurchaseRequest;
import com.project01.productservice.dto.response.ProductResponse;
import com.project01.productservice.dto.response.PurchaseResponse;
import com.project01.productservice.entity.Product;
import com.project01.productservice.exception.custom.AppException;
import com.project01.productservice.exception.custom.ErrorCode;
import com.project01.productservice.repository.ProductRepository;
import com.project01.productservice.repository.SearchRepository;
import com.project01.productservice.repository.criteria.BuildQuerySpecification;
import com.project01.productservice.repository.criteria.SearchCriteria;
import com.project01.productservice.repository.criteria.SpecSearchCriteria;
import com.project01.productservice.services.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductConvertor productConvertor;
    private final SearchRepository searchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(productConvertor.toProductResponse(product));
        }
        return productResponses;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request,MultipartFile image) throws IOException {
        Product product = productConvertor.toProductEntity(request,image);
        productRepository.save(product);
        return productConvertor.toProductResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return productConvertor.toProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest,MultipartFile image) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        Product updateproduct = productConvertor.toProductEntity(productRequest,image);
        updateproduct.setId(product.getId());
        return productConvertor.toProductResponse(productRepository.save(updateproduct));
    }

    @Override
    public void deleteProduct(List<Long> id) {
        List<Product> product = productRepository.findAllById(id);
        for(Product p : product){
            p.getShips().clear();
            productRepository.save(p);
        }
        productRepository.deleteByIdIn(id);
    }

    @Override
    public List<ProductResponse> getProductByCategory(Long categoryId) {
        List<ProductResponse> productResponses = new ArrayList<>();
        List<Product> products = productRepository.findByCategory_Id(categoryId);
        for (Product product : products) {
            productResponses.add(productConvertor.toProductResponse(product));
        }
        return productResponses;
    }

    @Override
    @Transactional(rollbackFor = {AppException.class})
    public List<PurchaseResponse> purchaseProduct(List<PurchaseRequest> request) {
        //Lay ra so luong san pham co trung voi truyen vao voi db:
         List<PurchaseResponse> purchaseResponses = new ArrayList<>();
         List<Product> storedProduct = productRepository.findAllByIdInOrderById(request.stream().map(PurchaseRequest::getProductId).collect(Collectors.toList()));
         if(storedProduct.size() != request.size()){
             throw new AppException(ErrorCode.PRODUCT_NOT_MATCH);
         }
        List<PurchaseRequest> sortedPurchase = request.stream().sorted(Comparator.comparing(PurchaseRequest::getProductId)).toList();
         for(int i=0; i<storedProduct.size(); i++){
             var StoredProduct = storedProduct.get(i);
             var PurchaseProduct = sortedPurchase.get(i);
             if(StoredProduct.getAvailableQuantity() < PurchaseProduct.getQuantity()){
                 throw new AppException(ErrorCode.INVENTORY_NOT_ENOUGH);
             }
             var newAvailableQuantity = StoredProduct.getAvailableQuantity() - PurchaseProduct.getQuantity();
             StoredProduct.setAvailableQuantity(newAvailableQuantity);
             productRepository.save(StoredProduct);
             purchaseResponses.add(productConvertor.toPurchaseResponse(newAvailableQuantity,StoredProduct));
         }
         return purchaseResponses;
    }

    @Override
    public List<ProductResponse> searchSpecificationProduct(String[] search) {
        List<Product> products;
        log.info("Total Element: {}",getTotalElement(search));
        if(search!=null){
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Product> query = cb.createQuery(Product.class);
            Root<Product> root = query.from(Product.class);

            Predicate userPredicate = cb.conjunction();

            List<SearchCriteria> searchCriterias = new ArrayList<>();
            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            for(String s : search){
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    searchCriterias.add(new SearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5)));
                }
            }

            for(int i = 0 ;i<searchCriterias.size(); i++){
                if(searchCriterias.get(i).getKey().equals("category_id")){
                    Predicate catePredicate = searchRepository.joinCateAndProduct(searchCriterias.get(i),root,cb);
                    userPredicate = cb.and(catePredicate,userPredicate);
                }
                else if(searchCriterias.get(i).getKey().equals("brand_id")){
                    Predicate brandPredicate = searchRepository.joinBrandAndProduct(searchCriterias.get(i),root,cb);
                    userPredicate = cb.and(brandPredicate,userPredicate);
                }
                else if(searchCriterias.get(i).getKey().equals("ship_id")){
                    Predicate shipPredicate = searchRepository.joinShipAndProduct(searchCriterias.get(i),root,cb);
                    userPredicate = cb.and(shipPredicate,userPredicate);
                }
                else{
                    SpecSearchCriteria specSearchCriteria = new SpecSearchCriteria(searchCriterias.get(i));
                    Predicate cateNormal = specSearchCriteria.toPredicate(root,query,cb);
                    userPredicate = cb.and(cateNormal,userPredicate);
                }
            }
            query.where(userPredicate);
            products = entityManager.createQuery(query).getResultList();
        }
        else{
            products = productRepository.findAll();
        }
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(productConvertor.toProductResponse(product));
        }
        return productResponses;
    }


    public Long getTotalElement(String[] search) {
        Long products;
        if(search!=null){
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<Product> root = query.from(Product.class);

            Predicate userPredicate = cb.conjunction();

            List<SearchCriteria> searchCriterias = new ArrayList<>();
            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            for(String s : search){
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    searchCriterias.add(new SearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5)));
                }
            }

            for(int i = 0 ;i<searchCriterias.size(); i++){
                if(searchCriterias.get(i).getKey().equals("category_id")){
                    Predicate catePredicate = searchRepository.joinCateAndProduct(searchCriterias.get(i),root,cb);
                    userPredicate = cb.and(catePredicate,userPredicate);
                }
                else if(searchCriterias.get(i).getKey().equals("brand_id")){
                    Predicate brandPredicate = searchRepository.joinBrandAndProduct(searchCriterias.get(i),root,cb);
                    userPredicate = cb.and(brandPredicate,userPredicate);
                }
                else if(searchCriterias.get(i).getKey().equals("ship_id")){
                    Predicate shipPredicate = searchRepository.joinShipAndProduct(searchCriterias.get(i),root,cb);
                    userPredicate = cb.and(shipPredicate,userPredicate);
                }
                else{
                    SpecSearchCriteria specSearchCriteria = new SpecSearchCriteria(searchCriterias.get(i));
                    Predicate cateNormal = specSearchCriteria.toPredicate(root,query,cb);
                    userPredicate = cb.and(cateNormal,userPredicate);
                }
            }
            query.select(cb.count(root));
            query.where(userPredicate);
//            query.groupBy(root.get("id"));
            products = entityManager.createQuery(query).getSingleResult();
        }
        else{
            products = Long.valueOf(productRepository.findAll().size());
        }
        return products;
    }
}
