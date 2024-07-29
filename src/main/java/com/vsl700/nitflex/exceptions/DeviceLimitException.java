package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class DeviceLimitException extends CustomException {
    public DeviceLimitException() { super(HttpStatus.FORBIDDEN, "Device limit exceeded!"); }
}
