package com.project01.cartservice.repository.httpClient;


import com.project01.cartservice.dto.response.ApiResponse;
import com.project01.cartservice.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-client",url = "http://localhost:8082/product")
public interface ProductClient {

    @GetMapping(value = "/public/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<ProductResponse> getProductById(@PathVariable Long id);
}
