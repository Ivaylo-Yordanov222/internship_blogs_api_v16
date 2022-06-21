package com.ivaylo.blog.services;

import com.ivaylo.blog.factories.UserFactory;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.UserLoginRequest;
import com.ivaylo.blog.models.UserRegisterRequest;
import com.ivaylo.blog.repositories.UserRepository;
import com.ivaylo.blog.services.interfaces.IAuthService;
import com.ivaylo.blog.utility.enums.ValidationResult;
import com.ivaylo.blog.utility.exceptions.CustomBlogsAuthenticationException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsConflictException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.SUCCESS;
import static com.ivaylo.blog.utility.validators.UserLoginValidator.*;
import static com.ivaylo.blog.utility.validators.UserRegisterValidator.*;
import static java.lang.String.format;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Override
    public User registerNewUser(UserRegisterRequest user) {
        ValidationResult result = validateUserRegister(user);
        if (result != SUCCESS) {
            throw new CustomBlogsValidationException(result.getMessage());
        }
        User userEntity = userFactory.assembleUser(user.getUsername(), user.getEmail(), user.getPassword());
        checkUsername(userEntity);
        String encodeEmail = Base64.getEncoder().encodeToString(user.getEmail().getBytes());
        userEntity.setEmail(encodeEmail);
        checkUserEmail(userEntity);
        String bcryptPassword = BCrypt.hashpw(userEntity.getPassword(), BCrypt.gensalt(12));
        userEntity.setPassword(bcryptPassword);
        userRepository.save(userEntity);
        return userEntity;
    }

    @Override
    public User login(UserLoginRequest user) {
        ValidationResult result = validateUserLogin(user);
        if (result != SUCCESS) {
            throw new CustomBlogsValidationException(result.getMessage());
        }
        String encodeEmail = Base64.getEncoder().encodeToString(user.getEmail().getBytes());
        Optional<User> userDB = userRepository.findByEmail(encodeEmail);
        if (!userDB.isPresent() || !BCrypt.checkpw(user.getPassword(), userDB.get().getPassword())) {
            throw new CustomBlogsValidationException(INVALID_CREDENTIALS.getMessage());
        }
        if(userDB.get().isLogin()){
            throw new CustomBlogsConflictException(USER_ALREADY_LOGGED_IN.getMessage());
        }
        userDB.get().setLogin(true);
        userDB.get().setSessionId(UUID.randomUUID().toString());
        userRepository.save(userDB.get());
        return userDB.get();
    }

    @Override
    public void logout(User profileOwner) {
        profileOwner.setSessionId(null);
        profileOwner.setLogin(false);
        userRepository.save(profileOwner);
    }

    @Override
    public User isLoginUser(String sessionId) {
        Optional<User> userDB = userRepository.findBySessionId(sessionId);
        if (!userDB.isPresent() || !userDB.get().isLogin()) {
            throw new CustomBlogsAuthenticationException();
        }
        return userDB.get();
    }

    @Override
    public User isProfileOwner(String sessionId, String username) {
        Optional<User> userDB = userRepository.findBySessionId(sessionId);
        if (!userDB.isPresent() || !userDB.get().getUsername().equals(username) || !userDB.get().isLogin()) {
            throw new CustomBlogsAuthenticationException();
        }
        return userDB.get();
    }

    private void checkUsername(User user) {
        Optional<User> userFromDbByName = userRepository.findByUsername(user.getUsername());
        if (userFromDbByName.isPresent()) {
            throw new CustomBlogsConflictException(format(USERNAME_ALREADY_TAKEN.getMessage(), user.getUsername()));
        }
    }

    private void checkUserEmail(User user) {
        Optional<User> userFromDbByEmail = userRepository.findByEmail(user.getEmail());
        if (userFromDbByEmail.isPresent()) {
            String decodeEmail = new String(Base64.getDecoder().decode(userFromDbByEmail.get().getEmail().getBytes()));
            throw new CustomBlogsConflictException(format(EMAIL_ALREADY_TAKEN.getMessage(), decodeEmail));
        }
    }

    private ValidationResult validateUserRegister(UserRegisterRequest user) {
        return isNameEqualsNull()
                .and(isValidNameLength())
                .and(isValidName())
                .and(isEmailEqualsToNull())
                .and(isValidEmail())
                .and(isPasswordEqualsToNull())
                .and(isValidPasswordLength())
                .and(isValidPassword())
                .apply(user);
    }

    private ValidationResult validateUserLogin(UserLoginRequest user) {
        return isEmailLoginEqualsToNull()
                .and(isValidLoginEmail())
                .and(isPasswordLoginEqualsToNull())
                .and(isValidLoginPasswordLength())
                .and(isValidLoginPassword())
                .apply(user);
    }
}