package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.DataBase.SQLType;
import com.github.ulwx.aka.dbutils.database.MDMethods.*;
import com.github.ulwx.aka.dbutils.database.MDMethods.InsertOptions.ReturnFlag;
import com.github.ulwx.aka.dbutils.database.annotation.AkaParam;
import com.github.ulwx.aka.dbutils.database.nsql.NSQL;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.support.NumberUtils;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.type.*;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapperFactory {
    private static Logger log = LoggerFactory.getLogger(DataBase.class);

    /**
     * 根据type指定的接口生成动态代理。type接口里的方法映射到名称相同的md方法
     *
     * @param mapperType 指定抽象接口的类型，从而生成代理对象
     * @param <T>
     * @return 返回继承type接口的代理对象
     */
    public static <T> T getMapper(Class<T> mapperType, MDataBase mDataBase) {
        if (!AkaMapper.class.isAssignableFrom(mapperType)) {
            throw new DbException("aka-dbutils mapper必须继承自" + AkaMapper.class.getName() + "!");
        }
        MapperProxy<T> mapperProxy = new MapperProxy<T>(mDataBase, mapperType);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(mapperType);
        enhancer.setCallback(mapperProxy);
        T mapperObj = (T) enhancer.create();
        ((AkaMapper) mapperObj).setMdDataBase(mDataBase);
        return mapperObj;
    }

    public static class MapperProxy<T> implements MethodInterceptor, Serializable {

        private static final long serialVersionUID = -6424540398559729839L;
        private final MDataBase mDataBase;
        private final Class<T> mapperType;

        public MapperProxy(MDataBase mDataBase, Class<T> mapperType) {
            this.mDataBase = mDataBase;
            this.mapperType = mapperType;
        }

        private String methodInfo(Method method) {
            return method.getDeclaringClass().getName() + "#" + method.getName() + "(...)";
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return invoke(obj, method, args, proxy);
        }

        public Object invoke(Object proxy, Method method, Object[] args, MethodProxy mthodProxy) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (method.isDefault()) {
                return invokeDefaultMethod(proxy, method, args);
            }
            if (proxy instanceof AkaMapper) {
                if (method.getDeclaringClass() == AkaMapper.class) {
                    return mthodProxy.invokeSuper(proxy, args);
                }
            } else {
                throw new DbException("类" + mapperType.getName() + "必须继承" + AkaMapper.class.getName() + "接口！");
            }
            if (!Modifier.isAbstract(method.getModifiers())) {

                return mthodProxy.invokeSuper(proxy, args);
            }

            Map<String, Object> argMap = new HashMap<>();
            PageOptions pageOptions = null;
            MapNestOptions mapNestOptions = null;
            InsertOptions insertOptions = null;
            if (args == null || args.length == 0) {
                ///
            } else {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    Parameter parameter = method.getParameters()[i];
                    Class parmType = parameter.getType();
                    Type parmGenericType = parameter.getParameterizedType();
                    if (arg instanceof Map || Map.class.isAssignableFrom(parmType)) { //存放参数的Map<String,Object>类型
                        if (parmGenericType instanceof ParameterizedType) {
                            Type[] types = ((ParameterizedType) parmGenericType).getActualTypeArguments();
                            Class keyClass = (Class) types[0];
                            Class valeClass = (Class) types[1];
                            if (keyClass == String.class && valeClass == Object.class) {
                                argMap.putAll((Map<String, Object>) arg);
                                continue;
                            }
                        }
                        throw new DbException(methodInfo(method) + "方法的参数类型[" + parmGenericType.getTypeName() + "]不被支持！方法参数类型可以定义Map<String,Object>类型!");

                    } else if (arg instanceof PageOptions || PageOptions.class.isAssignableFrom(parmType)) {
                        pageOptions = (PageOptions) arg;
                    } else if (arg instanceof InsertOptions || InsertOptions.class.isAssignableFrom(parmType)) {
                        insertOptions = (InsertOptions) arg;
                    } else if (arg instanceof MapNestOptions || MapNestOptions.class.isAssignableFrom(parmType)) {
                        mapNestOptions = (MapNestOptions) arg;
                    } else {
                        String argName = null;
                        AkaParam akaParam = parameter.getAnnotation(AkaParam.class);
                        if (akaParam != null) {
                            String annotationValue = StringUtils.trim(akaParam.value());
                            if (annotationValue.isEmpty()) {
                                argName = parameter.getName();
                            } else {
                                argName = annotationValue;
                            }
                        } else {
                            argName = parameter.getName();
                        }

                        if (checkSupportParmaterType(parmType)) {
                            argMap.put(argName, arg);
                        } else if (parmType.isArray() || List.class.isAssignableFrom(parmType)) {
                            //如果类型为数组和List，元素类型必须为基本类型
                            Class componetType = null;
                            if (parmType.isArray()) {
                                componetType = parmType.getComponentType();
                            } else {
                                if (arg != null) {
                                    List argList = (List) arg;
                                    if (argList.size() > 0) {
                                        componetType = argList.get(0).getClass();
                                    }
                                }
                            }
                            if (componetType != null) {
                                if (!checkSupportParmaterType(componetType)) {
                                    throw new DbException(methodInfo(method) + "方法声明的形参类型如果为List类型或数组类型，" +
                                            "则其中元素类型必须为java原始类型（及其包装类型）、String类型、" +
                                            "日期类型（java日期和java.sql下日期类型）、BigDecimal、BigInteger！");
                                } else {
                                    argMap.put(argName, arg);
                                }
                            } else {
                                argMap.put(argName, null);
                            }
                        } else if (!isJavaClass(parmType)) {//如果为自定义的java类型，转换成参数Map<String,Object>
                            argMap.putAll(MD.map(arg));
                        } else {
                            throw new DbException(methodInfo(method) + "方法的参数类型[" + parmType.getTypeName() + "]不被支持！");
                        }

                    }
                }
            }
            return this.executeMdMethod(argMap, method.getGenericReturnType(), pageOptions,
                    mapNestOptions, insertOptions, this.mDataBase, method);

        }

        public static boolean checkSupportParmaterType(Class clazz) {
            return checkSimpleType(clazz) || checkDateType(clazz) || checkMathNumberType(clazz);
        }

        public static boolean checkSimpleType(Class clazz) {
            return ObjectUtils.isPrimitiveWapper(clazz) || clazz.isPrimitive() || clazz == String.class;
        }

        public static boolean checkDateType(Class clazz) {
            return clazz == LocalDate.class || clazz == LocalDateTime.class || clazz == LocalTime.class
                    || clazz == Date.class || clazz == java.sql.Time.class || clazz == java.sql.Date.class
                    || clazz == java.sql.Timestamp.class;
        }

        public static boolean checkMathNumberType(Class clazz) {
            return clazz == BigInteger.class || clazz == BigDecimal.class;
        }

        public static boolean isJavaClass(Class<?> clz) {
            return clz != null && clz.getClassLoader() == null;
        }

        private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
                throws Throwable {
            final Constructor<Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            final Class<?> declaringClass = method.getDeclaringClass();
            return constructor
                    .newInstance(declaringClass,
                            MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                    | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                    .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
        }


        private Object returnListTypeHandler(Type listComponetType,
                                             MapNestOptions mapNestOptions,
                                             PageOptions pageOptions,
                                             MDataBase mdataBase,
                                             String mdMethodName,
                                             Map<String, Object> args,
                                             String errMsg,
                                             Method method) {
            if (listComponetType instanceof Class) {
                Class typesClassType = (Class) listComponetType;
                if (Map.class.isAssignableFrom(typesClassType)) {
                    if (pageOptions == null && mapNestOptions == null) {
                        return mdataBase.queryMap(mdMethodName, args);
                    } else if (pageOptions != null && mapNestOptions == null) {
                        return mdataBase.queryMap(mdMethodName,
                                args, pageOptions.getPage(),
                                pageOptions.getPerPage(), pageOptions.getPageBean(), pageOptions.getMdFullMethodNameForCountSql());
                    } else {
                        throw new DbException(methodInfo(method) + "方法如果返回List<Map<String,Object>>类型，则方法不能定义MapNestOptions类型形参！");
                    }
                } else if (Iterable.class.isAssignableFrom(typesClassType)
                        || typesClassType.isArray()) {
                    throw new DbException(methodInfo(method) + "方法返回的泛型类型定义非法，可以定义为List<X>，其中" +
                            "X只能为Map<String,Object>、自定义JavaBean类型、Integer等原始类型的封装类型、" +
                            "java.sql下日期类型（Time、Date、Timestamp），BigInteger，BigDecimal。" +
                            "如：List<Map<String,Object>> ,List<User>,List<String>,List<Integer>。");
                } else if (!isJavaClass(typesClassType)) {//自定义类型
                    if (pageOptions == null && mapNestOptions == null) {
                        return mdataBase.queryList((Class) listComponetType, mdMethodName, args);
                    } else if (pageOptions != null && mapNestOptions == null) {
                        return mdataBase.queryList((Class) listComponetType, mdMethodName, args, pageOptions.getPage(),
                                pageOptions.getPerPage(), pageOptions.getPageBean(),
                                pageOptions.getMdFullMethodNameForCountSql());
                    } else if (pageOptions == null && mapNestOptions != null) {
                        if (mapNestOptions instanceof One2OneMapNestOptions) {
                            One2OneMapNestOptions one2OneOpts = (One2OneMapNestOptions) mapNestOptions;
                            return mdataBase.queryListOne2One((Class) listComponetType,
                                    mdMethodName, args, one2OneOpts);
                        } else if (mapNestOptions instanceof One2ManyMapNestOptions) {
                            One2ManyMapNestOptions one2ManyOpts = (One2ManyMapNestOptions) mapNestOptions;
                            return mdataBase.queryListOne2Many((Class) listComponetType, mdMethodName, args,
                                    one2ManyOpts);
                        } else {
                            ////
                        }
                    } else if (pageOptions != null && mapNestOptions != null) {
                        if (mapNestOptions instanceof One2OneMapNestOptions) {
                            One2OneMapNestOptions one2OneOpts = (One2OneMapNestOptions) mapNestOptions;
                            return mdataBase.queryListOne2One((Class) listComponetType,
                                    mdMethodName, args, one2OneOpts, pageOptions.getPage(),
                                    pageOptions.getPerPage(), pageOptions.getPageBean(), pageOptions.getMdFullMethodNameForCountSql());
                        } else if (mapNestOptions instanceof One2ManyMapNestOptions) {
                            throw new DbException(methodInfo(method) + "方法为一对多关联查询，方法不支持分页功能，即不能定义PageOptions类型形参，相关解决方法请参考帮助文档！");
                        } else {

                        }
                    }
                }

            }
            throw new DbException(errMsg);
        }


        private static Class simpleType2TSimpleType(Class returnClass) {
            Class newReturnClass = returnClass;
            if (returnClass == Integer.class || returnClass == int.class) {
                newReturnClass = TInteger.class;
            } else if (returnClass == String.class) {
                newReturnClass = TString.class;
            } else if (returnClass == Double.class || returnClass == double.class) {
                newReturnClass = TDouble.class;
            } else if (returnClass == Long.class || returnClass == long.class) {
                newReturnClass = TLong.class;
            } else if (returnClass == Float.class || returnClass == float.class) {
                newReturnClass = TFloat.class;
            } else if (returnClass == Character.class || returnClass == char.class) {
                newReturnClass = TChar.class;
            } else if (returnClass == Byte.class || returnClass == byte.class) {
                newReturnClass = TByte.class;
            } else if (returnClass == Boolean.class || returnClass == boolean.class) {
                newReturnClass = TBoolean.class;
            } else if (returnClass == Short.class || returnClass == short.class) {
                newReturnClass = TShort.class;
            } else if (returnClass == LocalDate.class) {
                newReturnClass = TLocalDate.class;
            } else if (returnClass == LocalDateTime.class) {
                newReturnClass = TLocalDateTime.class;
            } else if (returnClass == LocalTime.class) {
                newReturnClass = TLocalTime.class;
            } else if (returnClass == Date.class) {
                newReturnClass = TDate.class;
            } else if (returnClass == java.sql.Date.class) {
                newReturnClass = TSqlDate.class;
            } else if (returnClass == java.sql.Time.class) {
                newReturnClass = TSqlTime.class;
            } else if (returnClass == java.sql.Timestamp.class) {
                newReturnClass = TSqlDateTime.class;
            } else if (returnClass == BigInteger.class) {
                newReturnClass = TBigInteger.class;
            } else if (returnClass == BigDecimal.class) {
                newReturnClass = TBigDecimal.class;
            }

            return newReturnClass;
        }

        private static Object returnValueFromTSimpleType(Class returnClass, Object ret) {
            if (ret instanceof TType) {
                if (((TType) ret).getWrappedClass() == returnClass) {
                    return ((TType) ret).getValue();
                }
            } else if (ret instanceof TResult) {
                return ((TResult) ret).getValue();
            }
            return ret;
        }

        private static Object convertListToTargetType(List list, Class list0Type, Type types0) {
            Object ret = list;
            if (list0Type == TInteger.class && types0 == Integer.class) {
                ret = list.stream().map(i -> {
                    return ((TInteger) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TLong.class && types0 == Long.class) {
                ret = list.stream().map(i -> {
                    return ((TLong) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TFloat.class && types0 == Float.class) {
                ret = list.stream().map(i -> {
                    return ((TFloat) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TDouble.class && types0 == Double.class) {
                ret = list.stream().map(i -> {
                    return ((TDouble) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TString.class && types0 == String.class) {
                ret = list.stream().map(i -> {
                    return ((TString) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TShort.class && types0 == Short.class) {
                ret = list.stream().map(i -> {
                    return ((TShort) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TChar.class && types0 == Character.class) {
                ret = list.stream().map(i -> {
                    return ((TChar) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TBoolean.class && types0 == Boolean.class) {
                ret = list.stream().map(i -> {
                    return ((TBoolean) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TByte.class && types0 == Byte.class) {
                ret = list.stream().map(i -> {
                    return ((TByte) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TLocalDate.class && types0 == LocalDate.class) {
                ret = list.stream().map(i -> {
                    return ((TLocalDate) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TLocalDateTime.class && types0 == LocalDateTime.class) {
                ret = list.stream().map(i -> {
                    return ((TLocalDateTime) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TLocalTime.class && types0 == LocalTime.class) {
                ret = list.stream().map(i -> {
                    return ((TLocalTime) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TDate.class && types0 == Date.class) {
                ret = list.stream().map(i -> {
                    return ((TDate) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TSqlDate.class && types0 == java.sql.Date.class) {
                ret = list.stream().map(i -> {
                    return ((TSqlDate) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TSqlTime.class && types0 == java.sql.Time.class) {
                ret = list.stream().map(i -> {
                    return ((TSqlTime) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TSqlDateTime.class && types0 == java.sql.Timestamp.class) {
                ret = list.stream().map(i -> {
                    return ((TSqlDateTime) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TBigInteger.class && types0 == BigInteger.class) {
                ret = list.stream().map(i -> {
                    return ((TBigInteger) i).getValue();
                }).collect(Collectors.toList());
            } else if (list0Type == TBigDecimal.class && types0 == BigDecimal.class) {
                ret = list.stream().map(i -> {
                    return ((TBigDecimal) i).getValue();
                }).collect(Collectors.toList());
            }
            return ret;
        }

        public Object executeMdMethod(
                Map<String, Object> args,
                Type returnType,
                PageOptions pageOptions,
                MapNestOptions mapNestOptions,
                InsertOptions insertOptions,
                MDataBase mdataBase,
                Method method) {
            String methodName = method.getName();
            String mdMethodName = MD.md(method.getDeclaringClass(), methodName);
            NSQL nsql = NSQL.getNSQL(mdMethodName, args);
            SQLType sqlType = nsql.getSqlType();
            if (sqlType == SQLType.SELECT) {
                String errMsg = methodInfo(method) + "为查询方法，其定义的返回类型不支持！" +
                        "返回类型只能定义为DataBaseSet或List<X>,其中X为Map<String,Object>类型或自定义JavaBean" +
                        "或Integer等原始类型的封装类型，如：List<Map<String,Object>>,List<User>,List<String>,List<Integer>。";
                if (returnType instanceof Class) {
                    Class returnClass = (Class) returnType;
                    if (returnClass == DataBaseSet.class) {
                        if (pageOptions == null) {
                            return mdataBase.queryForResultSet(mdMethodName, args);
                        } else if (pageOptions != null) {
                            return mdataBase.queryForResultSet(
                                    mdMethodName, args,
                                    pageOptions.getPage(), pageOptions.getPerPage(),
                                    pageOptions.getPageBean(), pageOptions.getMdFullMethodNameForCountSql());
                        }
                        throw new DbException(errMsg);
                    } else if (Iterable.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
                        throw new DbException(errMsg);
                    } else {
                        if (checkSupportParmaterType(returnClass)) {
                            Class newReturnClass = simpleType2TSimpleType(returnClass);
                            Object ret = mdataBase.queryOne(newReturnClass, mdMethodName, args);
                            return returnValueFromTSimpleType(returnClass, ret);
                        } else if (!isJavaClass(returnClass)) {//自定义类型
                            if (pageOptions == null && mapNestOptions == null && !isJavaClass(returnClass)) {
                                return mdataBase.queryOne(returnClass, mdMethodName, args);
                            }
                        }

                    }
                    throw new DbException(errMsg);
                } else if (returnType instanceof TypeVariable ||
                        returnType instanceof GenericArrayType) {
                    throw new DbException(errMsg);
                } else if (returnType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) returnType;
                    Type[] types = parameterizedType.getActualTypeArguments();
                    Class rawTypeClass = (Class) parameterizedType.getRawType();
                    if (List.class.isAssignableFrom(rawTypeClass)) {
                        Type inType = types[0];
                        if (types[0] instanceof Class) {
                            inType = simpleType2TSimpleType((Class) types[0]);
                        }
                        Object ret = this.returnListTypeHandler(inType, mapNestOptions, pageOptions,
                                mdataBase, mdMethodName, args, errMsg, method);
                        if (ret != null && List.class.isAssignableFrom(ret.getClass())) {
                            List list = (List) ret;
                            if (list.size() > 0) {
                                Class list0Type = list.get(0).getClass();
                                ret = convertListToTargetType(list, list0Type, types[0]);
                            }
                        }
                        return ret;
                    }
                }//
                throw new DbException(errMsg);
            } else if (sqlType == SQLType.INSERT || sqlType == SQLType.UPDATE ||
                    sqlType == SQLType.DELETE) {
                if (pageOptions != null || mapNestOptions != null) {
                    throw new DbException(methodInfo(method) + "方法对应的SQL语句为更新操作，所以方法定义的形参类型不能有PageOptions和MapNestOptions类型！");
                }
                if (returnType instanceof Class) {
                    Class t = (Class) returnType;
                    Object ret = null;
                    if (t == int.class || t == Integer.class || t == long.class || t == Long.class
                            || t == void.class) {
                        if (sqlType == SQLType.INSERT) {
                            if (insertOptions != null && insertOptions.getReturnFlag() == ReturnFlag.AutoKey) {
                                ret = mdataBase.insertReturnKey(mdMethodName, args);
                            } else {
                                ret = mdataBase.insert(mdMethodName, args);
                            }

                        } else if (sqlType == SQLType.UPDATE) {
                            ret = mdataBase.update(mdMethodName, args);
                        } else if (sqlType == SQLType.DELETE) {
                            ret = mdataBase.del(mdMethodName, args);

                        } else {
                            ////
                        }
                        if (t == void.class) {
                            return null;
                        } else {
                            ret = NumberUtils.convertNumberToTargetClass((Number) ret, t);
                            return ret;
                        }
                    }
                }
                throw new DbException(methodInfo(method) + "为更新方法，则返回类型只能为int/Integer,long/Long,void类型");
            } else {
                throw new DbException(methodInfo(method) + "方法对应的语句类型" + sqlType + "不支持!");
            }

        }


    }
}
