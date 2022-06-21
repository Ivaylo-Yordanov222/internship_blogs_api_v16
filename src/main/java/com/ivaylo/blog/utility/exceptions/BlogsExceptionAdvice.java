package com.ivaylo.blog.utility.exceptions;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class BlogsExceptionAdvice extends ResponseEntityExceptionHandler {
    private final static String FILE_MUST_BE_NOT_LARGER_THAN = "File must be not larger than 2 MB!";

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorMessage> handleMaxSizeException(MaxUploadSizeExceededException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.EXPECTATION_FAILED.value(),
                new Date(),
                FILE_MUST_BE_NOT_LARGER_THAN,
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(CustomBlogsValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(CustomBlogsValidationException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomBlogsAuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationException(WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.FORBIDDEN.value(),
                new Date(),
                "Not authorized interaction",
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomBlogsConflictException.class)
    public ResponseEntity<ErrorMessage> handleConflictException(CustomBlogsConflictException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
}
