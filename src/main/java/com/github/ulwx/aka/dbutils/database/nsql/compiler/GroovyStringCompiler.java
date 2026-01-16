package com.github.ulwx.aka.dbutils.database.nsql.compiler;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.io.*;
import java.net.URL;

public class GroovyStringCompiler {
    private static final Logger log = LoggerFactory.getLogger(GroovyStringCompiler.class);
    private GroovyClassLoader groovyClassLoader;

    public GroovyStringCompiler() {
        this.groovyClassLoader = new GroovyClassLoader();
    }

    public GroovyStringCompiler(ClassLoader parentClassLoader, String... classpaths) {
        this.groovyClassLoader = new GroovyClassLoader(parentClassLoader);
        if (classpaths != null) {
            for (String classpath : classpaths) {
                try {
                    groovyClassLoader.addClasspath(classpath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 编译Java/Groovy源代码字符串
     */
    public  Class<?> compile(String fileName, String source, String classpath) throws Exception {
        // 设置类路径
        try {
            if (classpath != null && !classpath.isEmpty()) {
                String[] paths = classpath.split(System.getProperty("path.separator", ":"));
                for (String path : paths) {
                    groovyClassLoader.addClasspath(path);
                }
            }

            // 编译源代码
            Class<?> compiledClass = groovyClassLoader.parseClass(source, getClassNameFromFileName(fileName));
            return compiledClass;
        }finally {
            this.close();
        }


    }



    private String getClassNameFromFileName(String fileName) {
        if (fileName.endsWith(".java") || fileName.endsWith(".groovy")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }

    public void close() {
        try {
            groovyClassLoader.close();
        } catch (IOException e) {
            log.error(""+e,e);
        }
    }
}
