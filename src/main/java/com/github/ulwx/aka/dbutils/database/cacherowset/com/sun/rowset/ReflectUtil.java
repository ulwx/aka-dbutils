

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset;


public final class ReflectUtil {

    private ReflectUtil() {
    }

    public static Class<?> forName(String name)
            throws ClassNotFoundException {
        return Class.forName(name);
    }

    public static Object newInstance(Class<?> cls)
            throws InstantiationException, IllegalAccessException {
        return cls.newInstance();
    }

}
