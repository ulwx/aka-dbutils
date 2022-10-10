package com.github.ulwx.aka.dbutils.tool.support.path;

import com.github.ulwx.aka.dbutils.tool.support.Path;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class PathResourceUtils {
    /**
     * @param antPath ant格式路径，例如classpath:spring/*.xml当前class路径，
     *                如果为classpath*:spring/*.xml会查找所有类路径（包含jar）
     * @return
     * @throws IOException
     */
    public static Resource[] find(String antPath) throws IOException {
        PathMatchingResourcePatternResolver resourcePatternResolver
                = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(antPath);
        return resources;
    }

    public static ClassPathRootResource[] convertToClassPathRootResource(Resource[] resources) throws IOException {
        Resource[] rootResources = Path.getResourcesLikeAntPathMatch("classpath*:/");
        TreeSet<String> classRootResources = new TreeSet<>();
        for (int i = 0; i < rootResources.length; i++) {
            if (!rootResources[i].getURL().getProtocol().equals("jar")) {
                classRootResources.add(rootResources[i].getURL().getPath());
            }
        }
        List<ClassPathRootResource> classPathRootResourceList = new ArrayList<>();
        if (resources != null && resources.length > 0) {// 预编译
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].getURL().getProtocol().equals("jar")) {
                    String path = resources[i].getURL().getPath();
                    int index = path.indexOf(".jar!/");
                    String relativePath = path.substring(index + 6);
                    relativePath = com.github.ulwx.aka.dbutils.tool.support.StringUtils.trimTailStrings(relativePath, new String[]{"/", "\\"});
                    String pakagePath = relativePath.replace("/", ".");
                    if (pakagePath.startsWith("META-INF")) {
                        continue;
                    }
                    ClassPathRootResource classPathRootResource = new ClassPathRootResource(resources[i]);
                    classPathRootResource.setPackagePath(pakagePath);
                    classPathRootResource.setRelativePath(relativePath);
                    String classPath = path.substring(0, index + 4);
                    classPath = com.github.ulwx.aka.dbutils.tool.support.StringUtils.trimLeadingString(classPath, "file:");
                    classPathRootResource.setClassPath(classPath);
                    classPathRootResourceList.add(classPathRootResource);
                } else if (resources[i].getURL().getProtocol().equals("file")) {
                    Iterator<String> iterator = classRootResources.descendingIterator();
                    while (iterator.hasNext()) {
                        String str = iterator.next();
                        if (resources[i].getURL().getPath().startsWith(str)) {
                            String relativePath = resources[i].getURL().getPath().substring(str.length());
                            relativePath = StringUtils.trimTailStrings(relativePath, new String[]{"/", "\\"});
                            String pakagePath = relativePath.replace("/", ".");
                            if (pakagePath.startsWith("META-INF")) {
                                break;
                            }
                            ClassPathRootResource classPathRootResource = new ClassPathRootResource(resources[i]);
                            classPathRootResource.setPackagePath(pakagePath);
                            classPathRootResource.setRelativePath(relativePath);
                            classPathRootResource.setClassPath(new File(str).getAbsolutePath());
                            classPathRootResourceList.add(classPathRootResource);
                            break;
                        }
                    }
                }

            }
        }

        return classPathRootResourceList.toArray(new ClassPathRootResource[0]);

    }
}
