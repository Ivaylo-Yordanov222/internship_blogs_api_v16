package com.ivaylo.blog.services;

import com.ivaylo.blog.models.ArticleRequest;
import com.ivaylo.blog.utility.exceptions.CustomBlogsValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ImageServiceTest {

    private final String imagesUploadFolderName = "uploads";
    private MultipartFile image;
    private Path path;
    @InjectMocks
    private ImageService imageService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        path = getRootPath();
    }

    @Test(expected = CustomBlogsValidationException.class)
    public void givenImageWhenUploadThenInvokesFileCopyThrowsCustomBlogsValidationException() throws IOException {
        imageService.upload(new ArticleRequest(), 1L);
        verify(Files.copy(image.getInputStream(), getRootPath()
                .resolve(Objects.requireNonNull(image.getOriginalFilename()))), times(1));
    }

    private Path getRootPath() {
        return Paths.get(imagesUploadFolderName);
    }
}
