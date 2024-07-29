package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class OutOfLuckException extends CustomException {
    public OutOfLuckException(String message){
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
