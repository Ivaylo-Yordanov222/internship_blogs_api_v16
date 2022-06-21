package com.ivaylo.blog.services;

import com.ivaylo.blog.factories.UserFactory;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.UserLoginRequest;
import com.ivaylo.blog.models.UserRegisterRequest;
import com.ivaylo.blog.repositories.UserRepository;
import com.ivaylo.blog.utility.exceptions.CustomBlogsAuthenticationException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsConflictException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {
    private static final UserRegisterRequest USER_REGISTRATION = new UserRegisterRequest();
    private static final UserLoginRequest USER_LOGIN = new UserLoginRequest();
    private static final User USER = new User();
    private static final  String sessionId = UUID.randomUUID().toString();

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFactory userFactory;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        setupUserRegisterRequest();
        setupUserLoginRequest();
        setUser();
    }

    //RegisterNewUser
    @Test
    public void givenNullValueUsernameInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setUsername(null);
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), NAME_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenEmptyValueUsernameInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setUsername("");
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), NAME_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenInvalidValueUsernameInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setUsername(" ivo");
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), NAME_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenNullValueEmailInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setEmail(null);
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), EMAIL_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenEmptyValueEmailInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setEmail("");
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), EMAIL_IS_NOT_VALID.getMessage());
        }
    }

    @Test
    public void givenInvalidValueEmailInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setEmail("email@@abc.net");
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), EMAIL_IS_NOT_VALID.getMessage());
        }
    }

    @Test
    public void givenNullValuePasswordInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setPassword(null);
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), PASSWORD_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenEmptyValuePasswordInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setPassword("");
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), PASSWORD_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenInvalidValuePasswordInUserRegisterRequestWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        USER_REGISTRATION.setPassword(" adsa");
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), PASSWORD_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenUsernameAlreadyExistInRepoWhenRegisterNewUserThenThrowCustomBlogsConflictException() {
        when(userFactory.assembleUser(USER_REGISTRATION.getUsername(),USER_REGISTRATION.getEmail(),USER_REGISTRATION.getPassword()))
                .thenReturn(USER);
        when(userRepository.findByUsername(USER_REGISTRATION.getUsername())).thenReturn(Optional.of(USER));
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsConflictException e) {
            assertEquals(e.getMessage(), String.format(USERNAME_ALREADY_TAKEN.getMessage(), USER_REGISTRATION.getUsername()));
        }
    }

    @Test
    public void givenEmailAlreadyExistInRepoWhenRegisterNewUserThenThrowCustomBlogsValidationException() {
        when(userFactory.assembleUser(USER_REGISTRATION.getUsername(),USER_REGISTRATION.getEmail(),USER_REGISTRATION.getPassword()))
                .thenReturn(USER);
        when(userRepository.findByEmail(USER_REGISTRATION.getEmail())).thenReturn(Optional.of(USER));
        try {
            authService.registerNewUser(USER_REGISTRATION);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), String.format(EMAIL_ALREADY_TAKEN.getMessage(), USER_REGISTRATION.getEmail()));
        }
    }

    @Test
    public void givenValidUserRegisterRequestWhenRegisterNewUserThenReturnUserWithBcryptPassword() {
        when(userFactory.assembleUser(USER_REGISTRATION.getUsername(),USER_REGISTRATION.getEmail(),USER_REGISTRATION.getPassword()))
                .thenReturn(USER);
        when(userRepository.save(USER)).thenReturn(USER);
        authService.registerNewUser(USER_REGISTRATION);
        String bcryptPassword = BCrypt.hashpw(USER_REGISTRATION.getPassword(), BCrypt.gensalt(12));
        USER.setPassword(bcryptPassword);
        assertEquals(USER.getPassword(), bcryptPassword);
    }
    @Test
    public void givenValidUserRegisterRequestWhenRegisterNewUserThenReturnUser(){
        when(userFactory.assembleUser(USER_REGISTRATION.getUsername(),USER_REGISTRATION.getEmail(),USER_REGISTRATION.getPassword()))
                .thenReturn(USER);
        authService.registerNewUser(USER_REGISTRATION);
        verify(userRepository, times(1)).save(USER);
    }

    //login
    @Test
    public void givenNullValueEmailInUserRegisterRequestWhenLoginThenThrowCustomBlogsValidationException() {
        USER_LOGIN.setEmail(null);
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), EMAIL_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenEmptyValueEmailInUserRegisterRequestWhenLoginThenThrowCustomBlogsValidationException() {
        USER_LOGIN.setEmail("");
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), EMAIL_IS_NOT_VALID.getMessage());
        }
    }

    @Test
    public void givenInvalidValueEmailInUserRegisterRequestWhenLoginThenThrowCustomBlogsValidationException() {
        USER_LOGIN.setEmail("email@@abc.net");
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), EMAIL_IS_NOT_VALID.getMessage());
        }
    }

    @Test
    public void givenNullValuePasswordInUserRegisterRequestWhenLoginThenThrowCustomBlogsValidationException() {
        USER_LOGIN.setPassword(null);
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), PASSWORD_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenEmptyValuePasswordInUserRegisterRequestWhenLoginThenThrowCustomBlogsValidationException() {
        USER_LOGIN.setPassword("");
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), PASSWORD_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenInvalidValuePasswordInUserRegisterRequestWhenLoginThenThrowCustomBlogsValidationException() {
        USER_LOGIN.setPassword(" adsa");
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), PASSWORD_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenValidUserLoginRequestCredentialNotMatchWhenLoginThenThrowsCustomBlogsValidationException() {
        when(userRepository.findByEmail(USER_LOGIN.getEmail())).thenReturn(Optional.empty());
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), INVALID_CREDENTIALS.getMessage());
        }
    }

    @Test
    public void givenValidUserLoginRequestAndCredentialBcryptNotMatchBcryptWhenLoginThenThrowsCustomBlogsValidationException() {
        String bcryptPassword = BCrypt.hashpw(USER_LOGIN.getPassword(), BCrypt.gensalt(12));
        String wrongPassword = BCrypt.hashpw("12345", BCrypt.gensalt(12));
        USER.setPassword(bcryptPassword);
        USER.setLogin(true);
        when(userRepository.findByEmail(USER_LOGIN.getEmail())).thenReturn(Optional.of(USER));

        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsValidationException e) {
            assertNotEquals(USER.getPassword(), wrongPassword);
        }
    }

    @Test
    public void givenValidUserLoginRequestAndCredentialAndUserAlreadyLoggedInMatchBcryptWhenLoginThenThrowsCustomBlogsConflictException() {
        String bcryptPassword = BCrypt.hashpw(USER_LOGIN.getPassword(), BCrypt.gensalt(12));
        USER.setPassword(bcryptPassword);
        USER.setLogin(true);
        String encodeEmail = Base64.getEncoder().encodeToString(USER.getEmail().getBytes());
        when(userRepository.findByEmail(encodeEmail)).thenReturn(Optional.of(USER));
        try {
            authService.login(USER_LOGIN);
        } catch (CustomBlogsConflictException e) {
            assertEquals(e.getMessage(), USER_ALREADY_LOGGED_IN.getMessage());
        }
    }

    @Test
    public void givenValidUserLoginRequestAndCredentialsWhenLoginThenReturnUserAndSaveIt() {
        String bcryptPassword = BCrypt.hashpw(USER_LOGIN.getPassword(), BCrypt.gensalt(12));
        USER.setPassword(bcryptPassword);
        String encodeEmail = Base64.getEncoder().encodeToString(USER_LOGIN.getEmail().getBytes());
        when(userRepository.findByEmail(encodeEmail)).thenReturn(Optional.of(USER));
        authService.login(USER_LOGIN);
        verify(userRepository, times(1)).save(USER);
    }

    @Test
    public void givenValidUserLoginRequestAndCredentialsWhenLoginThenReturnUserWithSetSessionIdAndLoginToTrue() {
        String bcryptPassword = BCrypt.hashpw(USER_LOGIN.getPassword(), BCrypt.gensalt(12));
        String encodeEmail = Base64.getEncoder().encodeToString(USER_LOGIN.getEmail().getBytes());
        User dbUser = new User(USER_REGISTRATION.getUsername(), USER_REGISTRATION.getEmail(),bcryptPassword);
        when(userRepository.findByEmail(encodeEmail)).thenReturn(Optional.of(dbUser));

        authService.login(USER_LOGIN);
        dbUser.setSessionId(sessionId);
        dbUser.setLogin(true);
        assertTrue(dbUser.isLogin());
        assertEquals(dbUser.getSessionId(), sessionId);
    }

    //IsLoginUser
    @Test(expected = CustomBlogsAuthenticationException.class)
    public void givenInvalidSessionIdWhenIsLoginUserThrowCustomBlogsAuthenticationException() {
        when(userRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        authService.isLoginUser(sessionId);
    }
    //IsProfileOwner
    @Test(expected = CustomBlogsAuthenticationException.class)
    public void givenInvalidSessionIdWhenIsProfileOwnerThrowCustomBlogsAuthenticationException() {
        when(userRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        authService.isProfileOwner(sessionId, USER_REGISTRATION.getUsername());
    }
    @Test(expected = CustomBlogsAuthenticationException.class)
    public void givenInvalidUsernameAndLoginValueIsFalseWhenIsProfileOwnerThrowCustomBlogsAuthenticationException() {
        when(userRepository.findBySessionId(sessionId)).thenReturn(Optional.of(USER));
        authService.isProfileOwner(sessionId, "gogo");
        assertFalse(USER.isLogin());
    }
    //logout
    @Test
    public void givenProfileOwnerWhenLogoutThenSetSessionIdToNullAndLoginToFalse(){
        USER.setSessionId(sessionId);
        USER.setLogin(true);
        authService.logout(USER);
        assertFalse(USER.isLogin());
        assertNull(USER.getSessionId());
    }
    @Test
    public void givenProfileOwnerWhenLogoutThenVerifyUserRepoSaveIsInvokedOnce(){
        authService.logout(USER);
        verify(userRepository, times(1)).save(USER);
    }

    private void setupUserRegisterRequest() {
        USER_REGISTRATION.setUsername("ivo");
        USER_REGISTRATION.setEmail("ivo@gmail.com");
        USER_REGISTRATION.setPassword("1234");
    }

    private void setupUserLoginRequest() {
        USER_LOGIN.setEmail("ivo@gmail.com");
        USER_LOGIN.setPassword("1234");
    }

    private void setUser(){
        USER.setUsername("ivo");
        USER.setEmail("ivo@gmail.com");
        USER.setPassword("1234");
    }
}
