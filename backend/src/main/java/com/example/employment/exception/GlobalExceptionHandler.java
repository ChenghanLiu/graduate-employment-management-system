package com.example.employment.exception;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.ApiCode;
import com.example.employment.common.BusinessException;
import com.example.employment.security.AccessDeniedException;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ApiResponse.failure(ApiCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler({ConstraintViolationException.class, MissingRequestHeaderException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception) {
        return ResponseEntity.badRequest().body(ApiResponse.failure(ApiCode.VALIDATION_ERROR, exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException exception) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ApiCode.ILLEGAL_STATE, exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure(ApiCode.FORBIDDEN, exception.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.failure(ApiCode.BAD_REQUEST, "请求方法不支持"));
    }

    @ExceptionHandler({CannotGetJdbcConnectionException.class, DataAccessResourceFailureException.class})
    public ResponseEntity<ApiResponse<Void>> handleDatabaseUnavailable(Exception exception) {
        log.error("Database unavailable", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.failure(ApiCode.DATABASE_UNAVAILABLE, "数据库暂不可用，请稍后重试"));
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleMyBatisSystemException(MyBatisSystemException exception) {
        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(exception);
        if (rootCause instanceof CannotGetJdbcConnectionException
                || rootCause instanceof java.sql.SQLTransientConnectionException
                || rootCause instanceof java.net.SocketException
                || (rootCause instanceof java.sql.SQLException
                && rootCause.getMessage() != null
                && rootCause.getMessage().contains("Communications link failure"))) {
            log.error("Database unavailable", exception);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.failure(ApiCode.DATABASE_UNAVAILABLE, "数据库暂不可用，请稍后重试"));
        }
        return handleOtherException(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOtherException(Exception exception) {
        log.error("Unhandled exception caught by global handler", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(ApiCode.INTERNAL_ERROR, "系统异常，请稍后重试"));
    }
}
