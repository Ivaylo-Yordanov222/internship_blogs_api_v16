package com.ivaylo.blog.factories;

import com.ivaylo.blog.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User assembleUser(String username, String email, String password){
        return new User(username, email, password);
    }
}
