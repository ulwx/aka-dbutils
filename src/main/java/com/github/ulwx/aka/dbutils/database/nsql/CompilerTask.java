package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.nsql.compiler.newer.JavaStringCompiler;
import com.github.ulwx.aka.dbutils.database.nsql.compiler.older.CompilerUtils;
import com.github.ulwx.aka.dbutils.tool.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompilerTask {
    private static Logger log = LoggerFactory.getLogger(CompilerTask.class);
    private static volatile String classpath = buildClassPath();
    public static volatile Map<String, Class> LoadedClasses = new ConcurrentHashMap<String, Class>();
    public static volatile Map<String, String> LoadedSoruces = new ConcurrentHashMap<String, String>();
    public static volatile boolean UseThirdpartCompilerTool = true;
    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    static {

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    preCompileAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        if (DbContext.permitDebugLog()) {
            log.debug("to compile--" + ObjectUtils.toString(mdPathList));
        }
        for (int i = 0; i < mdPathList.size(); i++) {
            Integer index = i;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        preCompileSingle(mdPathList.get(index), UseThirdpartCompilerTool);
                    } catch (Exception e) {
                        log.error("" + e, e);
                    }
                }
            });
        }
        threadPool.shutdown();

    }

    public static Class<?> preCompileSingle(String mdPath, boolean useThirdpartCompilerTool) throws Exception {

        String packagePath = mdPath.substring(0, mdPath.lastIndexOf(".md"));
        // 获得类名
        int lastPos = packagePath.lastIndexOf(".");
        String className = packagePath.substring(lastPos + 1) + "Md";
        String packageName = packagePath.substring(0, lastPos);
        String classFullName = packageName + "." + className;
        Class aClass = null;

        aClass = LoadedClasses.get(classFullName);
        if (aClass == null) {
            try {
                SegmentLock.lock(classFullName);
                aClass = LoadedClasses.get(classFullName);
                if (aClass == null) {
                    String source = MDTemplate.parseFromMdFileToJavaSource(packageName, className);
                    if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
                        log.debug("to compile java class source:" + classFullName
                                // +" ;source="+source
                        );
                        log.debug("build classpath=" + classpath);
                    }
                    if (useThirdpartCompilerTool) {
                        JavaStringCompiler compiler = new JavaStringCompiler();
                        Map<String, byte[]> results = compiler.compile(className + ".java", source, classpath);
                        aClass = compiler.loadClass(classFullName, results);
                    } else {
                        aClass = CompilerUtils.compileAndLoadClass(classFullName, source, classpath);
                    }
                    LoadedClasses.put(classFullName, aClass);
                    LoadedSoruces.put(classFullName, source);
                }
            } finally {
                SegmentLock.unlock(classFullName);
            }

        } else {
            if (log.isDebugEnabled()) {
                // log.debug("source-" + CompilerTask.getCompiledClassOfSource(classFullName));
            }
        }
        return aClass;
    }

    /**
     * @MethodName : 创建classpath
     */
    private static String buildClassPath() {
        classpath = null;
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        ;
        if (parentClassLoader instanceof URLClassLoader) {
            URLClassLoader urlLoader = (URLClassLoader) parentClassLoader;
            //log.debug("parentClassLoader" + parentClassLoader);
            StringBuilder sb = new StringBuilder();
            for (URL url : urlLoader.getURLs()) {
                String p = url.getFile().substring(1);
                String os = System.getProperty("os.name");
                if (os.toLowerCase().startsWith("win")) {
                    p = StringUtils.trimLeadingString(url.getFile(), "/");
                }

                sb.append(p).append(File.pathSeparator);
            }
            String classpathStr = EscapeUtil.unescapeUrl(sb.toString(), System.getProperty("file.encoding"));
            return classpathStr;
        }

        return null;
    }


    public static String getCompiledClassOfSource(String classFullName) {
        return LoadedSoruces.get(classFullName);

    }


}






