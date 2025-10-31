package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.database.DataBase.SQLType;
import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils.GroupHandler;
import com.github.ulwx.aka.dbutils.tool.support.type.TInteger;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;

/**
 * SQL语句抽象，提供基于命名参数的SQL语句功能
 */
public final class NSQL {

    private String sql_naming;
    private String sql_execute;
    private String methodFullName;
    private SQLType sqlType;
    private Map<Integer, Object> args = new HashMap<Integer, Object>();// 存放索引到值的关系
    private Map<Integer, String> argsToKey = new HashMap<Integer, String>(); // 存放索引到key的关系
    private static Logger log = LoggerFactory.getLogger(NSQL.class);
    private static String $javaNameReg = "[A-Za-z_$@\\d]+";

    public Map<Integer, Object> getArgs() {
        return args;
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SQLType sqlType) {
        this.sqlType = sqlType;
    }

    public void setMethodFullName(String methodFullName) {
        this.methodFullName = methodFullName;
    }

    public NSQL() {
        // 用户不能实例化对象
        // 通过get方法获取可用实例
    }

    public boolean hasName() {
        return args.size() > 0;
    }

    public Map<Integer, String> getArgsToKey() {
        return argsToKey;
    }

    /**
     * 获取用于数据库执行的SQL语句，如SELECT `id`, `name` FROM `teacher` where name like ?
     *
     * @return 返回执行的SQL语句
     */
    public String getExeSql() {
        return sql_execute;
    }

    /**
     * 获取用户定义的命名SQL语句，如SELECT `id`, `name` FROM `teacher` where name like #{lname%}
     *
     * @return 返回命名SQL语句，如 select * from t where
     */
    public String getNamingSql() {
        return sql_naming;
    }


