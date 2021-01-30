package com.github.ulwx.database;

import com.github.ulwx.tool.PageBean;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MDataBase extends DBObjectOperation, AutoCloseable{
    boolean isExternalControlConClose();

    DataBase getDataBase();

    void setDataBase(DataBase dataBase);

    boolean isAutoReconnect();

    void setAutoReconnect(boolean autoReconnect) throws DbException;

    boolean isMainSlaveMode();

    void setMainSlaveMode(boolean mainSlaveMode);

    boolean getInternalConnectionAutoCommit() throws DbException;

    void selectSlaveDb() throws DbException;

    String getDataBaseType();

    String exeScript(String packageFullName, String sqlFileName) throws DbException;

    String exeScript(String packageFullName, String sqlFileName, PrintWriter logWriter) throws DbException;

    String exeScript(String mdFullMethodName, Map<String, Object> args) throws DbException;

    DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                  PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args) throws DbException;

    <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean,
                          RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException;

    List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                       PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException;

    <T> T queryOne(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> args,
                          QueryMapNestOne2One[] queryMapNestList) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page,
                          int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String mdFullMethodName,
                          Map<String, Object> args, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                          PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String beanKey, String mdFullMethodName,
                          Map<String, Object> args, QueryMapNestOne2Many[] queryMapNestList) throws DbException;

    <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException;

    List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args) throws DbException;

    int del(String mdFullMethodName, Map<String, Object> args) throws DbException;

    int update(String mdFullMethodName, Map<String, Object> args) throws DbException;

    int callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
                      List<DataBaseSet> returnDataBaseSets) throws DbException;

    int insert(String mdFullMethodName, Map<String, Object> args) throws DbException;

    long insertReturnKey(String mdFullMethodName, Map<String, Object> args) throws DbException;

    int[] update(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException;

    int[] insert(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException;

    int[] update(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException;

    int[] insert(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException;

    int[] update(ArrayList<String> mdFullMethodNameList) throws DbException;

    int[] insert(ArrayList<String> mdFullMethodNameList) throws DbException;

    Connection getConnection();

    void setAutoCommit(boolean b) throws DbException;

    boolean getAutoCommit() throws DbException;

    void rollback() throws DbException;

    boolean isColsed() throws DbException;

    void commit() throws DbException;

    void closer() throws DbException;

    void close();
}
