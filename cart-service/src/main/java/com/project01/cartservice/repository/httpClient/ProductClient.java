package com.project01.cartservice.repository.httpClient;


import com.project01.cartservice.dto.response.SkuResponse;
import com.project01.common.core.dto.response.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-client", url = "http://localhost:8082/product")
public interface ProductClient {

    @GetMapping(value = "/public/skus", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseData<List<SkuResponse>> getSkusByIds(@RequestParam("ids") List<Long> ids);
}
