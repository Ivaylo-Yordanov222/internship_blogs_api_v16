package com.ivaylo.blog.api;

import com.ivaylo.blog.BlogApplication;
import com.ivaylo.blog.services.AuthService;
import com.ivaylo.blog.services.BlogService;
import com.ivaylo.blog.utility.exceptions.CustomBlogsAuthenticationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = BlogApplication.class)
@RunWith(SpringRunner.class)
@DirtiesContext
public class BlogControllerTest {
    private static final String BASE_PATH = "http://localhost:8080/api/v1/blogs";
    private static final String USERNAME = "ivo";
    private static final String SESSION_ID_HEADER_NAME = "session-id";
    private static final String SESSION_ID_VALUE = "e097dec8-2892-45fb-ad7d-62274ce4a304";
    private static final Long BLOG_ID = 1L;
    @MockBean
    private BlogService blogService;
    @MockBean
    private AuthService authService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenBlogIdWhenGetBlogThenReturnBlogStatus200Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isLoginUser(SESSION_ID_VALUE);
        verify(blogService, times(1)).getBlog(BLOG_ID);
    }

    @Test
    public void givenBlogIdWhenGetBlogThenReturnBlogStatus404NotFound() throws Exception {
        when(blogService.getBlog(BLOG_ID)).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenBlogIdWhenGetBlogThenReturnBlogStatus403Forbidden() throws Exception {
        when(authService.isLoginUser(any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(blogService, never()).getBlog(BLOG_ID);
    }

    @Test
    public void givenWhenGetAllBlogsThenReturnBlogsStatus200Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isLoginUser(SESSION_ID_VALUE);
        verify(blogService, times(1)).getAllBlogs();
    }

    @Test
    public void givenWhenGetAllBlogsThenReturnBlogsStatus404NotFound() throws Exception {
        when(blogService.getAllBlogs()).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenWhenGetAllBlogsThenReturnBlogsStatus403Forbidden() throws Exception {
        when(authService.isLoginUser(any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(blogService, never()).getAllBlogs();
    }

    @Test
    public void givenUsernameWhenGetUserBlogsThenReturnBlogsStatus200Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isLoginUser(SESSION_ID_VALUE);
        verify(blogService, times(1)).getUserBlogs(USERNAME);
    }

    @Test
    public void givenUsernameWhenGetUserBlogsThenReturnBlogsStatus404NotFound() throws Exception {
        when(blogService.getUserBlogs(USERNAME)).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void givenUsernameWhenGetUserBlogsThenReturnBlogsStatus403Forbidden() throws Exception {
        when(authService.isLoginUser(any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(blogService, never()).getUserBlogs(USERNAME);
    }
}
