package com.github.ulwx.tool;

import com.github.ulwx.database.*;
import com.github.ulwx.tool.support.StringUtils;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MDbUtils extends BaseDao {

    private static <R> R mdbExecute(Function<MDataBase, R> function, String dbpoolName) {
        MDataBase mdb = null;
        try {
            if (StringUtils.isEmpty(dbpoolName)) {
                mdb = MDbManager.getDataBase();
            } else {
                mdb = MDbManager.getDataBase(dbpoolName);
            }
            return function.apply(mdb);
        } finally {
            if (mdb != null) {
                mdb.close();
            }
        }

    }

    public static  String exeScript(String dbpoolName, String packageFullName, String sqlFileName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.exeScript(packageFullName, sqlFileName);
        }, dbpoolName);

    }

    public static  String exeScript(String dbpoolName, String packageFullName, String sqlFileName, PrintWriter logWriter) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.exeScript(packageFullName, sqlFileName, logWriter);
        }, dbpoolName);
    }

    public static  String exeScript(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.exeScript(mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  DataBaseSet queryForResultSet(String dbpoolName, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryForResultSet(mdFullMethodName, args, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    public static  DataBaseSet queryForResultSet(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryForResultSet(mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  <T> List<T> queryList(String dbpoolName, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(mdFullMethodName, args, page, perPage, pageBean, rowMapper, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    public static  List<Map<String, Object>> queryMap(String dbpoolName, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryMap(mdFullMethodName, args, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    public static  <T> List<T> queryList(String dbpoolName, Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(clazz, mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  <T> T queryOne(String dbpoolName, Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryOne(clazz, mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  <T> List<T> queryList(String dbpoolName, Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> args, QueryMapNestOne2One[] queryMapNestList) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(clazz, sqlPrefix, mdFullMethodName, args, queryMapNestList);
        }, dbpoolName);
    }

    public static  <T> List<T> queryList(String dbpoolName, Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(clazz, mdFullMethodName, args, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    public static  <T> List<T> queryList(String dbpoolName, Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> args, QueryMapNestOne2One[] queryMapNestList, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(clazz, sqlPrefix, mdFullMethodName, args, queryMapNestList, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    public static  <T> List<T> queryList(String dbpoolName, Class<T> clazz, String sqlPrefix, String beanKey, String mdFullMethodName, Map<String, Object> args, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(clazz, sqlPrefix, beanKey, mdFullMethodName, args, queryMapNestList);
        }, dbpoolName);
    }

    public static  <T> List<T> queryList(String dbpoolName, String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(mdFullMethodName, args, rowMapper);
        }, dbpoolName);
    }

    public static  List<Map<String, Object>> queryMap(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryMap(mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  int del(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.del(mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  int update(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  int callStoredPro(String dbpoolName, String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues, List<DataBaseSet> returnDataBaseSets) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.callStoredPro(mdFullMethodName, parms, outPramsValues, returnDataBaseSets);
        }, dbpoolName);
    }

    public static  int insert(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  long insertReturnKey(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insertReturnKey(mdFullMethodName, args);
        }, dbpoolName);
    }

    public static  int[] update(String dbpoolName, String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodNameList, vParametersArray);
        }, dbpoolName);
    }

    public static  int[] insert(String dbpoolName, String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodNameList, vParametersArray);
        }, dbpoolName);
    }

    public static  int[] update(String dbpoolName, String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodName, vParametersList);
        }, dbpoolName);
    }

    public static  int[] insert(String dbpoolName, String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodName, vParametersList);
        }, dbpoolName);
    }

    public static  int[] update(String dbpoolName, ArrayList<String> mdFullMethodNameList) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodNameList);
        }, dbpoolName);
    }

    public static  int[] insert(String dbpoolName, ArrayList<String> mdFullMethodNameList) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodNameList);
        }, dbpoolName);
    }


}
