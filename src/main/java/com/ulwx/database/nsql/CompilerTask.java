package com.ulwx.database.nsql;

import com.ulwx.tool.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CompilerTask {
    private static Logger log = LoggerFactory.getLogger(CompilerTask.class);
    private static volatile String classpath = buildClassPath();

    public static volatile Map<String, Class> LoadedClasses = new ConcurrentHashMap<String, Class>();
    public static volatile Map<String, String> LoadedSoruces = new ConcurrentHashMap<String, String>();


    public volatile static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    static {
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        preCompileAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e + "", e);
        }
    }

    public static void preCompileAll() throws Exception {
        // 搜索所有md文件，进行编译
        // Path.getClassPath()
        String fromRootDir = Path.getRootClassPath();
        List<String> list = new ArrayList<>();
        ClassUtils.findAndAddInPackageByFile(fromRootDir, true, list, ".md");
        List<String> mdPathList = new ArrayList<>();
        if (list != null && list.size() > 0) {// 预编译
            for (int i = 0; i < list.size(); i++) {
                String mdPath = StringUtils.trimLeadingString(list.get(i), fromRootDir)
                        .replace(File.separatorChar + "", ".").substring(1);
                mdPathList.add(mdPath);


            }
        }
        log.debug(ObjectUtils.toString(mdPathList));

        for (int i = 0; i < mdPathList.size(); i++) {
            preCompileSingle(mdPathList.get(i));
        }


    }

    public static Class<?> preCompileSingle(String mdPath) throws Exception {

        String packagePath = mdPath.substring(0, mdPath.lastIndexOf(".md"));
        // 获得类名
        int lastPos = packagePath.lastIndexOf(".");
        String className = packagePath.substring(lastPos + 1) + "Md";
        String packageName = packagePath.substring(0, lastPos);
        String classFullName = packageName + "." + className;
        Class<?> clazz = CompilerTask.getCompiledClass(classFullName);
        if (clazz == null) {
            synchronized (CompilerTask.class) {
                clazz = CompilerTask.getCompiledClass(classFullName);
                if (clazz == null) {
                    String source = MDTemplate.parseFromMdFileToJavaSource(packageName, className);
                    if (log.isDebugEnabled()) {
                        log.debug("compile-" + source);
                    }
                    clazz = CompilerTask.compileAndLoadClass(classFullName, source);
                }

            }

        } else {
            if (log.isDebugEnabled()) {
                log.debug("source-" + CompilerTask.getCompiledClassOfSource(classFullName));
            }
        }
        return clazz;
    }

    /**
     * @MethodName : 创建classpath
     */
    private static String buildClassPath() {
        classpath = null;
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();;
        if (parentClassLoader instanceof URLClassLoader) {
            URLClassLoader urlLoader = (URLClassLoader) parentClassLoader;
            log.debug("parentClassLoader" + parentClassLoader);
            StringBuilder sb = new StringBuilder();
            for (URL url : urlLoader.getURLs()) {
                String p = url.getFile().substring(1);
                String os = System.getProperty("os.name");
                if(os.toLowerCase().startsWith("win")){
                    p = StringUtils.trimLeadingString(url.getFile(),"/");
                }

                sb.append(p).append(File.pathSeparator);
            }
            String classpathStr = EscapeUtil.unescapeUrl(sb.toString(), System.getProperty("file.encoding"));
            return classpathStr;
        }

        return null;
    }

    public static void main(String[] arg) throws Exception {

        String classstr = "/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/classes/;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/activation-1.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/bcmail-jdk14-1.38.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/bcprov-jdk15on-1.61.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/bctsp-jdk14-1.38.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/carrier-1.109.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/classloader-leak-prevention-core-2.1.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/classloader-leak-prevention-servlet-2.1.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/cloning-1.9.4.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/common-base-spring-1.0.2-SNAPSHOT.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/common-database-1.0.2-SNAPSHOT.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/common-smssend-1.0.0-SNAPSHOT.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/common-tool-1.0.0-SNAPSHOT.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/common-ueditor-ext-1.4.3.3-SNAPSHOT.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-beanutils-1.9.3.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-codec-1.13.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-collections-3.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-collections4-4.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-compress-1.14.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-configuration-1.10.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-digester-2.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-fileupload-1.4.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-io-2.5.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-jexl-2.0.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-lang-2.6.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-lang3-3.6.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-logging-1.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-math3-3.6.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/commons-text-1.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/dom4j-1.6.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/ezmorph-1.0.6.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/FastInfoset-1.2.16.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/fastjson-1.2.58.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/flying-saucer-core-9.1.12.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/flying-saucer-pdf-9.1.12.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/geocoder-2.119.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/gson-2.8.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/guava-20.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/httpclient-4.5.9.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/httpclient-cache-4.5.9.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/httpcore-4.4.12.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/httpmime-4.5.9.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/istack-commons-runtime-3.0.8.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/itext-2.1.7.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/itext-asian-5.2.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/itextpdf-5.5.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jackson-annotations-2.9.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jackson-core-2.9.9.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jackson-databind-2.9.9.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jakarta.activation-api-1.2.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jakarta.xml.bind-api-2.3.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/javax.annotation-api-1.3.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/javax.mail-1.5.6.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jaxb-api-2.3.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jaxb-impl-2.3.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jaxb-runtime-2.3.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jaxen-1.1.6.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/json-20190722.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/json-lib-2.2.3-jdk15.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jstl-1.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/jxls-core-1.0.6.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/libphonenumber-8.10.13.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/log4j-1.2-api-2.11.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/log4j-api-2.11.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/log4j-core-2.11.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/log4j-jcl-2.11.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/log4j-jul-2.11.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/log4j-slf4j-impl-2.11.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/mysql-connector-java-5.1.43.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/objenesis-2.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/pinyin4j-2.5.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/poi-3.16.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/poi-ooxml-3.9.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/poi-ooxml-schemas-3.9.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/prefixmapper-2.119.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/slf4j-api-1.7.25.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/snakeyaml-1.24.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-aop-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-beans-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-context-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-core-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-expression-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-jcl-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-web-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spring-webmvc-5.3.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/spymemcached-2.12.3.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/stax-api-1.0.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/stax-ex-1.8.1.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/tomcat-jdbc-8.5.15.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/tomcat-juli-8.5.15.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/txw2-2.3.2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/xml-apis-1.0.b2.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/xmlbeans-2.3.0.jar;/C:/suncj/apache-tomcat-9.0.37/webapps/common-base-spring-test/WEB-INF/lib/xmlworker-5.5.1.jar";
        String txt = "C:\\test\\my.txt";
        String content = FileUtils.readTxt(txt, "utf-8");
        //CompilerTask.compileTest("com.ulwxbase.web.action.sys.services.dao.sys.SysRightDaoMd", content, "c:\\out");
        System.out.println(ObjectUtils.toString("xxxxxx"));

        CompilerTask.classpath=classstr;
        CompilerTask.compileAndLoadClass("com.ulwxbase.web.action.sys.services.dao.sys.SysRightDaoMd", content, true);


    }

    /**
     * @param classFullName ,例如 com.ulwx.database.test.SysRightDaoMd
     * @param source
     * @return
     * @throws Exception
     */
    public static Class<?> compileAndLoadClass(String classFullName, String source) throws Exception {
        Class<?> clz = getCompiledClass(classFullName);
        if (clz != null) {
            return clz;
        }

        return compileAndLoadClass(classFullName, source, false);
    }

    public static Class<?> getCompiledClass(String classFullName) {
        return LoadedClasses.get(classFullName);

    }

    public static String getCompiledClassOfSource(String classFullName) {
        return LoadedSoruces.get(classFullName);

    }

    /**
     * @param classFullName 包全路径名,例如 com.ulwx.database.test.SysRightDaoMd
     * @param source        java源代码字符串
     * @param reload        是否需要重新编译及加载
     * @return 返回生成的Class对象
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static Class<?> compileAndLoadClass(String classFullName, String source, boolean reload) throws Exception {

        if (LoadedClasses.get(classFullName) != null && !reload) {
            return LoadedClasses.get(classFullName);
        }

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
            LoadedClasses.put(classFullName, clazz);
            LoadedSoruces.put(classFullName, source);
            fileManager.close();
            return clazz;

        } else {
            String error = "";
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                error = error + compilePrint(diagnostic);
            }
            throw new NSQLException("编译出错！");
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void compileTest(String classFullName, String codeString, String classOutputFolder) throws Exception {
        // 通过 ToolProvider 取得 JavaCompiler 对象，JavaCompiler 对象是动态编译工具的主要对象
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 通过 JavaCompiler 取得标准 StandardJavaFileManager 对象，StandardJavaFileManager
        // 对象主要负责
        // 编译文件对象的创建，编译的参数等等，我们只对它做些基本设置比如编译 CLASSPATH 等。
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // 因为是从内存中读取 Java 源文件，所以需要创建我们的自己的 JavaFileObject，即 InMemoryJavaFileObject
        JavaFileObject fileObject = new StringSourceJavaObject(classFullName, codeString);
        Iterable<? extends JavaFileObject> files = Arrays.asList(fileObject);

        // 编译结果信息的记录
        StringWriter sw = new StringWriter();

        // 编译目的地设置
        Iterable options = Arrays.asList("-d", classOutputFolder);

        // 通过 JavaCompiler 对象取得编译 Task
        CompilationTask task = compiler.getTask(sw, fileManager, null, options, null, files);

        // 调用 call 命令执行编译，如果不成功输出错误信息
        boolean ret = task.call();
        if (!ret) {
            String failedMsg = sw.toString();

        }
        fileManager.close();

    }

    static class StringSourceJavaObject extends SimpleJavaFileObject {

        private String content = null;

        public StringSourceJavaObject(String className, String content) throws URISyntaxException {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return content;
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

/**
 * 将输出流交给JavaCompiler，最后JavaCompiler将编译后的class文件写入输出流中
 */
