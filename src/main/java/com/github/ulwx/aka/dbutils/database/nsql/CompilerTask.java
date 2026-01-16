package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.nsql.compiler.GroovyStringCompiler;
import com.github.ulwx.aka.dbutils.database.nsql.compiler.newer.JavaStringCompiler;
import com.github.ulwx.aka.dbutils.database.nsql.compiler.older.CompilerUtils;
import com.github.ulwx.aka.dbutils.tool.support.*;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompilerTask {
    public static enum CompilerTool{
        COMMON,
        JAVA,
        GROOVY
    }
    private static Logger log = LoggerFactory.getLogger(CompilerTask.class);
    private static volatile String classpath = buildClassPath();
    public static volatile Map<String, Class> LoadedClasses = new ConcurrentHashMap<String, Class>();
    public static volatile Map<String, String> LoadedSoruces = new ConcurrentHashMap<String, String>();
    public static volatile CompilerTool UseCompilerTool = CompilerTool.GROOVY;
    public static ExecutorService threadPool = Executors.newFixedThreadPool(4);
    public static volatile SegmentLock segmentLock = new SegmentLock();

    public static String getSource(String classFullName) {
        return LoadedSoruces.get(classFullName);
    }

    static {
        Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    preCompileAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void preCompileAll() throws Exception {
        // 搜索所有md文件，进行编译
        Resource[] resources = Path.getResourcesLikeAntPathMatch("classpath:**/*.md");
        Resource[] rootResources = Path.getResourcesLikeAntPathMatch("classpath:/");
        TreeSet<String> classRootResources = new TreeSet<>();
        for (int i = 0; i < rootResources.length; i++) {
            if (!rootResources[i].getURL().getProtocol().equals("jar")) {
                classRootResources.add(rootResources[i].getURL().getPath());
            }
        }
        List<String> mdPathList = new ArrayList<>();
        if (resources != null && resources.length > 0) {// 预编译
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].getURL().getProtocol().equals("jar")) {
                    String path = resources[i].getURL().getPath();
                    int index = path.indexOf(".jar!");
                    String mdPath = path.substring(index + 6);
                    mdPath = mdPath.replace("/", ".");
                    if (mdPath.startsWith("META-INF")) {
                        continue;
                    }
                    mdPath=StringUtils.trimLeadingString(mdPath,"BOOT-INF.classes!.");
                    mdPathList.add(mdPath);
                } else if (resources[i].getURL().getProtocol().equals("file")) {
                    Iterator<String> iterator = classRootResources.descendingIterator();
                    while (iterator.hasNext()) {
                        String str = iterator.next();
                        if (resources[i].getURL().getPath().startsWith(str)) {
                            String mdPath = resources[i].getURL().getPath().substring(str.length());
                            mdPath = mdPath.replace("/", ".");
                            if (mdPath.startsWith("META-INF")) {
                                break;
                            }
                            mdPath=StringUtils.trimLeadingString(mdPath,"BOOT-INF.classes!.");
                            mdPathList.add(mdPath);
                            break;
                        }
                    }
                }

            }
        }
        if (DbContext.permitDebugLog()) {
            log.debug("to compile--" + ObjectUtils.toString(mdPathList));
        }

        for (int i = 0; i < mdPathList.size(); i++) {
            Integer index = i;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        compileSingle(mdPathList.get(index), UseCompilerTool);
                    } catch (Exception e) {
                        log.error("" + e, e);
                    }
                }
            });
        }
        threadPool.shutdown();

    }
    public static Class<?> compileSingle(String mdPath,
                       CompilerTool UseCompilerTool) throws Exception {

        log.debug("mdPath="+mdPath);
        String packagePath = mdPath.substring(0, mdPath.lastIndexOf(".md"));
        // 获得类名
        int lastPos = packagePath.lastIndexOf(".");
        String className = packagePath.substring(lastPos + 1) + "Md";
        String packageName = packagePath.substring(0, lastPos);
        String classFullName = packageName + "." + className;
        Class aClass = null;
        String source="";
        try {
            aClass = LoadedClasses.get(classFullName);
            if (aClass == null) {
                try {
                    segmentLock.lock(classFullName);
                    aClass = LoadedClasses.get(classFullName);
                    if (aClass == null) {
                        source = MDTemplate.parseFromMdFileToJavaSource(packageName, className);

                        if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
                            log.debug("to compile java class source:" + classFullName
                                    + " ;source=" + source
                            );
                            log.debug("build classpath=" + classpath);
                        }
                        if (UseCompilerTool==CompilerTool.JAVA) {
                            JavaStringCompiler compiler = new JavaStringCompiler();
                            Map<String, byte[]> results = compiler.compile(className + ".java", source, classpath);
                            aClass = compiler.loadClass(classFullName, results);
                            log.debug("compile JAVA " +
                                    classFullName + "finished!");
                        } else if(UseCompilerTool==CompilerTool.GROOVY){
                            GroovyStringCompiler compiler = new GroovyStringCompiler();
                            aClass = compiler.compile(className + ".java", source, classpath);
                            log.debug("compile GROOVY " +
                                    classFullName + "finished!");

                        } else {
                            aClass = CompilerUtils.compileAndLoadClass(classFullName, source, classpath);
                        }
                        LoadedClasses.put(classFullName, aClass);
                        LoadedSoruces.put(classFullName, source);
                    }
                } finally {
                    segmentLock.unlock(classFullName);
                }

            } else {
                if (log.isDebugEnabled()) {
                     //log.debug("source-" + CompilerTask.getCompiledClassOfSource(classFullName));
                }
            }
            return aClass;
        }catch (Exception e) {
            throw new RuntimeException(""+classFullName+"\n"+source,e);
        }
    }

    /**
     * @MethodName : 创建classpath
     */
    public static String buildClassPath() {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        if (parentClassLoader instanceof URLClassLoader) {
//            URLClassLoader urlLoader = (URLClassLoader) parentClassLoader;
//            //log.debug("parentClassLoader" + parentClassLoader);
//            StringBuilder sb = new StringBuilder();
//            for (URL url : urlLoader.getURLs()) {
//                log.debug("fileurl="+url);
//                String p = Path.getFilePathFromURL(url);
//                sb.append(p).append(File.pathSeparator);
//            }
//            String classpathStr = EscapeUtil.unescapeUrl(sb.toString(), System.getProperty("file.encoding"));
           String  classpathStr=getClasspath();
           log.debug("classpathStr="+classpathStr);
            return classpathStr;
        } else {
            TreeSet<String> classPathResources = new TreeSet<>();
            StringBuilder sb = new StringBuilder();
            try {
                Resource[] rootResources = Path.getResourcesLikeAntPathMatch("classpath*:/");

                for (int i = 0; i < rootResources.length; i++) {
                    if (rootResources[i].getURL().getProtocol().equals("file")) {
                        String path = Path.getFilePathFromURL(rootResources[i].getURL());
                        classPathResources.add(path);
                        sb.append(path).append(File.pathSeparator);
                    } else if (rootResources[i].getURL().getProtocol().equals("jar")) {
                        String path = rootResources[i].getURL().getPath();
                        int index = path.indexOf(".jar!");
                        path = path.substring(0, index + 4);
                        path = StringUtils.trimLeadingString(path, "file:");
                        File file = new File(path);
                        String fpath = file.getAbsolutePath();
                        classPathResources.add(fpath);
                        sb.append(fpath).append(File.pathSeparator);
                    }
                }
            } catch (IOException e) {
                log.error("" + e, e);
            }
            return sb.toString();
        }


    }


    public static String getCompiledClassOfSource(String classFullName) {
        return LoadedSoruces.get(classFullName);

    }

    public static String getClasspath() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        StringBuilder classpath = new StringBuilder();

        try {
            // 处理 Spring Boot 的 LaunchedURLClassLoader
            if (classLoader.getClass().getName().equals("org.springframework.boot.loader.LaunchedURLClassLoader")) {
//                Method getUrlsMethod = classLoader.getClass().getMethod("getURLs");
//                URL[] urls = (URL[]) getUrlsMethod.invoke(classLoader);
//                for (URL url : urls) {
//                    addPath(classpath, url);
//                }
            }
            // 处理 JDK8 的 URLClassLoader
            else if (classLoader instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                for (URL url : urlClassLoader.getURLs()) {
                    addPath(classpath, url);
                }
            }
            // 处理 JDK11+ 的模块化类加载器
            else {
                try {
                    // 使用反射获取模块化类加载器的类路径
                    Field ucpField = classLoader.getClass().getDeclaredField("ucp");
                    ucpField.setAccessible(true);
                    Object ucp = ucpField.get(classLoader);
                    Field pathField = ucp.getClass().getDeclaredField("path");
                    pathField.setAccessible(true);

                    @SuppressWarnings("unchecked")
                    ArrayList<URL> paths = (ArrayList<URL>) pathField.get(ucp);
                    for (URL url : paths) {
                        addPath(classpath, url);
                    }
                } catch (Exception e) {
                    log.warn("Failed to get paths from JDK9+ classloader", e);
                }
            }

            // 添加系统类路径作为后备
            String sysClasspath = System.getProperty("java.class.path");
            if (sysClasspath != null && !sysClasspath.isEmpty()) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(sysClasspath);
            }

            return EscapeUtil.unescapeUrl(classpath.toString(), StandardCharsets.UTF_8.name());

        } catch (Exception e) {
            log.error("Failed to build classpath", e);
            // 终极回退方案
            return System.getProperty("java.class.path");
        }
    }

    private static void addPath(StringBuilder classpath, URL url) {
        try {
            String path = parseUrlToPath(url);
            if (path != null && !path.isEmpty()) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(path);
            }
        } catch (Exception e) {
            log.warn("Failed to parse URL: {}", url, e);
        }
    }

    private static String parseUrlToPath(URL url) throws Exception {
        // 处理 Spring Boot 嵌套 JAR（格式：jar:file:/xxx.jar!/BOOT-INF/lib/yyy.jar!/）
        if (url.toString().contains("!/BOOT-INF/lib/")) {
            return handleSpringBootNestedJar(url);
        }

        // 普通文件路径处理
        if ("file".equals(url.getProtocol())) {
            return new File(url.toURI()).getAbsolutePath();
        }

        // 通用 JAR 处理
        String path = url.getPath();
        if (path.contains("!")) {
            path = path.split("!")[0];
        }
        return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
    }

    private static String handleSpringBootNestedJar(URL url) {
        try {
            // Spring Boot 2.x 的临时解压目录格式
            String tempDirPrefix = "spring-boot-libs-";
            String originalPath = url.toString();

            // 提取外层 JAR 路径（如：/apps/xxx.jar）
            String outerJarPath = originalPath
                    .replaceFirst("jar:file:(.*)!.*", "$1")
                    .replace("file:", "");

            // 在临时目录中查找解压后的 JAR
            File tempLibDir = Arrays.stream(new File(System.getProperty("java.io.tmpdir")).listFiles())
                    .filter(f -> f.getName().startsWith(tempDirPrefix))
                    .findFirst()
                    .orElseThrow(() -> new IOException("Spring Boot temp lib directory not found"));

            // 提取内层 JAR 名称（如：spring-aop-5.3.31.jar）
            String nestedJarName = originalPath
                    .replaceFirst(".*BOOT-INF/lib/", "")
                    .replaceFirst("!.*", "");

            return new File(tempLibDir, nestedJarName).getAbsolutePath();
        } catch (Exception e) {
            log.warn("Failed to parse Spring Boot nested JAR: {}", url, e);
            return url.toString(); // 返回原始 URL 作为后备
        }
    }
}






