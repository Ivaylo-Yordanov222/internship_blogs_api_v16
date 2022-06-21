package com.ivaylo.blog.services;

import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.repositories.UserRepository;
import com.ivaylo.blog.services.AuthService;
import com.ivaylo.blog.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {
    private static final String username = "ivo";
    private static final User user = new User();
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        createUser();
    }

    @Test
    public void givenUsernameWhenGetUserByUsernameThenReturnOptionalUser(){

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.getUserByUsername(username);
    }
    @Test
    public void givenUsernameWhenGetUserByUsernameThenReturnOptionalEmpty(){
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        userService.getUserByUsername(username);
    }
    private void createUser(){
        user.setUsername("ivo");
        user.setEmail("ivo@gmail.com");
        user.setPassword("1234");
    }
}
