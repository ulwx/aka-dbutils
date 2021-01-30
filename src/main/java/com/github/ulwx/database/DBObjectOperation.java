package com.github.ulwx.database;

import com.github.ulwx.tool.PageBean;

import java.util.List;

public interface DBObjectOperation {

	public <T> int insertBy(T insertObject) throws DbException;
	public <T> long insertReturnKeyBy(T insertObject) throws DbException;

	public <T> int insertBy(T insertObject, String[] properties) throws DbException;;

	public <T> long insertReturnKeyBy(T insertObject, String[] properties) throws DbException;

	public <T> int[] insertBy(T[] objs) throws DbException;

	public <T> int[] insertBy(T[] objs, String[] properties) throws DbException;

	public <T> int updateBy(T updateObject, String beanKey) throws DbException;

	public <T> int updateBy(T updateObject, String beanKey, String[] properties) throws DbException;

	public <T> int[] updateBy(T[] objects, String beanKey, String[] properties) throws DbException;

	public <T> int[] updateBy(T[] objects, String beanKey) throws DbException;

	public <T> int[] updateBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException;

	public <T> T queryOneBy(T selectObject, String selectProperties) throws DbException;

	public <T> List<T> queryListBy(T selectObject, String selectProperties) throws DbException;

	public <T> List<T> queryListBy(T selectObject) throws DbException;
	
	public <T> List<T> queryListBy(T selectObject, String selectProperties,int page, int perPage, PageBean pb) throws DbException;

	public <T> List<T> queryListBy(T selectObject,int page, int perPage, PageBean pb) throws DbException;
	
	public <T> T queryOneBy(T selectObject) throws DbException;

	public <T> int delBy(T deleteObject, String deleteProperteis) throws DbException;

	public <T> int[] delBy(T[] deleteObjects, String deleteProperteis) throws DbException;

	public <T> int[] delBy(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException;

	public <T> int insertWholeBy(T insertObject) throws DbException;

	public <T> long insertWholeReturnKeyBy(T insertObject) throws DbException;

	public <T> int insertWholeBy(T insertObject, String[] properties) throws DbException;

	public <T> long insertWholeReturnKeyBy(T insertObject, String[] properties) throws DbException;

	public <T> int[] insertWholeBy(T[] objs) throws DbException;

	public <T> int[] insertWholeBy(T[] objs, String[] properties) throws DbException;

	public <T> int updateWholeBy(T updateObject, String beanKey) throws DbException;

	public <T> int updateWholeBy(T updateObject, String beanKey, String[] properties) throws DbException;

	public <T> int[] updateWholeBy(T[] objects, String beanKey, String[] properties) throws DbException;

	public <T> int[] updateWholeBy(T[] objects, String beanKey) throws DbException;

	public <T> int[] updateWholeBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException;
}
