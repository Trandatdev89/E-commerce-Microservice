package com.project01.chatservice.exception;
import com.project01.chatservice.dto.ApiResponse;
import com.project01.chatservice.exception.custom.AppException;
import com.project01.chatservice.exception.custom.ResourceNotFoundException;
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


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> feignException(ResourceNotFoundException ex){
        return ResponseEntity.status(ex.getCode()).body(ex.getMessage());
    }
}
