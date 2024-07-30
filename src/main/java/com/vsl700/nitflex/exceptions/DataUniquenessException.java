package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class DataUniquenessException extends CustomException {
    public DataUniquenessException(String message) { super(HttpStatus.BAD_REQUEST, message); }
}
