package com.ivaylo.blog.controllers;

import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.services.interfaces.IArticleService;
import com.ivaylo.blog.services.interfaces.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ivaylo.blog.utility.BlogsConstants.*;


@RestController
@RequestMapping("api/v1/articles/")
public class ArticleController {
    @Autowired
    private IArticleService articleService;
    @Autowired
    private IAuthService authService;

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles(@RequestHeader(SESSION_ID) String sessionId){
        authService.isLoginUser(sessionId);
        return new ResponseEntity<>(articleService.getAllArticles(), HttpStatus.OK);
    }
    @GetMapping(path = "blog/{blogTitle}")
    public ResponseEntity<List<Article>> getBlogArticles(@PathVariable("blogTitle") String blogTitle,
                                                         @RequestHeader(SESSION_ID) String sessionId){
        authService.isLoginUser(sessionId);
        return new ResponseEntity<>(articleService.getAllBlogArticles(blogTitle), HttpStatus.OK);
    }
    @GetMapping(path = "user/{username}")
    public ResponseEntity<List<Article>> getUserArticles(@PathVariable("username") String username,
                                                         @RequestHeader(SESSION_ID) String sessionId){
        authService.isLoginUser(sessionId);
        return new ResponseEntity<>(articleService.getAllUserArticles(username), HttpStatus.OK);
    }
}
