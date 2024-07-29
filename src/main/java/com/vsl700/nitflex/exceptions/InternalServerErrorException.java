package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends CustomException {
    public InternalServerErrorException(String message) { super(HttpStatus.INTERNAL_SERVER_ERROR, message); }
}
