package com.github.ulwx.tool;

import com.github.ulwx.database.*;
import com.github.ulwx.database.sql.Result;
import com.github.ulwx.database.sql.ResultSupport;
import com.github.ulwx.database.sql.SqlUtils;
import com.github.ulwx.tool.support.ObjectUtils;
import com.github.ulwx.tool.support.StringUtils;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MDbUtils extends BaseDao {

	public static int callStoredPro(String pollName, String mdFullMethodName, Map<String, Object> parms,
			Map<String, Object> outPramsValues, List<DataBaseSet> returnDataBaseSets) throws Exception {
		return MDbUtils.executeStoredProcedure(pollName, mdFullMethodName, parms, outPramsValues, returnDataBaseSets);
	}

	public static int del(String pollName, String mdFullMethodName, Map<String, Object> vParameters) throws Exception {
		return MDbUtils.executeBindDelete(pollName, mdFullMethodName, vParameters);
	}

	public static DataBaseSet doCachedPageQuery(String pollName, String mdFullMethodName,
			Map<String, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSqlMdFullMethodName)
			throws Exception {

		return MDbUtils.doCachedPageQuery(pollName, mdFullMethodName, vParameters, page, perPage, pageUtils, countSqlMdFullMethodName,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static DataBaseSet doCachedPageQuery(String pollName, String mdFullMethodName,
			Map<String, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSqlMdFullMethodName,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doCachedPageQuery(mdFullMethodName, vParameters, page, perPage, pageUtils, countSqlMdFullMethodName);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static DataBaseSet doCachedQuery(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {

		return MDbUtils.doCachedQuery(pollName, mdFullMethodName, vParameters,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static DataBaseSet doCachedQuery(String pollName, String mdFullMethodName, Map<String, Object> vParameters,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doCachedQuery(mdFullMethodName, vParameters);
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
	 * @param mdFullMethodName
	 *            sql查询语句
	 * @param vParameters
	 *            放置参数的map,无参数则传null
	 * @param page
	 *            第几页
	 * @param perPage
	 *            每一页的大小
	 * @param pageBean
	 *            分页相关信息的javaBean,保存了分页相关的总页数等信息
	 * @param countSqlMdFullMethodName
	 *            查询总页数的sql,为空则为自动生成count(*)语句
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> doPageQueryClass(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName)
			throws Exception {

		return MDbUtils.doPageQueryClass(pollName, clazz, mdFullMethodName, vParameters, page, perPage, pageBean,
				countSqlMdFullMethodName, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> List<T> doPageQueryClass(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSqlMdFullMethodName,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doPageQueryClass(clazz, mdFullMethodName, vParameters, page, perPage, pageUtils, countSqlMdFullMethodName);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> List<T> doPageQueryObject(String pollName, String mdFullMethodName, Map<String, Object> args,
                                                int page, int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws Exception {

		return MDbUtils.doPageQueryObject(pollName, mdFullMethodName, args, page, perPage, pageUtils, rowMapper,
				countSqlMdFullMethodName, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> List<T> doPageQueryObject(String pollName, String mdFullMethodName, Map<String, Object> args,
			int page, int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSqlMdFullMethodName,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doPageQueryObject(mdFullMethodName, args, page, perPage, pageUtils, rowMapper, countSqlMdFullMethodName);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> List<T> doQueryClass(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters) throws Exception {

		return MDbUtils.doQueryClass(pollName, clazz, mdFullMethodName, vParameters,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> List<T> doQueryClass(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClass(clazz, mdFullMethodName, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> List<T> doQueryClassNoSql(String pollName, Class<T> clazz, T selectObject,
			String selectProperties) throws Exception {

		return MDbUtils.doQueryClassNoSql(pollName, clazz, selectObject, selectProperties,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> List<T> doQueryClassNoSql(String pollName, Class<T> clazz, T selectObject,
			String selectProperties, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassNoSql(clazz, selectObject, selectProperties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> List<T> doQueryClassNoSql(String pollName, T selectObject) throws Exception {

		return MDbUtils.doQueryClassNoSql(pollName, selectObject, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> List<T> doQueryClassNoSql(String pollName, T selectObject,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassNoSql(selectObject);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> T doQueryClassOne(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters) throws Exception {

		return MDbUtils.doQueryClassOne(pollName, clazz, mdFullMethodName, vParameters,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> T doQueryClassOne(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		List<T> list = MDbUtils.doQueryClass(pollName, clazz, mdFullMethodName, vParameters, mainSlaveModeConnectMode);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public static <T> List<T> doQueryClassOne2Many(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String mdFullMethodName, Map<String, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList)
			throws Exception {
		return MDbUtils.doQueryClassOne2Many(pollName, clazz, sqlPrefix, beanKey, mdFullMethodName, vParameters,
				queryMapNestList, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> List<T> doQueryClassOne2Many(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String mdFullMethodName, Map<String, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassOne2Many(clazz, sqlPrefix, beanKey, mdFullMethodName, vParameters, queryMapNestList);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static <T> List<T> doQueryClassOne2One(String pollName, Class<T> clazz, String sqlPrefix,
			String mdFullMethodName, Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList)
			throws Exception {
		return MDbUtils.doQueryClassOne2One(pollName, clazz, sqlPrefix, mdFullMethodName, vParameters, queryMapNestList,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
	}

	public static <T> List<T> doQueryClassOne2One(String pollName, Class<T> clazz, String sqlPrefix,
			String mdFullMethodName, Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryClassOne2One(clazz, sqlPrefix, mdFullMethodName, vParameters, queryMapNestList);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static List<Map<String, Object>> doQueryMap(String pollName, String mdFullMethodName,
			Map<String, Object> args) throws Exception {

		List<Map<String, Object>> list = MDbUtils.doQueryMap(pollName, mdFullMethodName, args,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
		return list;

	}

	public static List<Map<String, Object>> doQueryMap(String pollName, String mdFullMethodName,
			Map<String, Object> args, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {

		List<Map<String, Object>> list = MDbUtils.doQueryObject(pollName, mdFullMethodName, args,
				new RowMapper<Map<String, Object>>() {

					@Override
					public Map<String, Object> mapRow(DataBaseSet rs) throws Exception {
						// TODO Auto-generated method stub
						return ObjectUtils.getMapFromResultSet(rs.getResultSet());

					}
				}, mainSlaveModeConnectMode);
		return list;
	}

	public static <T> List<T> doQueryObject(String pollName, String mdFullMethodName, Map<String, Object> args,
			RowMapper<T> rowMapper) throws Exception {

		return MDbUtils.doQueryObject(pollName, mdFullMethodName, args, rowMapper,
				DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);

	}

	public static <T> List<T> doQueryObject(String pollName, String mdFullMethodName, Map<String, Object> args,
			RowMapper<T> rowMapper, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doQueryObject(mdFullMethodName, args, rowMapper);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	

	public static <T> int excuteDeleteClass(String pollName, T deleteObject, String deleteProperteis) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteDeleteObjects(deleteObjects, deletePropertiesArray);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteInsertClass(String pollName, T insertObject) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteInsertClass(insertObject);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static <T> int excuteInsertClass(String pollName, T insertObject, String[] properties) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}

			return db.excuteInsertClass(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertClass(String pollName, T[] objs) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteInsertClass(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> long excuteInsertClassReturnKey(String pollName, T insertObject) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}

			return db.excuteInsertClassReturnKey(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertObjects(String pollName, Object[] insertObjects) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteInsertObjects(insertObjects);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static int[] excuteInsertObjects(String pollName, Object[] insertObjects, String[][] properties)
			throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteInsertObjects(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteInsertWholeClass(String pollName, T insertObject) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}

			return db.excuteInsertWholeClass(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertWholeClass(String pollName, T[] objs) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteInsertWholeClass(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> long excuteInsertWholeClassReturnKey(String pollName, T insertObject) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}

			return db.excuteInsertWholeClassReturnKey(insertObject, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteInsertWholeObjects(String pollName, Object[] insertObjects) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteInsertWholeObjects(insertObjects);
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static int[] excuteInsertWholeObjects(String pollName, Object[] insertObjects, String[][] properties)
			throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteInsertWholeObjects(insertObjects, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteUpdateClass(String pollName, T updateObject, String beanKey) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteUpdateClass(updateObject, beanKey, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteUpdateClass(String pollName, T[] updateObject, String beanKey) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteUpdateObjects(objects, beanKeys, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int excuteUpdateWholeClass(String pollName, T updateObject, String beanKey) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteUpdateWholeClass(updateObject, beanKey, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static <T> int[] excuteUpdateWholeClass(String pollName, T[] updateObject, String beanKey) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
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
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.excuteUpdateWholeObjects(objects, beanKeys, properties);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] executeBatch(String pollName, ArrayList<String> mdFullMethodNames) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeBatch(mdFullMethodNames);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] executeBindBatch(String pollName, String mdFullMethodName,
			List<Map<String, Object>> vParametersList) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeBindBatch(mdFullMethodName, vParametersList);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int[] executeBindBatch(String pollName, String[] mdFullMethodName,
			Map<String, Object>[] vParametersArray) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeBindBatch(mdFullMethodName, vParametersArray);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeBindDelete(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeBindDelete(mdFullMethodName, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeBindInsert(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeBindInsert(mdFullMethodName, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static long executeBindInsertReturnKey(String pollName, String mdFullMethodName,
			Map<String, Object> vParameters) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeBindInsertReturnKey(mdFullMethodName, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeBindUpdate(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeBindUpdate(mdFullMethodName, vParameters);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static int executeStoredProcedure(String pollName, String mdFullMethodName, Map<String, Object> parms,
			Map<String, Object> outPramsValues, List<DataBaseSet> returnDataBaseSets) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.executeStoredProcedure(mdFullMethodName, parms, outPramsValues, returnDataBaseSets);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	/**
	 * @param pollName :连接池名称
	 * @param mdFullMethodName：定位md文件里的方法，格式为：{@code com.github.ulwx.database.test.SysRightDao.md:getDataCount} ,
	 *   表示定位到com/ulwx/database/test/SysRightDao.md文件里的{@code codegetDataCount}方法
	 * @param parms :参数，在md文件中，只能用${xx},不能用#{xx}
	 * @return 执行成功的结果 ，否则抛出异常
	 * @throws Exception
	 */
	public static String exeScript(String pollName, String mdFullMethodName, Map<String, Object> parms) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.exeScript(mdFullMethodName, parms);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	/**
	 * @param pollName :连接池名称
	 * @param packageFullName :sql脚本所在都包，例如com.xx.yy
	 * @param sqlFileName ：sql脚本的文件名，例如 db.sql
	 * @return 执行成功的结果 ，否则抛出异常
	 * @throws Exception
	 */
	public static String exeScript(String pollName,String packageFullName, String sqlFileName) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.exeScript(packageFullName, sqlFileName);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	/**
	 * @param pollName :连接池名称
	 * @param packageFullName :sql脚本所在都包，例如com.xx.yy
	 * @param sqlFileName ：sql脚本的文件名，例如 db.sql
	 * @param logWriter ：日志的输出
	 * @return 执行成功的结果 ，否则抛出异常
	 * @throws Exception
	 */
	public static String exeScript(String pollName,String packageFullName, String sqlFileName,PrintWriter logWriter) throws Exception {
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase();
			} else {
				db = MDbManager.getDataBase(pollName);
			}
			return db.exeScript(packageFullName, sqlFileName, logWriter);
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

	public static int[] insert(String pollName, ArrayList<String> mdFullMethodNames) throws Exception {
		return MDbUtils.executeBatch(pollName, mdFullMethodNames);
	}

	public static int[] insert(String pollName, String mdFullMethodName, List<Map<String, Object>> vParametersList)
			throws Exception {
		return MDbUtils.executeBindBatch(pollName, mdFullMethodName, vParametersList);
	}

	public static int insert(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {
		return MDbUtils.executeBindInsert(pollName, mdFullMethodName, vParameters);
	}

	public static long insertReturnKey(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {
		return MDbUtils.executeBindInsertReturnKey(pollName, mdFullMethodName, vParameters);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("xxxxx");

	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters) throws Exception {
		return MDbUtils.doQueryClass(pollName, clazz, mdFullMethodName, vParameters);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName)
			throws Exception {
		return MDbUtils.doPageQueryClass(pollName, clazz, mdFullMethodName, vParameters, page, perPage, pageBean,
				countSqlMdFullMethodName);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, int page, int perPage, PageBean pageUtils, String countSqlMdFullMethodName,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doPageQueryClass(pollName, clazz, mdFullMethodName, vParameters, page, perPage, pageUtils,
				countSqlMdFullMethodName, mainSlaveModeConnectMode);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doQueryClass(pollName, clazz, mdFullMethodName, vParameters, mainSlaveModeConnectMode);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String mdFullMethodName, Map<String, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList)
			throws Exception {
		return MDbUtils.doQueryClassOne2Many(pollName, clazz, sqlPrefix, beanKey, mdFullMethodName, vParameters,
				queryMapNestList);

	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, String beanKey,
			String mdFullMethodName, Map<String, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doQueryClassOne2Many(pollName, clazz, sqlPrefix, beanKey, mdFullMethodName, vParameters,
				queryMapNestList, mainSlaveModeConnectMode);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList) throws Exception {
		return MDbUtils.doQueryClassOne2One(pollName, clazz, sqlPrefix, mdFullMethodName, vParameters,
				queryMapNestList);
	}

	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doQueryClassOne2One(pollName, clazz, sqlPrefix, mdFullMethodName, vParameters, queryMapNestList,
				mainSlaveModeConnectMode);
	}
	public static <T> List<T> query(String pollName,Class<T> clazz, String sqlPrefix, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws Exception{
		//MainSlaveModeConnectMode.Connect_SlaveServer
		
		return MDbUtils.query(pollName, clazz, sqlPrefix, mdFullMethodName,
				vParameters, queryMapNestList, page, perPage, pageUtils, 
				countSqlMdFullMethodName, DataBase.MainSlaveModeConnectMode.Connect_SlaveServer);
		
	}
	public static <T> List<T> query(String pollName, Class<T> clazz, String sqlPrefix, String mdFullMethodName,
                                    Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                                    PageBean pageUtils, String countSqlMdFullMethodName, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception{
		MDataBase db = null;
		try {
			if (StringUtils.isEmpty(pollName)) {
				db = MDbManager.getDataBase(mainSlaveModeConnectMode);
			} else {
				db = MDbManager.getDataBase(pollName, mainSlaveModeConnectMode);
			}
			return db.doPageQueryClassOne2One(clazz, sqlPrefix, mdFullMethodName, 
					vParameters, queryMapNestList, page, perPage, pageUtils, countSqlMdFullMethodName);
		} finally {
			if (db != null) {
				db.close();
			}
		} 
	}
	
	public static <T> List<T> doQueryClassOne2One(String pollName, Class<T> clazz, String sqlPrefix, String mdFullMethodName,
                                                  Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                                                  PageBean pageUtils, String countSqlMdFullMethodName, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception{
		
		return MDbUtils.query(pollName, clazz, sqlPrefix, mdFullMethodName, vParameters, queryMapNestList, page,
				perPage, pageUtils, countSqlMdFullMethodName,mainSlaveModeConnectMode);
	}

	public static <T> List<T> doQueryClassOne2One(String pollName,Class<T> clazz, String sqlPrefix, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws Exception{
		
		return MDbUtils.query(pollName, clazz, sqlPrefix, mdFullMethodName, vParameters, queryMapNestList, page,
				perPage, pageUtils, countSqlMdFullMethodName);
	}
	
	public static DataBaseSet query(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {
		return MDbUtils.doCachedQuery(pollName, mdFullMethodName, vParameters);
	}

	public static <T> List<T> query(String pollName, String mdFullMethodName, Map<String, Object> args, int page,
			int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws Exception {
		return MDbUtils.doPageQueryObject(pollName, mdFullMethodName, args, page, perPage, pageUtils, rowMapper,
				countSqlMdFullMethodName);
	}

	public static <T> List<T> query(String pollName, String mdFullMethodName, Map<String, Object> args, int page,
			int perPage, PageBean pageUtils, RowMapper<T> rowMapper, String countSqlMdFullMethodName,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doPageQueryObject(pollName, mdFullMethodName, args, page, perPage, pageUtils, rowMapper,
				countSqlMdFullMethodName, mainSlaveModeConnectMode);

	}

	public static DataBaseSet query(String pollName, String mdFullMethodName, Map<String, Object> vParameters, int page,
			int perPage, PageBean pageUtils, String countSqlMdFullMethodName) throws Exception {
		return MDbUtils.doCachedPageQuery(pollName, mdFullMethodName, vParameters, page, perPage, pageUtils, countSqlMdFullMethodName);
	}

	public static DataBaseSet query(String pollName, String mdFullMethodName, Map<String, Object> vParameters, int page,
			int perPage, PageBean pageUtils, String countSqlMdFullMethodName, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode)
			throws Exception {
		return MDbUtils.doCachedPageQuery(pollName, mdFullMethodName, vParameters, page, perPage, pageUtils, countSqlMdFullMethodName,
				mainSlaveModeConnectMode);
	}

	public static DataBaseSet query(String pollName, String mdFullMethodName, Map<String, Object> vParameters,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doCachedQuery(pollName, mdFullMethodName, vParameters, mainSlaveModeConnectMode);
	}

	public static <T> List<T> query(String pollName, String mdFullMethodName, Map<String, Object> args,
			RowMapper<T> rowMapper) throws Exception {
		return MDbUtils.doQueryObject(pollName, mdFullMethodName, args, rowMapper);
	}

	public static <T> List<T> query(String pollName, String mdFullMethodName, Map<String, Object> args,
			RowMapper<T> rowMapper, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doQueryObject(pollName, mdFullMethodName, args, rowMapper, mainSlaveModeConnectMode);
	}

	public static List<Map<String, Object>> queryMap(String pollName, String mdFullMethodName, Map<String, Object> args)
			throws Exception {
		return MDbUtils.doQueryMap(pollName, mdFullMethodName, args);
	}

	public static List<Map<String, Object>> queryMap(String pollName, String mdFullMethodName, Map<String, Object> args,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doQueryMap(pollName, mdFullMethodName, args, mainSlaveModeConnectMode);
	}

	public static <T> T queryOne(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters) throws Exception {
		return MDbUtils.doQueryClassOne(pollName, clazz, mdFullMethodName, vParameters);
	}

	public static <T> T queryOne(String pollName, Class<T> clazz, String mdFullMethodName,
			Map<String, Object> vParameters, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {
		return MDbUtils.doQueryClassOne(pollName, clazz, mdFullMethodName, vParameters, mainSlaveModeConnectMode);
	}

	public static Map<String, Object>[] toMapArray(DataBaseSet dbs) {

		Result result = ResultSupport.toResult(dbs.getRowSet());
		return result.getRows();

	}

	public static int[] update(String pollName, ArrayList<String> mdFullMethodNames) throws Exception {
		return MDbUtils.executeBatch(pollName, mdFullMethodNames);
	}

	public static int[] update(String pollName, String mdFullMethodName, List<Map<String, Object>> vParametersList)
			throws Exception {
		return MDbUtils.executeBindBatch(pollName, mdFullMethodName, vParametersList);
	}

	public static int update(String pollName, String mdFullMethodName, Map<String, Object> vParameters)
			throws Exception {
		return MDbUtils.executeBindUpdate(pollName, mdFullMethodName, vParameters);
	}

	public static int[] update(String pollName, String[] mdFullMethodName, Map<String, Object>[] vParametersArray)
			throws Exception {
		return MDbUtils.executeBindBatch(pollName, mdFullMethodName, vParametersArray);
	}

}
