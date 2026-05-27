package com.example.reservation.common;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//전체 예외 처리 
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) throws JsonProcessingException {
        String body = objectMapper.writeValueAsString(Map.of("status", "error", "message", e.getMessage()));
        
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) throws JsonProcessingException {
        String body = objectMapper.writeValueAsString(Map.of("status", "error", "message", e.getMessage()));
        log.warn("Business Exception: {} - Status: {}", e.getMessage(), e.getErrorCode());
        return ResponseEntity.status(e.getErrorCode().geHttpStatus()).body(body);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handle404(NoHandlerFoundException e) {
        log.error("No Handler Found Exception occurred: ", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) throws  JsonProcessingException{
        log.error("Unhandled Exception occurred: ", e);
        String body = objectMapper.writeValueAsString(Map.of("status", "error", "message", e.getMessage()));
        return ResponseEntity.internalServerError().body(body);
    }

}
