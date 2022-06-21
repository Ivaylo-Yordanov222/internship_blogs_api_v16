package com.ivaylo.blog.utility;

public class BlogsConstants {
    //Api constants
    public final static String SESSION_ID = "session-id";
    public final static int USERNAME_MIN_LENGTH = 3;
    public final static int USERNAME_MAX_LENGTH = 100;
    public final static int PASSWORD_MIN_LENGTH = 4;
    public final static int PASSWORD_MAX_LENGTH = 72;
    public final static int BLOG_AND_ARTICLE_TITLES_MIN_LENGTH = 3;
    public final static int BLOG_AND_ARTICLE_TITLES_MAX_LENGTH = 100;
    public final static int ARTICLE_CONTENT_MIN_LENGTH = 4;
    public final static int ARTICLE_CONTENT_MAX_LENGTH = 10000;

    public final static String USERNAME_REGEX = "(^(?!\\d)^(?!_)^([a-z\\d_]+)$)";
    public final static String PASSWORD_REGEX = "(^[a-zA-Z1-9]+$)";
    public final static String EMAIL_REGEX = "(^(?!\\d)^(?!_)^([a-z1-9_]{3,})@([a-z]{3,})(.[a-z]{2,})$)";
    public final static String BLOG_AND_ARTICLE_TITLE_REGEX = "((?i)^(?!INSERT)^(?!TRUNCATE)^(?!DELETE)^(?!DROP)^(?!SELECT)^(?!UPDATE)(^[a-zA-Z]+(( [*a-zA-Z]+))*$))";
    public final static String ARTICLE_CONTENT_REGEX = "((?i)^(?!INSERT)^(?!TRUNCATE)^(?!DELETE)^(?!DROP)^(?!SELECT)^(?!UPDATE)(^(([a-zA-Z]+)([a-zA-Z1-9 ?!.,\\-+:;()\"'&#@$%]*))$))";
}
