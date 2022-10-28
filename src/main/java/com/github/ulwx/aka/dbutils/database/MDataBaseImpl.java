package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.database.nsql.MDTemplate;
import com.github.ulwx.aka.dbutils.database.nsql.NSQL;
import com.github.ulwx.aka.dbutils.tool.PageBean;
import com.github.ulwx.aka.dbutils.tool.support.Path;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;

import java.io.*;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDataBaseImpl implements MDataBase {

    private DataBase dataBase;

    public MDataBaseImpl() {

    }

    public MDataBaseImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public DBMS getDataBaseType() {
        return this.dataBase.getDataBaseType();
    }


    @Override
    public String exeScriptInDir(String dirFilePath, String sqlFileName,
                            boolean throwWarning,String delimiters,String encoding) throws DbException {

        try {
            File f = new File(dirFilePath,sqlFileName);
            String source = f.toURI().toURL().toString();
            Resource resource=getResource(source);
            return exeScript(resource.getInputStream(),source,throwWarning,delimiters,encoding);
        } catch (Exception e) {
            if (e instanceof DbException) {
                throw (DbException) e;
            }
            throw new DbException(e);
        }
    }
    private Resource getResource(String source)throws Exception{
        Resource[] rs= Path.getResourcesLikeAntPathMatch(source);
        if(rs==null || rs.length==0 ){
            throw new DbException("无法找到"+source+"文件!");
        }else{
            if(rs.length==1){
                if(!rs[0].exists()){
                    throw new DbException("无法找到"+source+"文件!");
                }
            }else{
                String str="";
                for(int i=0;i<rs.length; i++) {
                    if(i==0){
                        str=rs[i].getURL().toString();
                        continue;
                    }
                    str=str+";"+ rs[i].getURL().toString();
                }
                throw new DbException("" + source + "存在多个文件!【"+str+"]");
            }
        }
        return rs[0];
    }

    @Override
    public String exeScript(String packageFullName, String sqlFileName,
                            boolean throwWarning,String delimiters,String encoding) throws DbException {
        try {
            packageFullName = packageFullName.replace(".", "/");
            String source = "classpath*:" + packageFullName + "/" + sqlFileName;
            Resource resource=this.getResource(source);
            return exeScript(resource.getInputStream(),source,throwWarning,delimiters,encoding);
        } catch (Exception e) {
            if (e instanceof DbException) {
                throw (DbException) e;
            }
            throw new DbException(e);
        }

    }

   private String exeScript(InputStream in,String source, boolean throwWarning,String delimiters,String encoding){
       BufferedReader bufReader = null;
       try {
           if(encoding==null || encoding.isEmpty()){
               encoding="utf-8";
           }
           bufReader = new BufferedReader(new InputStreamReader(in, encoding));
           Map<String, Object> args = new HashMap<>();
           ScriptOption scriptOption = new ScriptOption();
           scriptOption.setSource(source);
           scriptOption.setFromMDMethod(false);
           args.put(ScriptOption.class.getName(), scriptOption);
           return this.dataBase.exeScript(bufReader, throwWarning, delimiters,args);
       } catch (Exception e) {
           if (e instanceof DbException) {
               throw (DbException) e;
           }
           throw new DbException(e);
       }
   }
    @Override
    public String exeScript(String mdFullMethodName,boolean throwWarning, String delimiters, Map<String, Object> args) throws DbException {
        String[] strs = mdFullMethodName.split(":");
        String sql = null;
        try {
            sql = MDTemplate.getResultString(strs[0], strs[1], args);
            sql = sql.replace(delimiters, delimiters+"\n");
        } catch (Exception e) {
            if (e instanceof DbException) {
                throw (DbException) e;
            }
            throw new DbException(e);
        }

        StringReader sr = new StringReader(sql);
        ScriptOption scriptOption = new ScriptOption();
        scriptOption.setSource(mdFullMethodName);
        scriptOption.setFromMDMethod(true);
        args.put(ScriptOption.class.getName(), scriptOption);
        return this.dataBase.exeScript(sr, throwWarning,delimiters, args);

    }

    public static String getCountSql(String countSqlMdFullMethodName, Map<String, Object> args) {
        String countSql = null;
        countSqlMdFullMethodName = StringUtils.trim(countSqlMdFullMethodName);
        if (StringUtils.hasText(countSqlMdFullMethodName)) {
            if (StringUtils.isNumber(countSqlMdFullMethodName)
                    || countSqlMdFullMethodName.equals("-1")) {
                countSql = countSqlMdFullMethodName;
            } else {
                NSQL cnsql = NSQL.getNSQL(countSqlMdFullMethodName, args);
                countSql = cnsql.getExeSql();
            }
        }
        return countSql;
    }

    @Override
    public DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                         PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryForResultSet(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, countSql);

    }

    @Override
    public DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryForResultSet(nsql.getExeSql(), nsql.getArgs());

    }


    @Override
    public <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean,
                                 RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryList(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, rowMapper, countSql);
    }


    @Override
    public List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                              PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryMap(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, countSql);
    }


    @Override
    public <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryList(clazz, nsql.getExeSql(), nsql.getArgs());
    }

    @Override
    public <T> T queryOne(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryOne(clazz, nsql.getExeSql(), nsql.getArgs());
    }


    @Override
    public <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page,
                                 int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryList(clazz, nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, countSql);
    }

    @Override
    public <T> List<T> queryListOne2One(Class<T> clazz, String mdFullMethodName, Map<String, Object> args,
                                        One2OneMapNestOptions one2OneMapNestOptions) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryListOne2One(clazz, nsql.getExeSql(), nsql.getArgs(), one2OneMapNestOptions);
    }

    @Override
    public <T> List<T> queryListOne2One(Class<T> clazz, String mdFullMethodName,
                                        Map<String, Object> args, One2OneMapNestOptions one2OneMapNestOptions,
                                        int page, int perPage, PageBean pageBean,
                                        String countSqlMdFullMethodName) throws DbException {

        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryListOne2One(clazz, nsql.getExeSql(),
                nsql.getArgs(), one2OneMapNestOptions, page, perPage, pageBean, countSql);
    }


    @Override
    public <T> List<T> queryListOne2Many(Class<T> clazz,
                                         String mdFullMethodName,
                                         Map<String, Object> args,
                                         One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryListOne2Many(clazz, nsql.getExeSql(), nsql.getArgs(), one2ManyMapNestOptions);
    }


    @Override
    public <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryList(nsql.getExeSql(), nsql.getArgs(), rowMapper);

    }


    @Override
    public List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);

        return this.dataBase.queryMap(nsql.getExeSql(), nsql.getArgs());

    }

    @Override
    public int del(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.del(nsql.getExeSql(), nsql.getArgs());
    }


    @Override
    public int update(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.update(nsql.getExeSql(), nsql.getArgs());
    }


    @Override
    public <T> T getMapper(Class<T> type) throws DbException {
        return MapperFactory.getMapper(type, this);

    }


    @Override
    public void callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
                              List<DataBaseSet> returnDataBaseSets) throws DbException {

        NSQL nsql = NSQL.getNSQL(mdFullMethodName, parms);
        Map<Integer, Object> args = nsql.getArgs();
        Map<String, Object> argsNew = new HashMap<String, Object>();
        for (Integer key : args.keySet()) {
            TResult2<String, Object> val = (TResult2<String, Object>) args.get(key);
            argsNew.put(key + ":" + val.getFirstValue(), val.getSecondValue());
        }
        Map<Integer, Object> outPramsValuesNew = new HashMap<Integer, Object>();
        this.dataBase.callStoredPro(nsql.getExeSql(), argsNew, outPramsValuesNew, returnDataBaseSets);
        Map<Integer, String> argsToKey = nsql.getArgsToKey();
        for (Integer key : outPramsValuesNew.keySet()) {
            Object val = outPramsValuesNew.get(key);
            outPramsValues.put(argsToKey.get(key), val);
        }


    }


    @Override
    public int insert(String mdFullMethodName, Map<String, Object> args) throws DbException {

        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.insert(nsql.getExeSql(), nsql.getArgs());
    }

    @Override
    public long insertReturnKey(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.insertReturnKey(nsql.getExeSql(), nsql.getArgs());
    }


    @Override
    public int[] update(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (int i = 0; i < mdFullMethodNameList.length; i++) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodNameList[i], vParametersArray[i]);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.update(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));
    }

    @Override
    public int[] insert(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (int i = 0; i < mdFullMethodNameList.length; i++) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodNameList[i], vParametersArray[i]);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.insert(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));
    }


    @Override
    public int[] update(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (Map<String, Object> arg : vParametersList) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodName, arg);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.update(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));

    }

    @Override
    public int[] insert(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (Map<String, Object> arg : vParametersList) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodName, arg);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.insert(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));
    }


    @Override
    public int[] update(ArrayList<String> mdFullMethodNameList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        for (String mdMethod : mdFullMethodNameList) {
            NSQL nsql = NSQL.getNSQL(mdMethod, null);
            sqlList.add(nsql.getExeSql());
        }
        return this.dataBase.update(sqlList);
    }

    @Override
    public int[] insert(ArrayList<String> mdFullMethodNameList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        for (String mdMethod : mdFullMethodNameList) {
            NSQL nsql = NSQL.getNSQL(mdMethod, null);
            sqlList.add(nsql.getExeSql());
        }
        return this.dataBase.insert(sqlList);
    }


    @Override
    public Connection getConnection(boolean force) {
        return this.dataBase.getConnection(force);
    }

    @Override
    public <T> int insertBy(T insertObject) throws DbException {

        return this.dataBase.insertBy(insertObject);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject) throws DbException {

        return this.dataBase.insertReturnKeyBy(insertObject);
    }

    @Override
    public <T> int[] insertBy(T[] objs) throws DbException {

        return this.dataBase.insertBy(objs);
    }


    @Override
    public <T> List<T> queryListBy(T selectObject) throws DbException {

        return this.dataBase.queryListBy(selectObject);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException {

        return this.dataBase.queryListBy(selectObject, page, perPage, pb);
    }

    @Override
    public <T> T queryOneBy(T selectObject) throws DbException {
        return this.dataBase.queryOneBy(selectObject);
    }


    @Override
    public void setAutoCommit(boolean b) throws DbException {
        this.dataBase.setAutoCommit(b);
    }


    @Override
    public boolean getAutoCommit() throws DbException {
        return this.dataBase.getAutoCommit();
    }


    @Override
    public void rollback() throws DbException {
        this.dataBase.rollback();
    }

    @Override
    public Map<String, Savepoint> getSavepoint() {
        return this.dataBase.getSavepoint();
    }


    @Override
    public void setSavepoint(String savepointName) throws DbException {
        this.dataBase.setSavepoint(savepointName);
    }

    @Override
    public void releaseSavepoint(String savepointName) throws DbException {
        this.dataBase.releaseSavepoint(savepointName);
    }

    @Override
    public void rollbackToSavepoint(String savepointName) throws DbException {
        this.dataBase.rollbackToSavepoint(savepointName);
    }



    @Override
    public boolean isColsed() throws DbException {
        return this.dataBase.isColsed();
    }



    @Override
    public void commit() throws DbException {
        this.dataBase.commit();
    }


    @Override
    public void close() {
        this.dataBase.close();
    }

    @Override
    public <T> int insertBy(T insertObject, boolean includeNull) throws DbException {
        return dataBase.insertBy(insertObject, includeNull);
    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties) throws DbException {
        return dataBase.insertBy(insertObject, insertProperties);
    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        return dataBase.insertBy(insertObject, insertProperties, includeNull);
    }

    @Override
    public <T> int[] insertBy(T[] objs, boolean includeNull) throws DbException {
        return dataBase.insertBy(objs, includeNull);
    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties) throws DbException {
        return dataBase.insertBy(objs, insertProperties);
    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties, boolean includeNull) throws DbException {
        return dataBase.insertBy(objs, insertProperties, includeNull);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, boolean includeNull) throws DbException {
        return dataBase.insertReturnKeyBy(insertObject, includeNull);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties) throws DbException {
        return dataBase.insertReturnKeyBy(insertObject, insertProperties);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        return dataBase.insertReturnKeyBy(insertObject, insertProperties, includeNull);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis) throws DbException {
        return dataBase.updateBy(updateObject, whereProperteis);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObject, whereProperties, includeNull);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis, Object[] updateProperties) throws DbException {
        return dataBase.updateBy(updateObject, whereProperteis, updateProperties);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObject, whereProperties, updateProperties, includeNull);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis, Object[] updateProperties) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperteis, updateProperties);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperties, updateProperties, includeNull);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperteis);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperties, includeNull);
    }

    @Override
    public <T> T queryOneBy(T selectObject, Object[] whereProperteis) throws DbException {
        return dataBase.queryOneBy(selectObject, whereProperteis);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis) throws DbException {
        return dataBase.queryListBy(selectObject, whereProperteis);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis, int page, int perPage, PageBean pb) throws DbException {
        return dataBase.queryListBy(selectObject, whereProperteis, page, perPage, pb);
    }

    @Override
    public <T> int delBy(T deleteObject, Object[] whereProperteis) throws DbException {
        return dataBase.delBy(deleteObject, whereProperteis);
    }

    @Override
    public <T> int[] delBy(T[] deleteObjects, Object[] whereProperteis) throws DbException {
        return dataBase.delBy(deleteObjects, whereProperteis);
    }
}
