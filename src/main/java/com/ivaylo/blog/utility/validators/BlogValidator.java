package com.ivaylo.blog.utility.validators;

import com.ivaylo.blog.models.BlogRequest;
import com.ivaylo.blog.utility.enums.ValidationResult;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ivaylo.blog.utility.BlogsConstants.*;
import static com.ivaylo.blog.utility.enums.ValidationResult.*;

public interface BlogValidator extends Function<BlogRequest, ValidationResult> {

    static BlogValidator isBlogTitleEqualsToNull() {
        return blog -> blog.getTitle() != null ?
                SUCCESS :
                BLOG_TITLE_IS_MANDATORY;
    }

    static BlogValidator isValidBlogTitleLength() {
        return blog -> blog.getTitle().length() >= BLOG_AND_ARTICLE_TITLES_MIN_LENGTH
                && blog.getTitle().length() <= BLOG_AND_ARTICLE_TITLES_MAX_LENGTH ?
                SUCCESS :
                BLOG_TITLE_MUST_BE_BETWEEN;
    }

    static BlogValidator isValidBlogTitle() {
        return blog -> {
            Pattern pattern = Pattern.compile(BLOG_AND_ARTICLE_TITLE_REGEX);
            Matcher matcher = pattern.matcher(blog.getTitle());
            return matcher.matches() ?
                    SUCCESS :
                    BLOG_TITLE_MUST_HAVE_THESE_SYMBOLS;
        };
    }

    default BlogValidator and(BlogValidator other) {
        return blog -> {
            ValidationResult result = this.apply(blog);
            return result.equals(SUCCESS) ? other.apply(blog) : result;
        };
    }
}
