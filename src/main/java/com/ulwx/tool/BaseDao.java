package com.ulwx.tool;

import com.ulwx.database.DataBase;

import java.util.List;

public class BaseDao {


	public  static  <T>  int[] del(String pollName, Object[] deleteObjects,
			String[] deletePropertiesArray) throws Exception {

		return DbUtils.excuteDeleteObjects(pollName, deleteObjects,
				deletePropertiesArray);

	}

	public  static  <T>  int del(String pollName, T deleteObject, String deleteProperteis)
			throws Exception {
		return DbUtils.excuteDeleteClass(pollName, deleteObject,
				deleteProperteis);
	}
	public  static  <T>  int[] del(String pollName, T[] deleteObjects, String deleteProperteis)
			throws Exception {
		return DbUtils.excuteDeleteClass(pollName, deleteObjects,
				deleteProperteis);

	}


	public static <T> int insert(String pollName, T insertObject) throws Exception {

		return DbUtils.excuteInsertClass(pollName, insertObject);

	}
	public static  <T> int   insert(String pollName, T insertObject, String[] properties)
			throws Exception {

		return DbUtils.excuteInsertClass(pollName, insertObject, properties);

	}
	public  static  <T>  int[] insert(String pollName, T[] objs) throws Exception {

		return DbUtils.excuteInsertClass(pollName, objs);

	}
	public  static  <T>  int[] insert(String pollName, T[] objs, String[] properties)
			throws Exception {

		return DbUtils.excuteInsertClass(pollName, objs, properties);

	}

	public static  <T>  long insertReturnKey(String pollName, T insertObject) throws Exception {

		return DbUtils.excuteInsertClassReturnKey(pollName, insertObject);

	}
	public  static  <T>   long insertReturnKey(String pollName, T insertObject, String[] properties)
			throws Exception {

		return DbUtils.excuteInsertClassReturnKey(pollName, insertObject, properties);

	}
	

	public static <T> int insertWhole(String pollName, T insertObject) throws Exception {

		return DbUtils.excuteInsertWholeClass(pollName, insertObject);

	}
	public static  <T> int   insertWhole(String pollName, T insertObject, String[] properties)
			throws Exception {

		return DbUtils.excuteInsertWholeClass(pollName, insertObject, properties);

	}
	public  static  <T>  int[] insertWhole(String pollName, T[] objs) throws Exception {

		return DbUtils.excuteInsertWholeClass(pollName, objs);

	}
	public  static  <T>  int[] insertWhole(String pollName, T[] objs, String[] properties)
			throws Exception {

		return DbUtils.excuteInsertWholeClass(pollName, objs, properties);

	}
	

	public static  <T>  long insertWholeReturnKey(String pollName, T insertObject) throws Exception {

		return DbUtils.excuteInsertWholeClassReturnKey(pollName, insertObject);

	}
	

	public  static  <T>   long insertWholeReturnKey(String pollName, T insertObject, String[] properties)
			throws Exception {

		return DbUtils.excuteInsertWholeClassReturnKey(pollName, insertObject, properties);

	}
	


	public  static  <T>  List<T> query(String pollName, T selectObject) throws Exception {

		return DbUtils.doQueryClassNoSql(pollName, selectObject);
	}
	
	public static <T> List<T> query(String pollName,T selectObject, int page, int perPage, PageBean pb) throws Exception {
		return DbUtils.doPageQueryClassNoSql(pollName, selectObject, page, perPage, pb);
	}
	@SuppressWarnings("unchecked")
	public  static  <T>  List<T> query(String pollName, T selectObject,
			String selectProperties) throws Exception {

		return DbUtils.doQueryClassNoSql(pollName,
				(Class<T>) selectObject.getClass(), selectObject,
				selectProperties);
	}
	public static  <T> List<T> query(String pollName,T selectObject, String selectProperties, int page, int perPage, PageBean pb)throws Exception {
		
		return DbUtils.doPageQueryClassNoSql(pollName,  selectObject, selectProperties, page, perPage, pb);
	}
	public  static  <T>  List<T> query(String pollName, T selectObject,
			String selectProperties,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {

		return DbUtils.doQueryClassNoSql(pollName,
				(Class<T>) selectObject.getClass(), selectObject,
				selectProperties, mainSlaveModeConnectMode);
	}

	
	public  static  <T>  T queryOne(String pollName, T selectObject) throws Exception {

		List<T> list= query(pollName,selectObject);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	
	
	public  static  <T>  T queryOne(String pollName, T selectObject,
			String selectProperties) throws Exception {

		@SuppressWarnings("unchecked")
		List<T> list= DbUtils.doQueryClassNoSql(pollName,
				(Class<T>) selectObject.getClass(), selectObject,
				selectProperties);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	public  static  <T>  T queryOne(String pollName, T selectObject,
			String selectProperties,
			DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws Exception {

		@SuppressWarnings("unchecked")
		List<T> list= DbUtils.doQueryClassNoSql(pollName,
				(Class<T>) selectObject.getClass(), selectObject,
				selectProperties, mainSlaveModeConnectMode);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
		
	}

	public  static  <T>  int[] update(String pollName, Object[] objects, String[] beanKeys,
			String[][] properties) throws Exception {
		return DbUtils.excuteUpdateObjects(pollName, objects, beanKeys,
				properties);
	}
	public  static  <T>  int update(String pollName, T updateObject, String beanKey)
			throws Exception {
		return DbUtils.excuteUpdateClass(pollName, updateObject, beanKey);
	}
	public  static  <T>  int update(String pollName, T updateObject, String beanKey,
			String[] properties) throws Exception {
		return DbUtils.excuteUpdateClass(pollName, updateObject, beanKey,
				properties);
	}
	public  static  <T>  int[] update( String pollName,T[] objects, String beanKey) throws Exception {
		return DbUtils
				.excuteUpdateClass(pollName, objects, beanKey);
	}
	public  static  <T>  int[] update(String pollName, T[] objects, String beanKey,
			String[] properties) throws Exception {
		return DbUtils
				.excuteUpdateClass(pollName, objects, beanKey, properties);
	}

	public  static  <T>  int[] updateWhole(String pollName, Object[] objects, String[] beanKeys,
			String[][] properties) throws Exception {
		return DbUtils.excuteUpdateWholeObjects(pollName, objects, beanKeys,
				properties);
	}
	
	public  static  <T>  int updateWhole(String pollName, T updateObject, String beanKey)
			throws Exception {
		return DbUtils.excuteUpdateWholeClass(pollName, updateObject, beanKey);
	}
	
	public  static  <T>  int updateWhole(String pollName, T updateObject, String beanKey,
			String[] properties) throws Exception {
		return DbUtils.excuteUpdateWholeClass(pollName, updateObject, beanKey,
				properties);
	}
	public  static  <T>  int[] updateWhole( String pollName,T[] objects, String beanKey) throws Exception {
		return DbUtils
				.excuteUpdateWholeClass(pollName, objects, beanKey);
	}
	public  static  <T>  int[] updateWhole(String pollName, T[] objects, String beanKey,
			String[] properties) throws Exception {
		return DbUtils
				.excuteUpdateWholeClass(pollName, objects, beanKey, properties);
	}



}
