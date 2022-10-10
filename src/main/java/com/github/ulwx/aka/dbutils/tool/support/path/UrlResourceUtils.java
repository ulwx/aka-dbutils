package com.github.ulwx.aka.dbutils.tool.support.path;

public class UrlResourceUtils {
    public static Resource newURLResource(String url) throws Exception {
        return new UrlResource(url);
    }
}
