package com.dusan.twitterclient.api;

import com.dusan.twitterclient.service.exception.TwitterApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice("com.dusan.twitterclient.api")
public class ApiExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleTwitterApiException(TwitterApiException e, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                e.getStatusCode(),
                HttpStatus.valueOf(e.getStatusCode()).name(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMediaUploadException(MediaUploadException e, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                400,
                "Bad Request",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAll(RuntimeException e, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                500,
                "Internal server error",
                "",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
