package com.ivaylo.blog.models;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;
}
