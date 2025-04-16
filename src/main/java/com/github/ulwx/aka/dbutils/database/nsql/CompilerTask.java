package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.nsql.compiler.newer.JavaStringCompiler;
import com.github.ulwx.aka.dbutils.database.nsql.compiler.older.CompilerUtils;
import com.github.ulwx.aka.dbutils.tool.support.*;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompilerTask {
    private static Logger log = LoggerFactory.getLogger(CompilerTask.class);
    private static volatile String classpath = buildClassPath();
    public static volatile Map<String, Class> LoadedClasses = new ConcurrentHashMap<String, Class>();
    public static volatile Map<String, String> LoadedSoruces = new ConcurrentHashMap<String, String>();
    public static volatile boolean UseThirdpartCompilerTool = false;
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
                        compileSingle(mdPathList.get(index), UseThirdpartCompilerTool);
                    } catch (Exception e) {
                        log.error("" + e, e);
                    }
                }
            });
        }
        threadPool.shutdown();

    }

    public static Class<?> compileSingle(String mdPath, boolean useThirdpartCompilerTool) throws Exception {


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
                        if (useThirdpartCompilerTool) {
                            JavaStringCompiler compiler = new JavaStringCompiler();
                            Map<String, byte[]> results = compiler.compile(className + ".java", source, classpath);
                            aClass = compiler.loadClass(classFullName, results);
                            log.debug("compile " +
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
            URLClassLoader urlLoader = (URLClassLoader) parentClassLoader;
            //log.debug("parentClassLoader" + parentClassLoader);
            StringBuilder sb = new StringBuilder();
            for (URL url : urlLoader.getURLs()) {
                String p = Path.getFilePathFromURL(url);
                sb.append(p).append(File.pathSeparator);
            }
            String classpathStr = EscapeUtil.unescapeUrl(sb.toString(), System.getProperty("file.encoding"));
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


}






