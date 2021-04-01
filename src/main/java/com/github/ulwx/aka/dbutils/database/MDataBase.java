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

public interface MDataBase extends DBObjectOperation, AutoCloseable {
    DataBase getDataBase();

    String exeScript(String packageFullName, String sqlFileName,
                     boolean throwWarning);

    String exeScript(String mdFullMethodName, String delimiters, Map<String, Object> args) throws DbException;

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
                                  One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException;

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
     * 根据interfaceType指定的接口生成动态代理。interfaceType接口里的方法映射到对应的md方法，
     * 接口名称与md文件名称相同（不包含.md后缀）
     *
     * @param interfaceType 指定接口，生成代理对象
     * @param <T>
     * @return 返回根据interfaceType接口生成的动态代理对象
     */
    <T> T getMapper(Class<T> interfaceType) throws DbException;

    Connection getConnection(boolean force);

    void setAutoCommit(boolean b) throws DbException;

    boolean getAutoCommit() throws DbException;

    void rollback() throws DbException;

    /**
     * 得到保存点信息；
     *
     * @return
     */
    Map<String, Savepoint> getSavepoint();

    /**
     * 设置保存点
     *
     * @param savepointName 保存点名称
     * @throws DbException
     */
    void setSavepoint(String savepointName) throws DbException;

    /**
     * 释放并删除指定名称的savepoint
     *
     * @param savepointName
     * @throws DbException
     */
    void releaseSavepoint(String savepointName) throws DbException;

    /**
     * 用于事务回滚到保存点。
     *
     * @throws DbException
     */
    void rollbackToSavepoint(String savepointName) throws DbException;


    boolean isColsed() throws DbException;

    void commit() throws DbException;

    void close();

    DBMS getDataBaseType();
}
