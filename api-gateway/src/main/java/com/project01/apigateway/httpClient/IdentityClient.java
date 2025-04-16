package com.project01.apigateway.httpClient;

import com.project01.apigateway.dto.request.IntrospectTokenRequest;
import com.project01.apigateway.dto.response.ApiResponse;
import com.project01.apigateway.dto.response.AuthenticationResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    @PostExchange(value = "/auth/introspect",contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<AuthenticationResponse>> introspect(@RequestBody IntrospectTokenRequest introspectTokenRequest);
}