    /**
     * 根据md文件里的方法名和参数，获取NSQL对象
     *
     * @param mdPathMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             存放参数的map或JavaBean
     * @return 返回NSQL对象
     * @throws DbException 异常
     */
    public static NSQL getNSQL(String mdPathMethodName, Object args) throws DbException {
        mdPathMethodName=mdPathMethodName.trim();
        try {
            int pos=mdPathMethodName.indexOf(":");
            String mdPath=mdPathMethodName.substring(0,pos);
            String mdMethodName=mdPathMethodName.substring(pos+1);
            return NSQL.getNSQL(mdPath, mdMethodName, args);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e.getMessage()+e, e);
        }
    }

    /**
     * 根据md文件里的方法名和参数，获取NSQL对象
     *
     * @param mdPath     ：有两种表含义。<ul>
     *                   <li>md文件的包路径全名称，如com.github.ulwx.database.test.SysRightDao.md。</li>
     *                  <li>固定值sql : 表示methodName参数的内容其实为SQL语句（语句里可以带#{sname}参数占位符）。</li>
     *                   </ul>
     * @param methodName ：methodName根据mdPath具体内容分别为两种含义。
     *                   （1）md里对应的方法名例如 getDataCount ；
     *                   （2）为SQL语句（如果mdPath="sql"）
     * @param args       ：存放参数的map或JavaBean
     * @return 返回NSQL对象
     * @throws Exception 异常
     */
    public static NSQL getNSQL(String mdPath, String methodName, Object args) throws Exception {

        Map<String, Object> map = null;
        if (args instanceof Map) {
            map = (Map) args;
        } else {
            map = ObjectUtils.fromJavaBeanToMap2(args);
        }
        if (mdPath.equalsIgnoreCase("sql")) {
            return getNSQL(methodName, "sql", map);
        }
        log.debug("mdPath="+mdPath);
        String sql = MDTemplate.getResultString(mdPath, methodName, map);
        return getNSQL(sql, mdPath + ":" + methodName, map);
    }

    public static NSQL getNSQL(String sql, String methodFullName,
                               Map<String, Object> args) throws Exception {

        NSQL nsql = new NSQL();
        nsql.setMethodFullName(methodFullName);
        nsql = parseSql(sql, args, nsql);

        if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
            log.debug("nsql:" + ObjectUtils.toString(nsql));
        }

        return nsql;
    }


    /**
     * 分析命名SQL语句获取抽象NSQl实例；java(JDBC)提供SQL语句命名参数而是通过?标识参数位置，
     * 通过此对象可以命名参数方式使用SQL语句，命名参数以#{[%]xxx[%]}形式，%用于like语句中。<br>
     * 例如：<blockquote><pre>
     * SELECT * FROM table WHERE name = #{ key1} AND email = #{key2} and phone like #{ %key3% };
     * </pre></blockquote>
     * 例如使用参数如下： <blockquote><pre>
     *   Map&lt;String,Object&gt; args=new HashMap&lt;String,Object&gt;();
     *   args.put("name","陈三");
     *   args.pub("age",123);
     *   如果isStoredProc为true，表明为存储过程，此时args里存放内容的如下：
     *  {
     *    "sex:in":12334,
     *    "name:inout":"lilei",
     *    "money":out":Long.class
     *
     *  }</pre></blockquote>
     *
     * @param sqltxt sql语句
     * @param args   sql语句里用到的参数
     * @param nsql   NSQL对象
     * @return 返回NSQL对象
     */

    public static NSQL parseSql(String sqltxt,  Map<String, Object> _args, NSQL nsql) {

        sqltxt = StringUtils.trim(sqltxt);
        if (sqltxt.isEmpty()) {
            throw new NullPointerException("SQL String is empty or null");
        }
        Map<String, Object> args=new HashMap<>();
        if(_args!=null){
            args.putAll(_args);
        }
        String sql = sqltxt;
        //判断sql的类型
        SQLType sqlType = SqlUtils.decideSqlType(sql);
        //if(sql)
        if (sqlType == SQLType.STORE_DPROCEDURE) {
            final Map<String, TResult2<String, Object>> newArgs = new HashMap<String, TResult2<String, Object>>();
            for (String key : args.keySet()) {
                String[] strs = key.split(":");
                String inout = "in";
                if (strs.length == 2) {
                    inout = strs[1];
                }
                newArgs.put(strs[0], new TResult2<String, Object>(inout, args.get(key)));
            }
            args.clear();
            args.putAll(newArgs);
        }

        final Map<Integer, Object> indexToValue = new HashMap<Integer, Object>();
        final Map<Integer, String> IndexToKey = new HashMap<Integer, String>();
        final TInteger i = new TInteger(1);

        String exeSql = StringUtils.replaceAll(sql,
                "#\\{(\\s*(\\[Array\\])?\\%?\\s*" + $javaNameReg + "\\s*\\%?\\s*)\\}", 1, new GroupHandler() {

                    @Override
                    public String handler(String groupStr) {
                        String key = groupStr.trim();

                        String ret = "?";
                        boolean prePercentSigns = false;
                        boolean lastPercentSigns = false;
                        boolean isArray = false;
                        boolean isList = false;
                        Object val = null;
                        if (key.startsWith("[Array]")) {
                            isArray = true;
                            key = StringUtils.trimLeadingString(key, "[Array]");
                            if (!args.containsKey(key)) {
                                log.error("sql=" + sql);
                                log.error("arg map=" + ObjectUtils.toString(args));
                                throw new DbException("解析sql失败！[" + sql + "][" + nsql.getMethodFullName() + "]"
                                        + "，参数[" + key + "]在md文件里没有定义");
                            }
                            val = args.get(key);
                            // 判断是否为数组类型
                            if (val != null) {
                                if (val.getClass().isArray()) {
                                    //
                                } else {
                                    throw new DbException("解析sql失败![" + sql + "][" + nsql.getMethodFullName() + "]"
                                            + "，参数[" + key + "]应该为数组");
                                }
                            }
                        } else {
                            if (key.startsWith("%")) {
                                prePercentSigns = true;
                                key = key.substring(1);
                            }
                            if (key.length() > 0 && key.endsWith("%")) {
                                lastPercentSigns = true;
                                key = key.substring(0, key.length() - 1);
                            }
                            if (!args.containsKey(key)) {
                                throw  new DbException("解析sql失败![" + nsql.getMethodFullName() + "]"+"[" + sql + "]"
                                        + "里的参数"+key+ "在参数数Map里没有指定!");
                            }
                            val = args.get(key);
                            if (val != null) {
                                if (val.getClass().isArray()) {
                                    isArray = true;
                                } else if (val instanceof List) {// list类型
                                    isList = true;
                                } else if (val instanceof Set) {// Set类型
                                    isList = true;
                                }
                            }
                        }

                        if (isArray || isList) {// 数组或list
                            if (prePercentSigns || lastPercentSigns) {
                                throw new DbException("解析sql失败![" + sql + "][" + nsql.getMethodFullName() + "]"
                                        + "，参数[" + key + "]为数组或集合类型，不支持在#{" + groupStr + "}含有%");
                            }
                            if (val == null) {
                                indexToValue.put(i.getValue(), val);
                                IndexToKey.put(i.getValue(), key);
                                i.setValue(i.getValue() + 1);

                            } else {
                                if (isArray) {
                                    int len = Array.getLength(val);
                                    for (int m = 0; m < len; m++) {
                                        Object item = Array.get(val, m);
                                        if (m == 0) {
                                            ret = "";
                                        } else {
                                            ret = ret + ",";
                                        }
                                        ret = ret + "?";

                                        indexToValue.put(i.getValue(), item);
                                        IndexToKey.put(i.getValue(), key + "$" + m);
                                        i.setValue(i.getValue() + 1);

                                    }
                                } else if (isList) {
                                    List<?> listValue = null;
                                    if (val instanceof Set) {
                                        listValue = new ArrayList<>();
                                        listValue.addAll((Set) val);
                                    } else {
                                        listValue = (List<?>) val;
                                    }

                                    for (int m = 0; m < listValue.size(); m++) {
                                        Object item = listValue.get(m);
                                        if (m == 0) {
                                            ret = "";
                                        } else {
                                            ret = ret + ",";
                                        }
                                        ret = ret + "?";

                                        indexToValue.put(i.getValue(), item);
                                        IndexToKey.put(i.getValue(), key + "$" + m);
                                        i.setValue(i.getValue() + 1);

                                    }
                                }

                            }
                        } else {
                            if (val == null) {
                                ///
                            } else {
                                if (val instanceof String) {
                                    if (prePercentSigns) {
                                        val = "%" + val;
                                    }
                                    if (lastPercentSigns) {
                                        val = val + "%";
                                    }
                                } else {
                                    if (prePercentSigns || lastPercentSigns) {
                                        throw new DbException("解析sql失败![" + sql + "][" + nsql.getMethodFullName() + "]"
                                                + "，参数[" + key + "]不为String类型，不支持在#{" + groupStr + "}含有%");
                                    }
                                }
                            }
                            indexToValue.put(i.getValue(), val);
                            IndexToKey.put(i.getValue(), key);

                            i.setValue(i.getValue() + 1);
                        }

                        return ret;
                    }

                });

        NSQL dbsql = nsql;
        dbsql.sql_naming = sql;
        dbsql.sql_execute = exeSql;
        dbsql.sqlType = sqlType;
        dbsql.args = indexToValue;
        dbsql.argsToKey = IndexToKey;

        return dbsql;
    }


    public static void main(String[] args) throws Exception {
        // String sql = "select * from tt where s=#{xxx } and b=#{ bbb} #33 #{xxxx}";
        // System.out.println(ObjectUtils.toString(NSQL.parse(sql)));
        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("sysRightCode", "3345");
        mp.put("sysRightName", "666");
        // String s=parseFromFileToJava("com.github.ulwx.database.test.SysRightDao",mp);
        // String
        // s=ObjectUtils.toString(parse("com.github.ulwx.database.test.SysRightDao",mp));
        // System.out.println(s);
        System.out.println("xxxx" + "ss".substring(0, 0));
    }

}
