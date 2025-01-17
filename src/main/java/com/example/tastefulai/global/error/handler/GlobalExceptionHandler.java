package com.example.tastefulai.global.error.handler;

import com.example.tastefulai.global.error.errorcode.ErrorCode;
import com.example.tastefulai.global.error.exception.BadRequestException;
import com.example.tastefulai.global.error.exception.CustomException;
import com.example.tastefulai.global.error.exception.ForbiddenException;
import com.example.tastefulai.global.error.exception.NotFoundException;
import com.example.tastefulai.global.error.exception.ServiceUnavailableException;
import com.example.tastefulai.global.error.exception.UnAuthorizedException;
import com.example.tastefulai.global.error.response.ErrorResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 및 하위 예외 처리
    @ExceptionHandler({CustomException.class, BadRequestException.class, ForbiddenException.class, NotFoundException.class, UnAuthorizedException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(CustomException customException) {
        return ErrorResponse.toResponseEntity(customException.getErrorCode());
    }

    // 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException customException) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : customException.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());}
        return ResponseEntity.badRequest().body(errors);
    }

    // AuthenticationException 처리 (토큰 관련 예외)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthenticationException customException) {
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_TOKEN);
    }

    // AccessDeniedException 처리 (권한 부족 예외)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException customException) {
        return ErrorResponse.toResponseEntity(ErrorCode.FORBIDDEN);
    }

    // AuthorizationDeniedException 처리 (권한 부족 예외)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException customException) {
        return ErrorResponse.toResponseEntity(ErrorCode.FORBIDDEN);
    }

    // JwtException 처리 (JWT 토큰 관련 예외)
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException customException) {
        return ErrorResponse.toResponseEntity(ErrorCode.EXPIRED_TOKEN);
    }

    // 파일 크기 제한 예외 처리
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException customException) {
        return ErrorResponse.toResponseEntity(ErrorCode.LARGE_FILE);
    }

    // 메뉴 추천 요청 횟수 초과 예외 처리
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleRequestLimitExceededException(ServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
    }

    // 잘못된 데이터 요청 예외 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException customException) {
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_JSON);
    }

    // 존재하지 않는 URL 요청 또는 잘못된 경로 접근 예외 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoHandlerFoundException customException) {
        return ErrorResponse.toResponseEntity(ErrorCode.NOT_FOUND);
    }
}
