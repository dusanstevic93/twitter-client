package com.dusan.twitterclient.auth;

public class OAuthException extends RuntimeException{

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
