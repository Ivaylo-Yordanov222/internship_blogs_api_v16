package com.ivaylo.blog.utility.exceptions;

public class CustomBlogsConflictException extends RuntimeException{
    private final String message;
    public CustomBlogsConflictException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
