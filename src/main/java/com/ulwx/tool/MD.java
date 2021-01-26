package com.ulwx.tool;

/**处理md文件工具类*/
public class MD {
    public static String md(Class daoClass, String method) {
        String prefix = daoClass.getName();
        return prefix + ".md:" + method;
    }
    public static String  md() {
        return md(2);
    }
    public static String  md(int level) {
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        StackTraceElement ste = stack[level];
        String className=ste.getClassName();
        String methodName=ste.getMethodName();
        return className+".md:"+methodName;
    }

    public static void main(String[] args) {
        md();
    }
}
