package com.ivaylo.blog.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class ArticleRequest {
    private String title;
    private String content;
    private MultipartFile file;
    private String url;
    private String imageAssembledName;
}
