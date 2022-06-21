package com.ivaylo.blog.services;

import com.ivaylo.blog.models.ArticleRequest;
import com.ivaylo.blog.services.interfaces.IImageService;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.ivaylo.blog.utility.enums.ValidationMessage.*;

@Service
public class ImageService implements IImageService {

    @Value("${blogs.application.image-upload-folder}")
    private String imagesUploadFolderName;

    @Override
    public void init() {
        try {
            Files.createDirectory(getRootPath());
        } catch (IOException e) {
            throw new CustomBlogsValidationException(COULD_NOT_INITIALIZE_FOLDER.getMessage());
        }
    }

    @Override
    public void upload(ArticleRequest articleRequest, Long userId) {
        try {
            String extension = FilenameUtils.getExtension(articleRequest.getFile().getOriginalFilename());
            Long name = System.currentTimeMillis();
            String imageName = userId + "-" + name + "." + extension;
            articleRequest.setImageAssembledName(imageName);
            Files.copy(articleRequest.getFile().getInputStream(),
                    getRootPath().resolve(Objects.requireNonNull(imageName)));

        } catch (Exception e) {
            throw new CustomBlogsValidationException(String.format(COULD_NOT_STORE_IMAGE.getMessage(), e.getMessage()));
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = getRootPath().resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new CustomBlogsValidationException(COULD_NOT_LOAD_FILE.getMessage());
            }
        } catch (MalformedURLException e) {
            throw new CustomBlogsValidationException("Error: " + e.getMessage());
        }
    }

    @Override
    public void delete(String imageName) {
        try {
            Path file = getRootPath().resolve(imageName);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new CustomBlogsValidationException(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(getRootPath().toFile());
    }

    private Path getRootPath() {
        return Paths.get(imagesUploadFolderName);
    }
}
