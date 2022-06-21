package com.ivaylo.blog.repositories;

import com.ivaylo.blog.entities.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findById(long id);

    List<Blog> findAllBySlug(String slug);
}