package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.tool.PageBean;

import javax.sql.DataSource;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DataBaseDecorator implements DBObjectOperation, AutoCloseable, DataBase {
    protected DataBase db;

    public DataBaseDecorator(DataBase db) {
        this.db = db;
    }

    public DataBase getContainedDataBase() {
        return this.db;
    }

    @Override
    public MainSlaveModeConnectMode getMainSlaveModeConnectMode() {
        return db.getMainSlaveModeConnectMode();
    }

    @Override
    public void connectDb(Connection connection, boolean externalControlConClose) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void connectDb(DataSource dataSource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isExternalControlConClose() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDbPoolName() {
        return db.getDbPoolName();
    }

    @Override
    public boolean isMainSlaveMode() {
        return db.isMainSlaveMode();
    }
    @Override
    public DataSource getDataSource() {
        return db.getDataSource();
    }
    @Override
    public void setMainSlaveMode(boolean mainSlaveMode) {
        db.setMainSlaveMode(mainSlaveMode);
    }

    @Override
    public boolean getInternalConnectionAutoCommit() throws DbException {
        return db.getInternalConnectionAutoCommit();
    }

    @Override
    public DBMS getDataBaseType() {
        return db.getDataBaseType();
    }

    @Override
    public ConnectType getConnectionType() {
        return db.getConnectionType();
    }

    @Override
    public boolean getAutoCommit() throws DbException {
        return db.getAutoCommit();
    }

    @Override
    public boolean isColsed() throws DbException {
        return db.isColsed();
    }

    @Override
    public Map<String, Savepoint> getSavepoint() {
        return db.getSavepoint();
    }

    @Override
    public Connection getConnection(boolean force) {
        return db.getConnection(force);
    }

    @Override
    public Boolean connectedToMaster() {
        return db.connectedToMaster();
    }

    @Override
    public DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                                         PageBean pageUtils, String countSql) throws DbException {
        return db.queryForResultSet(sqlQuery, vParameters, page, perPage, pageUtils, countSql);
    }

    @Override
    public DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        return db.queryForResultSet(sqlQuery, vParameters);
    }

    @Override
    public <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageUtils,
                                 RowMapper<T> rowMapper, String countSql) throws DbException {
        return db.queryList(sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql);
    }

    @Override
    public List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
                                              PageBean pageUtils, String countSql) throws DbException {
        return db.queryMap(sqlQuery, args, page, perPage, pageUtils, countSql);
    }

    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        return db.queryList(clazz, sqlQuery, vParameters);
    }

    @Override
    public <T> T queryOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        return db.queryOne(clazz, sqlQuery, vParameters);
    }

    @Override
    public <T> List<T> queryListOne2One(Class<T> clazz, String sqlQuery,
                                        Map<Integer, Object> vParameters,
                                        One2OneMapNestOptions one2OneMapNestOptions) throws DbException {
        return db.queryListOne2One(clazz, sqlQuery, vParameters, one2OneMapNestOptions);
    }

    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                                 PageBean pageUtils, String countSql) throws DbException {
        return db.queryList(clazz, sqlQuery, vParameters, page, perPage, pageUtils, countSql);
    }

    @Override
    public <T> List<T> queryListOne2One(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                        One2OneMapNestOptions one2OneMapNestOptions, int page, int perPage, PageBean pageUtils, String countSql)
            throws DbException {
        return db.queryListOne2One(clazz, sqlQuery, vParameters, one2OneMapNestOptions, page, perPage, pageUtils, countSql);
    }

    @Override
    public <T> List<T> queryListOne2Many(Class<T> clazz, String sqlQuery,
                                         Map<Integer, Object> vParameters,
                                         One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException {
        return db.queryListOne2Many(clazz, sqlQuery, vParameters, one2ManyMapNestOptions);
    }

    @Override
    public <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException {
        return db.queryList(sqlQuery, args, rowMapper);
    }

    @Override
    public List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args) throws DbException {
        return db.queryMap(sqlQuery, args);
    }

    @Override
    public int del(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return db.del(sqltext, vParameters);
    }

    @Override
    public int update(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return db.update(sqltext, vParameters);
    }

    @Override
    public void callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
                              List<DataBaseSet> returnDataBaseSets) throws DbException {
        db.callStoredPro(sqltext, parms, outPramsValues, returnDataBaseSets);
    }

    @Override
    public int insert(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return db.insert(sqltext, vParameters);
    }

    @Override
    public long insertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        return db.insertReturnKey(sqltext, vParameters);
    }


    @Override
    public int[] update(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
        return db.update(sqltxts, vParametersArray);
    }

    @Override
    public int[] insert(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
        return db.insert(sqltxts, vParametersArray);
    }

    @Override
    public int[] update(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
        return db.update(sqltxt, vParametersList);
    }

    @Override
    public int[] insert(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
        return db.insert(sqltxt, vParametersList);
    }

    @Override
    public int[] update(ArrayList<String> sqltxts) throws DbException {
        return db.update(sqltxts);
    }

    @Override
    public int[] insert(ArrayList<String> sqltxts) throws DbException {
        return db.insert(sqltxts);
    }

    @Override
    public <T> int insertBy(T insertObject) throws DbException {
        return db.insertBy(insertObject);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject) throws DbException {
        return db.insertReturnKeyBy(insertObject);
    }

    @Override
    public <T> int insertBy(T insertObject, boolean includeNull) throws DbException {
        return db.insertBy(insertObject, includeNull);
    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties) throws DbException {
        return db.insertBy(insertObject, insertProperties);
    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        return db.insertBy(insertObject, insertProperties, includeNull);
    }

    @Override
    public <T> int[] insertBy(T[] objs) throws DbException {
        return db.insertBy(objs);
    }

    @Override
    public <T> int[] insertBy(T[] objs, boolean includeNull) throws DbException {
        return db.insertBy(objs, includeNull);
    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties) throws DbException {
        return db.insertBy(objs, insertProperties);
    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties, boolean includeNull) throws DbException {
        return db.insertBy(objs, insertProperties, includeNull);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, boolean includeNull) throws DbException {
        return db.insertReturnKeyBy(insertObject, includeNull);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties) throws DbException {
        return db.insertReturnKeyBy(insertObject, insertProperties);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        return db.insertReturnKeyBy(insertObject, insertProperties, includeNull);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis) throws DbException {
        return db.updateBy(updateObject, whereProperteis);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, boolean includeNull) throws DbException {
        return db.updateBy(updateObject, whereProperties, includeNull);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis, Object[] updateProperties) throws DbException {
        return db.updateBy(updateObject, whereProperteis, updateProperties);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        return db.updateBy(updateObject, whereProperties, updateProperties, includeNull);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis, Object[] updateProperties) throws DbException {
        return db.updateBy(updateObjects, whereProperteis, updateProperties);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        return db.updateBy(updateObjects, whereProperties, updateProperties, includeNull);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis) throws DbException {
        return db.updateBy(updateObjects, whereProperteis);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, boolean includeNull) throws DbException {
        return db.updateBy(updateObjects, whereProperties, includeNull);
    }

    @Override
    public <T> T queryOneBy(T selectObject) throws DbException {
        return db.queryOneBy(selectObject);
    }

    @Override
    public <T> T queryOneBy(T selectObject, Object[] whereProperteis) throws DbException {
        return db.queryOneBy(selectObject, whereProperteis);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis) throws DbException {
        return db.queryListBy(selectObject, whereProperteis);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject) throws DbException {
        return db.queryListBy(selectObject);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis, int page, int perPage, PageBean pb) throws DbException {
        return db.queryListBy(selectObject, whereProperteis, page, perPage, pb);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException {
        return db.queryListBy(selectObject, page, perPage, pb);
    }

    @Override
    public <T> int delBy(T deleteObject, Object[] whereProperteis) throws DbException {
        return db.delBy(deleteObject, whereProperteis);
    }

    @Override
    public <T> int[] delManyBy(T[] deleteObjects, Object[] whereProperteis) throws DbException {
        return db.delManyBy(deleteObjects, whereProperteis);
    }

    @Override
    public String exeScript(Reader reader, boolean throwWarning,String delimiters, Map<String, Object> args) {
        return db.exeScript(reader, throwWarning,delimiters, args);
    }

    @Override
    public String exeScript(Reader reader, boolean throwWarning, boolean continueIfError, String delimiters, Map<String, Object> args) throws DbException {
        return db.exeScript(reader, throwWarning,continueIfError,delimiters, args);
    }
}
