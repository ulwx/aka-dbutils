package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.database.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbUtils extends BaseDao {

    public static DataBaseSet queryForResultSet(String dbpoolName, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSql) throws DbException {
        return execute(db -> {
            return db.queryForResultSet(sqlQuery, vParameters, page, perPage, pageUtils, countSql);
        }, dbpoolName);

    }

    public static DataBaseSet queryForResultSet(String dbpoolName, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        return execute(db -> {
            return db.queryForResultSet(sqlQuery, vParameters);
        }, dbpoolName);
    }

    public static <T> List<T> queryList(String dbpoolName, String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSql) throws DbException {
        return execute(db -> {
            return db.queryList(sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql);
        }, dbpoolName);
    }

    public static List<Map<String, Object>> queryMap(String dbpoolName, String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageUtils, String countSql) throws DbException {
        return execute(db -> {
            return db.queryMap(sqlQuery, args, page, perPage, pageUtils, countSql);
        }, dbpoolName);
    }

    public static List<Map<String, Object>> queryMap(String dbpoolName, String sqlQuery, Map<Integer, Object> args) throws DbException {
        return execute(db -> {
            return db.queryMap(sqlQuery, args);
        }, dbpoolName);
    }

    public static <T> List<T> queryList(String dbpoolName, Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        return execute(db -> {
            return db.queryList(clazz, sqlQuery, vParameters);
        }, dbpoolName);
    }

    public static <T> T queryOne(String dbpoolName, Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        return execute(db -> {
            return db.queryOne(clazz, sqlQuery, vParameters);
        }, dbpoolName);
    }

    public static <T> List<T> queryListByOne2One(String dbpoolName, Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList) throws DbException {
        return execute(db -> {
            return db.queryListByOne2One(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList);
        }, dbpoolName);
    }

    public static <T> List<T> queryList(String dbpoolName, Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSql) throws DbException {
        return execute(db -> {
            return db.queryList(clazz, sqlQuery, vParameters, page, perPage, pageUtils, countSql);
        }, dbpoolName);
    }

    public static <T> List<T> queryListByOne2One(String dbpoolName, Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage, PageBean pageUtils, String countSql) throws DbException {
        return execute(db -> {
            return db.queryListByOne2One(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList, page, perPage, pageUtils, countSql);
        }, dbpoolName);
    }

    public static <T> List<T> queryListByOne2Many(String dbpoolName, Class<T> clazz, String sqlPrefix, String[] parentBeanKeys, String sqlQuery, Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
        return execute(db -> {
            return db.queryListByOne2Many(clazz, sqlPrefix, parentBeanKeys, sqlQuery, vParameters, queryMapNestList);
        }, dbpoolName);
    }

    public static <T> List<T> queryList(String dbpoolName, String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException {
        return execute(db -> {
            return db.queryList(sqlQuery, args, rowMapper);
        }, dbpoolName);
    }

    public static int del(String dbpoolName, String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return execute(db -> {
            return db.del(sqltext, vParameters);
        }, dbpoolName);
    }

    public static int update(String dbpoolName, String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return execute(db -> {
            return db.update(sqltext, vParameters);
        }, dbpoolName);
    }

    public static void callStoredPro(String dbpoolName, String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues, List<DataBaseSet> returnDataBaseSets) throws DbException {
         execute(db -> {
             db.callStoredPro(sqltext, parms, outPramsValues, returnDataBaseSets);
             return 1;
        }, dbpoolName);
    }

    public static int insert(String dbpoolName, String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return execute(db -> {
            return db.insert(sqltext, vParameters);
        }, dbpoolName);
    }

    public static long insertReturnKey(String dbpoolName, String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return execute(db -> {
            return db.insertReturnKey(sqltext, vParameters);
        }, dbpoolName);
    }

    public static int[] update(String dbpoolName, String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
        return execute(db -> {
            return db.update(sqltxts, vParametersArray);
        }, dbpoolName);
    }

    public static int[] insert(String dbpoolName, String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
        return execute(db -> {
            return db.insert(sqltxts, vParametersArray);
        }, dbpoolName);
    }

    public static int[] update(String dbpoolName, String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
        return execute(db -> {
            return db.update(sqltxt, vParametersList);
        }, dbpoolName);
    }

    public static int[] insert(String dbpoolName, String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
        return execute(db -> {
            return db.insert(sqltxt, vParametersList);
        }, dbpoolName);
    }

    public static int[] update(String dbpoolName, ArrayList<String> sqltxts) throws DbException {
        return execute(db -> {
            return db.update(sqltxts);
        }, dbpoolName);
    }

    public static int[] insert(String dbpoolName, ArrayList<String> sqltxts) throws DbException {
        return execute(db -> {
            return db.insert(sqltxts);
        }, dbpoolName);
    }


    public static String exeScript(String dbpoolName, Reader reader, Boolean throwWarning) throws DbException {
        return execute(db -> {
            return db.exeScript(reader, throwWarning,null);
        }, dbpoolName);
    }


}
