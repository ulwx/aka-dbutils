package com.github.ulwx.database;

import com.github.ulwx.database.DataBaseImpl.ConnectType;
import com.github.ulwx.tool.PageBean;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract   class DataBaseDecorator implements DBObjectOperation, AutoCloseable, DataBase {
	protected DataBase db;

	public DataBaseDecorator(DataBase db) {
		this.db = db;
	}

	public DataBase getContentDataBase() {
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
	public void setMainSlaveMode(boolean mainSlaveMode) {
		db.setMainSlaveMode(mainSlaveMode);
	}
	@Override
	public boolean getInternalConnectionAutoCommit() throws DbException {
		return db.getInternalConnectionAutoCommit();
	}

	@Override
	public String getDataBaseType() {
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
	public Connection getConnection() {
		return db.getConnection();
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
	public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList) throws DbException {
		return db.queryList(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList);
	}
	@Override
	public <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException {
		return db.queryList(clazz, sqlQuery, vParameters, page, perPage, pageUtils, countSql);
	}
	@Override
	public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList, int page, int perPage, PageBean pageUtils, String countSql)
			throws DbException {
		return db.queryList(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList, page, perPage, pageUtils, countSql);
	}

	@Override
	public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
		return db.queryList(clazz, sqlPrefix, beanKey, sqlQuery, vParameters, queryMapNestList);
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
	public int callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
			List<DataBaseSet> returnDataBaseSets) throws DbException {
		return db.callStoredPro(sqltext, parms, outPramsValues, returnDataBaseSets);
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
	public <T> int insertBy(T insertObject, String[] properties) throws DbException {
		return db.insertBy(insertObject, properties);
	}
	@Override
	public <T> long insertReturnKeyBy(T insertObject, String[] properties) throws DbException {
		return db.insertReturnKeyBy(insertObject, properties);
	}
	@Override
	public <T> int[] insertBy(T[] objs) throws DbException {
		return db.insertBy(objs);
	}
	@Override
	public <T> int[] insertBy(T[] objs, String[] properties) throws DbException {
		return db.insertBy(objs, properties);
	}
	@Override
	public <T> int updateBy(T updateObject, String beanKey) throws DbException {
		return db.updateBy(updateObject, beanKey);
	}
	@Override
	public <T> int updateBy(T updateObject, String beanKey, String[] properties) throws DbException {
		return db.updateBy(updateObject, beanKey, properties);
	}
	@Override
	public <T> int[] updateBy(T[] objects, String beanKey, String[] properties) throws DbException {
		return db.updateBy(objects, beanKey, properties);
	}
	@Override
	public <T> int[] updateBy(T[] objects, String beanKey) throws DbException {
		return db.updateBy(objects, beanKey);
	}
	@Override
	public <T> int[] updateBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
		return db.updateBy(objects, beanKeys, properties);
	}
	@Override
	public <T> T queryOneBy(T selectObject, String selectProperties) throws DbException {
		return db.queryOneBy(selectObject, selectProperties);
	}
	@Override
	public <T> List<T> queryListBy(T selectObject, String selectProperties) throws DbException {
		return db.queryListBy(selectObject, selectProperties);
	}
	@Override
	public <T> List<T> queryListBy(T selectObject, String selectProperties, int page, int perPage, PageBean pb)
			throws DbException {
		return db.queryListBy(selectObject, selectProperties, page, perPage, pb);
	}
	@Override
	public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException {
		return db.queryListBy(selectObject, page, perPage, pb);
	}
	@Override
	public <T> List<T> queryListBy(T selectObject) throws DbException {
		return db.queryListBy(selectObject);
	}
	@Override
	public <T> T queryOneBy(T selectObject) throws DbException {
		return db.queryOneBy(selectObject);
	}
	@Override
	public <T> int delBy(T deleteObject, String deleteProperteis) throws DbException {
		return db.delBy(deleteObject, deleteProperteis);
	}
	@Override
	public <T> int[] delBy(T[] deleteObjects, String deleteProperteis) throws DbException {
		return db.delBy(deleteObjects, deleteProperteis);
	}
	@Override
	public <T> int[] delBy(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException {
		return db.delBy(deleteObjects, deletePropertiesArray);
	}
	@Override
	public <T> int insertWholeBy(T insertObject) throws DbException {
		return db.insertWholeBy(insertObject);
	}

	@Override
	public <T> long insertWholeReturnKeyBy(T insertObject) throws DbException {
		return db.insertWholeReturnKeyBy(insertObject);
	}

	@Override
	public <T> int insertWholeBy(T insertObject, String[] properties) throws DbException {
		return db.insertWholeBy(insertObject, properties);
	}
	@Override
	public <T> long insertWholeReturnKeyBy(T insertObject, String[] properties) throws DbException {
		return db.insertWholeReturnKeyBy(insertObject, properties);
	}
	@Override
	public <T> int[] insertWholeBy(T[] objs) throws DbException {
		return db.insertWholeBy(objs);
	}
	@Override
	public <T> int[] insertWholeBy(T[] objs, String[] properties) throws DbException {
		return db.insertWholeBy(objs, properties);
	}
	@Override
	public <T> int updateWholeBy(T updateObject, String beanKey) throws DbException {
		return db.updateWholeBy(updateObject, beanKey);
	}
	@Override
	public <T> int updateWholeBy(T updateObject, String beanKey, String[] properties) throws DbException {
		return db.updateWholeBy(updateObject, beanKey, properties);
	}
	@Override
	public <T> int[] updateWholeBy(T[] objects, String beanKey, String[] properties) throws DbException {
		return db.updateWholeBy(objects, beanKey, properties);
	}
	@Override
	public <T> int[] updateWholeBy(T[] objects, String beanKey) throws DbException {
		return db.updateWholeBy(objects, beanKey);
	}
	@Override
	public <T> int[] updateWholeBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
		return db.updateWholeBy(objects, beanKeys, properties);
	}

	@Override
	public String exeScript(Reader reader, PrintWriter logWriter) throws DbException {
		return db.exeScript(reader, logWriter);
	}

	@Override
	public String exeScript(Reader reader) throws DbException {
		return db.exeScript(reader);
	}

}
