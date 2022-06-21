package com.ivaylo.blog.services.interfaces;

import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.entities.User;
import com.ivaylo.blog.models.ArticleRequest;

import java.util.List;

public interface IArticleService {
    Article addArticle(User profileOwner, String blogTitle, ArticleRequest articleModel);
    Article updateArticle(User profileOwner, Long articleId, ArticleRequest articleModel);
    void deleteArticle(User profileOwner, Long articleId);

    List<Article> getAllArticles();
    List<Article> getAllBlogArticles(String blogTitle);
    List<Article> getAllUserArticles(String username);
}
