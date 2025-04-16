package com.project01.identityservice.exception;

import com.project01.identityservice.dto.response.ApiResponse;
import com.project01.identityservice.exception.custom.AppException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GolbalException {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> appException(AppException ex){
        ApiResponse apiResponse = ApiResponse.builder()
                .message(ex.getErrorCode().getMessage())
                .code(ex.getErrorCode().getCode())
                .build();
        return ResponseEntity.status(ex.getErrorCode().getCode()).body(apiResponse);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse> feignException(FeignException ex){
          log.info("Errror {}",ex.responseBody());
          log.info("Errror {}",ex.contentUTF8());
          log.info("Errror {}",ex.getMessage());
          log.info("Errror {}",ex.request());
          return null;
    }

}
