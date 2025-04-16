package com.project01.apigateway.config;


import com.project01.apigateway.httpClient.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl("http://localhost:8080/identity").build();
    }

    @Bean
    public IdentityClient identityClient(WebClient webClient) {
         HttpServiceProxyFactory serviceProxyFactory = HttpServiceProxyFactory
                 .builderFor(WebClientAdapter.create(webClient))
                 .build();
         return serviceProxyFactory.createClient(IdentityClient.class);
    }
}
