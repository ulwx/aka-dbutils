package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.reflect.CGetFun;
import com.github.ulwx.aka.dbutils.tool.support.reflect.GetFun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MD {
    public static String md(Class daoClass, String method) {
        String prefix = daoClass.getName();
        return prefix + ".md:" + method;
    }
    public static String  md() {
        return md(2);
    }
    private static String  md(int level) {
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        StackTraceElement ste = stack[level];
        String className=ste.getClassName();
        String methodName=ste.getMethodName();
        return className+".md:"+methodName;
    }
    public static Object[] o(Object... args){
        return args;
    }
    public static String[] of(String... args){
        return args;
    }
    public static <R> GetFun<R>[] of(GetFun<R>... args){
        return args;
    }
    public static <T, R>  CGetFun<T, R>[] of(CGetFun<T, R>... args){
        return args;
    }
    public static Map<String, Object>[] maps(Object... javaBeans){
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i=0; i<javaBeans.length; i++){
            list.add(ObjectUtils.fromJavaBeanToMap(javaBeans[i]));
        }
        return list.toArray(new HashMap[0]);
    }
    public static List<Map<String,Object>> mapList(Object... javaBeans){
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i=0; i<javaBeans.length; i++){
            list.add(ObjectUtils.fromJavaBeanToMap(javaBeans[i]));
        }
        return list;
    }
    public static Map<String, Object> map(Object javaBean){
       return ObjectUtils.fromJavaBeanToMap(javaBean);
    }



}
