package com.ivaylo.blog.services.interfaces;

import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.BlogRequest;

import java.util.List;

public interface IBlogService {

    Blog getBlog(long id);

    List<Blog> getBlogsByTitle(String blogTitle);

    List<Blog> getAllBlogs();

    List<Blog> getUserBlogs(String username);

    Blog addBlog(User profileOwner, BlogRequest blogModel);

    Blog updateBlog(User profileOwner, Long blogId, BlogRequest blogModel);

    Blog findSearchedBlogByTitle(User profileOwner, String blogTitle);

    void deleteBlog(User profileOwner, long blogId);

}
