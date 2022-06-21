package com.ivaylo.blog.controllers;

import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.ArticleRequest;
import com.ivaylo.blog.models.BlogRequest;
import com.ivaylo.blog.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.BlogsConstants.SESSION_ID;

@RestController
@RequestMapping(path = "api/v1/")
public class UserController {
    @Autowired
    private IArticleService articleService;
    @Autowired
    private IAuthService authService;
    @Autowired
    private IBlogService blogService;
    @Autowired
    private IImageService imageService;

    @PostMapping(path = "{username}/blog")
    public ResponseEntity<Blog> addBlock(@RequestBody BlogRequest blog,
                                         @PathVariable("username") String username,
                                         @RequestHeader(SESSION_ID) String sessionId) {
        User profileOwner = authService.isProfileOwner(sessionId, username);
        return new ResponseEntity<>(blogService.addBlog(profileOwner, blog), HttpStatus.CREATED);
    }

    @PutMapping(path = "{username}/blog/{blogId}")
    public ResponseEntity<Blog> updateBlog(@PathVariable("username") String username,
                                           @PathVariable("blogId") Long blogId,
                                           @RequestBody BlogRequest blog,
                                           @RequestHeader(SESSION_ID) String sessionId) {
        User profileOwner = authService.isProfileOwner(sessionId, username);
        return new ResponseEntity<>(blogService.updateBlog(profileOwner, blogId, blog), HttpStatus.OK);
    }

    @DeleteMapping(path = "{username}/blog/{blogId}")
    public ResponseEntity<String> deleteBlog(@PathVariable("username") String username,
                                             @PathVariable("blogId") Long blogId,
                                             @RequestHeader(SESSION_ID) String sessionId) {
        User profileOwner = authService.isProfileOwner(sessionId, username);
        blogService.deleteBlog(profileOwner, blogId);
        return new ResponseEntity<>(String.format(BLOG_WITH_ID_SUCCESSFULLY_DELETED.getMessage(), blogId), HttpStatus.OK);
    }

    @PostMapping(path = "{username}/article/{blogTitle}")
    public ResponseEntity<Article> addArticle(ArticleRequest articleModel,
                                              @PathVariable("username") String username,
                                              @PathVariable("blogTitle") String blogTitle,
                                              @RequestHeader(SESSION_ID) String sessionId) {
        User profileOwner = authService.isProfileOwner(sessionId, username);
        return new ResponseEntity<>(articleService.addArticle(profileOwner, blogTitle, articleModel), HttpStatus.CREATED);
    }

    @PutMapping(path = "{username}/article/{articleId}")
    public ResponseEntity<Article> updateArticle(ArticleRequest articleModel,
                                                 @PathVariable("username") String username,
                                                 @PathVariable("articleId") Long articleId,
                                                 @RequestHeader(SESSION_ID) String sessionId) {
        User profileOwner = authService.isProfileOwner(sessionId, username);
        return new ResponseEntity<>(articleService.updateArticle(profileOwner, articleId, articleModel), HttpStatus.OK);
    }

    @DeleteMapping(path = "{username}/article/{articleId}")
    public ResponseEntity<String> deleteArticle(@PathVariable("username") String username,
                                                @PathVariable("articleId") Long articleId,
                                                @RequestHeader(SESSION_ID) String sessionId) {
        User profileOwner = authService.isProfileOwner(sessionId, username);
        articleService.deleteArticle(profileOwner, articleId);
        return new ResponseEntity<>(String.format(ARTICLE_WITH_ID_SUCCESSFULLY_DELETED.getMessage(), articleId), HttpStatus.OK);
    }

    @GetMapping(value = "/files/{filename:.+}",
            produces = {MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = imageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
