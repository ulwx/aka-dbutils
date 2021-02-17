package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.PageBean;

import java.io.PrintWriter;
import java.sql.Connection;
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

    <T> List<T> queryListByOne2One(Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> args,
                          QueryMapNestOne2One[] queryMapNestList) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page,
                          int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    <T> List<T> queryListByOne2One(Class<T> clazz, String sqlPrefix, String mdFullMethodName,
                          Map<String, Object> args, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                          PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    <T> List<T> queryListByOne2Many(Class<T> clazz, String sqlPrefix, String[] parentBeanKeys, String mdFullMethodName,
                          Map<String, Object> args, QueryMapNestOne2Many[] queryMapNestList) throws DbException;

    <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException;

    List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args) throws DbException;

    int del(String mdFullMethodName, Map<String, Object> args) throws DbException;

    int callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
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

    Connection getConnection();

    void setAutoCommit(boolean b) throws DbException;

    boolean getAutoCommit() throws DbException;

    void rollback() throws DbException;

    boolean isColsed() throws DbException;

    void commit() throws DbException;

    void close();
}
