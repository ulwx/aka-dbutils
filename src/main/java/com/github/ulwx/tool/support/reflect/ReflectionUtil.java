package com.github.ulwx.tool.support.reflect;

import com.github.ulwx.tool.support.ObjectUtils;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {

    public static <T, R> String getFieldName(GetFun<T, R> function) {
        Field field = ReflectionUtil.getField(function);
        return field.getName();
    }
    public static Field getField(GetFun<?, ?> function) {
        return ReflectionUtil.findField(function);
    }
    public static Field findField(GetFun<?, ?> function) {
        Field field = null;
        String fieldName = null;
        try {
            // 第1步 获取SerializedLambda
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
            // 第2步 implMethodName 即为Field对应的Getter方法名
            String implMethodName = serializedLambda.getImplMethodName();
            if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
                fieldName = Introspector.decapitalize(implMethodName.substring(3));

            } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
                fieldName = Introspector.decapitalize(implMethodName.substring(2));
            } else if (implMethodName.startsWith("lambda$")) {
                throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");

            } else {
                throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
            }
            // 第3步 获取的Class是字符串，并且包名是“/”分割，需要替换成“.”，才能获取到对应的Class对象
            String declaredClass = serializedLambda.getImplClass().replace("/", ".");
            Class<?> aClass = Class.forName(declaredClass);
            //第4步  找到对应的字段
            field=ObjectUtils.getDeclaredField(aClass, fieldName, aClass);

        } catch (Exception e) {
        }
        // 第5步 如果没有找到对应的字段应该抛出异常
        if (field != null) {
            return field;
        }
        throw new NoSuchFieldError(fieldName);
    }

    public static void main(String[] args) throws Exception{
        Domain domain = new Domain();
        String s=ReflectionUtil.getFieldName(domain::getName);
        ReflectionUtil.getFieldName(domain::getName);
        ReflectionUtil.getFieldName(domain::getName);

    }
}
