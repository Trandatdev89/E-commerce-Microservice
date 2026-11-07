package com.project01.apigateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project01.apigateway.dto.response.ResponseData;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import com.project01.apigateway.service.IdentityService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayConfig implements GlobalFilter, Ordered {

    private final IdentityService identityService;
    private static final String UNAUTHENTICATED_MESSAGE = "Tài khoản chưa được xác thực, yêu cầu đăng nhập!";

    private final ObjectMapper objectMapper;

    @Value("${api.prefix}")
    private String apiPrefix;

    @NonFinal
    private String[] publicEnpoint=
            {"/identity/auth/.*","/product/public(/.*)?","/product/category/public(/.*)?","/aggregate/.*"};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Authentication filter ...");
        var publicEnpoint = isPublicEnpoint(exchange.getRequest());
        if(publicEnpoint){
            return chain.filter(exchange);
        }
        List<String> authHeader =exchange.getRequest().getHeaders().get("Authorization");
        if(CollectionUtils.isEmpty(authHeader)) {
            return Unauthenticated(exchange);
        }
        String token = authHeader.get(0).replace("Bearer ","").trim();
        return identityService.authenticate(token).flatMap(authenticationResponseApiResponse -> {
             if(authenticationResponseApiResponse.getData().isValid()){
                 return chain.filter(exchange);
             }else{
                 return Unauthenticated(exchange);
             }
        }).onErrorResume(throwable ->  Unauthenticated(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public boolean isPublicEnpoint(ServerHttpRequest serverHttpRequest){
        return Arrays.stream(publicEnpoint).anyMatch(item->serverHttpRequest.getURI().getPath().matches(apiPrefix+item));
    }

    private Mono<Void> Unauthenticated(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        // Cùng mã lỗi với CommonErrorCode.UNAUTHORIZED của common-core
        ResponseData<Object> responseData = ResponseData.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(UNAUTHENTICATED_MESSAGE)
                .path(exchange.getRequest().getURI().getPath())
                .processDate(LocalDateTime.now())
                .soaErrorCode("GL_ER_401")
                .soaErrorDesc(UNAUTHENTICATED_MESSAGE)
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(responseData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
