package com.github.ulwx.tool;

import com.github.ulwx.database.*;
import com.github.ulwx.database.sql.Result;
import com.github.ulwx.database.sql.ResultSupport;
import com.github.ulwx.database.sql.SqlUtils;
import com.github.ulwx.tool.support.ObjectUtils;
import com.github.ulwx.tool.support.StringUtils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbUtils extends BaseDao {

	public static int callStoredPro(String pollName, String sqltext, Map<String, Object> parms,
			Map<Integer, Object> outPramsValues, List<DataBaseSet> returnDataBaseSets) throws Exception {
		return DbUtils.executeStoredProcedure(pollName, sqltext, parms, outPramsValues, returnDataBaseSets);
	}

	public static int del(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		return DbUtils.executeBindDelete(pollName, sqltext, vParameters);
	}

	public static DataBaseSet doCachedPageQuery(String pollName, String sqlQuery, Map<Integer, Object> vParameters,
			int page, int perPage, PageBean pageUtils, String countSql) throws Exception {

		return DbUtils.doCachedPageQuery(pollName, sqlQuery, vParameters, page, perPage, pageUtils, countSql,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	
	public static DataBaseSet doCachedPageQuery(String pollName, String sqlQuery, Map<Integer, Object> vParameters,
			int page, int perPage, PageBean pageUtils, String countSql,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doCachedPageQuery(sqlQuery, vParameters, page, perPage, pageUtils, countSql);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}
	


	public static DataBaseSet doCachedQuery(String pollName, String sqlQuery, Map<Integer, Object> vParameters)
			throws Exception {

		return DbUtils.doCachedQuery(pollName, sqlQuery, vParameters, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static DataBaseSet doCachedQuery(String pollName, String sqlQuery, Map<Integer, Object> vParameters,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doCachedQuery(sqlQuery, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}
	/**
	 * @param pollName
	 *            数据库连接池的名字
	 * @param clazz
	 *            pojo类的的class对象
	 * @param sqlQuery
	 *            sql查询语句
	 * @param vParameters
	 *            放置参数的map,无参数则传null
	 * @param page
	 *            第几页，从第一页开始
	 * @param perPage
	 *            每一页的大小
	 * @param pageBean
	 *            分页相关信息的javaBean,保存了分页相关的总页数等信息
	 * @param countSql
	 *            查询总页数的sql,为空则为自动生成count(*)语句
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> doPageQueryClass(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, int page, int perPage, PageBean pageBean, String countSql)
			throws Exception {

		return DbUtils.doPageQueryClass(pollName, clazz, sqlQuery, vParameters, page, perPage, pageBean, countSql,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	
	/**
	 * 
	 * @param pollName
	 * @param clazz
	 * @param sqlQuery
	 * @param vParameters
	 * @param page  页码，从第一页开始
	 * @param perPage  每页多少条
	 * @param pageUtils
	 * @param countSql
	 * @param mainSlaveModeConnectMode
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> doPageQueryClass(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSql,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doPageQueryClass(clazz, sqlQuery, vParameters, page, perPage, pageUtils, countSql);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * 
	 * @param pollName
	 * @param selectObject
	 * @param page 页码，从第一页开始
	 * @param perPage ，每页多少条
	 * @param pb
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> doPageQueryClassNoSql(String pollName, T selectObject, int page, int perPage, PageBean pb)
			throws Exception {

		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
			} else {
				db = DataBaseFactory.getDataBase(pollName, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
			}
			return db.query(selectObject, page, perPage, pb);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}
	
	public static <T> List<T> doPageQueryClassNoSql(String pollName, T selectObject, String selectProperties, int page,
			int perPage, PageBean pb) throws Exception {

		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
			} else {
				db = DataBaseFactory.getDataBase(pollName, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
			}
			return db.query(selectObject, selectProperties, page, perPage, pb);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static <T> List<T> doPageQueryObject(String pollName, String sqlQuery, Map<Integer, Object> args, int page,
                                                int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSql) throws Exception {

		return DbUtils.doPageQueryObject(pollName, sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	
	public static <T> List<T> doPageQueryObject(String pollName, String sqlQuery, Map<Integer, Object> args, int page,
			int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSql,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doPageQueryObject(sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> List<T> doQueryClass(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters) throws Exception {

		return DbUtils.doQueryClass(pollName, clazz, sqlQuery, vParameters,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	
	public static <T> List<T> doQueryClass(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClass(clazz, sqlQuery, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> List<T> doQueryClassNoSql(String pollName, Class<T> clazz, T selectObject,
			String selectProperties) throws Exception {

		return DbUtils.doQueryClassNoSql(pollName, clazz, selectObject, selectProperties,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	public static <T> List<T> doQueryClassNoSql(String pollName, Class<T> clazz, T selectObject,
			String selectProperties, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassNoSql(clazz, selectObject, selectProperties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> List<T> doQueryClassNoSql(String pollName, T selectObject) throws Exception {

		return DbUtils.doQueryClassNoSql(pollName, selectObject, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	
	public static <T> List<T> doQueryClassNoSql(String pollName, T selectObject,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassNoSql(selectObject);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> T doQueryClassOne(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters) throws Exception {

		List<T> list = DbUtils.doQueryClass(pollName, clazz, sqlQuery, vParameters,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;

	}
	
	public static <T> T doQueryClassOne(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		List<T> list = DbUtils.doQueryClass(pollName, clazz, sqlQuery, vParameters, mainSlaveModeConnectMode);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public static <T> List<T> doQueryClassOne2Many(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws Exception {
		return DbUtils.doQueryClassOne2Many(pollName, clazz, sqlPrefix, beanKey, sql, vParameters, queryMapNestList,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	
	public static <T> List<T> doQueryClassOne2Many(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassOne2Many(clazz, sqlPrefix, beanKey, sql, vParameters, queryMapNestList);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static <T> List<T> doQueryClassOne2One(String pollName, Class<T> clazz, String sqlPrefix, 
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList)
			throws Exception {
		return DbUtils.doQueryClassOne2One(pollName, clazz, sqlPrefix,  sql, vParameters,
				queryMapNestList, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
	}
	
	public static <T> List<T> doQueryClassOne2One(String pollName, Class<T> clazz, String sqlPrefix, 
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassOne2One(clazz, sqlPrefix, sql, vParameters, queryMapNestList);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static List<Map<String, Object>> doQueryMap(String pollName, String sqlQuery, Map<Integer, Object> args)
			throws Exception {

		List<Map<String, Object>> list = DbUtils.doQueryMap(pollName, sqlQuery, args,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
		return list;

	}
	
	public static List<Map<String, Object>> doQueryMap(String pollName, String sqlQuery, Map<Integer, Object> args,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {

		List<Map<String, Object>> list = DbUtils.doQueryObject(pollName, sqlQuery, args,
				new RowMapper<Map<String, Object>>() {

					@Override
					public Map<String, Object> mapRow(DataBaseSet rs) throws Exception {
						// TODO Auto-generated method stub
						return ObjectUtils.getMapFromResultSet(rs.getResultSet());

					}
				}, mainSlaveModeConnectMode);
		return list;
	}

	public static <T> List<T> doQueryObject(String pollName, String sqlQuery, Map<Integer, Object> args,
			RowMapper<T> rowMapper) throws Exception {

		return DbUtils.doQueryObject(pollName, sqlQuery, args, rowMapper, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}
	
	public static <T> List<T> doQueryObject(String pollName, String sqlQuery, Map<Integer, Object> args,
			RowMapper<T> rowMapper, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = DataBaseFactory.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryObject(sqlQuery, args, rowMapper);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}


	
	public static <T> int excuteDeleteClass(String pollName, T deleteObject, String deleteProperteis) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteDeleteClass(deleteObject, deleteProperteis);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteDeleteClass(String pollName, T[] deleteObject, String deleteProperteis)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteDeleteClass(deleteObject, deleteProperteis);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static int[] excuteDeleteObjects(String pollName, Object[] deleteObjects, String[] deletePropertiesArray)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteDeleteObjects(deleteObjects, deletePropertiesArray);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteInsertClass(String pollName, T insertObject) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertClass(insertObject);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}
	
	public static <T> int excuteInsertClass(String pollName, T insertObject, String[] properties) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}

			return db.excuteInsertClass(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertClass(String pollName, T[] objs) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertClass(objs);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static <T> int[] excuteInsertClass(String pollName, T[] insertObjects, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertClass(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> long excuteInsertClassReturnKey(String pollName, T insertObject) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertClassReturnKey(insertObject);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}
	
	public static <T> long excuteInsertClassReturnKey(String pollName, T insertObject, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}

			return db.excuteInsertClassReturnKey(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertObjects(String pollName, Object[] insertObjects) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertObjects(insertObjects);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}
	
	public static int[] excuteInsertObjects(String pollName, Object[] insertObjects, String[][] properties)
			throws DbException {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertObjects(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteInsertWholeClass(String pollName, T insertObject) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertWholeClass(insertObject);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}
	

	public static <T> int excuteInsertWholeClass(String pollName, T insertObject, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}

			return db.excuteInsertWholeClass(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertWholeClass(String pollName, T[] objs) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertWholeClass(objs);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertWholeClass(String pollName, T[] insertObjects, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertWholeClass(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> long excuteInsertWholeClassReturnKey(String pollName, T insertObject) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertWholeClassReturnKey(insertObject);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static <T> long excuteInsertWholeClassReturnKey(String pollName, T insertObject, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}

			return db.excuteInsertWholeClassReturnKey(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertWholeObjects(String pollName, Object[] insertObjects) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertWholeObjects(insertObjects);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static int[] excuteInsertWholeObjects(String pollName, Object[] insertObjects, String[][] properties)
			throws DbException {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteInsertWholeObjects(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteUpdateClass(String pollName, T updateObject, String beanKey) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateClass(updateObject, beanKey);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteUpdateClass(String pollName, T updateObject, String beanKey, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateClass(updateObject, beanKey, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteUpdateClass(String pollName, T[] updateObject, String beanKey) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateClass(updateObject, beanKey);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteUpdateClass(String pollName, T[] objects, String beanKey, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateClass(objects, beanKey, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样,忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param objects
	 *            待更新到数据库的多个对象
	 * @param beanKeys
	 *            每个对象分别对应主键属性名
	 * @return
	 * @throws DbException
	 */
	public static int[] excuteUpdateObjects(String pollName, Object[] objects, String[] beanKeys) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateObjects(objects, beanKeys);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param objects
	 *            待更新到数据库的多个对象
	 * @param beanKeys
	 *            每个对象分别对应主键属性名
	 * @param properties
	 *            每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
	 *            如果指定的某个属性在对应对象里值为null，则忽略。
	 * @return
	 * @throws DbException
	 */
	public static int[] excuteUpdateObjects(String pollName, Object[] objects, String[] beanKeys, String[][] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateObjects(objects, beanKeys, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteUpdateWholeClass(String pollName, T updateObject, String beanKey) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateWholeClass(updateObject, beanKey);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteUpdateWholeClass(String pollName, T updateObject, String beanKey, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateWholeClass(updateObject, beanKey, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteUpdateWholeClass(String pollName, T[] updateObject, String beanKey) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateWholeClass(updateObject, beanKey);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteUpdateWholeClass(String pollName, T[] objects, String beanKey, String[] properties)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateWholeClass(objects, beanKey, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] excuteUpdateWholeObjects(String pollName, Object[] objects, String[] beanKeys)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateWholeObjects(objects, beanKeys);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] excuteUpdateWholeObjects(String pollName, Object[] objects, String[] beanKeys,
			String[][] properties) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.excuteUpdateWholeObjects(objects, beanKeys, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] executeBatch(String pollName, ArrayList<String> sqltxt) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeBatch(sqltxt);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] executeBindBatch(String pollName, String sqltxt, List<Map<Integer, Object>> vParametersList)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeBindBatch(sqltxt, vParametersList);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] executeBindBatch(String pollName, String[] sqltxts, Map<Integer, Object>[] vParametersArray)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeBindBatch(sqltxts, vParametersArray);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeBindDelete(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeBindDelete(sqltext, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeBindInsert(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeBindInsert(sqltext, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static long executeBindInsertReturnKey(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeBindInsertReturnKey(sqltext, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeBindUpdate(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeBindUpdate(sqltext, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeStoredProcedure(String pollName, String sqltext, Map<String, Object> parms,
			Map<Integer, Object> outPramsValues, List<DataBaseSet> returnDataBaseSets) throws Exception {
		DataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = DataBaseFactory.getDataBase();
			} else {
				db = DataBaseFactory.getDataBase(pollName);
			}
			return db.executeStoredProcedure(sqltext, parms, outPramsValues, returnDataBaseSets);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * 把rs的一行转换成一个javabean
	 * 
	 * @param <T>
	 * @param clazz
	 * @param rs
	 * @return
	 */
	public static <T> T getBeanFromResultSet(String pollName,Class<T> clazz, ResultSet rs) {

		return SqlUtils.getBeanFromResultSet(pollName,clazz, rs);

	}

	public static int[] insert(String pollName, ArrayList<String> sqltxt) throws Exception {
		return DbUtils.executeBatch(pollName, sqltxt);
	}

	public static int[] insert(String pollName, String sqltxt, List<Map<Integer, Object>> vParametersList)
			throws Exception {
		return DbUtils.executeBindBatch(pollName, sqltxt, vParametersList);
	}

	public static int insert(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		return DbUtils.executeBindInsert(pollName, sqltext, vParameters);
	}

	public static int[] insert(String pollName, String[] sqltxts, Map<Integer, Object>[] vParametersArray)
			throws Exception {
		return DbUtils.executeBindBatch(pollName, sqltxts, vParametersArray);
	}

	public static long insertReturnKey(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		return DbUtils.executeBindInsertReturnKey(pollName, sqltext, vParameters);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("xxxxx");

	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters) throws Exception {
		
		return DbUtils.doQueryClass(pollName, clazz, sqlQuery, vParameters);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, int page, int perPage, PageBean pageBean, String countSql)
			throws Exception {
		return DbUtils.doPageQueryClass(pollName, clazz, sqlQuery, vParameters, page, perPage, pageBean, countSql);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSql,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doPageQueryClass(pollName, clazz, sqlQuery, vParameters, page, perPage, pageUtils, countSql, mainSlaveModeConnectMode);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doQueryClass(pollName, clazz, sqlQuery, vParameters, mainSlaveModeConnectMode);
	}
	
	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws Exception {
		return DbUtils.doQueryClassOne2Many(pollName, clazz, sqlPrefix, beanKey, sql, vParameters, queryMapNestList);
	}
	
	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		
		return DbUtils.doQueryClassOne2Many(pollName, clazz, sqlPrefix, beanKey, sql, vParameters, queryMapNestList, mainSlaveModeConnectMode);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, 
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList)
			throws Exception {
		return  DbUtils.doQueryClassOne2One(pollName, clazz, sqlPrefix,  sql, vParameters, queryMapNestList);
	}
	
	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, 
			String sql, Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		
		return DbUtils.doQueryClassOne2One(pollName, clazz, sqlPrefix,  sql, vParameters, queryMapNestList, mainSlaveModeConnectMode);
	}
	
	public static DataBaseSet query(String pollName, String sqlQuery, Map<Integer, Object> vParameters)
			throws Exception {
		return DbUtils.doCachedQuery(pollName, sqlQuery, vParameters);
	}

	public static <T> List<T> query(String pollName, String sqlQuery, Map<Integer, Object> args, int page,
			int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSql) throws Exception {
		
		return DbUtils.doPageQueryObject(pollName, sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql);
	}
	
	public static <T> List<T> query(String pollName, String sqlQuery, Map<Integer, Object> args, int page,
			int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSql,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doPageQueryObject(pollName, sqlQuery, args, page, perPage, pageUtils, rowMapper, countSql, mainSlaveModeConnectMode);
	}
	
	public static DataBaseSet query(String pollName, String sqlQuery, Map<Integer, Object> vParameters,
			int page, int perPage, PageBean pageUtils, String countSql) throws Exception {
		return DbUtils.doCachedPageQuery(pollName, sqlQuery, vParameters, page, perPage, pageUtils, countSql);
	}

	public static DataBaseSet query(String pollName, String sqlQuery, Map<Integer, Object> vParameters,
			int page, int perPage, PageBean pageUtils, String countSql,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doCachedPageQuery(pollName, sqlQuery, vParameters, page, perPage, pageUtils, countSql,mainSlaveModeConnectMode);
	}
	
	public static DataBaseSet query(String pollName, String sqlQuery, Map<Integer, Object> vParameters,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doCachedQuery(pollName, sqlQuery, vParameters, mainSlaveModeConnectMode);
	}

	public static <T> List<T> query(String pollName, String sqlQuery, Map<Integer, Object> args,
			RowMapper<T> rowMapper) throws Exception {
		return DbUtils.doQueryObject(pollName, sqlQuery, args, rowMapper);
		
	}
	public static <T> List<T> query(String pollName, String sqlQuery, Map<Integer, Object> args,
			RowMapper<T> rowMapper, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doQueryObject(pollName, sqlQuery, args, rowMapper,mainSlaveModeConnectMode);
	}

	public static List<Map<String, Object>> queryMap(String pollName, String sqlQuery, Map<Integer, Object> args)
			throws Exception {
		
		return DbUtils.doQueryMap(pollName, sqlQuery, args);
	}
	
	public static List<Map<String, Object>> queryMap(String pollName, String sqlQuery, Map<Integer, Object> args,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doQueryMap(pollName, sqlQuery, args, mainSlaveModeConnectMode);
	}

	public static <T> T queryOne(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters) throws Exception {
		return DbUtils.doQueryClassOne(pollName, clazz, sqlQuery, vParameters);
	}
	
	public static <T> T queryOne(String pollName, Class<T> clazz, String sqlQuery,
			Map<Integer, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return DbUtils.doQueryClassOne(pollName, clazz, sqlQuery, vParameters, mainSlaveModeConnectMode);
	}

	public static Map<String, Object>[] toMapArray(DataBaseSet dbs) {

		Result result = ResultSupport.toResult(dbs.getRowSet());
		return result.getRows();

	}
	
	public static int[] update(String pollName, ArrayList<String> sqltxt) throws Exception {
		return DbUtils.executeBatch(pollName, sqltxt);
	}

	public static int[] update(String pollName, String sqltxt, List<Map<Integer, Object>> vParametersList)
			throws Exception {
		return DbUtils.executeBindBatch(pollName, sqltxt, vParametersList);
	}

	public static int update(String pollName, String sqltext, Map<Integer, Object> vParameters)
			throws Exception {
		return DbUtils.executeBindUpdate(pollName, sqltext, vParameters);
	}

	public static int[] update(String pollName, String[] sqltxts, Map<Integer, Object>[] vParametersArray)
			throws Exception {
		return DbUtils.executeBindBatch(pollName, sqltxts, vParametersArray);
	}

}
