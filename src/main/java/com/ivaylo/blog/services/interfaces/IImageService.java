package com.ivaylo.blog.services.interfaces;

import com.ivaylo.blog.entities.Article;
import com.ivaylo.blog.models.ArticleRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

public interface IImageService {
    void init();
    void upload(ArticleRequest articleRequest, Long userId);
    void delete(String imageName);
    Resource load(String filename);
    void deleteAll();
}
