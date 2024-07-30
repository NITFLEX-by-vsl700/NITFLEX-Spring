package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class InitialRegisterClosedException extends CustomException {
    public InitialRegisterClosedException() { super(HttpStatus.FORBIDDEN, "This server already has users!"); }
}
