package com.github.ulwx.aka.dbutils.tool.support;

/**
 * <p>Title:Path </p>
 * <p>Description: 文件路径的处</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company:ldsoft </p>
 *
 * @author xuyw
 * @version 1.0
 */

import com.github.ulwx.aka.dbutils.tool.support.path.ClassPathRootResource;
import com.github.ulwx.aka.dbutils.tool.support.path.PPathResourceUtils;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public class Path {
    private static Logger log = LoggerFactory.getLogger(Path.class);

    /**
     * 得到当前工程的类加载路径
     *
     * @return
     */
    public static String getClassPath() {
        return getRootClassPath();
    }

    public static String getFilePathFromURI(URI uri) {
        try {
            return new File(uri).getAbsolutePath();

        } catch (Exception e) {
            log.error("" + e, e);
            return null;
        }
    }

    public static String getFilePathFromURL(URL url) {
        try {
            return new File(url.toURI()).getAbsolutePath();

        } catch (Exception e) {
            log.error("" + e, e);
            return null;
        }
    }

    public static String getRootClassPath() {

        String str = Path.class.getResource("/").getPath();
        if (!str.startsWith("file:")) {
            str = "file:" + str;
        }
        try {
            return new File(new URI(str)).getAbsolutePath();

        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 得到执行class的文件路径
     * @param clazz
     * @return
     */
    public static String getCurClassExecutePath(Class clazz) {
        try {
            return new File(clazz.getResource("").toURI()).getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRootClassPathFromCurTreadLoader(String context) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(context);
        if (url == null) return "";
        String protocol = url.getProtocol();

        // 如果是以文件的形式保存在服务器上
        if ("file".equals(protocol)) {
            String filePath = "";
            try {
                filePath = URLDecoder.decode(url.getFile(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block

            }
            return filePath;
        }
        return "";
    }

    /**
     * 根据文件后缀在某个目录下查找（是否递归）文件并将绝对文件路径名称存入到fileNames里
     * @param fromDir
     * @param recursive
     * @param fileNames
     * @param suffix
     */
    public static void findAndAddInPackageByFile(String fromDir, final boolean recursive,
                                                 List<String> fileNames, String suffix) {

        // 获取此包的目录 建立一个File
        File dir = new File(fromDir);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以suffix结尾的文件
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(suffix));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddInPackageByFile(file.getAbsolutePath(), recursive,
                        fileNames, suffix);
            } else {
                String fileName = file.getAbsolutePath();
                try {
                    // 添加到集合中去
                    fileNames.add(fileName);
                } catch (Exception e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    log.error("", e);
                }
            }
        }

    }

    public static String getCurClassExecutePath() {
        return getCurClassExecutePath(Path.class);
    }

    public static String getRootClassPath(String name) {
        return getRootClassPath() + File.separator + name;
    }


    /**
     * 可读入jar包里的文件,传入的参数如：/1.xml
     *
     * @param relaPathFile
     * @return
     */
    public static InputStream getResource(String relaPathFile) {
        return Path.class.getResourceAsStream(relaPathFile);
    }


    /**
     * <p>可以指定如：<code><file:/dbpool.xml，classpath:mysql/dbpool.xml，classpath*:mysql/dbpool.xml</code>等样式的路径。</p>
     * <p>如果没有指定<code>"file:"， "classpath:"，"classpath*:"</code> 前缀，则系统会默认添加classpath*:前缀。</p>
     * <p>
     * 例如：
     * <ul><li><code>/mysql/dbpool.xml</code>，则为<code>classpath*:/mysql/dbpool.xml</code>；</li>
     * <li>如果为<code>mysql/dbpool.xml</code>，则为<code>classpath*:mysql/dbpool.xml</code>。
     * <br/>注意：<code>/mysql/dbpool.xml</code>和 <code>mysql/dbpool.xml </code>效果相同。</li>
     *
     *</ul>
     * </p>
     * @param pathName
     * @return
     * @throws IOException
     */
    public static Resource[] getResources(String pathName)throws IOException {
        Resource[] resources=null;
        if (pathName.startsWith("file:") ||
                pathName.startsWith("classpath:") ||
                pathName.startsWith("classpath*:")) {
            resources = Path.getResourcesLikeAntPathMatch(pathName);

        }  else {
            resources = Path.getResourcesLikeAntPathMatch("classpath*:" + pathName);
        }
        return resources;
    }

    /**
     * 打印Resource[]数组
     * @param resources
     * @return
     * @throws Exception
     */
    public static String printResources(Resource[] resources) throws Exception{
        String str="";
        for (Resource resource : resources) {
            if (str.isEmpty()) {
                str = resource.getURL().toString();
            } else {
                str = str + " ; " + resource.getURL().toString();
            }
        }
        return str;
    }

    /**
     * 根据location查找的资源文件是否只能有一个存在，如果存在多个会报异常
     * @param resources
     * @param location
     */
    public static void checkResource(Resource[] resources, String location)  throws Exception{
        if (resources == null || resources.length == 0) {
            throw new RuntimeException("错误！没有找到" + location + "配置文件!");
        } else if(resources.length == 1){
            if(!resources[0].exists()){
                throw new Exception("错误！" + location + "配置文件不存在!");
            }
        }else if (resources.length >1) {
            String str = Path.printResources(resources);
            throw new Exception("错误！根据" + location + "找到多个文件![" + str + "]");
        }
    }
    /**
     * 类 似于Spring的PathMatchingResourcePatternResolver用法
     *
     * @param antPath ant格式路径，例如file:/c:/spring/*.xml，classpath:spring/*.xml
     *                ，classpath*:spring/*.xml当前class路径，如果为classpath*:spring/*.xml会查找所有类路径（包含jar）
     * @return
     * @throws IOException
     */
    public static Resource[] getResourcesLikeAntPathMatch(String antPath) throws IOException {
        return PPathResourceUtils.find(antPath);
    }

    public static ClassPathRootResource[] convertToClassPathRootResource(Resource[] resources) throws IOException {
        return PPathResourceUtils.convertToClassPathRootResource(resources);
    }

    public static InputStream getClassPathResource(String fileName)
            throws IOException {

        FileInputStream fin = new FileInputStream(getRootClassPath(fileName));
        return fin;
    }

    public static void main(String args[]) throws Exception {
        String str = Path.getCurClassExecutePath();
        List<String> list = new ArrayList<>();
        findAndAddInPackageByFile(str, true, list, ".class");

        System.out.println(ObjectUtils.toString(list));

        System.out.println(getRootClassPath("com"));
        System.out.println(getRootClassPath());

    }

}
