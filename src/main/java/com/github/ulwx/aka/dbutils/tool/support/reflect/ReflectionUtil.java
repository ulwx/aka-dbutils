package com.github.ulwx.aka.dbutils.tool.support.reflect;

import com.github.ulwx.aka.dbutils.database.MDataBase;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtil {

    public static <T, R> String getFieldName(CGetFun<T, R>  function) {
        return getFieldName((Serializable)function);
    }
    public static <R> String getFieldName( GetFun<R> function) {
        return getFieldName((Serializable)function);
    }
    private static <T, R> String getFieldName(Serializable function) {
        Field field = ReflectionUtil.getField(function);
        return field.getName();
    }
    private static Field getField(Serializable function) {
        return ReflectionUtil.findField(function);
    }
    private static Field findField(Serializable function) {
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
            field= ObjectUtils.getDeclaredField(aClass, fieldName, aClass);

        } catch (Exception e) {
        }
        // 第5步 如果没有找到对应的字段应该抛出异常
        if (field != null) {
            return field;
        }
        throw new NoSuchFieldError(fieldName);
    }
    public static Method[] getClassMethods(Class<?> cls) {
        Map<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = cls;
        while (currentClass != null && currentClass != Object.class) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            //获取接口中的所有方法
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            //获取父类，继续while循环
            currentClass = currentClass.getSuperclass();
        }

        Collection<Method> methods = uniqueMethods.values();

        return methods.toArray(new Method[methods.size()]);
    }

    private static void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            if (!currentMethod.isBridge()) {
                //获取方法的签名，格式是：返回值类型#方法名称:参数类型列表
                String signature = getSignature(currentMethod);
                //检查是否在子类中已经添加过该方法，如果在子类中已经添加过，则表示子类覆盖了该方法，无须再向uniqueMethods集合中添加该方法了
                if (!uniqueMethods.containsKey(signature)) {
                    if (canControlMemberAccessible()) {
                        try {
                            currentMethod.setAccessible(true);
                        } catch (Exception e) {
                            // Ignored. This is only a final precaution, nothing we can do.
                        }
                    }
                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }

    private static String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        if(Modifier.isSynchronized(method.getModifiers())){
            sb.append("synchronized  ");
        }
        if(Modifier.isPublic(method.getModifiers())){
            sb.append("public  ");

        } else if(Modifier.isProtected(method.getModifiers())){
            sb.append("protected  ");
        } else if(Modifier.isPrivate(method.getModifiers())){
            sb.append("private  ");
        }
        if(method.getTypeParameters().length>0){
            sb.append("<");
            for(int i=0; i<method.getTypeParameters().length; i++){
                if(i==0){
                    sb.append(method.getTypeParameters()[i].getTypeName());
                }else{
                    sb.append(","+method.getTypeParameters()[i].getTypeName());
                }
            }
            sb.append(">");
        }
        if(Modifier.isFinal(method.getModifiers())){
            sb.append("final  ");
        }



       if(Modifier.isStatic(method.getModifiers())){
           sb.append("static ");
       }
        Type returnType = method.getGenericReturnType();
        if (returnType != null) {
            sb.append(returnType.getTypeName()).append(' ');
        }
        sb.append(method.getName()+"(");
        Parameter[]  parameters=method.getParameters();
       // Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getParameterizedType().getTypeName()+" "+parameters[i].getName());
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Checks whether can control member accessible.
     *
     * @return If can control member accessible, it return {@literal true}
     * @since 3.5.0
     */
    private static boolean canControlMemberAccessible() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }
    public static void main(String[] args) throws Exception{
        Domain domain = new Domain();
        String s=ReflectionUtil.getFieldName(domain::getName);
        String s2=ReflectionUtil.getFieldName(domain::getName);
        String s3=ReflectionUtil.getFieldName(Domain::getName);
        MD.of(Domain::getName);

        System.out.println("s="+s+",s2="+s2+",s3="+s3);

        getClassMethods(MDataBase.class);

    }
}
