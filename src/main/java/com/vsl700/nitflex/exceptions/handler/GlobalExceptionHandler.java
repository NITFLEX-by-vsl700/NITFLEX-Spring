package com.vsl700.nitflex.exceptions.handler;

import com.vsl700.nitflex.exceptions.CustomException;
import com.vsl700.nitflex.models.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ CustomException.class })
    public ResponseEntity<ErrorDto> handleCustomException(CustomException ce){
        return ResponseEntity
                .status(ce.getStatusCode())
                .body(new ErrorDto(ce.getMessage()));
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public ResponseEntity<ErrorDto> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorDto(e.getMessage()));
    }
}
