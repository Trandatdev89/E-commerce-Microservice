package com.project01.common.core.exception;

import com.project01.common.core.dto.BaseObjectInfo;
import com.project01.common.core.dto.response.ResponseData;
import com.project01.common.core.dto.response.SubError;
import com.project01.common.core.enums.CommonErrorCode;
import io.micrometer.common.util.StringUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
@NoArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Object> handleAllExceptionImpl(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ThreadContext.put("logType", "response");

        if (ex instanceof BusinessException e) {
            ThreadContext.put("status", String.valueOf(e.getHttpStatus().value()));
            ThreadContext.put("responseCode", String.valueOf(e.getCode()));

            if (StringUtils.isNotBlank(e.getMessage())) {
                ThreadContext.put("responseMsg", e.getMessage());
            }

            return new ResponseEntity<>(new ResponseData<>().error(e.getHttpStatus().value(), e.getMessage(), e.getCode(),
                    this.getMessage(e.getMessage(), e.getMessageArg())), e.getHttpStatus());
        } else if (ex instanceof MethodArgumentNotValidException e) {
            return handleMethodArgumentNotValid(e);
        } else if (ex instanceof MaxUploadSizeExceededException e) {
            return handleMaximumFileUpload(e);
        } else if (ex instanceof AccessDeniedException e) {
            return handleAccessDeniedException(e);
        } else if (ex instanceof DataIntegrityViolationException e) {
            return handleDataIntegrityViolation(e);
        } else {
            ThreadContext.put("status", String.valueOf(status.value()));
            ThreadContext.put("responseCode", CommonErrorCode.INTERNAL_SERVER_ERROR.getCode());
            this.logger.error(ex.getMessage(), ex);
            return new ResponseEntity((new ResponseData()).error(status.value(), "Có lỗi trong quá trình xử lý! Vui lòng liên hệ IT để được hỗ trợ.", CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(), "Có lỗi trong quá trình xử lý! Vui lòng liên hệ IT để được hỗ trợ."), status);
        }
    }

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        return this.handleAllExceptionImpl(ex, (HttpHeaders) null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return this.handleMethodArgumentNotValid(ex);
    }

    private ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<SubError> lstSubErr = new ArrayList<>();
        List<FieldError> fieldError = ex.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder("");

        for (FieldError error : fieldError) {
            if (error != null) {
                String objectName = "";
                if (error.getRejectedValue() instanceof BaseObjectInfo) {
                    BaseObjectInfo baseObjectInfo = (BaseObjectInfo) error.getRejectedValue();
                    if (baseObjectInfo != null) {
                        objectName = baseObjectInfo.getObjectName();
                    }
                }

                SubError subError = new SubError(error.getField(), error.getRejectedValue(), error.getDefaultMessage());
                lstSubErr.add(subError);
                sb.append(System.lineSeparator());
                if (StringUtils.isNotBlank(objectName)) {
                    sb.append(objectName + ": ");
                }

                sb.append(error.getDefaultMessage());
            }
        }

        ThreadContext.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        ThreadContext.put("responseCode", CommonErrorCode.INTERNAL_SERVER_ERROR.getCode());
        ThreadContext.put("responseMsg", ex.getMessage());
        this.logger.warn(sb.toString(), ex);
        return new ResponseEntity((new ResponseData()).error(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                sb.toString(), lstSubErr), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> handleMaximumFileUpload(MaxUploadSizeExceededException ex) {
        String msg = ex.getMessage();
        String maxSize = "";
        if (!StringUtils.isEmpty(msg) && msg.contains("SizeLimitExceededException")) {
            String maxFileSize = msg.substring(msg.indexOf("maximum")).replaceAll("\\D+", "");
            if (!maxFileSize.isEmpty()) {
                maxSize = this.asReadableFileSize(Long.parseLong(maxFileSize));
            }
        }

        String errorMess = "Maximum upload file is : " + maxSize;
        ThreadContext.put("status", String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        ThreadContext.put("responseCode", CommonErrorCode.INTERNAL_SERVER_ERROR.getCode());
        ThreadContext.put("responseMsg", errorMess);
        this.logger.warn(errorMess, ex);
        return new ResponseEntity((new ResponseData()).error(HttpStatus.UNPROCESSABLE_ENTITY.value(), errorMess, CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(), errorMess), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        CommonErrorCode errorCode = CommonErrorCode.FORBIDDEN;
        ThreadContext.put("status", String.valueOf(errorCode.getHttpStatus().value()));
        ThreadContext.put("responseCode", errorCode.getCode());
        ThreadContext.put("responseMsg", errorCode.getMessage());
        this.logger.warn(e.getMessage());
        return new ResponseEntity<>(new ResponseData<>().error(errorCode.getHttpStatus().value(), errorCode.getMessage(),
                errorCode.getCode(), errorCode.getMessage()), errorCode.getHttpStatus());
    }

    private ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        CommonErrorCode errorCode = CommonErrorCode.DATA_INTEGRITY_VIOLATION;
        ThreadContext.put("status", String.valueOf(errorCode.getHttpStatus().value()));
        ThreadContext.put("responseCode", errorCode.getCode());
        ThreadContext.put("responseMsg", errorCode.getMessage());
        this.logger.error(e.getMostSpecificCause().getMessage(), e);
        return new ResponseEntity<>(new ResponseData<>().error(errorCode.getHttpStatus().value(), errorCode.getMessage(),
                errorCode.getCode(), errorCode.getMessage()), errorCode.getHttpStatus());
    }

    private String asReadableFileSize(long size) {
        if (size <= 0L) {
            return "0";
        } else {
            String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10((double) size) / Math.log10((double) 1024.0F));
            String var10000 = (new DecimalFormat("#,##0.#")).format((double) size / Math.pow((double) 1024.0F, (double) digitGroups));
            return var10000 + " " + units[digitGroups];
        }
    }

    private String getMessage(String code, Object[] args) {
        try {
            return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception var4) {
            return code;
        }
    }

}
