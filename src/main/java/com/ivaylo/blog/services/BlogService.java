package com.ivaylo.blog.services;

import com.ivaylo.blog.factories.BlogFactory;
import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.BlogRequest;
import com.ivaylo.blog.repositories.BlogRepository;
import com.ivaylo.blog.services.interfaces.IBlogService;
import com.ivaylo.blog.services.interfaces.IImageService;
import com.ivaylo.blog.services.interfaces.IUserService;
import com.ivaylo.blog.utility.enums.ValidationResult;
import com.ivaylo.blog.utility.exceptions.CustomBlogsConflictException;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ivaylo.blog.utility.BlogsUtilityMethods.getSlug;
import static com.ivaylo.blog.utility.enums.ValidationMessage.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.SUCCESS;
import static com.ivaylo.blog.utility.validators.BlogValidator.*;

@Service
public class BlogService implements IBlogService {

    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private BlogFactory blogFactory;
    @Autowired
    private IUserService userService;
    @Autowired
    private IImageService imageService;

    @Override
    public Blog getBlog(long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (!blog.isPresent()) {
            throw new IllegalStateException(BLOG_NOT_FOUND.getMessage());
        }
        return blog.get();
    }

    @Override
    public List<Blog> getBlogsByTitle(String blogTitle) {
        List<Blog> blogs = blogRepository.findAllBySlug(blogTitle);
        if (blogs.size() <= 0) {
            throw new IllegalStateException(NO_ARTICLES_FOUND.getMessage());
        }
        return blogs;
    }

    @Override
    public List<Blog> getAllBlogs() {
        List<Blog> blogs = blogRepository.findAll();
        if (blogs.size() <= 0) {
            throw new IllegalStateException(NO_BLOGS_FOUND.getMessage());
        }
        return blogs;
    }

    @Override
    public List<Blog> getUserBlogs(String username) {
        Optional<User> userDb = userService.getUserByUsername(username);
        if (!userDb.isPresent()) {
            throw new IllegalStateException(USER_NOT_FOUND.getMessage());
        }
        if (userDb.get().getBlogs().size() <= 0) {
            throw new IllegalStateException(NO_BLOGS_FOUND.getMessage());
        }
        return userDb.get().getBlogs();
    }

    @Override
    public Blog addBlog(User profileOwner, BlogRequest blogModel) {
        ValidationResult result = validateBlogModel(blogModel);
        if (result != SUCCESS) {
            throw new CustomBlogsValidationException(result.getMessage());
        }
        String slug = getSlug(blogModel.getTitle());
        checkForDuplicateBlogNames(profileOwner, slug);
        Blog blog = blogFactory.assembleBlog(blogModel.getTitle(), slug, profileOwner);
        blogRepository.save(blog);
        return blog;
    }

    @Override
    public Blog updateBlog(User profileOwner, Long blogId, BlogRequest blogModel) {
        ValidationResult result = validateBlogModel(blogModel);
        if (result != SUCCESS) {
            throw new CustomBlogsValidationException(result.getMessage());
        }
        Blog blogToUpdate = findSearchedBlogById(profileOwner, blogId);
        String slug = getSlug(blogModel.getTitle());
        checkForDuplicateBlogNames(profileOwner, slug);
        blogToUpdate.setSlug(slug);
        blogToUpdate.setTitle(blogModel.getTitle());
        blogRepository.save(blogToUpdate);
        return blogToUpdate;
    }

    @Override
    public void deleteBlog(User profileOwner, long blogId) {
        Blog blogToDelete = findSearchedBlogById(profileOwner, blogId);
        blogToDelete.getArticles().forEach(a -> imageService.delete(a.getImage().getImageName()));
        blogRepository.delete(blogToDelete);
    }

    @Override
    public Blog findSearchedBlogByTitle(User profileOwner, String blogTitle) {
        return profileOwner.getBlogs().stream()
                .filter(b -> b.getSlug().equals(blogTitle.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalStateException(BLOG_NOT_FOUND.getMessage());
                });
    }

    private Blog findSearchedBlogById(User profileOwner, Long blogId) {
        return profileOwner.getBlogs().stream()
                .filter(b -> b.getId().equals(blogId))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalStateException(BLOG_NOT_FOUND.getMessage());
                });
    }

    private void checkForDuplicateBlogNames(User profileOwner, String slug) {
        Optional<Blog> duplicateBlog = profileOwner.getBlogs().stream()
                .filter(b -> b.getSlug().equals(slug))
                .findFirst();
        if (duplicateBlog.isPresent()) {
            throw new CustomBlogsConflictException(BLOG_NAME_ALREADY_EXIST.getMessage());
        }
    }

    private ValidationResult validateBlogModel(BlogRequest blogModel) {
        return isBlogTitleEqualsToNull()
                .and(isValidBlogTitleLength())
                .and(isValidBlogTitle())
                .apply(blogModel);
    }
}