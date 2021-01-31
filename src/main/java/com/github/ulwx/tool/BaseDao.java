package com.github.ulwx.tool;

import com.github.ulwx.database.DataBase;
import com.github.ulwx.database.DataBaseFactory;
import com.github.ulwx.database.DbException;
import com.github.ulwx.tool.support.StringUtils;

import java.util.List;
import java.util.function.Function;

public class BaseDao {

    protected static <R> R execute(Function<DataBase, R> function, String dbpoolName) {
        DataBase db = null;
        try {
            if (StringUtils.isEmpty(dbpoolName)) {
                db = DataBaseFactory.getDataBase();
            } else {
                db = DataBaseFactory.getDataBase(dbpoolName);
            }
            return function.apply(db);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public static <T> int[] delBy(String pollName, Object[] deleteObjects,
                                  String[] deletePropertiesArray) throws DbException {
        return execute(db -> {
            return db.delBy(deleteObjects, deletePropertiesArray);
        }, pollName);

    }

    public static <T> int delBy(String pollName, T deleteObject, Object[] whereProperties)
            throws DbException {
        return execute(db -> {
            return db.delBy(deleteObject,
                    whereProperties);
        }, pollName);
    }

    public static <T> int[] delBy(String pollName, T[] deleteObjects, Object[] whereProperties)
            throws DbException {
        return execute(db -> {
            return db.delBy(deleteObjects,
                    whereProperties);
        }, pollName);
    }


    public static <T> int insertBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject);
        }, pollName);
    }

    public static <T> int insertBy(String pollName, T insertObject, String[] insertProperties)
            throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject, insertProperties);
        }, pollName);
    }

    public static <T> int[] insertBy(String pollName, T[] objs) throws DbException {
        return execute(db -> {
            return db.insertBy(objs);
        }, pollName);
    }

    public static <T> int[] insertBy(String pollName, T[] objs, Object[] insertProperties)
            throws DbException {
        return execute(db -> {
            return db.insertBy(objs, insertProperties);
        }, pollName);
    }

    public static <T> long insertReturnKeyBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject);
        }, pollName);
    }

    public static <T> long insertReturnKeyBy(String pollName, T insertObject, Object[] insertProperties)
            throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject, insertProperties);
        }, pollName);
    }


    public static <T> int insertBy(String pollName, T insertObject, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject, includeNull);
        }, pollName);
    }

    public static <T> int insertBy(String pollName, T insertObject, Object[] insertProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject, insertProperties, includeNull);
        }, pollName);
    }

    public static <T> int[] insertBy(String pollName, T[] objs, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.insertBy(objs, includeNull);
        }, pollName);
    }

    public static <T> int[] insertBy(String pollName, T[] objs, Object[] insertProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.insertBy(objs, insertProperties, includeNull);
        }, pollName);
    }


    public static <T> long insertReturnKeyBy(String pollName, T insertObject, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject, includeNull);
        }, pollName);
    }


    public static <T> long insertReturnKeyBy(String pollName, T insertObject, Object[] insertProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject, insertProperties, includeNull);
        }, pollName);
    }


    public static <T> List<T> queryListBy(String pollName, T selectObject) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject);
        }, pollName);
    }

    public static <T> List<T> queryListBy(String pollName, T selectObject, int page, int perPage, PageBean pb) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject, page, perPage, pb);
        }, pollName);
    }

    public static <T> List<T> queryListBy(String pollName, T selectObject,
                                          Object[] whereProperties) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject,
                    whereProperties);
        }, pollName);
    }

    public static <T> List<T> queryListBy(String pollName, T selectObject, Object[] whereProperties, int page,
                                          int perPage, PageBean pb) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject, whereProperties, page, perPage, pb);
        }, pollName);
    }


    public static <T> T queryOneBy(String pollName, T selectObject) throws DbException {

        return execute(db -> {
            return db.queryOneBy(selectObject);
        }, pollName);
    }


    public static <T> T queryOneBy(String pollName, T selectObject,
                                   Object[] whereProperties) throws DbException {

        return execute(db -> {
            return db.queryOneBy(selectObject, whereProperties);
        }, pollName);

    }

    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties)
            throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties);
        }, pollName);
    }

    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties,
                                   Object[] updateProperties) throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties,
                    updateProperties);
        }, pollName);
    }

    public static <T> int[] updateBy(String pollName, T[] objects, Object[] whereProperties) throws DbException {
        return execute(db -> {
            return db.updateBy(objects, whereProperties);
        }, pollName);
    }

    public static <T> int[] updateBy(String pollName, T[] objects, Object[] whereProperties,
                                     Object[] updateProperties) throws DbException {
        return execute(db -> {
            return db
                    .updateBy(objects, whereProperties, updateProperties);
        }, pollName);
    }


    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties, includeNull);
        }, pollName);
    }

    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties,
                                   Object[] updateProperties, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties,
                    updateProperties, includeNull);
        }, pollName);
    }

    public static <T> int[] updateBy(String pollName, T[] objects, Object[] whereProperties, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.updateBy(objects, whereProperties, includeNull);
        }, pollName);
    }

    public static <T> int[] updateBy(String pollName, T[] objects, Object[] whereProperties,
                                     Object[] updateProperties, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.updateBy(objects, whereProperties, updateProperties, includeNull);
        }, pollName);
    }


}
