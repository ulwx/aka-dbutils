package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.PageBean;

import java.util.List;

public interface DBObjectOperation {

    public <T> int insertBy(T insertObject) throws DbException;

    public <T> int insertBy(T insertObject, boolean includeNull) throws DbException;

    public <T> int insertBy(T insertObject, Object[] insertProperties) throws DbException;

    public <T> int insertBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException;

    public <T> int[] insertBy(T[] objs) throws DbException;

    public <T> int[] insertBy(T[] objs, boolean includeNull) throws DbException;

    public <T> int[] insertBy(T[] objs, Object[] insertProperties) throws DbException;

    public <T> int[] insertBy(T[] objs, Object[] insertProperties, boolean includeNull) throws DbException;

    public <T> long insertReturnKeyBy(T insertObject) throws DbException;

    public <T> long insertReturnKeyBy(T insertObject, boolean includeNull) throws DbException;

    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties) throws DbException;

    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException;

    public <T> int updateBy(T updateObject, Object[] whereProperteis) throws DbException;

    public <T> int updateBy(T updateObject, Object[] whereProperties, boolean includeNull) throws DbException;

    public <T> int updateBy(T updateObject, Object[] whereProperteis, Object[] updateProperties) throws DbException;

    public <T> int updateBy(T updateObject, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException;

    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis, Object[] updateProperties) throws DbException;

    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException;

    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis) throws DbException;

    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, boolean includeNull) throws DbException;

    public <T> T queryOneBy(T selectObject) throws DbException;

    public <T> T queryOneBy(T selectObject, Object[] whereProperteis) throws DbException;

    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis) throws DbException;

    public <T> List<T> queryListBy(T selectObject) throws DbException;

    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis, int page, int perPage, PageBean pb) throws DbException;

    public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException;

    public <T> int delBy(T deleteObject, Object[] whereProperteis) throws DbException;

    public <T> int[] delBy(T[] deleteObjects, Object[] whereProperteis) throws DbException;


}
