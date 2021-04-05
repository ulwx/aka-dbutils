package com.github.ulwx.aka.dbutils.database.nsql.compiler.older;

import com.github.ulwx.aka.dbutils.database.DbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompilerUtils {
    private static Logger log = LoggerFactory.getLogger(CompilerUtils.class);
    public volatile static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    /**
     * 根据类名和源文件进行编译并加载类到jvm
     *
     * @param classFullName 包全路径名,例如 com.github.ulwx.database.test.SysRightDaoMd
     * @param source        java源代码字符串
     * @param  classpath    编译的类路径
     * @return 返回生成的Class对象
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static Class<?> compileAndLoadClass(String classFullName, String source, String classpath) throws Exception {

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));
        StringSourceJavaObject sourceObject = new StringSourceJavaObject(classFullName, source);
        Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(sourceObject);
        List<String> options = new ArrayList<String>();
        options.add("-target");
        options.add("1.8");
        options.add("-source");
        options.add("1.8");
        options.add("-Xlint:unchecked");
        options.add("-encoding");
        options.add("UTF-8");
        if (classpath != null && !classpath.isEmpty()) {
            options.add("-classpath");
            log.debug("classpathStr=" + classpath);
            options.add(classpath);
        }

        // 通过JavaCompiler进行编译都是在当前目录下生成.class文件，而使用编译选项能改动这个默认目录 。
        // options.add("-d");
        // options.add("c:/out");
        CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileObjects);
        boolean result = task.call();
        if (result) {
            // 如果编译成功，用类加载器加载该类
            JavaClassObject jco = fileManager.getJavaClassObject();

            ClassDataClassLoader dynamicClassLoader = new ClassDataClassLoader(jco.getBytes());
            Class clazz = dynamicClassLoader.loadClass(classFullName);
            fileManager.close();

            return clazz;

        } else {
            String error = "";
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                error = error + compilePrint(diagnostic);
            }
            throw new DbException("编译出错！");
        }

    }


    private static String compilePrint(Diagnostic diagnostic) {

        StringBuffer res = new StringBuffer();
        res.append("Code:[" + diagnostic.getCode() + "]\n");
        res.append("Kind:[" + diagnostic.getKind() + "]\n");
        res.append("Position:[" + diagnostic.getPosition() + "]\n");
        res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
        res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
        res.append("Source:[" + diagnostic.getSource() + "]\n");
        res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
        res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
        res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
        return res.toString();
    }
}
