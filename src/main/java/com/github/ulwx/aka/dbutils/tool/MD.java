package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions.InsertOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions.InsertOptions.ReturnFlag;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2Many;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2One;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
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
        return md(2,null);
    }
    public static String md(String mdMethodName){
        return md(2,mdMethodName);
    }
    private static String  md(int level,String mdMehtodName) {
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        StackTraceElement ste = stack[level];
        String className=ste.getClassName();
        String methodName="";
        if(StringUtils.hasText(mdMehtodName)){
            methodName=mdMehtodName;
        }else {
            methodName=ste.getMethodName();
        }
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
     * ????????????JavaBean???????????????????????????Map<String,Object>?????????????????????JavaBean????????????????????????Map<String,Object>??????
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
     * ????????????JavaBean???????????????????????????List<Map<String,Object>>???????????????JavaBean????????????????????????Map<String,Object>??????
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
     * ????????????JavaBean????????????Map<String,Object>??????
     * @param javaBean
     * @return
     */
    public static Map<String, Object> map(Object javaBean){
       return ObjectUtils.fromJavaBeanToMap(javaBean);
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
     * ?????????????????????Mapper?????????????????????PageOptions?????????????????????????????????????????????ofPage???????????????PageOptions?????????
     *  ?????????PageOptions????????????mdFullMethodNameForCountSql???""?????????aka-dbutils???????????????????????????sql?????????
     * @param pageNum ??????????????????1??????
     * @param perPage  ?????????????????????
     * @param pageBean ??????PageBean????????????????????????????????????????????????UI??????
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
     * ?????????????????????Mapper?????????????????????PageOptions?????????????????????????????????????????????ofPage???????????????PageOptions?????????
     * @param pageNum ??????????????????1??????
     * @param perPage  ?????????????????????
     * @param mdFullMethodNameForCountSql  ????????????????????????????????????<br/>
     *                                  null???""??????ak-dbutils???????????????????????????count???select?????????<br/>
     *                                  ???????????????????????????????????????????????????????????????????????????<br/>
     *                                  md????????????????????????????????????SQL???md????????????<br/>
     *                                  -1 ??????????????????????????????ak-dbutils????????????????????????count???select?????????
     * @param pageBean ??????PageBean????????????????????????????????????????????????UI??????
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
     * ??????InsertOptions?????????????????????????????????Mapper???????????????????????????????????????????????????????????????????????????id???
     * ??????????????????????????????
     * @param needReturnKey true???????????????id???false????????????????????????
     * @return
     */
    public static InsertOptions ofInsert(boolean needReturnKey){
        InsertOptions insertOptions=new InsertOptions();
        if(needReturnKey) {
            insertOptions.setReturnFlag(ReturnFlag.AutoKey);
        }
        return insertOptions;
    }

}
