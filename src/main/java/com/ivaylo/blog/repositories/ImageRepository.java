package com.ivaylo.blog.repositories;

import com.ivaylo.blog.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
