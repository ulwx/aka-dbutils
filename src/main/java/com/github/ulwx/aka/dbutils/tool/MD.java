package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.database.MapperMethodParm.MapNestOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.PageOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.PageOptions.InsertOptions;
import com.github.ulwx.aka.dbutils.database.MapperMethodParm.PageOptions.InsertOptions.ReturnFlag;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2Many;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2One;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.reflect.CGetFun;
import com.github.ulwx.aka.dbutils.tool.support.reflect.GetFun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MD {
    public static String md(Class daoClass, String mdMethodName) {
        String prefix = daoClass.getName();
        return prefix + ".md:" + mdMethodName;
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
    public static Object[] objs(Object... args){
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
    /**
     * 根据一个JavaBean对象数组，返回一个Map<String,Object>对象数组，每个JavaBean对象会转换成一个Map<String,Object>对象
     * @param javaBeans
     * @return
     */
    public static Map<String, Object>[] maps(Object... javaBeans){
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i=0; i<javaBeans.length; i++){
            list.add(ObjectUtils.fromJavaBeanToMap(javaBeans[i]));
        }
        return list.toArray(new HashMap[0]);
    }

    /**
     * 根据一个JavaBean对象数组，返回一个List<Map<String,Object>>对象，每个JavaBean对象会转换成一个Map<String,Object>对象
     * @param javaBeans
     * @return
     */
    public static List<Map<String,Object>> mapList(Object... javaBeans){
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i=0; i<javaBeans.length; i++){
            list.add(ObjectUtils.fromJavaBeanToMap(javaBeans[i]));
        }
        return list;
    }

    /**
     * 根据一个JavaBean对象生成Map<String,Object>对象
     * @param javaBean
     * @return
     */
    public static Map<String, Object> map(Object javaBean){
       return ObjectUtils.fromJavaBeanToMap(javaBean);
    }

    public static MapNestOptions ofOne2One(String sqlPrefix, QueryMapNestOne2One[] queryMapNests) {
        One2OneMapNestOptions o2os = new One2OneMapNestOptions();
        o2os.setSqlPrefix(sqlPrefix);
        o2os.setQueryMapNestOne2Ones(queryMapNests);
        return o2os;
    }
    public static MapNestOptions ofOne2Many(String sqlPrefix, String[] parentBeanKeys,
                                            QueryMapNestOne2Many[] QueryMapNestOne2Manys) {
        One2ManyMapNestOptions o2ms = new One2ManyMapNestOptions();
        o2ms.setSqlPrefix(sqlPrefix);
        o2ms.setParentBeanKeys(parentBeanKeys);
        o2ms.setQueryMapNestOne2Manys(QueryMapNestOne2Manys);
        return o2ms;

    }
    /**
     * 工具方法，如果Mapper接口方法定义了PageOptions类型的形参，则通过本工具方法（ofPage）生成一个PageOptions对象。
     *  生成的PageOptions对象里的mdFullMethodNameForCountSql为""，表明aka-dbutils自动生成计算总数的sql语句。
     * @param pageNum 当前页码，从1开始
     * @param perPage  每页多少条记录
     * @param pageBean 设置PageBean对象，用于返回分页信息，用于前端UI显示
     * @return
     */
    public static PageOptions ofPage(int pageNum,int perPage,PageBean pageBean){
        PageOptions pageOptions=new PageOptions();
        pageOptions.setPage(pageNum);
        pageOptions.setPerPage(perPage);
        if(pageBean!=null){
            pageOptions.setPageBean(pageBean);
        }
       return pageOptions;
    }

    /**
     * 工具方法，如果Mapper接口方法定义了PageOptions类型的形参，则通过本工具方法（ofPage）生成一个PageOptions对象。
     * @param pageNum 当前页码，从1开始
     * @param perPage  每页多少条记录
     * @param mdFullMethodNameForCountSql  可以指定四种类型的参数，<br/>
     *                                  null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br/>
     *                                  数字：则表明以指定的数字为总数，用于计算分页信息；<br/>
     *                                  md方法地址：表示计算总数的SQL的md方法地址<br/>
     *                                  -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句。
     * @param pageBean 设置PageBean对象，用于返回分页信息，用于前端UI显示
     * @return
     */
    public static PageOptions ofPage(int pageNum,int perPage,String mdFullMethodNameForCountSql,PageBean pageBean){
        PageOptions pageOptions=new PageOptions();
        pageOptions.setPage(pageNum);
        pageOptions.setPerPage(perPage);
        pageOptions.setMdFullMethodNameForCountSql(mdFullMethodNameForCountSql);
        if(pageBean!=null){
            pageOptions.setPageBean(pageBean);
        }
        return pageOptions;
    }

    /**
     * 生成InsertOptions对象的工具类方法，用于Mapper接口里定义的插入方法，用于指定插入方法是否返回主键id，
     * 否则返回为插入的条数
     * @param needReturnKey true：返回主键id，false：返回插入的条数
     * @return
     */
    public static InsertOptions ofReturnKey(boolean needReturnKey){
        InsertOptions insertOptions=new InsertOptions();
        if(needReturnKey) {
            insertOptions.setReturnFlag(ReturnFlag.AutoKey);
        }
        return insertOptions;
    }

}
