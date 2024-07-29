package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {
    private HttpStatus statusCode;

    public CustomException(HttpStatus statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
