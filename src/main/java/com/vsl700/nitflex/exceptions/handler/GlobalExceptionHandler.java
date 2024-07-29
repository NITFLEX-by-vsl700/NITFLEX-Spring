package com.vsl700.nitflex.exceptions.handler;

import com.vsl700.nitflex.exceptions.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ CustomException.class })
    public ResponseEntity<String> handleCustomException(CustomException ce){
        return ResponseEntity
                .status(ce.getStatusCode())
                .body(ce.getMessage());
    }
}
