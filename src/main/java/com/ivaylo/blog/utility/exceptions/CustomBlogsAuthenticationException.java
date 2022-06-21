package com.ivaylo.blog.utility.exceptions;

import lombok.NoArgsConstructor;
@NoArgsConstructor
public class CustomBlogsAuthenticationException extends RuntimeException{
    private String message;

    public CustomBlogsAuthenticationException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
