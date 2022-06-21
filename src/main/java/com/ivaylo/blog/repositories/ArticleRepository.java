package com.ivaylo.blog.repositories;
import com.ivaylo.blog.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Long> {

}
