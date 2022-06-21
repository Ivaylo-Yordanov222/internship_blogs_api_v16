package com.ivaylo.blog.services;

import com.ivaylo.blog.factories.ArticleFactory;
import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.Image;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.ArticleRequest;
import com.ivaylo.blog.repositories.ArticleRepository;
import com.ivaylo.blog.repositories.ImageRepository;
import com.ivaylo.blog.utility.exceptions.CustomBlogsConflictException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ArticleServiceTest {
    private static final User user = new User();
    private static final Blog blog1 = new Blog();
    private static final Blog blog2 = new Blog();
    private static final Article article1 = new Article();
    private static final Article article2 = new Article();

    private static final Image image2 = new Image();

    private static final ArticleRequest articleRequest = new ArticleRequest();
    @InjectMocks
    private ArticleService articleService;
    @Mock
    private ArticleFactory articleFactory;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private BlogService blogService;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        setUser();
        setArticleRequest();
    }

    private void setArticleRequest() {
        articleRequest.setTitle("some title");
        articleRequest.setContent("some content");
        articleRequest.setUrl("some url");
    }

    @Test(expected = IllegalStateException.class)
    public void givenWhenGetAllArticlesThenThrowIllegalStateException() {
        List<Article> articles = new ArrayList<>();
        when(articleRepository.findAll()).thenReturn(articles);
        articleService.getAllArticles();
    }

    @Test
    public void givenWhenGetAllArticlesThenReturnArticles() {
        when(articleRepository.findAll()).thenReturn(blog1.getArticles());
        articleService.getAllArticles();
        assertTrue(blog1.getArticles().size() > 0);
    }

    @Test(expected = IllegalStateException.class)
    public void givenBlogTitleWhenGetAllBlogArticlesThrowIllegalStateException() {
        List<Article> articles = new ArrayList<>();
        when(articleService.getAllBlogArticles(blog1.getTitle())).thenReturn(articles);
        articleService.getAllBlogArticles(blog1.getTitle());
    }

    @Test
    public void givenWhenGetAllBlogArticlesThenReturnArticles() {
        when(articleRepository.findAll()).thenReturn(blog1.getArticles());
        articleService.getAllArticles();
        assertTrue(blog1.getArticles().size() > 0);
    }

    @Test(expected = IllegalStateException.class)
    public void givenUsernameWithNoArticlesWhenGetAllUserArticlesThrowIllegalStateException() {
        List<Article> articles = new ArrayList<>();
        when(articleService.getAllUserArticles(user.getUsername())).thenReturn(articles);
        articleService.getAllUserArticles(user.getUsername());
    }

    @Test
    public void givenUsernameOptionalEmptyWhenGetAllUserArticlesThrowIllegalStateExceptionWithMessage() {
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.empty());
        try {
            articleService.getAllUserArticles(user.getUsername());
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), USER_NOT_FOUND.getMessage());
        }
    }

    @Test
    public void givenUsernameWhenGetAllUserArticlesThenReturnArticles() {
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        articleService.getAllUserArticles(user.getUsername());
        assertTrue(blog1.getArticles().size() > 0);
    }

    //Add article
    @Test
    public void givenArticleRequestWithNullValueTittleWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setTitle(null);
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_TITLE_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithEmptyValueTittleWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setTitle("sd");
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_TITLE_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithInvalidValueTittleWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setTitle(" sd");
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_TITLE_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithNullValueContentWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent(null);
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_CONTENT_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithEmptyValueContentWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent("sds");
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_CONTENT_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithInvalidValueContentWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent(" sd~%$@");
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_CONTENT_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenArticleRequestArticleImageFileIsMandatoryWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent("some content");
        articleRequest.setFile(null);
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), IMAGE_FILE_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenArticleRequestImageMustEndWithImageExtensionWhenAddArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent("some content");
        setArticleRequestImage("some_image_name");
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), FILE_IS_NOT_IMAGE.getMessage());
        }
    }

    @Test
    public void givenArticleRequestArticleTitleAlreadyExistWhenAddArticleThenThrowCustomBlogsConflictException() {
        articleRequest.setContent("some content");
        setArticleRequestImage("some name.jpg");
        when(blogService.findSearchedBlogByTitle(user, blog1.getTitle())).thenReturn(blog1);
        try {
            articleService.addArticle(user, blog1.getTitle(), articleRequest);
        } catch (CustomBlogsConflictException e) {
            assertEquals(e.getMessage(), ARTICLE_NAME_ALREADY_EXIST.getMessage());
        }
    }
    @Test
    public void givenValidArticleRequestWhenAddArticleThenInvokesArticleRepoAndImageRepoSaveMethods(){
        articleRequest.setTitle("title");
        setArticleRequestImage("some name.jpg");
        when(blogService.findSearchedBlogByTitle(user, blog1.getTitle())).thenReturn(blog1);
        when(articleFactory.assembleArticle(any(),any(),any(),any())).thenReturn(article2);
        when(articleFactory.assembleImage(any(),any())).thenReturn(image2);
        articleService.addArticle(user, blog1.getTitle(), articleRequest);
        verify(articleRepository, times(1)).save(article2);
        verify(imageRepository, times(1)).save(image2);
    }

    //Update Article
    @Test
    public void givenArticleRequestWithNullValueTittleWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setTitle(null);
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_TITLE_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithEmptyValueTittleWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setTitle("sd");
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_TITLE_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithInvalidValueTittleWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setTitle(" sd");
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_TITLE_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithNullValueContentWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent(null);
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_CONTENT_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithEmptyValueContentWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent("sds");
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_CONTENT_MUST_BE_BETWEEN.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWithInvalidValueContentWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent(" sd~%$@");
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), ARTICLE_CONTENT_MUST_HAVE_THESE_SYMBOLS.getMessage());
        }
    }

    @Test
    public void givenArticleRequestArticleImageFileIsMandatoryWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent("some content");
        articleRequest.setFile(null);
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), IMAGE_FILE_IS_MANDATORY.getMessage());
        }
    }

    @Test
    public void givenArticleRequestImageMustEndWithImageExtensionWhenUpdateArticleThenThrowCustomBlogsValidationException() {
        articleRequest.setContent("some content");
        setArticleRequestImage("some_image_name");
        try {
            articleService.updateArticle(user, article1.getId(), articleRequest);
        } catch (CustomBlogsValidationException e) {
            assertEquals(e.getMessage(), FILE_IS_NOT_IMAGE.getMessage());
        }
    }

    @Test
    public void givenArticleAndArticleIdDoNotExistRequestWhenUpdateArticleThenThrowIllegalStateException() {
        articleRequest.setContent("some content");
        setArticleRequestImage("some name.jpg");

        try {
            articleService.updateArticle(user, 3L, articleRequest);
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), ARTICLE_NOT_FOUND.getMessage());
        }
    }

    @Test
    public void givenArticleRequestArticleTitleAlreadyExistWhenUpdateArticleThenThrowCustomBlogsConflictException() {
        articleRequest.setTitle("some title two");
        articleRequest.setContent("some content");
        setArticleRequestImage("some name.jpg");
        try {
            articleService.updateArticle(user, article2.getId(), articleRequest);
        } catch (CustomBlogsConflictException e) {
            assertEquals(e.getMessage(), ARTICLE_NAME_ALREADY_EXIST.getMessage());
        }
    }

    @Test
    public void givenArticleRequestWhenUpdateArticleThenVerifyImageServiceDeleteInvokeOnceAfterThatInvokeImageServiceUpload() {
        articleRequest.setTitle("some title three");
        articleRequest.setContent("some content");
        articleRequest.setImageAssembledName("some name.jpg");
        setArticleRequestImage("some name.jpg");

        articleService.updateArticle(user, article2.getId(), articleRequest);
        verify(imageService, times(1)).delete(article2.getImage().getImageName());
        verify(imageService, times(1)).upload(articleRequest, user.getId());
        assertEquals(articleRequest.getTitle(), article2.getTitle());
        assertEquals(articleRequest.getContent(), article2.getContent());
    }
    //Delete Article
    @Test
    public void givenArticleIdDoNotExistWhenDeleteArticleThrowIllegalStateException(){
        try{
            articleService.deleteArticle(user,3L);
        }catch (IllegalStateException e){
            assertEquals(e.getMessage(), ARTICLE_NOT_FOUND.getMessage());
        }
    }
    @Test
    public void givenArticleIdWhenDeleteArticleThenInvokeOnceImageServiceDeleteAndThenInvokeOnceArticleRepositoryDelete(){
        articleService.deleteArticle(user, article2.getId());
        verify(imageService, times(1)).delete(image2.getImageName());
        verify(articleRepository, times(1)).delete(article2);
    }
    private void setArticleRequestImage(String imageName) {
        articleRequest.setFile(new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return imageName;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        });
    }

    private void setUser() {
        user.setId(1L);
        user.setUsername("example");
        user.setEmail("example@somemail.com");
        user.setPassword("1234");
        user.setBlogs(new ArrayList<>());
        blog1.setId(1L);
        blog1.setTitle("java");
        blog1.setSlug("java");
        blog1.setArticles(new ArrayList<>());
        blog2.setId(2L);
        blog2.setTitle("java two");
        blog2.setSlug("java-two");
        blog2.setArticles(new ArrayList<>());
        blog1.getArticles().add(article1);
        blog2.getArticles().add(article2);
        article1.setId(1L);
        article1.setTitle("some title");
        article1.setSlug("some-title");
        article1.setContent("some content");
        article1.setBlog(blog1);
        article2.setId(2L);
        article2.setTitle("some title two");
        article2.setSlug("some-title-two");
        article2.setContent("some content 2");
        article2.setBlog(blog2);
        article2.setImage(image2);
        image2.setImageName("some name.jpg");
        image2.setArticle(article2);
        user.getBlogs().add(blog1);
        user.getBlogs().add(blog2);
    }
}
