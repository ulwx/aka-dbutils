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

    public static <T> int delBy(String pollName, T deleteObject, String deleteProperteis)
            throws DbException {
        return execute(db -> {
            return db.delBy(deleteObject,
                    deleteProperteis);
        }, pollName);
    }

    public static <T> int[] delBy(String pollName, T[] deleteObjects, String deleteProperteis)
            throws DbException {
        return execute(db -> {
            return db.delBy(deleteObjects,
                    deleteProperteis);
        }, pollName);
    }


    public static <T> int insertBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject);
        }, pollName);
    }

    public static <T> int insertBy(String pollName, T insertObject, String[] properties)
            throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject, properties);
        }, pollName);
    }

    public static <T> int[] insertBy(String pollName, T[] objs) throws DbException {
        return execute(db -> {
            return db.insertBy(objs);
        }, pollName);
    }

    public static <T> int[] insertBy(String pollName, T[] objs, String[] properties)
            throws DbException {
        return execute(db -> {
            return db.insertBy(objs, properties);
        }, pollName);
    }

    public static <T> long insertReturnKeyBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject);
        }, pollName);
    }

    public static <T> long insertReturnKeyBy(String pollName, T insertObject, String[] properties)
            throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject, properties);
        }, pollName);
    }


    public static <T> int insertWholeBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertWholeBy(insertObject);
        }, pollName);
    }

    public static <T> int insertWholeBy(String pollName, T insertObject, String[] properties)
            throws DbException {
        return execute(db -> {
            return db.insertWholeBy(insertObject, properties);
        }, pollName);
    }

    public static <T> int[] insertWholeBy(String pollName, T[] objs) throws DbException {
        return execute(db -> {
            return db.insertWholeBy(objs);
        }, pollName);
    }

    public static <T> int[] insertWholeBy(String pollName, T[] objs, String[] properties)
            throws DbException {
        return execute(db -> {
            return db.insertWholeBy(objs, properties);
        }, pollName);
    }


    public static <T> long insertWholeReturnKeyBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertWholeReturnKeyBy(insertObject);
        }, pollName);
    }


    public static <T> long insertWholeReturnKeyBy(String pollName, T insertObject, String[] properties)
            throws DbException {
        return execute(db -> {
            return db.insertWholeReturnKeyBy(insertObject, properties);
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
                                          String selectProperties) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject,
                    selectProperties);
        }, pollName);
    }

    public static <T> List<T> queryListBy(String pollName, T selectObject, String selectProperties, int page,
                                          int perPage, PageBean pb) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject, selectProperties, page, perPage, pb);
        }, pollName);
    }


    public static <T> T queryOneBy(String pollName, T selectObject) throws DbException {

        return execute(db -> {
            return db.queryOneBy(selectObject);
        }, pollName);
    }


    public static <T> T queryOneBy(String pollName, T selectObject,
                                   String selectProperties) throws DbException {

        return execute(db -> {
            return db.queryOneBy(selectObject, selectProperties);
        }, pollName);

    }


    public static <T> int[] updateBy(String pollName, Object[] objects, String[] beanKeys,
                                     String[][] properties) throws DbException {
        return execute(db -> {
            return db.updateBy(objects, beanKeys,
                    properties);
        }, pollName);
    }

    public static <T> int updateBy(String pollName, T updateObject, String beanKey)
            throws DbException  {
        return execute(db -> {
            return db.updateBy(updateObject, beanKey);
        }, pollName);
    }

    public static <T> int updateBy(String pollName, T updateObject, String beanKey,
                                   String[] properties) throws DbException  {
        return execute(db -> {
            return db.updateBy(updateObject, beanKey,
                    properties);
        }, pollName);
    }

    public static <T> int[] updateBy(String pollName, T[] objects, String beanKey) throws DbException {
        return execute(db -> {
            return db.updateBy(objects, beanKey);
        }, pollName);
    }

    public static <T> int[] updateBy(String pollName, T[] objects, String beanKey,
                                     String[] properties) throws DbException {
        return execute(db -> {
            return db
                    .updateBy(objects, beanKey, properties);
        }, pollName);
    }

    public static <T> int[] updateWholeBy(String pollName, Object[] objects, String[] beanKeys,
                                          String[][] properties) throws DbException {
        return execute(db -> {
            return db.updateWholeBy(objects, beanKeys,
                    properties);
        }, pollName);
    }

    public static <T> int updateWholeBy(String pollName, T updateObject, String beanKey)
            throws DbException {
        return execute(db -> {
            return db.updateWholeBy(updateObject, beanKey);
        }, pollName);
    }

    public static <T> int updateWholeBy(String pollName, T updateObject, String beanKey,
                                        String[] properties) throws DbException {
        return execute(db -> {
            return db.updateWholeBy(updateObject, beanKey,
                    properties);
        }, pollName);
    }

    public static <T> int[] updateWholeBy(String pollName, T[] objects, String beanKey) throws DbException {
        return execute(db -> {
            return db.updateWholeBy(objects, beanKey);
        }, pollName);
    }

    public static <T> int[] updateWholeBy(String pollName, T[] objects, String beanKey,
                                        String[] properties) throws DbException {
        return execute(db -> {
        return db.updateWholeBy(objects, beanKey, properties);
        }, pollName);
    }


}
