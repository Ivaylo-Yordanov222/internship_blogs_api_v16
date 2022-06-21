package com.ivaylo.blog.utility.enums;

public enum ValidationMessage {
    USER_SUCCESSFULLY_REGISTERED("%s register successfully"),
    USER_LOG_IN("%s logged in, Header key: session-id, Header value: %s"),
    USER_LOGGED_OUT("%s logged out"),
    ARTICLE_WITH_ID_SUCCESSFULLY_DELETED("Article with id \"%d\" successfully deleted"),
    BLOG_WITH_ID_SUCCESSFULLY_DELETED("Blog with \"%d\" successfully deleted"),
    USERNAME_ALREADY_TAKEN("\"%s\" is already taken!"),
    EMAIL_ALREADY_TAKEN("The email \"%s\" is already taken!"),
    USER_ALREADY_LOGGED_IN("You are already logged in"),
    INVALID_CREDENTIALS("Invalid email or password"),
    USER_NOT_FOUND("User not found"),
    BLOG_NOT_FOUND("Blog not found"),
    ARTICLE_NOT_FOUND("Article not found"),
    BLOG_NAME_ALREADY_EXIST("You can`t have duplicate names in your blogs"),
    ARTICLE_NAME_ALREADY_EXIST("You can`t have duplicate article names in your blog"),
    NO_BLOGS_FOUND("No blogs found"),
    NO_ARTICLES_FOUND("No articles found"),
    COULD_NOT_INITIALIZE_FOLDER("Could not initialize folder for upload!"),
    COULD_NOT_STORE_IMAGE("Could not store the file. Error: %s"),
    COULD_NOT_LOAD_FILE("Could not read the file!");
    private final String message;

    ValidationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}


