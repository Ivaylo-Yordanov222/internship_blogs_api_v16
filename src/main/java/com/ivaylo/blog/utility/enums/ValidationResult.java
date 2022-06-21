package com.ivaylo.blog.utility.enums;

import static com.ivaylo.blog.utility.BlogsConstants.*;

public enum ValidationResult {
    SUCCESS("Success"),
    NAME_IS_MANDATORY("Username is mandatory"),
    NAME_MUST_BE_BETWEEN(String.format("Username must be between %d and %d symbols", USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH)),
    NAME_MUST_HAVE_THESE_SYMBOLS("Username must have only alphabetical symbols ,numbers and underscore"),
    PASSWORD_IS_MANDATORY("The password is mandatory"),
    PASSWORD_MUST_BE_BETWEEN(String.format("Password must be between %d and %d symbols", PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH)),
    PASSWORD_MUST_HAVE_THESE_SYMBOLS("Password must have only alphabetical symbols and numbers"),
    EMAIL_IS_MANDATORY("The email is mandatory"),
    EMAIL_IS_NOT_VALID("Email is invalid"),
    BLOG_TITLE_IS_MANDATORY("Blog title is mandatory"),
    BLOG_TITLE_MUST_BE_BETWEEN(String.format("Blog title must be between %d and %d symbols", BLOG_AND_ARTICLE_TITLES_MIN_LENGTH, BLOG_AND_ARTICLE_TITLES_MAX_LENGTH)),
    BLOG_TITLE_MUST_HAVE_THESE_SYMBOLS("Blog title must have only alphabetical symbols and white spaces. Title must start ana ends with alphabetical symbols. Between words is allowed only one space"),
    ARTICLE_TITLE_IS_MANDATORY("Article title is mandatory"),
    ARTICLE_TITLE_MUST_BE_BETWEEN(String.format("Article title must be between %d and %d symbols", BLOG_AND_ARTICLE_TITLES_MIN_LENGTH, BLOG_AND_ARTICLE_TITLES_MAX_LENGTH)),
    ARTICLE_TITLE_MUST_HAVE_THESE_SYMBOLS("Article title must have only alphabetical symbols and white spaces. Title must start ana ends with alphabetical symbols. Between words is allowed only one space"),
    ARTICLE_CONTENT_IS_MANDATORY("Article content is mandatory"),
    ARTICLE_CONTENT_MUST_BE_BETWEEN(String.format("Article content must be between %d and %d symbols", ARTICLE_CONTENT_MIN_LENGTH,ARTICLE_CONTENT_MAX_LENGTH)),
    ARTICLE_CONTENT_MUST_HAVE_THESE_SYMBOLS("Article content must have only alphabetical symbols ,numbers, white spaces and symbols - (?!.,-+:()\"'*&#@$%)"),
    IMAGE_FILE_IS_MANDATORY("Image file is mandatory"),
    FILE_IS_NOT_IMAGE("The given file is not image");
    private final String message;

    ValidationResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
