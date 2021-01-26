package com.ulwx.database;

import com.ulwx.database.DataBaseImpl.ConnectType;
import com.ulwx.tool.PageBean;

import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DataBaseDecorator implements NoSqlOperation, AutoCloseable, DataBase {
	protected DataBase db;

	public DataBaseDecorator(DataBase db) {
		// TODO Auto-generated constructor stub
		this.db = db;
	}
	public DataBase getContentDataBase() {
		return this.db;
	}

	public MainSlaveModeConnectMode getMainSlaveModeConnectMode() {
		return db.getMainSlaveModeConnectMode();
	}
	public boolean isAutoReconnect() {
		return db.isAutoReconnect();
	}
	public void setAutoReconnect(boolean autoReconnect) throws DbException {
		db.setAutoReconnect(autoReconnect);
	}
	public String getDbPoolName() {
		return db.getDbPoolName();
	}
	public boolean isMainSlaveMode() {
		return db.isMainSlaveMode();
	}
	public void setMainSlaveMode(boolean mainSlaveMode) {
		db.setMainSlaveMode(mainSlaveMode);
	}
	public boolean getInternalConnectionAutoCommit() throws DbException {
		return db.getInternalConnectionAutoCommit();
	}
	public void selectSlaveDb() throws DbException {
		db.selectSlaveDb();
	}
	public String getDataBaseType() {
		return db.getDataBaseType();
	}
	public void connectDb(String dbPoolName) throws DbException {
		 this.connectDb(dbPoolName, MainSlaveModeConnectMode.Try_Connect_MainServer);
	}
	public ConnectType getConnectionType() {
		return db.getConnectionType();
	}
	public void connectDb(String dbPoolName, MainSlaveModeConnectMode mainSlaveModeConnectMode) throws DbException {
		db.connectDb(dbPoolName, mainSlaveModeConnectMode);
	}
	public DataBaseSet doCachedQuery(String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
		return db.doCachedQuery(sqlQuery, vParameters);
	}
	public DataBaseSet doCachedPageQuery(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException {
		return db.doCachedPageQuery(sqlQuery, vParameters, page, perPage, pageUtils, countSql);
	}
	public DataBaseSet query(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException {
		return db.query(sqlQuery, vParameters, page, perPage, pageUtils, countSql);
	}
	public <T> List<T> doPageQueryObject(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
			PageBean pageUtils, RowMapper<T> rowMapper, String countSql) throws DbException {
		return db.doPageQueryObject(sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql);
	}
	public <T> List<T> query(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageUtils,
			RowMapper<T> rowMapper, String countSql) throws DbException {
		return db.query(sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql);
	}
	public List<Map<String, Object>> doPageQueryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException {
		return db.doPageQueryMap(sqlQuery, args, page, perPage, pageUtils, countSql);
	}
	public List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException {
		return db.queryMap(sqlQuery, args, page, perPage, pageUtils, countSql);
	}
	public <T> List<T> doQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters)
			throws DbException {
		return db.doQueryClass(clazz, sqlQuery, vParameters);
	}
	public <T> List<T> query(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
		return db.query(clazz, sqlQuery, vParameters);
	}
	public <T> T doQueryClassOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
		return db.doQueryClassOne(clazz, sqlQuery, vParameters);
	}
	public <T> T queryOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
		return db.queryOne(clazz, sqlQuery, vParameters);
	}
	public <T> List<T> doQueryClassNoSql(Class<T> clazz, T selectObject, String selectProperties) throws DbException {
		return db.doQueryClassNoSql(clazz, selectObject, selectProperties);
	}
	public <T> List<T> doQueryClassNoSql(T selectObject) throws DbException {
		return db.doQueryClassNoSql(selectObject);
	}
	public <T> List<T> doQueryClassNoSql(T selectObject, String selectProperties) throws DbException {
		return db.doQueryClassNoSql(selectObject, selectProperties);
	}
	public <T> List<T> doQueryClassOne2One(Class<T> clazz, String sqlPrefix, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList) throws DbException {
		return db.doQueryClassOne2One(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList);
	}
	public <T> List<T> query(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList) throws DbException {
		return db.query(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList);
	}
	public <T> List<T> doPageQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page,
			int perPage, PageBean pageUtils, String countSql) throws DbException {
		return db.doPageQueryClass(clazz, sqlQuery, vParameters, page, perPage, pageUtils, countSql);
	}
	public <T> List<T> query(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException {
		return db.query(clazz, sqlQuery, vParameters, page, perPage, pageUtils, countSql);
	}
	public <T> List<T> query(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList, int page, int perPage, PageBean pageUtils, String countSql)
			throws DbException {
		return db.query(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList, page, perPage, pageUtils, countSql);
	}
	public <T> List<T> doPageQueryClassOne2One(Class<T> clazz, String sqlPrefix, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException {
		return db.doPageQueryClassOne2One(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList, page, perPage,
				pageUtils, countSql);
	}
	public <T> List<T> doQueryClassOne2Many(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
		return db.doQueryClassOne2Many(clazz, sqlPrefix, beanKey, sqlQuery, vParameters, queryMapNestList);
	}
	public <T> List<T> query(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
		return db.query(clazz, sqlPrefix, beanKey, sqlQuery, vParameters, queryMapNestList);
	}
	public <T> List<T> doQueryObject(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper)
			throws DbException {
		return db.doQueryObject(sqlQuery, args, rowMapper);
	}
	public <T> List<T> query(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException {
		return db.query(sqlQuery, args, rowMapper);
	}
	public List<Map<String, Object>> doQueryMap(String sqlQuery, Map<Integer, Object> args) throws DbException {
		return db.doQueryMap(sqlQuery, args);
	}
	public List<Map<String, Object>> query(String sqlQuery, Map<Integer, Object> args) throws DbException {
		return db.query(sqlQuery, args);
	}
	public int executeBindDelete(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.executeBindDelete(sqltext, vParameters);
	}
	public int del(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.del(sqltext, vParameters);
	}
	public int executeBindUpdate(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.executeBindUpdate(sqltext, vParameters);
	}
	public int update(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.update(sqltext, vParameters);
	}
	public int executeStoredProcedure(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
			List<DataBaseSet> returnDataBaseSets) throws DbException {
		return db.executeStoredProcedure(sqltext, parms, outPramsValues, returnDataBaseSets);
	}
	public int callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
			List<DataBaseSet> returnDataBaseSets) throws DbException {
		return db.callStoredPro(sqltext, parms, outPramsValues, returnDataBaseSets);
	}
	public int executeBindInsert(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.executeBindInsert(sqltext, vParameters);
	}
	public int insert(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.insert(sqltext, vParameters);
	}
	public <T> int excuteInsertWholeClass(T insertObject) throws DbException {
		return db.excuteInsertWholeClass(insertObject);
	}
	public <T> int excuteInsertClass(T insertObject) throws DbException {
		return db.excuteInsertClass(insertObject);
	}
	public <T> int excuteInsertClass(T insertObject, String[] properties) throws DbException {
		return db.excuteInsertClass(insertObject, properties);
	}
	public <T> int excuteInsertWholeClass(T insertObject, String[] properties) throws DbException {
		return db.excuteInsertWholeClass(insertObject, properties);
	}
	public <T> long excuteInsertClassReturnKey(T insertObject, String[] properties) throws DbException {
		return db.excuteInsertClassReturnKey(insertObject, properties);
	}
	public <T> long excuteInsertWholeClassReturnKey(T insertObject, String[] properties) throws DbException {
		return db.excuteInsertWholeClassReturnKey(insertObject, properties);
	}
	public <T> long excuteInsertClassReturnKey(T insertObject) throws DbException {
		return db.excuteInsertClassReturnKey(insertObject);
	}
	public <T> long excuteInsertWholeClassReturnKey(T insertObject) throws DbException {
		return db.excuteInsertWholeClassReturnKey(insertObject);
	}
	public <T> int[] excuteInsertClass(T[] insertObjects, String[] properties) throws DbException {
		return db.excuteInsertClass(insertObjects, properties);
	}
	public <T> int[] excuteInsertWholeClass(T[] insertObjects, String[] properties) throws DbException {
		return db.excuteInsertWholeClass(insertObjects, properties);
	}
	public int[] excuteInsertObjects(Object[] insertObjects, String[][] properties) throws DbException {
		return db.excuteInsertObjects(insertObjects, properties);
	}
	public int[] excuteInsertWholeObjects(Object[] insertObjects, String[][] properties) throws DbException {
		return db.excuteInsertWholeObjects(insertObjects, properties);
	}
	public int[] excuteInsertWholeObjects(Object[] insertObjects) throws DbException {
		return db.excuteInsertWholeObjects(insertObjects);
	}
	public int[] excuteInsertObjects(Object[] insertObjects) throws DbException {
		return db.excuteInsertObjects(insertObjects);
	}
	public <T> int[] excuteInsertClass(T[] objs) throws DbException {
		return db.excuteInsertClass(objs);
	}
	public <T> int[] excuteInsertWholeClass(T[] objs) throws DbException {
		return db.excuteInsertWholeClass(objs);
	}
	public <T> int excuteUpdateClass(T updateObject, String beanKey, String[] properties) throws DbException {
		return db.excuteUpdateClass(updateObject, beanKey, properties);
	}
	public <T> int excuteUpdateWholeClass(T updateObject, String beanKey, String[] properties) throws DbException {
		return db.excuteUpdateWholeClass(updateObject, beanKey, properties);
	}
	public <T> int[] excuteDeleteClass(T[] deleteObject, String deleteProperteis) throws DbException {
		return db.excuteDeleteClass(deleteObject, deleteProperteis);
	}
	public <T> int excuteDeleteClass(T deleteObject, String deleteProperteis) throws DbException {
		return db.excuteDeleteClass(deleteObject, deleteProperteis);
	}
	public <T> int excuteUpdateClass(T updateObject, String beanKey) throws DbException {
		return db.excuteUpdateClass(updateObject, beanKey);
	}
	public <T> int excuteUpdateWholeClass(T updateObject, String beanKey) throws DbException {
		return db.excuteUpdateWholeClass(updateObject, beanKey);
	}
	public <T> int[] excuteUpdateClass(T[] updateObject, String beanKey) throws DbException {
		return db.excuteUpdateClass(updateObject, beanKey);
	}
	public <T> int[] excuteUpdateWholeClass(T[] updateObject, String beanKey) throws DbException {
		return db.excuteUpdateWholeClass(updateObject, beanKey);
	}
	public <T> int[] excuteUpdateClass(T[] objects, String beanKey, String[] properties) throws DbException {
		return db.excuteUpdateClass(objects, beanKey, properties);
	}
	public <T> int[] excuteUpdateWholeClass(T[] objects, String beanKey, String[] properties) throws DbException {
		return db.excuteUpdateWholeClass(objects, beanKey, properties);
	}
	public int[] excuteUpdateObjects(Object[] objects, String[] beanKeys) throws DbException {
		return db.excuteUpdateObjects(objects, beanKeys);
	}
	public int[] excuteUpdateObjects(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
		return db.excuteUpdateObjects(objects, beanKeys, properties);
	}
	public int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys, String[][] properties)
			throws DbException {
		return db.excuteUpdateWholeObjects(objects, beanKeys, properties);
	}
	public int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys) throws DbException {
		return db.excuteUpdateWholeObjects(objects, beanKeys);
	}
	public int[] excuteDeleteObjects(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException {
		return db.excuteDeleteObjects(deleteObjects, deletePropertiesArray);
	}
	public long executeBindInsertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.executeBindInsertReturnKey(sqltext, vParameters);
	}
	public long insertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException {
		return db.insertReturnKey(sqltext, vParameters);
	}
	public void setAutoCommit(boolean b) throws DbException {
		db.setAutoCommit(b);
	}
	public void reConnectDb() throws DbException {
		db.reConnectDb();
	}
	public boolean getAutoCommit() throws DbException {
		return db.getAutoCommit();
	}
	public void rollback() throws DbException {
		db.rollback();
	}
	public void rollbackAndClose() throws DbException {
		db.rollbackAndClose();
	}
	public boolean isColsed() throws DbException {
		return db.isColsed();
	}
	public void commit() throws DbException {
		db.commit();
	}
	public void commitAndClose() throws DbException {
		db.commitAndClose();
	}
	public int[] executeBindBatch(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
		return db.executeBindBatch(sqltxts, vParametersArray);
	}
	public int[] update(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
		return db.update(sqltxts, vParametersArray);
	}
	public int[] insert(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
		return db.insert(sqltxts, vParametersArray);
	}
	public int[] executeBindBatch(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
		return db.executeBindBatch(sqltxt, vParametersList);
	}
	public int[] update(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
		return db.update(sqltxt, vParametersList);
	}
	public int[] insert(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
		return db.insert(sqltxt, vParametersList);
	}
	public int[] executeBatch(ArrayList<String> sqltxt) throws DbException {
		return db.executeBatch(sqltxt);
	}
	public int[] update(ArrayList<String> sqltxts) throws DbException {
		return db.update(sqltxts);
	}
	public int[] insert(ArrayList<String> sqltxts) throws DbException {
		return db.insert(sqltxts);
	}
	public void closer() throws DbException {
		db.closer();
	}
	public void close() {
		db.close();
	}
	public Connection getConnection() {
		return db.getConnection();
	}
	public <T> int insert(T insertObject) throws DbException {
		return db.insert(insertObject);
	}
	public <T> long insertReturnKey(T insertObject) throws DbException {
		return db.insertReturnKey(insertObject);
	}
	public <T> int insert(T insertObject, String[] properties) throws DbException {
		return db.insert(insertObject, properties);
	}
	public <T> long insertReturnKey(T insertObject, String[] properties) throws DbException {
		return db.insertReturnKey(insertObject, properties);
	}
	public <T> int[] insert(T[] objs) throws DbException {
		return db.insert(objs);
	}
	public <T> int[] insert(T[] objs, String[] properties) throws DbException {
		return db.insert(objs, properties);
	}
	public <T> int update(T updateObject, String beanKey) throws DbException {
		return db.update(updateObject, beanKey);
	}
	public <T> int update(T updateObject, String beanKey, String[] properties) throws DbException {
		return db.update(updateObject, beanKey, properties);
	}
	public <T> int[] update(T[] objects, String beanKey, String[] properties) throws DbException {
		return db.update(objects, beanKey, properties);
	}
	public <T> int[] update(T[] objects, String beanKey) throws DbException {
		return db.update(objects, beanKey);
	}
	public <T> int[] update(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
		return db.update(objects, beanKeys, properties);
	}
	public <T> T queryOne(T selectObject, String selectProperties) throws DbException {
		return db.queryOne(selectObject, selectProperties);
	}
	public <T> List<T> query(T selectObject, String selectProperties) throws DbException {
		return db.query(selectObject, selectProperties);
	}
	public <T> List<T> query(T selectObject, String selectProperties, int page, int perPage, PageBean pb)
			throws DbException {
		return db.query(selectObject, selectProperties, page, perPage, pb);
	}
	public <T> List<T> query(T selectObject, int page, int perPage, PageBean pb) throws DbException {
		return db.query(selectObject, page, perPage, pb);
	}
	public <T> List<T> query(T selectObject) throws DbException {
		return db.query(selectObject);
	}
	public <T> T queryOne(T selectObject) throws DbException {
		return db.queryOne(selectObject);
	}
	public <T> int del(T deleteObject, String deleteProperteis) throws DbException {
		return db.del(deleteObject, deleteProperteis);
	}
	public <T> int[] del(T[] deleteObjects, String deleteProperteis) throws DbException {
		return db.del(deleteObjects, deleteProperteis);
	}
	public <T> int[] del(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException {
		return db.del(deleteObjects, deletePropertiesArray);
	}
	public <T> int insertWhole(T insertObject) throws DbException {
		return db.insertWhole(insertObject);
	}
	public <T> long insertWholeReturnKey(T insertObject) throws DbException {
		return db.insertWholeReturnKey(insertObject);
	}
	public <T> int insertWhole(T insertObject, String[] properties) throws DbException {
		return db.insertWhole(insertObject, properties);
	}
	public <T> long insertWholeReturnKey(T insertObject, String[] properties) throws DbException {
		return db.insertWholeReturnKey(insertObject, properties);
	}
	public <T> int[] insertWhole(T[] objs) throws DbException {
		return db.insertWhole(objs);
	}
	public <T> int[] insertWhole(T[] objs, String[] properties) throws DbException {
		return db.insertWhole(objs, properties);
	}
	public <T> int updateWhole(T updateObject, String beanKey) throws DbException {
		return db.updateWhole(updateObject, beanKey);
	}
	public <T> int updateWhole(T updateObject, String beanKey, String[] properties) throws DbException {
		return db.updateWhole(updateObject, beanKey, properties);
	}
	public <T> int[] updateWhole(T[] objects, String beanKey, String[] properties) throws DbException {
		return db.updateWhole(objects, beanKey, properties);
	}
	public <T> int[] updateWhole(T[] objects, String beanKey) throws DbException {
		return db.updateWhole(objects, beanKey);
	}
	public <T> int[] updateWhole(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
		return db.updateWhole(objects, beanKeys, properties);
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
