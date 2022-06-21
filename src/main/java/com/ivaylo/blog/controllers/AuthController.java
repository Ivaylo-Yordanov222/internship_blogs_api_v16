package com.ivaylo.blog.controllers;

import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.UserLoginRequest;
import com.ivaylo.blog.models.UserRegisterRequest;
import com.ivaylo.blog.services.interfaces.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.BlogsConstants.SESSION_ID;

@RestController
@RequestMapping("api/v1/auth/")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @PostMapping(path = "register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequest user) {
        authService.registerNewUser(user);
        return new ResponseEntity<>(String.format(USER_SUCCESSFULLY_REGISTERED.getMessage(), user.getUsername()), HttpStatus.CREATED);
    }

    @PostMapping(path = "login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest user) {
        User userResponse = authService.login(user);
        return ResponseEntity.ok()
                .header(SESSION_ID, userResponse.getSessionId())
                .body(String.format(USER_LOG_IN.getMessage(), userResponse.getUsername(), userResponse.getSessionId()));
    }

    @PostMapping(path = "logout/{username}")
    public ResponseEntity<String> logout(@PathVariable("username") String username,
                                         @RequestHeader(SESSION_ID) String sessionId) {
        User profileOwner = authService.isProfileOwner(sessionId,username);
        authService.logout(profileOwner);
        return new ResponseEntity<>(String.format(USER_LOGGED_OUT.getMessage(), profileOwner.getUsername()), HttpStatus.OK);
    }
}