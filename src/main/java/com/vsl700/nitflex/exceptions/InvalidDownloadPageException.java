package com.vsl700.nitflex.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDownloadPageException extends CustomException {
    public InvalidDownloadPageException(){
        super(HttpStatus.BAD_REQUEST, "The provided download page is not valid!");
    }

    public InvalidDownloadPageException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }
}
