package com.github.ulwx.database;

import com.github.ulwx.tool.PageBean;

import java.util.List;

public interface NoSqlOperation {
	public <T> int insert(T insertObject) throws Exception;

	public <T> long insertReturnKey(T insertObject) throws Exception;

	public <T> int insert(T insertObject, String[] properties) throws Exception;;

	public <T> long insertReturnKey(T insertObject, String[] properties) throws Exception;

	public <T> int[] insert(T[] objs) throws Exception;

	public <T> int[] insert(T[] objs, String[] properties) throws Exception;

	public <T> int update(T updateObject, String beanKey) throws Exception;

	public <T> int update(T updateObject, String beanKey, String[] properties) throws Exception;

	public <T> int[] update(T[] objects, String beanKey, String[] properties) throws Exception;

	public <T> int[] update(T[] objects, String beanKey) throws Exception;

	public <T> int[] update(Object[] objects, String[] beanKeys, String[][] properties) throws Exception;

	public <T> T queryOne(T selectObject, String selectProperties) throws Exception;

	public <T> List<T> query(T selectObject, String selectProperties) throws Exception;

	public <T> List<T> query(T selectObject) throws Exception;
	
	public <T> List<T> query(T selectObject, String selectProperties,int page, int perPage, PageBean pb) throws Exception;

	public <T> List<T> query(T selectObject,int page, int perPage, PageBean pb) throws Exception;
	
	public <T> T queryOne(T selectObject) throws Exception;

	public <T> int del(T deleteObject, String deleteProperteis) throws Exception;

	public <T> int[] del(T[] deleteObjects, String deleteProperteis) throws Exception;

	public <T> int[] del(Object[] deleteObjects, String[] deletePropertiesArray) throws Exception;

	public <T> int insertWhole(T insertObject) throws Exception;

	public <T> long insertWholeReturnKey(T insertObject) throws Exception;

	public <T> int insertWhole(T insertObject, String[] properties) throws Exception;

	public <T> long insertWholeReturnKey(T insertObject, String[] properties) throws Exception;

	public <T> int[] insertWhole(T[] objs) throws Exception;

	public <T> int[] insertWhole(T[] objs, String[] properties) throws Exception;

	public <T> int updateWhole(T updateObject, String beanKey) throws Exception;

	public <T> int updateWhole(T updateObject, String beanKey, String[] properties) throws Exception;

	public <T> int[] updateWhole(T[] objects, String beanKey, String[] properties) throws Exception;

	public <T> int[] updateWhole(T[] objects, String beanKey) throws Exception;

	public <T> int[] updateWhole(Object[] objects, String[] beanKeys, String[][] properties) throws Exception;
}
