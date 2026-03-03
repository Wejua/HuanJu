package jieyi.lu.huanjuweflux.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理参数验证异常
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<?>>> handleValidationExceptions(WebExchangeBindException ex) {
        log.warn("参数验证失败");

        String errorMessage = ex.getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .findFirst()
                .orElse("参数验证失败");

        ApiResponse errorResponse = ApiResponse.error(errorMessage, HttpStatus.BAD_REQUEST.value());

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    // 处理运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ApiResponse>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage());

        ApiResponse errorResponse = ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    // 处理其他所有异常
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse>> handleException(Exception e) {
        log.error("系统异常: ", e);

        ApiResponse errorResponse = ApiResponse.error("系统内部错误", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}