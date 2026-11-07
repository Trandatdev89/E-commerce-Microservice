package com.project01.apigateway.service;

import com.project01.apigateway.dto.request.IntrospectTokenRequest;
import com.project01.apigateway.dto.response.ResponseData;
import com.project01.apigateway.dto.response.AuthenticationResponse;
import com.project01.apigateway.httpClient.IdentityClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final IdentityClient identityClient;

    public Mono<ResponseData<AuthenticationResponse>> authenticate(String token) {
         return identityClient.introspect(IntrospectTokenRequest.builder().token(token).build());
    }
}
