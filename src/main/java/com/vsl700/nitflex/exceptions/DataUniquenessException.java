package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DataUniquenessException extends RuntimeException {
    public DataUniquenessException(String message) { super(message); }
}
