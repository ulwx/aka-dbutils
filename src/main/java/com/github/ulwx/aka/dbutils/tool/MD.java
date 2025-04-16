package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.database.*;
import com.github.ulwx.aka.dbutils.database.MDMethods.InsertOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.InsertOptions.ReturnFlag;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions;
import com.github.ulwx.aka.dbutils.database.dbpool.ReadConfig;
import com.github.ulwx.aka.dbutils.database.parameter.IDGeneratorParmeter;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import com.github.ulwx.aka.dbutils.tool.support.reflect.CGetFun;
import com.github.ulwx.aka.dbutils.tool.support.reflect.GetFun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MD {
    public static String md(Class daoClass, String mdMethodName) {
        String prefix = daoClass.getName();
        return prefix + ".md:" + mdMethodName;
    }

    public static String md() {
        return md(2, null);
    }


    public static void setReflectClass(Class c){
        DbContext.setReflectClass(c);
    }
    public static String md(String mdMethodName) {
        return md(2, mdMethodName);
    }

    private static String md(int level, String mdMehtodName) {
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        StackTraceElement ste = stack[level];
        String className = ste.getClassName();
        String methodName = "";
        if (StringUtils.hasText(mdMehtodName)) {
            methodName = mdMehtodName;
        } else {
            methodName = ste.getMethodName();
        }

        className=StringUtils.trimLeadingString(className,"BOOT-INF.classes!.");
        return className + ".md:" + methodName;
    }

    public static Object[] objs(Object... args) {
        return args;
    }

    public static Object[] of(Object... args) {
        return args;
    }

    public static Integer[] of(Integer... args) {
        return args;
    }

    public static String[] of(String... args) {
        return args;
    }

    public static <R> GetFun<R>[] of(GetFun<R>... args) {
        return args;
    }

    public static <T, R> CGetFun<T, R>[] of(CGetFun<T, R>... args) {
        return args;
    }

    /**
     * 根据一个JavaBean对象数组，返回一个Map&lt;String,Object&gt;对象数组，
     * 每个JavaBean对象会转换成一个Map&lt;String,Object&gt;对象
     *
     * @param javaBeans
     * @return
     */
    public static Map<String, Object>[] maps(Object... javaBeans) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < javaBeans.length; i++) {
            list.add(ObjectUtils.fromJavaBeanToMap2(javaBeans[i]));
        }
        return list.toArray(new HashMap[0]);
    }

    /**
     * 根据一个JavaBean对象数组，返回一个List&lt;Map&lt;String,Object&gt;&gt;对象，
     * 每个JavaBean对象会转换成一个Map&lt;String,Object&gt;对象
     *
     * @param javaBeans javaBean对象
     * @return
     */
    public static List<Map<String, Object>> mapList(Object... javaBeans) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < javaBeans.length; i++) {
            list.add(ObjectUtils.fromJavaBeanToMap2(javaBeans[i]));
        }
        return list;
    }

    /**
     * 根据一个JavaBean对象生成Map&lt;String,Object&gt;对象
     *
     * @param javaBean
     * @return
     */
    public static Map<String, Object> map(Object javaBean) {

        return ObjectUtils.fromJavaBeanToMap2(javaBean);
    }

    public static String ofSQL(String sql){
        return "sql:"+sql;
    }
    public static One2OneMapNestOptions ofOne2One(String sqlPrefix,
                                                  QueryMapNestOne2One... queryMapNests) {
        One2OneMapNestOptions o2os = new One2OneMapNestOptions();
        o2os.setSqlPrefix(sqlPrefix);
        o2os.setQueryMapNestOne2Ones(queryMapNests);
        return o2os;
    }

    public static One2ManyMapNestOptions ofOne2Many(String sqlPrefix, String[] parentBeanKeys,
                                                    QueryMapNestOne2Many... QueryMapNestOne2Manys) {
        One2ManyMapNestOptions o2ms = new One2ManyMapNestOptions();
        o2ms.setSqlPrefix(sqlPrefix);
        o2ms.setParentBeanKeys(parentBeanKeys);
        o2ms.setQueryMapNestOne2Manys(QueryMapNestOne2Manys);
        return o2ms;

    }

    /**
     * 工具方法，如果Mapper接口方法定义了PageOptions类型的形参，则通过本工具方法（ofPage）生成一个PageOptions对象。
     * 生成的PageOptions对象里的mdFullMethodNameForCountSql为""，表明aka-dbutils自动生成计算总数的sql语句。
     *
     * @param pageNum  当前页码，从1开始
     * @param perPage  每页多少条记录
     * @param pageBean 设置PageBean对象，用于返回分页信息，用于前端UI显示
     * @return
     */
    public static PageOptions ofPage(int pageNum, int perPage, PageBean pageBean) {
        PageOptions pageOptions = new PageOptions();
        pageOptions.setPage(pageNum);
        pageOptions.setPerPage(perPage);
        if (pageBean != null) {
            pageOptions.setPageBean(pageBean);
        }
        return pageOptions;
    }

    /**
     * 工具方法，如果Mapper接口方法定义了PageOptions类型的形参，则通过本工具方法（ofPage）生成一个PageOptions对象。
     *
     * @param pageNum                     当前页码，从1开始
     * @param perPage                     每页多少条记录
     * @param mdFullMethodNameForCountSql 可以指定四种类型的参数，<br>
     *                                    null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                                    数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                                    md方法地址：表示计算总数的SQL的md方法地址<br>
     *                                    -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句。
     * @param pageBean                    设置PageBean对象，用于返回分页信息，用于前端UI显示
     * @return
     */
    public static PageOptions ofPage(int pageNum, int perPage, String mdFullMethodNameForCountSql, PageBean pageBean) {
        PageOptions pageOptions = new PageOptions();
        pageOptions.setPage(pageNum);
        pageOptions.setPerPage(perPage);
        pageOptions.setMdFullMethodNameForCountSql(mdFullMethodNameForCountSql);
        if (pageBean != null) {
            pageOptions.setPageBean(pageBean);
        }
        return pageOptions;
    }

    /**
     * 生成InsertOptions对象的工具类方法，一般用于Mapper接口里定义的插入方法传入InsertOptions参数时。InsertOptions对象
     * 指定插入方法是否返回主键id，否则插入方法返回的是插入的条数。
     *
     * @param needReturnKey 指定Mapper的insert方法是否返回主键。true：返回主键id，false：返回插入的条数
     * @return
     */
    public static InsertOptions ofInsert(boolean needReturnKey) {
        InsertOptions insertOptions = new InsertOptions();
        if (needReturnKey) {
            insertOptions.setReturnFlag(ReturnFlag.AutoID);
        }
        return insertOptions;
    }

    public static String[] parsePoolName(String dbPoolXmlFileNameAndDbPoolName) {
        String dbPoolKey = null;
        String dbPoolXmlName = null;
        String[] strs = dbPoolXmlFileNameAndDbPoolName.split("\\#");
        if (strs.length == 1) {
            dbPoolXmlName = ReadConfig.DEFAULT;
            dbPoolKey = dbPoolXmlFileNameAndDbPoolName;

        } else if (strs.length == 2) {
            dbPoolXmlName = strs[0];
            dbPoolKey = strs[1];
        } else {

        }
        return new String[]{dbPoolXmlName, dbPoolKey};
    }

    public final static String AKA_PROFILE_NAME = "AKA_DBUTILS_PROFILE";
    private static Map<String, String> poolFileNameProfileMap = new ConcurrentHashMap<>();

    public static String ofPool(String dbPoolXmlFileNameAndDbPoolName) {
        String[] strs = parsePoolName(dbPoolXmlFileNameAndDbPoolName);
        String dbpoolFileName = strs[0];
        String dbpoolName = strs[1];
        String realFileName = poolFileNameProfileMap.get(dbpoolFileName);
        if (realFileName != null) {
            return realFileName + "#" + dbpoolName;
        }
        realFileName = dbpoolFileName;
        String profileValue = StringUtils.trim(System.getProperty(AKA_PROFILE_NAME));
        if (profileValue.isEmpty()) {
            profileValue = StringUtils.trim(System.getenv(AKA_PROFILE_NAME));
        }
        if (!profileValue.isEmpty()) {
            realFileName = StringUtils.trimTailString(realFileName, ".xml");
            realFileName = realFileName + "-" + profileValue + ".xml";
        }
        try {
            Resource[] resources =  ReadConfig.getResource(realFileName);;
            ReadConfig.checkResource(resources, realFileName);
            synchronized (MD.class) {
                String ret = poolFileNameProfileMap.get(dbpoolFileName);
                if (ret == null) {
                    poolFileNameProfileMap.put(dbpoolFileName, realFileName);
                } else {
                    realFileName = ret;
                }
            }
            return realFileName + "#" + dbpoolName;

        } catch (Exception e) {
            throw new DbException(e);
        }

    }

    public  static QueryHint ofQueryHint(){
        return new QueryHint();
    }

    public static IDGeneratorParmeter ofIDGenParmeter(String parmName,String sequenceName){
        IDGeneratorParmeter idGeneratorParmeter=new IDGeneratorParmeter();
        idGeneratorParmeter.setName(parmName);
        idGeneratorParmeter.setClazz(Long.class);
        idGeneratorParmeter.setSequenceName(sequenceName);
        return idGeneratorParmeter;

    }
    public static void main(String[] args) {
        ofPool("dbpool.xml");
    }
}
