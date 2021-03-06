package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.tool.PageBean;

import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MDataBase extends DBObjectOperation, AutoCloseable{
    DataBase getDataBase() ;
    String exeScript(String packageFullName, String sqlFileName,
                     boolean throwWarning) ;
    String exeScript(String mdFullMethodName,String delimiters, Map<String, Object> args) throws DbException;

    DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                  PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args) throws DbException;

    <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean,
                          RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException;

    List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                       PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException;

    <T> T queryOne(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException;

    <T> List<T> queryListOne2One(Class<T> clazz, String mdFullMethodName, Map<String, Object> args,
                                        One2OneMapNestOptions one2OneMapNestOptions) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page,
                          int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    <T> List<T> queryListOne2One(Class<T> clazz, String mdFullMethodName,
                                        Map<String, Object> args, One2OneMapNestOptions one2OneMapNestOptions,
                                        int page, int perPage, PageBean pageBean,
                                        String countSqlMdFullMethodName) throws DbException;

     <T> List<T> queryListOne2Many(Class<T> clazz, String mdFullMethodName,
                                         Map<String, Object> args,
                                         One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException ;

    <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException;

    List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args) throws DbException;

    int del(String mdFullMethodName, Map<String, Object> args) throws DbException;

    void callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
                      List<DataBaseSet> returnDataBaseSets) throws DbException;

    int insert(String mdFullMethodName, Map<String, Object> args) throws DbException;
    long insertReturnKey(String mdFullMethodName, Map<String, Object> args) throws DbException;
    int[] insert(String[] mdFullMethodNames, Map<String, Object>[] args) throws DbException;
    int[] insert(ArrayList<String> mdFullMethodNames) throws DbException;
    int[] insert(String mdFullMethodName, List<Map<String, Object>> args) throws DbException;

    int[] update(String mdFullMethodName, List<Map<String, Object>> args) throws DbException;
    int[] update(String[] mdFullMethodNames, Map<String, Object>[] args) throws DbException;
    int[] update(ArrayList<String> mdFullMethodNames) throws DbException;
    int update(String mdFullMethodName, Map<String, Object> args) throws DbException;
    /**
     * ??????interfaceType????????????????????????????????????interfaceType????????????????????????????????????md?????????
     * ???????????????md??????????????????????????????.md?????????
     * @param interfaceType  ?????????????????????????????????
     * @param <T>
     * @return ????????????interfaceType?????????????????????????????????
     */
    <T> T getMapper(Class<T> interfaceType) throws DbException;

    Connection getConnection(boolean force);

    void setAutoCommit(boolean b) throws DbException;

    boolean getAutoCommit() throws DbException;

    void rollback() throws DbException;
    /**
     * ????????????????????????
     * @return
     */
    Map<String, Savepoint> getSavepoint();
    /**
     * ???????????????
     * @param savepointName ???????????????
     * @throws DbException
     */
    void setSavepoint(String savepointName) throws DbException;

    /**
     * ??????????????????????????????savepoint
     * @param savepointName
     * @throws DbException
     */
    void releaseSavepoint(String savepointName) throws DbException;
    /**
     * ?????????????????????????????????
     * @throws DbException
     */
    void rollbackToSavepoint(String savepointName) throws DbException;


    boolean isColsed() throws DbException;

    void commit() throws DbException;

    void close();

    DBMS getDataBaseType();
}
