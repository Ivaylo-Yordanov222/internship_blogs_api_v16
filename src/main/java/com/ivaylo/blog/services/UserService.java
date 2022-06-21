package com.ivaylo.blog.services;

import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.repositories.UserRepository;
import com.ivaylo.blog.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
