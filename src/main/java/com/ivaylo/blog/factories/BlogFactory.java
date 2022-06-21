package com.ivaylo.blog.factories;

import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.User;
import org.springframework.stereotype.Component;

@Component
public class BlogFactory {
    public Blog assembleBlog(String title, String slug, User user){
        return new Blog(title, slug, user);
    }
}
