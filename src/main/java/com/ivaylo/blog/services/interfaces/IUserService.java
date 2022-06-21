package com.ivaylo.blog.services.interfaces;

import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> getUserByUsername(String username);
}
