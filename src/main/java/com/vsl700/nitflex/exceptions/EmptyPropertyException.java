package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class EmptyPropertyException extends CustomException {
    public EmptyPropertyException(String message) { super(HttpStatus.BAD_REQUEST, message); }
}
