package com.ivaylo.blog.utility;

public class BlogsUtilityMethods {
    public static String getSlug(String title) {
        return title.trim().replaceAll(" ", "-").toLowerCase();
    }
}
