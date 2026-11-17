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
    private static final String FORBIDDEN_MESSAGE = "Bạn không có quyền truy cập vào tài nguyên này";

    private final ObjectMapper objectMapper;

    @Value("${api.prefix}")
    private String apiPrefix;

    @NonFinal
    private String[] publicEnpoint=
            {"/identity/auth/.*","/aggregate/.*"};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Authentication filter ...");
        if (isInternalEndpoint(exchange.getRequest())) {
            return Forbidden(exchange);
        }
        var publicEnpoint = isPublicEnpoint(exchange.getRequest());
        if(publicEnpoint){
            return chain.filter(exchange);
        }
        List<String> authHeader =exchange.getRequest().getHeaders().get("Authorization");
        if(CollectionUtils.isEmpty(authHeader)) {
            log.info("Tài khoản chưa được xác thực!");
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
        return Arrays.stream(publicEnpoint).anyMatch(item->serverHttpRequest
                .getURI()
                .getPath()
                .matches(apiPrefix+item));
    }

    public boolean isInternalEndpoint(ServerHttpRequest serverHttpRequest){
        return serverHttpRequest.getURI().getPath().matches(apiPrefix + "/[^/]+/internal(/.*)?");
    }

    private Mono<Void> Unauthenticated(ServerWebExchange exchange){
        return writeError(exchange, HttpStatus.UNAUTHORIZED, "GL_ER_401", UNAUTHENTICATED_MESSAGE);
    }

    private Mono<Void> Forbidden(ServerWebExchange exchange){
        return writeError(exchange, HttpStatus.FORBIDDEN, "GL_ER_403", FORBIDDEN_MESSAGE);
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String errorCode, String message){
        ServerHttpResponse response = exchange.getResponse();
        ResponseData<Object> responseData = ResponseData.builder()
                .status(status.value())
                .error(message)
                .path(exchange.getRequest().getURI().getPath())
                .processDate(LocalDateTime.now())
                .soaErrorCode(errorCode)
                .soaErrorDesc(message)
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(responseData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
