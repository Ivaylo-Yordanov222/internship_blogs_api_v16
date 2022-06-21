package com.ivaylo.blog.controllers;

import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.services.interfaces.IAuthService;
import com.ivaylo.blog.services.interfaces.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.ivaylo.blog.utility.BlogsConstants.SESSION_ID;

@RestController
@RequestMapping("api/v1/blogs/")
public class BlogController {
    @Autowired
    private IBlogService blogService;
    @Autowired
    private IAuthService authService;

    @GetMapping(path = "blog/{id}")
    public ResponseEntity<Blog> getBlog(@PathVariable("id") Long id,
                                        @RequestHeader(SESSION_ID) String sessionId) {
        authService.isLoginUser(sessionId);
        return new ResponseEntity<>(blogService.getBlog(id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Blog>> getAllBlogs(@RequestHeader(SESSION_ID) String sessionId) {
        authService.isLoginUser(sessionId);
        List<Blog> blogs = blogService.getAllBlogs();
        return new ResponseEntity<>(blogs,HttpStatus.OK);
    }

    @GetMapping(path = "{username}")
    public ResponseEntity<List<Blog>> getUserBlogs(@PathVariable("username") String username,
                                                   @RequestHeader(SESSION_ID) String sessionId) {
        authService.isLoginUser(sessionId);
        List<Blog> userBlogs = blogService.getUserBlogs(username);
        return new ResponseEntity<>(userBlogs,HttpStatus.OK);
    }
}