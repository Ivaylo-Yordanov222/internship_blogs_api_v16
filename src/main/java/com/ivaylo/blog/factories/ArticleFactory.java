package com.ivaylo.blog.factories;

import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.entities.Blog;
import com.ivaylo.blog.entities.Image;
import org.springframework.stereotype.Component;

@Component
public class ArticleFactory {
    public Article assembleArticle(String title, String content, String slug, Blog blog){
        return new Article(title, content, slug, blog);
    }
    public Image assembleImage(String imageName, String url){
        return new Image(imageName, url);
    }
}
