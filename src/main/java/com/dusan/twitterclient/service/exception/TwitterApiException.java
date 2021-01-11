package com.dusan.twitterclient.service.exception;

import lombok.Getter;

@Getter
public class TwitterApiException extends RuntimeException{

    private final int statusCode;

    public TwitterApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
