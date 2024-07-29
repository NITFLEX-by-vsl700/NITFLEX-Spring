package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class WebClientLoginException extends CustomException {
    public WebClientLoginException(){ super(HttpStatus.INTERNAL_SERVER_ERROR, "Server couldn't login in the torrent website!"); }

    public WebClientLoginException(String message){
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
