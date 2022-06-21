package com.ivaylo.blog.services;

import com.ivaylo.blog.factories.BlogFactory;
import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.Image;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.BlogRequest;
import com.ivaylo.blog.repositories.BlogRepository;
import com.ivaylo.blog.utility.exceptions.CustomBlogsConflictException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ivaylo.blog.utility.BlogsUtilityMethods.getSlug;
import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@SpringBootTest
public class BlogServiceTest {
    private static final Blog blog = new Blog();

    private static final BlogRequest blogRequest = new BlogRequest();
    private static final long blogId = 1L;
    private static final String blogTitle = "java";
    private static final String username = "ivo";

    private static final User user = new User();

    @InjectMocks
    private BlogService blogService;
    @Mock
    private BlogFactory blogFactory;
    @Mock
    private BlogRepository blogRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;


    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        setUser();
        setupBlogRequest();
    }



    @Test
    public void givenBlogIdWhenGetBlogThenReturnBlog() {
        blog.setId(blogId);
        when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
        blogService.getBlog(blogId);
    }

    @Test
    public void givenBlogIdWhenGetBlogReturnOptionalBlogThenReturnThrowIllegalStateException() {
        blog.setId(blogId);
        when(blogRepository.findById(blogId)).thenReturn(Optional.empty());
        try {
            blogService.getBlog(blogId);
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), BLOG_NOT_FOUND.getMessage());
        }
    }

    @Test
    public void givenBlogTitleWhenGetBlogByTitleThenReturnListOfBlogs() {
        blog.setTitle(blogTitle);
        when(blogRepository.findAllBySlug(blogTitle)).thenReturn(List.of(blog));
        blogService.getBlogsByTitle(blogTitle);
    }

    @Test
    public void givenBlogTitleWhenGetBlogByTitleReturnEmptyListThenThrowIllegalStateException() {
        List<Blog> blogs = new ArrayList<>();
        when(blogRepository.findAllBySlug(blogTitle)).thenReturn(blogs);
        try {
            blogService.getBlogsByTitle(blogTitle);
        } catch (IllegalStateException e) {
            assertEquals(blogs.size(), 0L);
        }
    }

    @Test
    public void givenUsernameWhenGetUserBlogsAndUserIsNotPresentThenThrowIllegalStateExceptionWithMessage() {
        when(userService.getUserByUsername(username)).thenReturn(Optional.empty());
        try {
            blogService.getUserBlogs(username);
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), USER_NOT_FOUND.getMessage());
        }
    }

    @Test
    public void givenUsernameWhenGetUserBlogsAndListOfBlogsIsEmptyThenThrowIllegalStateException() {
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        try {
            blogService.getUserBlogs(user.getUsername());
        } catch (IllegalStateException e) {
            assertEquals(user.getBlogs().size(), 0L);
        }
    }

    @Test
    public void givenUsernameWhenGetUserBlogsThenReturnListOfUserBlogs() {
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        blogService.getUserBlogs(user.getUsername());
        assertTrue(user.getBlogs().size() > 0L);
    }

    //Add blog
    @Test
    public void givenNullValueTitleInBlogRequestWhenAddBlogThenThrowCustomBlogsValidationException() {
        blogRequest.setTitle(null);
        try {
            blogService.addBlog(user, blogRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), BLOG_TITLE_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenInvalidLengthValueTitleInBlogRequestWhenAddBlogThenThrowCustomBlogsValidationException() {
        blogRequest.setTitle("aa");
        try {
            blogService.addBlog(user, blogRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), BLOG_TITLE_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenInvalidValueTitleInBlogRequestWhenAddBlogThenThrowCustomBlogsValidationException() {
        blogRequest.setTitle("a@%$Y$a");
        try {
            blogService.addBlog(user, blogRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), BLOG_TITLE_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenTitleAndTitleAlreadyExistInUserBlogsWhenAddBlogThenThrowCustomBlogsConflictException() {
        blogRequest.setTitle(blogTitle);
        try {
            blogService.addBlog(user, blogRequest);
        } catch (CustomBlogsConflictException e) {
            assertEquals(e.getMessage(), BLOG_NAME_ALREADY_EXIST.getMessage());
        }
    }


    @Test
    public void givenBlogRequestWhenAddBlogThenVerifyBlogRepositoryIsInvokedAndSave() {
        when(blogFactory.assembleBlog(blogRequest.getTitle(), getSlug(blogRequest.getTitle()), user)).thenReturn(blog);
        blogService.addBlog(user, blogRequest);
        verify(blogRepository, times(1)).save(blog);
    }

    //Update Blog
    @Test
    public void givenNullValueTitleInBlogRequestWhenUpdateThenThrowCustomBlogsValidationException() {
        blogRequest.setTitle(null);
        try {
            blogService.updateBlog(user, blogId, blogRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), BLOG_TITLE_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenInvalidLengthValueTitleInBlogRequestWhenUpdateThenThrowCustomBlogsValidationException() {
        blogRequest.setTitle("aa");
        try {
            blogService.updateBlog(user, blogId, blogRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), BLOG_TITLE_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenInvalidValueTitleInBlogRequestWhenUpdateThenThrowCustomBlogsValidationException() {
        blogRequest.setTitle("a@%$Y$a");
        try {
            blogService.updateBlog(user, blogId, blogRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), BLOG_TITLE_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenBlogIdDoNotExistWhenUpdateBlogThenThrowIllegalStateException() {
        blogRequest.setTitle("java two");
        try {
            blogService.updateBlog(user, 2L, blogRequest);
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), BLOG_NOT_FOUND.getMessage());
        }
    }

    @Test
    public void givenTitleAlreadyExistInUserBlogsWhenUpdateBlogThenThrowCustomBlogsConflictException() {
        blogRequest.setTitle("java");
        try {
            blogService.updateBlog(user, blogId, blogRequest);
        } catch (CustomBlogsConflictException e) {
            assertEquals(e.getMessage(), BLOG_NAME_ALREADY_EXIST.getMessage());
        }
    }

    @Test
    public void givenTitleWhenUpdateBlogThenSaveIt() {
        blogRequest.setTitle("java two");
        when(blogRepository.save(blog)).thenReturn(blog);
        blogService.updateBlog(user, blogId, blogRequest);
        verify(blogRepository, times(1)).save(blog);
    }

    //Delete blog
    @Test
    public void givenIdWhenDeleteBlogThenThrowIllegalStateException() {
        try {
            blogService.deleteBlog(user, 2L);
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), BLOG_NOT_FOUND.getMessage());
        }
    }

    @Test
    public void givenIdWhenDeleteBlogThenInvokesTwiceImageServiceDeleteByImageName() {
        blogService.deleteBlog(user, blogId);
        verify(imageService, times(2)).delete("someName.jpg");
    }

    @Test
    public void givenIdWhenDeleteBlogThenDeleteOnceBlog() {
        blogService.deleteBlog(user, blogId);
        verify(blogRepository, times(1)).delete(blog);
    }

    @Test
    public void givenTitleWhenFindSearchedBlogByTitleThenThrowIllegalStateException() {
        try {
            blogService.findSearchedBlogByTitle(user, "javas");
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), BLOG_NOT_FOUND.getMessage());
        }
    }

    @Test
    public void givenTitleWhenFindSearchedBlogByTitleThenReturnBlog() {

        Blog blog1 = blogService.findSearchedBlogByTitle(user, blogTitle);
        assertEquals(blog1.getTitle(), blog.getTitle());
    }

    private void setUser() {
        user.setUsername(username);
        user.setEmail("ivo@gmail.com");
        user.setPassword("1234");
        user.setBlogs(new ArrayList<>());
        blog.setId(blogId);
        blog.setTitle(blogTitle);
        blog.setSlug(getSlug(blogTitle));
        blog.setUser(user);
        blog.setArticles(new ArrayList<>());
        Article article1 = new Article("java one", "bla bla", "java-one", blog);
        Article article2 = new Article("java two", "bla2 bla2", "java-two", blog);
        Image image1 = new Image();
        Image image2 = new Image();
        image1.setImageName("someName.jpg");
        image2.setImageName("someName.jpg");
        article1.setImage(image1);
        article2.setImage(image2);
        blog.getArticles().add(article1);
        blog.getArticles().add(article2);
        user.getBlogs().add(blog);
    }
    private void setupBlogRequest() {
        blogRequest.setTitle("java four");
    }
}

