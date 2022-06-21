package com.ivaylo.blog.utility.validators;

import com.ivaylo.blog.models.ArticleRequest;
import com.ivaylo.blog.utility.enums.ValidationResult;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ivaylo.blog.utility.BlogsConstants.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.SUCCESS;

public interface ArticleValidator extends Function<ArticleRequest, ValidationResult> {

    static ArticleValidator isArticleTitleEqualsToNull() {
        return article -> article.getTitle() != null ?
                SUCCESS :
                ARTICLE_TITLE_IS_MANDATORY;
    }

    static ArticleValidator isValidArticleTitleLength() {
        return article -> article.getTitle().length() >= BLOG_AND_ARTICLE_TITLES_MIN_LENGTH
                && article.getTitle().length() <= BLOG_AND_ARTICLE_TITLES_MAX_LENGTH ?
                SUCCESS :
                ARTICLE_TITLE_MUST_BE_BETWEEN;
    }

    static ArticleValidator isValidArticleTitle() {
        return article -> {
            Pattern pattern = Pattern.compile(BLOG_AND_ARTICLE_TITLE_REGEX);
            Matcher matcher = pattern.matcher(article.getTitle());
            return matcher.matches() ?
                    SUCCESS :
                    ARTICLE_TITLE_MUST_HAVE_THESE_SYMBOLS;
        };
    }

    static ArticleValidator isArticleContentEqualsToNull() {
        return article -> article.getContent() != null ?
                SUCCESS :
                ARTICLE_CONTENT_IS_MANDATORY;
    }

    static ArticleValidator isValidArticleContentLength() {
        return article -> article.getContent().length() >= ARTICLE_CONTENT_MIN_LENGTH
                && article.getTitle().length() <= ARTICLE_CONTENT_MAX_LENGTH ?
                SUCCESS :
                ARTICLE_CONTENT_MUST_BE_BETWEEN;
    }

    static ArticleValidator isValidArticleContent() {
        return article -> {
            Pattern pattern = Pattern.compile(ARTICLE_CONTENT_REGEX);
            Matcher matcher = pattern.matcher(article.getContent());
            return matcher.matches() ?
                    SUCCESS :
                    ARTICLE_CONTENT_MUST_HAVE_THESE_SYMBOLS;
        };
    }

    static ArticleValidator isImageFileEqualsToNull() {
        return article -> article.getFile() != null && !article.getFile().getOriginalFilename().isEmpty() ?
                SUCCESS :
                IMAGE_FILE_IS_MANDATORY;
    }

    static ArticleValidator isValidImageFile() {
        return article -> article.getFile().getOriginalFilename().endsWith(".JPEG")
                || article.getFile().getOriginalFilename().endsWith(".JPG")
                || article.getFile().getOriginalFilename().endsWith(".jpg")
                || article.getFile().getOriginalFilename().endsWith(".png")
                || article.getFile().getOriginalFilename().endsWith(".PNG")
                || article.getFile().getOriginalFilename().endsWith(".giff")
                || article.getFile().getOriginalFilename().endsWith(".gif") ?
                SUCCESS :
                FILE_IS_NOT_IMAGE;
    }

    default ArticleValidator and(ArticleValidator other) {
        return article -> {
            ValidationResult result = this.apply(article);
            return result.equals(SUCCESS) ? other.apply(article) : result;
        };
    }
}
