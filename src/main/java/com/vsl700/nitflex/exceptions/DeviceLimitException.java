package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class DeviceLimitException extends RuntimeException {
    public DeviceLimitException() { super("Device limit exceeded!"); }
}
