package com.ivaylo.blog.utility.exceptions;

public class CustomBlogsValidationException extends RuntimeException{
    private final String message;
    public CustomBlogsValidationException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
