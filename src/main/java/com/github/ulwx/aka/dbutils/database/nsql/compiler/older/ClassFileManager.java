package com.github.ulwx.aka.dbutils.database.nsql.compiler.older;

import javax.tools.*;
import java.io.IOException;

/**
 * 类文件管理器 用于JavaCompiler将编译好后的class，保存到jclassObject中
 */
public class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    /**
     * 保存编译后Class文件的对象
     */
    private JavaClassObject jclassObject;

    @Override
    public void close() throws IOException {
        super.close();
        jclassObject.close();

    }

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