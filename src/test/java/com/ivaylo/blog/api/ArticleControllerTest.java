package com.ivaylo.blog.api;

import com.ivaylo.blog.BlogApplication;
import com.ivaylo.blog.services.ArticleService;
import com.ivaylo.blog.services.AuthService;
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
public class ArticleControllerTest {
    private static final String BASE_PATH = "http://localhost:8080/api/v1/articles";
    private static final String USERNAME = "ivo";
    private static final String BLOG_TITLE = "title";
    private static final String SESSION_ID_HEADER_NAME = "session-id";
    private static final String SESSION_ID_VALUE = "e097dec8-2892-45fb-ad7d-62274ce4a304";
    @MockBean
    private AuthService authService;
    @MockBean
    private ArticleService articleService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenWhenGetAllArticlesThenReturnArticlesStatus200Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isLoginUser(SESSION_ID_VALUE);
        verify(articleService, times(1)).getAllArticles();
    }

    @Test
    public void givenWhenGetAllArticlesThenReturnArticlesStatus404NotFound() throws Exception {
        when(articleService.getAllArticles()).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenWhenGetAllArticlesThenReturnArticlesStatus403Forbidden() throws Exception {
        when(authService.isLoginUser(any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(articleService, never()).getAllArticles();
    }

    @Test
    public void givenBlogTitleWhenGetBlogArticlesThenReturnArticlesStatus200Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/blog/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isLoginUser(SESSION_ID_VALUE);
        verify(articleService, times(1)).getAllBlogArticles(BLOG_TITLE);
    }

    @Test
    public void givenBlogTitleWhenGetBlogArticlesThenReturnArticlesStatus404NotFound() throws Exception {
        when(articleService.getAllBlogArticles(BLOG_TITLE)).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/blog/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenBlogTitleWhenGetBlogArticlesThenReturnArticlesStatus403Forbidden() throws Exception {
        when(authService.isLoginUser(any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/blog/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(articleService, never()).getAllBlogArticles(BLOG_TITLE);
    }

    @Test
    public void givenUsernameWhenGetAllUserArticlesThenReturnArticlesStatus200Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/user/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isLoginUser(SESSION_ID_VALUE);
        verify(articleService, times(1)).getAllUserArticles(USERNAME);
    }

    @Test
    public void givenUsernameWhenGetAllUserArticlesThenReturnArticlesStatus404NotFound() throws Exception {
        when(articleService.getAllUserArticles(USERNAME)).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/user/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUsernameWhenGetAllUserArticlesThenReturnArticlesStatus403Forbidden() throws Exception {
        when(authService.isLoginUser(any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/user/" + USERNAME)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(articleService, never()).getAllUserArticles(USERNAME);
    }
}
