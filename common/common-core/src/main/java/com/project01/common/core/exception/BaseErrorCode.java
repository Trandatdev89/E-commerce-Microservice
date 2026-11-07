package com.project01.common.core.exception;


import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
