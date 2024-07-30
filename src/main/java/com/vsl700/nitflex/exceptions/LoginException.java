package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class LoginException extends CustomException {
    public LoginException() { super(HttpStatus.UNAUTHORIZED, "Login failed!"); }
}
