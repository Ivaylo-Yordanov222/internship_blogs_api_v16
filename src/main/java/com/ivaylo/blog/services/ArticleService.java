package com.ivaylo.blog.services;

import com.ivaylo.blog.factories.ArticleFactory;
import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.Image;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.ArticleRequest;
import com.ivaylo.blog.repositories.ArticleRepository;
import com.ivaylo.blog.repositories.ImageRepository;
import com.ivaylo.blog.services.interfaces.IArticleService;
import com.ivaylo.blog.services.interfaces.IBlogService;
import com.ivaylo.blog.services.interfaces.IImageService;
import com.ivaylo.blog.services.interfaces.IUserService;
import com.ivaylo.blog.utility.enums.ValidationResult;
import com.ivaylo.blog.utility.exceptions.CustomBlogsConflictException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ivaylo.blog.utility.BlogsUtilityMethods.getSlug;
import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.SUCCESS;
import static com.ivaylo.blog.utility.validators.ArticleValidator.*;

@Service
public class ArticleService implements IArticleService {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleFactory articleFactory;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private IBlogService blogService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IImageService imageService;

    @Value("${blogs.application.domain-image-resource-path}")
    private String DOMAIN_IMAGE_PATH;
    @Override
    public List<Article> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        checkArticlesSize(articles);
        return articles;
    }

    @Override
    public List<Article> getAllBlogArticles(String blogTitle) {
        List<Blog> blogs = blogService.getBlogsByTitle(blogTitle);
        List<Article> articles = blogs.stream().
                map(Blog::getArticles).toList()
                .stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
        checkArticlesSize(articles);
        return articles;
    }

    @Override
    public List<Article> getAllUserArticles(String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (!user.isPresent()) {
            throw new IllegalStateException(USER_NOT_FOUND.getMessage());
        }
        List<Article> articles = user.get().getBlogs().stream().
                map(Blog::getArticles).toList()
                .stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
        checkArticlesSize(articles);
        return articles;
    }

    @Override
    public Article addArticle(User profileOwner, String blogTitle, ArticleRequest articleModel) {
        checkValidatedArticleModel(articleModel);
        Blog blog = blogService.findSearchedBlogByTitle(profileOwner, blogTitle);
        String slug = getSlug(articleModel.getTitle());
        checkForDuplicateArticleNames(blog, slug);
        Article article = articleFactory.assembleArticle(articleModel.getTitle(), articleModel.getContent(), slug, blog);
        imageService.upload(articleModel, profileOwner.getId());
        Image imageToUpload = articleFactory.assembleImage(articleModel.getImageAssembledName(), DOMAIN_IMAGE_PATH + articleModel.getImageAssembledName());
        imageToUpload.setArticle(article);
        articleRepository.save(article);
        imageRepository.save(imageToUpload);
        article.setImage(imageToUpload);
        return article;
    }

    @Override
    public Article updateArticle(User profileOwner, Long articleId, ArticleRequest articleModel) {
        checkValidatedArticleModel(articleModel);
        Article article = findSearchedArticleById(profileOwner, articleId);
        String slug = getSlug(articleModel.getTitle());
        checkForDuplicateArticleNames(article.getBlog(), slug);
        imageService.delete(article.getImage().getImageName());
        imageService.upload(articleModel, profileOwner.getId());
        updateArticleValues(articleModel, article, slug);
        return article;
    }


    @Override
    public void deleteArticle(User profileOwner, Long articleId) {
        Article articleToDelete = findSearchedArticleById(profileOwner, articleId);
        imageService.delete(articleToDelete.getImage().getImageName());
        articleRepository.delete(articleToDelete);
    }

    private void updateArticleValues(ArticleRequest articleModel, Article article, String slug) {
        article.setTitle(articleModel.getTitle());
        article.setSlug(slug);
        article.setContent(articleModel.getContent());
        article.getImage().setImageName(articleModel.getImageAssembledName());
        article.getImage().setUrl(DOMAIN_IMAGE_PATH + articleModel.getImageAssembledName());
        articleRepository.save(article);
    }

    private void checkValidatedArticleModel(ArticleRequest articleModel) {
        ValidationResult result = validateArticleModel(articleModel);
        if (result != SUCCESS) {
            throw new CustomBlogsValidationException(result.getMessage());
        }
    }

    private void checkArticlesSize(List<Article> articles) {
        if (articles.size() <= 0) {
            throw new IllegalStateException(NO_ARTICLES_FOUND.getMessage());
        }
    }

    private void checkForDuplicateArticleNames(Blog blog, String slug) {
        Optional<Article> duplicateArticle = blog.getArticles().stream()
                .filter(a -> a.getSlug().equals(slug))
                .findFirst();
        if (duplicateArticle.isPresent()) {
            throw new CustomBlogsConflictException(ARTICLE_NAME_ALREADY_EXIST.getMessage());
        }
    }

    private Article findSearchedArticleById(User profileOwner, Long articleId) {
        return profileOwner.getBlogs().stream()
                .map(Blog::getArticles)
                .flatMap(Collection::stream)
                .filter(a -> a.getId().equals(articleId))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalStateException(ARTICLE_NOT_FOUND.getMessage());
                });
    }

    private ValidationResult validateArticleModel(ArticleRequest articleModel) {
        return isArticleTitleEqualsToNull()
                .and(isValidArticleTitleLength())
                .and(isValidArticleTitle())
                .and(isArticleContentEqualsToNull())
                .and(isValidArticleContentLength())
                .and(isValidArticleContent())
                .and(isImageFileEqualsToNull())
                .and(isValidImageFile())
                .apply(articleModel);
    }
}