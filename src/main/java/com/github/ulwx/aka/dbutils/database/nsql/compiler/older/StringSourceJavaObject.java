package com.github.ulwx.aka.dbutils.database.nsql.compiler.older;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StringSourceJavaObject extends SimpleJavaFileObject {

    private String content = null;

    public StringSourceJavaObject(String className, String content) throws URISyntaxException {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = content;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }
}