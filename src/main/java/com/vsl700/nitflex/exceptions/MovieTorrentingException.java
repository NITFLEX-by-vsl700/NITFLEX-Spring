package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class MovieTorrentingException extends CustomException {
    public MovieTorrentingException(String message){
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
