package com.project01.cartservice.exception;

import com.project01.cartservice.dto.response.ApiResponse;
import com.project01.cartservice.exception.custom.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GolbalException {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> appException(AppException ex){
        ApiResponse apiResponse = ApiResponse.builder()
                .message(ex.getErrorCode().getMessage())
                .code(ex.getErrorCode().getCode())
                .build();
        return ResponseEntity.status(ex.getErrorCode().getCode()).body(apiResponse);
    }
}
