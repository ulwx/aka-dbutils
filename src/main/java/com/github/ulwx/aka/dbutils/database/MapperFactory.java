package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.DataBase.SQLType;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.MapNestOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.PageOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.PageOptions.InsertOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.PageOptions.InsertOptions.ReturnFlag;
import com.github.ulwx.aka.dbutils.database.annotation.AkaParam;
import com.github.ulwx.aka.dbutils.database.nsql.NSQL;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MapperFactory {
    private static Logger log = LoggerFactory.getLogger(DataBase.class);
    private static volatile Map<Class, Object> mappersCache = new ConcurrentHashMap<>();

    public static <T> T getMapper(Class<T> mapperInterfaceType, MDataBase mDataBase) {
        T mapperObj = (T) mappersCache.get(mapperInterfaceType);
        if (mapperObj == null) {
            synchronized (MapperFactory.class) {
                mapperObj = (T) mappersCache.get(mapperInterfaceType);
                if (mapperObj == null) {
                    MapperProxy<T> mapperProxy = new MapperProxy<T>(mDataBase, mapperInterfaceType);
                    mapperObj = (T) Proxy.newProxyInstance(mapperInterfaceType.getClassLoader(),
                            new Class[]{mapperInterfaceType}, mapperProxy);
                }
            }
        }
        return mapperObj;
    }

    public static class MapperProxy<T> implements InvocationHandler, Serializable {

        private static final long serialVersionUID = -6424540398559729839L;
        private final MDataBase mDataBase;
        private final Class<T> mapperInterface;

        public MapperProxy(MDataBase mDataBase, Class<T> mapperInterface) {
            this.mDataBase = mDataBase;
            this.mapperInterface = mapperInterface;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (method.isDefault()) {
                return invokeDefaultMethod(proxy, method, args);
            }
            String methodName = method.getName();
            String mdMethodName = MD.md(method.getDeclaringClass(), methodName);
            DataBase dataBase = this.mDataBase.getDataBase();
            Map<String, Object> argMap = new HashMap<>();
            PageOptions pageOptions = null;
            MapNestOptions mapNestOptions = null;
            InsertOptions insertOptions=null;
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
                        log.warn("方法的参数类型[" + parmGenericType + "]不被支持！方法参数类型可以定义Map<String,Object>类型!");

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

                        if (SqlUtils.checkedSimpleType(parmType)) {
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
                                if (!SqlUtils.checkedSimpleType(componetType)) {
                                    throw new DbException("方法声明的形参类型如果为List类型或数组类型，则其中元素类型必须为java简单类型！");
                                } else {
                                    argMap.put(argName, arg);
                                }
                            } else {
                                argMap.put(argName, null);
                            }
                        } else if (!isJavaClass(parmType)) {//如果为自定义的java类型，转换成参数Map<String,Object>
                            argMap.putAll(MD.map(arg));
                        } else {
                            log.warn("方法的参数类型[" + parmType + "]不被支持！");
                        }

                    }
                }
            }
            return this.executeMdMethod(mdMethodName, argMap, method.getGenericReturnType(), pageOptions,
                    mapNestOptions,insertOptions, dataBase);

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



        private Object returnListTypeHandler(Type litComponetType,
                                             MapNestOptions mapNestOptions,
                                             PageOptions pageOptions,
                                             DataBase dataBase,
                                             NSQL nsql,
                                             String countSql,
                                             String  errMsg) {
            SQLType sqlType = nsql.getSqlType();
            String sqlTxt = nsql.getExeSql();
            Map<Integer, Object> nargs = nsql.getArgs();
            if (litComponetType instanceof Class) {
                Class types0ClassType = (Class) litComponetType;
                if (Map.class.isAssignableFrom(types0ClassType)) {
                    if (pageOptions == null && mapNestOptions == null) {
                        return dataBase.queryMap(sqlTxt, nargs);
                    } else if (pageOptions != null && mapNestOptions == null) {
                        return dataBase.queryMap(sqlTxt,
                                nargs, pageOptions.getPage(),
                                pageOptions.getPerPage(), pageOptions.getPageBean(), countSql);
                    } else {
                        throw new DbException("返回List<Map<String,Object>>类型的方法不能声明MapNestOptions类型形参！");
                    }
                } else if (Iterable.class.isAssignableFrom(types0ClassType)
                        || types0ClassType.isArray()) {
                    throw new DbException("方法返回的泛型类型List<>里元素类型只能为Map<String,Object>或自定义类型，" +
                            "如：List<Map<String,Object>> , List<User>。");
                } else if (!isJavaClass(types0ClassType)) {//自定义类型
                    if (pageOptions == null && mapNestOptions == null) {
                        return dataBase.queryList((Class) litComponetType, sqlTxt, nargs);
                    } else if (pageOptions != null && mapNestOptions == null) {
                        return dataBase.queryList((Class) litComponetType, sqlTxt, nargs, pageOptions.getPage(),
                                pageOptions.getPerPage(), pageOptions.getPageBean(),
                                countSql);
                    } else if (pageOptions == null && mapNestOptions != null) {
                        if (mapNestOptions instanceof One2OneMapNestOptions) {
                            One2OneMapNestOptions one2OneOpts = (One2OneMapNestOptions) mapNestOptions;
                            return dataBase.queryListOne2One((Class) litComponetType, one2OneOpts.getSqlPrefix(),
                                    sqlTxt, nargs, one2OneOpts.getQueryMapNestOne2Ones());
                        } else if (mapNestOptions instanceof One2ManyMapNestOptions) {
                            One2ManyMapNestOptions one2ManyOpts = (One2ManyMapNestOptions) mapNestOptions;
                            return dataBase.queryListOne2Many((Class) litComponetType, one2ManyOpts.getSqlPrefix(),
                                    one2ManyOpts.getParentBeanKeys(), sqlTxt, nargs, one2ManyOpts.getQueryMapNestOne2Manys());
                        } else {
                            ////
                        }
                    } else if (pageOptions != null && mapNestOptions != null) {
                        if (mapNestOptions instanceof One2OneMapNestOptions) {
                            One2OneMapNestOptions one2OneOpts = (One2OneMapNestOptions) mapNestOptions;
                            return dataBase.queryListOne2One((Class) litComponetType, one2OneOpts.getSqlPrefix(),
                                    sqlTxt, nargs, one2OneOpts.getQueryMapNestOne2Ones(), pageOptions.getPage(),
                                    pageOptions.getPerPage(), pageOptions.getPageBean(), countSql);
                        } else if (mapNestOptions instanceof One2ManyMapNestOptions) {
                            throw new DbException("一对多查询方法不支持分页功能，相关解决方法请参考帮助文档！");
                        } else {

                        }
                    }
                }

            }
            throw  new DbException(errMsg);
        }
        private String getCountSql(PageOptions pageOptions,Map<String,Object> args){
            String countSql="";
            if(StringUtils.hasText(pageOptions.getMdFullMethodNameForCountSql())) {
                countSql = MDataBaseImpl.getCountSql(pageOptions.getMdFullMethodNameForCountSql(), args);
            }
            return countSql;
        }
        public Object executeMdMethod(String mdFullMethodName,
                                      Map<String, Object> args,
                                      Type returnType,
                                      PageOptions pageOptions,
                                      MapNestOptions mapNestOptions,
                                      InsertOptions insertOptions,
                                      DataBase dataBase) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
            SQLType sqlType = nsql.getSqlType();
            String sqlTxt = nsql.getExeSql();
            Map<Integer, Object> nargs = nsql.getArgs();

            String errMsg="返回类型不支持，只能声明如：DataBaseSet,List<User>,List<Map<String,Object>>,User形式的类型，这里假设User为自定义类型！";

            if (sqlType == SQLType.SELECT) {
                //Map<String,Object>,List<User>,DataBaseSet,List<Map<String,Object>>,User
                if (returnType instanceof Class) {
                    Class returnClass = (Class) returnType;
                    if (returnClass == DataBaseSet.class) {
                        if (pageOptions == null) {
                            return dataBase.queryForResultSet(sqlTxt, nargs);
                        } else if (pageOptions != null) {
                            String countSql=this.getCountSql(pageOptions, args);
                            return dataBase.queryForResultSet(sqlTxt, nargs, pageOptions.getPage(),
                                    pageOptions.getPerPage(), pageOptions.getPageBean(), countSql);
                        }
                    } else if (Iterable.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
                        throw  new DbException(errMsg);
                    } else {
                        if (!isJavaClass(returnClass)) {//自定义类型
                            if (pageOptions == null && mapNestOptions == null && !isJavaClass(returnClass)) {
                                return dataBase.queryOne(returnClass, sqlTxt, nargs);
                            }
                        }

                    }
                    throw  new DbException(errMsg);
                } else if (returnType instanceof TypeVariable ||
                        returnType instanceof GenericArrayType) {
                    throw  new DbException(errMsg);
                } else if (returnType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) returnType;
                    Type[] types = parameterizedType.getActualTypeArguments();
                    Class rawTypeClass = (Class) parameterizedType.getRawType();
                    if (List.class.isAssignableFrom(rawTypeClass)) {
                        String countSql=this.getCountSql(pageOptions, args);
                        return this.returnListTypeHandler(types[0],mapNestOptions,pageOptions,
                                dataBase,nsql,countSql,errMsg);
                    }
                }//
                throw  new DbException(errMsg);
            } else if (sqlType == SQLType.INSERT || sqlType == SQLType.UPDATE ||
                    sqlType == SQLType.DELETE) {
                if (pageOptions != null || mapNestOptions != null) {
                    throw new DbException("由于方法对应的SQL语句为更新操作，所以方法定义的形参类型不能为PageOptions和MapNestOptions类型！");
                }
                if (returnType instanceof Class) {
                    Class t = (Class) returnType;
                    if (t == int.class || t == Integer.class || t == long.class || t == Long.class) {
                        if (sqlType == SQLType.INSERT) {
                            if(insertOptions!=null && insertOptions.getReturnFlag()== ReturnFlag.AutoKey){
                                return dataBase.insertReturnKey(sqlTxt, nargs);
                            }
                            return dataBase.insert(sqlTxt, nargs);
                        } else if (sqlType == SQLType.UPDATE) {
                            return dataBase.update(sqlTxt, nargs);
                        } else if (sqlType == SQLType.DELETE) {
                            return dataBase.del(sqlTxt, nargs);
                        } else {
                            throw new DbException("不支持的方法！");
                        }
                    }

                } else {
                    throw new DbException("更新方法的返回类型只能为int,long类型");
                }

            }

            throw  new DbException(errMsg);
        }
    }


}
