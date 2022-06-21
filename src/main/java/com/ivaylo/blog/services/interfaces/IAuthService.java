package com.ivaylo.blog.services.interfaces;

import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.UserLoginRequest;
import com.ivaylo.blog.models.UserRegisterRequest;

public interface IAuthService {
    User registerNewUser(UserRegisterRequest user);

    User login(UserLoginRequest user);

    void logout(User profileOwner);

    User isLoginUser(String sessionId);

    User isProfileOwner(String sessionId, String username);
}
