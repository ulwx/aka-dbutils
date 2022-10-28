package com.github.ulwx.aka.dbutils.database.nsql.compiler.newer;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * In-memory compile Java source code as String.
 *
 * @author michael
 */
public class JavaStringCompiler {
    public volatile static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager stdManager;

    public JavaStringCompiler() {
        this.stdManager = compiler.getStandardFileManager(null, null, null);
    }

    /**
     * Compile a Java source file in memory.
     *
     * @param fileName  Java file name, e.g. "Test.java"
     * @param source    The source code as String.
     * @param classpath classpath.
     * @return The compiled results as Map that contains class name as key,
     * class binary as value.
     * @throws IOException If compile error.
     */
    public Map<String, byte[]> compile(String fileName, String source, String classpath) throws IOException {
        try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
            JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
            System.setProperty("useJavaUtilZip", "true");
            List<String> options = new ArrayList<String>();
            options.add("-target");
            options.add("1.8");
            options.add("-source");
            options.add("1.8");
            options.add("-Xlint:unchecked");
            options.add("-encoding");
            options.add("UTF-8");
            //不使用SharedNameTable （jdk1.7自带的软引用，会影响GC的回收，jdk1.9已经解决）
            options.add("-XDuseUnsharedTable");
            options.add("-XDuseJavaUtilZip");
            if (classpath != null && !classpath.isEmpty()) {
                options.add("-classpath");
                options.add(classpath);
            }
            CompilationTask task = compiler.getTask(null, manager, null, options, null, Arrays.asList(javaFileObject));
            Boolean result = task.call();
            if (result == null || !result.booleanValue()) {
                throw new RuntimeException("Compilation failed.["+fileName+"]");
            }
            return manager.getClassBytes();
        }
    }

    /**
     * Load class from compiled classes.
     *
     * @param name       Full class name.
     * @param classBytes Compiled results as a Map.
     * @return The Class instance.
     * @throws ClassNotFoundException If class not found.
     * @throws IOException            If load error.
     */
    public Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
        MemoryClassLoader classLoader = null;
        try {
            classLoader = new MemoryClassLoader(classBytes);
            return classLoader.loadClass(name);
        } finally {
            if (classLoader != null) {
                classLoader.close();
            }
        }

    }
}
