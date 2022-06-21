package com.ivaylo.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivaylo.blog.BlogApplication;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.BlogRequest;
import com.ivaylo.blog.repositories.ArticleRepository;
import com.ivaylo.blog.repositories.BlogRepository;
import com.ivaylo.blog.repositories.ImageRepository;
import com.ivaylo.blog.services.*;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = BlogApplication.class)
@RunWith(SpringRunner.class)
@DirtiesContext
public class UserControllerTest {
    private static final String BASE_PATH = "http://localhost:8080/api/v1/";
    private static final String USERNAME = "ivo";
    private static final User USER = new User();
    private static final String SESSION_ID_HEADER_NAME = "session-id";
    private static final String SESSION_ID_VALUE = "e097dec8-2892-45fb-ad7d-62274ce4a304";
    private static final Long BLOG_ID = 1L;
    private static final String BLOG_TITLE = "Java";
    private static final Long ARTICLE_ID = 1L;

    private static final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private ArticleService articleService;
    @MockBean
    private AuthService authService;
    @MockBean
    private BlogService blogService;

    @MockBean
    private ImageService imageService;
    @MockBean
    private BlogRepository blogRepository;
    @MockBean
    private ArticleRepository articleRepository;
    @MockBean
    private ImageRepository imageRepository;

    @Captor
    private ArgumentCaptor<BlogRequest> blogCaptor;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenValidUsernameWhenAddBlogThenReturn201Created() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/blog")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(authService, times(1)).isProfileOwner(any(), any());
        verify(blogService, times(1)).addBlog(any(), blogCaptor.capture());
        assertEquals("blog title", blogCaptor.getValue().getTitle());
    }

    @Test
    public void givenValidUsernameAndBlogNameAlreadyExistWhenAddBlogThenReturn409Conflict() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(blogService.addBlog(any(), any())).thenThrow(CustomBlogsConflictException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/blog")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(blogRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenAddBlogThenReturn404NotFound() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(blogService.addBlog(any(), any())).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/blog")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(blogRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenAddBlogThenReturn400BadRequest() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(blogService.addBlog(any(), any())).thenThrow(CustomBlogsValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/blog")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(blogRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenAddBlogThenReturn403Forbidden() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/blog")
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(blogService, never()).addBlog(any(), any());
    }

    @Test
    public void givenValidUsernameWhenUpdateBlogThenReturn200Ok() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isProfileOwner(any(), any());
        verify(blogService, times(1)).updateBlog(any(), eq(BLOG_ID), blogCaptor.capture());
        assertEquals("blog title", blogCaptor.getValue().getTitle());
    }

    @Test
    public void givenValidUsernameAndBlogTitleAlreadyExistWhenUpdateBlogThenReturn409Conflict() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(blogService.updateBlog(any(), any(), any())).thenThrow(CustomBlogsConflictException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(blogRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenUpdateBlogThenReturn404NotFound() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(blogService.updateBlog(any(), any(), any())).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(blogRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenUpdateBlogThenReturn400BadRequest() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(blogService.updateBlog(any(), any(), any())).thenThrow(CustomBlogsValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(blogRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenUpdateBlogThenReturn403Forbidden() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .content(mapper.writeValueAsString(buildBlogRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(blogService, never()).updateBlog(any(), any(), any());
    }

    @Test
    public void givenValidUsernameWhenDeleteBlogThenReturn200Ok() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + USERNAME + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isProfileOwner(any(), any());
        verify(blogService, times(1)).deleteBlog(any(), eq(BLOG_ID));
    }

    @Test
    public void givenValidUsernameWhenDeleteBlogThenReturn403Forbidden() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + USERNAME + "/blog/" + BLOG_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(blogService, never()).deleteBlog(any(), eq(BLOG_ID));
    }

    @Test
    public void givenValidUsernameWhenAddArticleThenReturn201Created() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/article/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(authService, times(1)).isProfileOwner(any(), any());
        verify(articleService, times(1)).addArticle(any(), any(), any());
    }

    @Test
    public void givenValidUsernameWhenAddArticleThenReturn417ExpectationFailed() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.addArticle(any(), any(), any())).thenThrow(MaxUploadSizeExceededException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/article/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameAndArticleNameAlreadyExistWhenAddArticleThenReturn409Conflict() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.addArticle(any(), any(), any())).thenThrow(CustomBlogsConflictException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/article/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenAddArticleThenReturn404NotFound() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.addArticle(any(), any(), any())).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/article/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenAddArticleThenReturn400BadRequest() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.addArticle(any(), any(), any())).thenThrow(CustomBlogsValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/article/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenAddArticleThenReturn403Forbidden() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/article/" + BLOG_TITLE)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(articleService, never()).addArticle(any(), any(), any());
    }

    @Test
    public void givenValidUsernameWhenUpdateArticleThenReturn200Ok() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isProfileOwner(any(), any());
        verify(articleService, times(1)).updateArticle(any(), any(), any());
    }

    @Test
    public void givenValidUsernameWhenUpdateArticleThenReturn417ExpectationFailed() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.updateArticle(any(), any(), any())).thenThrow(MaxUploadSizeExceededException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameAndArticleNameAlreadyExistWhenUpdateArticleThenReturn409Conflict() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.addArticle(any(), any(), any())).thenThrow(CustomBlogsConflictException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenUpdateArticleThenReturn404NotFound() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.updateArticle(any(), any(), any())).thenThrow(IllegalStateException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenUpdateArticleThenReturn400BadRequest() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        when(articleService.updateArticle(any(), any(), any())).thenThrow(CustomBlogsValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(articleRepository, never()).save(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void givenValidUsernameWhenUpdateArticleThenReturn403Forbidden() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(articleService, never()).updateArticle(any(), eq(ARTICLE_ID), any());
    }

    @Test
    public void givenValidUsernameWhenDeleteArticleThenReturn200Ok() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenReturn(USER);
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authService, times(1)).isProfileOwner(any(), any());
        verify(articleService, times(1)).deleteArticle(any(), any());
    }

    @Test
    public void givenValidUsernameWhenDeleteArticleThenReturn403Forbidden() throws Exception {
        when(authService.isProfileOwner(any(), any())).thenThrow(CustomBlogsAuthenticationException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + USERNAME + "/article/" + ARTICLE_ID)
                        .header(SESSION_ID_HEADER_NAME, SESSION_ID_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(articleService, never()).deleteArticle(any(), eq(ARTICLE_ID));
    }

    //WORKS ONLY WITH test_uploads folder and given image inside
    @Test
    public void givenValidImageResourceWhenLoadThenReturn200Ok() throws Exception {
        Path path = Paths.get("test_uploads");
        Path file = path.resolve("1-1655737584676.png");
        Resource resource = new UrlResource(file.toUri());
        when(imageService.load("1-1655737584676.png")).thenReturn(resource);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "files/1-1655737584676.png")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private BlogRequest buildBlogRequest() {
        BlogRequest blogRequest = new BlogRequest();
        blogRequest.setTitle("blog title");
        return blogRequest;
    }
}
