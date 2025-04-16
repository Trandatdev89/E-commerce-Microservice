package com.project01.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project01.orderservice.exception.custom.AppException;
import com.project01.orderservice.exception.custom.ErrorCode;
import com.project01.orderservice.exception.custom.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String responseBody = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            return new ResourceNotFoundException(response.status(),responseBody);
        } catch (IOException e) {
            return new RuntimeException("Lỗi khi xử lý response", e);
        }
    }
}
