package com.ami.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse>
    handleRuntimeException(
            RuntimeException ex
    ) {

        ErrorResponse error =
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                error,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleException(
            Exception ex
    ) {

        ErrorResponse error =
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                error,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}