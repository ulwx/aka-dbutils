package com.github.ulwx.aka.dbutils.database.nsql.compiler.older;

import com.github.ulwx.aka.dbutils.database.nsql.CompilerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义加载器，一个类可以重新加载，但每次加载都必须用一个新的ClassDataClassLoader实例用于加载同一个类的字节码，才能做到重复加载。
 * <p>
 * java虚拟机识别一个类是根据加载器实例+全路径包名来的，如果做到重复加载，必须不同的加载器实例来加载同一个类的字节码
 */
public class ClassDataClassLoader extends ClassLoader {
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