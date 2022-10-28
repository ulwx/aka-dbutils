package com.github.ulwx.aka.dbutils.tool.support.path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class ClassPathRootResource implements Resource {
    private Resource resource;
    private String packagePath;
    private String relativePath;
    private String classPath;

    /**
     * 得到资源类路径
     * @return
     */
    public String getClassPath() {
        return classPath;
    }

    /**
     * 得到相对classpath root的相对文件路径
     * @return
     */
    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public ClassPathRootResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * 得到资源
     * @return
     */
    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }


    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public URL getURL() throws IOException {
        return resource.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return resource.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return resource.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return resource.lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return resource.createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return resource.getFilename();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }
}