class JavaClassObject extends SimpleJavaFileObject {

    /**
     * 定义一个输出流，用于装载JavaCompiler编译后的Class文件
     */
    protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    /**
     * 调用父类构造器
     *
     * @param name
     * @param kind
     */
    public JavaClassObject(String name, Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
    }

    /**
     * 获取输出流为byte[]数组
     *
     * @return
     */
    public byte[] getBytes() {
        return bos.toByteArray();
    }

    /**
     * 重写openOutputStream，将我们的输出流交给JavaCompiler，让它将编译好的Class装载进来
     *
     * @return
     * @throws IOException
     */
    @Override
    public OutputStream openOutputStream() throws IOException {
        return bos;
    }

    /**
     * 重写finalize方法，在对象被回收时关闭输出流
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        bos.close();
    }
}

/**
 * 类文件管理器 用于JavaCompiler将编译好后的class，保存到jclassObject中
 */
class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    /**
     * 保存编译后Class文件的对象
     */
    private JavaClassObject jclassObject;

    /**
     * 调用父类构造器
     *
     * @param standardManager
     */
    public ClassFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    /**
     * 将JavaFileObject对象的引用交给JavaCompiler，让它将编译好后的Class文件装载进来
     *
     * @param location
     * @param className
     * @param kind
     * @param sibling
     * @return
     * @throws IOException
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                               FileObject sibling) throws IOException {
        if (jclassObject == null)
            jclassObject = new JavaClassObject(className, kind);
        return jclassObject;
    }

    public JavaClassObject getJavaClassObject() {
        return jclassObject;
    }
}


/**
 * 自定义加载器，一个类可以重新加载，但每次加载都必须用一个新的ClassDataClassLoader实例用于加载同一个类的字节码，才能做到重复加载。
 * <p>
 * java虚拟机识别一个类是根据加载器实例+全路径包名来的，如果做到重复加载，必须不同的加载器实例来加载同一个类的字节码
 */
class ClassDataClassLoader extends ClassLoader {
    private static Logger log = LoggerFactory.getLogger(ClassDataClassLoader.class);

    private byte[] classData;

    public ClassDataClassLoader(byte[] classData) {
        this.classData = classData;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassData();
        if (classData == null) {
            // 调用父亲加载器加载
            return CompilerTask.class.getClassLoader().loadClass(name);
        } else {
            Class<?> clazz = defineClass(name, classData, 0, classData.length);
            this.classData = null;// 清空字节码数据
            return clazz;
        }
    }

    private byte[] getClassData() {
        return this.classData;
    }


}