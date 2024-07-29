package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTorrentException extends CustomException {
    public InvalidTorrentException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }
}
