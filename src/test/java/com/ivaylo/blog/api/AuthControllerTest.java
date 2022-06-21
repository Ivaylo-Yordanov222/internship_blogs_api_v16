package com.ivaylo.blog.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivaylo.blog.BlogApplication;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.UserLoginRequest;
import com.ivaylo.blog.models.UserRegisterRequest;
import com.ivaylo.blog.repositories.UserRepository;
import com.ivaylo.blog.services.AuthService;
import com.ivaylo.blog.utility.exceptions.CustomBlogsAuthenticationException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsConflictException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = BlogApplication.class)
@RunWith(SpringRunner.class)
@DirtiesContext
public class AuthControllerTest {
    private static final String BASE_PATH = "http://localhost:8080/api/v1";
    private static final User USER = new User();
    private static final String SESSION_ID_HEADER_NAME = "session-id";
    private static final String SESSION_ID_VALUE = "e097dec8-2892-45fb-ad7d-62274ce4a304";
    private static final String USERNAME = "ivo";
    private static final String EMAIL = "ivo@gmail.com";
    private static final String PASSWORD = "1234";
    private static final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private AuthService authService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Captor
    private ArgumentCaptor<UserRegisterRequest> registerCaptor;
    @Captor
    private ArgumentCaptor<UserLoginRequest> loginCaptor;

    @Test
    public void givenValidUserRegisterRequestWhenRegisterThenReturn201Created() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/register")
                        .content(mapper.writeValueAsString(buildUserRegisterRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(authService, times(1)).registerNewUser(registerCaptor.capture());
        assertEquals(USERNAME, registerCaptor.getValue().getUsername());
    }

    @Test
    public void givenValidUserRegisterRequestAndNameOrEmailAlreadyExistWhenRegisterThenThrowCustomBlogsConflictException() throws Exception {
        when(authService.registerNewUser(any())).thenThrow(CustomBlogsConflictException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/register")
                        .content(mapper.writeValueAsString(buildUserRegisterRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void givenInvalidUserRegisterRequestWhenRegisterThenReturn400BadRequest() throws Exception {
        when(authService.registerNewUser(any())).thenThrow(CustomBlogsValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/register")
                        .content(mapper.writeValueAsString(buildUserRegisterRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void givenValidUserLoginRequestWhenLoginThenReturn200Ok() throws Exception {
        when(authService.login(any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/login")
                        .content(mapper.writeValueAsString(buildUserLoginRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).login(loginCaptor.capture());
        assertEquals(EMAIL, loginCaptor.getValue().getEmail());
    }

    @Test
    public void givenValidUserLoginRequestAndUserAlreadyLoggedInWhenRegisterThenThrowCustomBlogsConflictException() throws Exception {
        when(authService.login(any())).thenThrow(CustomBlogsConflictException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/login")
                        .content(mapper.writeValueAsString(buildUserRegisterRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void givenInvalidUserLoginRequestWhenLoginThenReturn400BadRequest() throws Exception {
        when(authService.login(any())).thenThrow(CustomBlogsValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/login")
                        .content(mapper.writeValueAsString(buildUserLoginRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameLogoutRequestWhenLogoutThenReturn200Ok() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/logout/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isProfileOwner(any(), any());
        verify(authService, times(1)).logout(USER);
    }

    @Test
    public void givenInvalidUserLogoutRequestWhenLogoutThenReturn403Forbidden() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/auth/logout/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(authService, never()).logout(any());
    }

    private UserRegisterRequest buildUserRegisterRequest() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername(USERNAME);
        userRegisterRequest.setEmail(EMAIL);
        userRegisterRequest.setPassword(PASSWORD);
        return userRegisterRequest;
    }

    private UserLoginRequest buildUserLoginRequest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(EMAIL);
        userLoginRequest.setPassword(PASSWORD);
        return userLoginRequest;
    }
}

