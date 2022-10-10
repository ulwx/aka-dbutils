package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.database.DataBase;
import com.github.ulwx.aka.dbutils.database.DataBaseFactory;
import com.github.ulwx.aka.dbutils.database.DbException;

import java.util.List;
import java.util.function.Function;

public class BaseDao {

    protected static <R> R execute(Function<DataBase, R> function, String dbpoolName) {
        DataBase db = null;
        try {
            db = DataBaseFactory.getDataBase(dbpoolName);
            return function.apply(db);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 根据deleteObject对象生成delete语句，删除相应的记录，其中whereProperties里指定的
     * 属性生成了delete的where条件部分，其中whereProperties里为null的属性也会包含。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param deleteObject    根据deleteObject对象生成delete语句
     * @param whereProperties whereProperties里指定的属性生成了delete的where条件部分。
     * @param <T>
     * @return 返回删除的条数
     * @throws DbException
     */
    public static <T> int delBy(String pollName, T deleteObject, Object[] whereProperties)
            throws DbException {
        return execute(db -> {
            return db.delBy(deleteObject,
                    whereProperties);
        }, pollName);
    }

    /**
     * 根据deleteObjects对象数组生批量成delete语句，删除相应的记录，其中whereProperties里指定的
     * 属性生成了delete的where条件部分，其中whereProperties里为null的属性也会包含。整个批量删除操作
     * 在一个事务里，数组里每个对象生成的delete语句执行失败，整个事务会回滚。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param deleteObjects   根据deleteObjects对象数组生批量成delete语句
     * @param whereProperties whereProperties里指定的属性生成了delete的where条件部分
     * @param <T>
     * @return 数组里每个对象生成的delete语句执行后返回删除的条数
     * @throws DbException
     */
    public static <T> int[] delBy(String pollName, T[] deleteObjects, Object[] whereProperties)
            throws DbException {
        return execute(db -> {
            return db.delBy(deleteObjects,
                    whereProperties);
        }, pollName);
    }


    /**
     * 插入指定对象里所有值不会null属性到数据库
     *
     * @param pollName     对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                     如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject 指定的插入对象，根据此对象反射生成insert语句，对象里所有不会null的属性会插入到数据库
     * @param <T>
     * @return 返回插入记录的行数（成功为1，不成功为0）
     * @throws DbException
     */
    public static <T> int insertBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject);
        }, pollName);
    }

    /**
     * 插入指定对象的指定属性到数据库，在insertProperties里指定哪些属性需要插入数据库，其中为null值的属性也会作为null值插入数据库。
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject     指定的插入对象，根据此对象和insertProperties里指定的属性反射生成insert语句
     * @param insertProperties 在insertProperties里指定哪些属性需要插入数据库
     * @param <T>
     * @return 返回插入记录的行数（成功为1，不成功为0）
     * @throws DbException
     */
    public static <T> int insertBy(String pollName, T insertObject, Object[] insertProperties)
            throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject, insertProperties);
        }, pollName);
    }

    /**
     * 插入指定对象里所有属性到数据库，根据includeNull指定是否包含null值的属性
     *
     * @param pollName     对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                     如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject 指定的插入对象，根据此对象里的属性反射生成insert语句
     * @param includeNull  是否包含null值的属性，如果为true，表示insertObject里null值的属性将会以null值插入到数据库，为false，表示不包含null值的属性。
     * @param <T>
     * @return 返回插入记录的行数（成功为1，不成功为0）
     * @throws DbException
     */
    public static <T> int insertBy(String pollName, T insertObject, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject, includeNull);
        }, pollName);
    }

    /**
     * 插入对象指定属性到数据库，根据此对象里的属性、insertProperties、includeNull三者反射生成insert语句
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject     指定的插入的对象。根据此对象里的属性、insertProperties、includeNull三者反射生成insert语句
     * @param insertProperties 指定插入对象里哪些属性需要插入
     * @param includeNull      指定insertProperties里属性为null的值是否忽略。
     * @param <T>
     * @return 返回插入记录的行数（成功为1，不成功为0）
     * @throws DbException
     */
    public static <T> int insertBy(String pollName, T insertObject, Object[] insertProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.insertBy(insertObject, insertProperties, includeNull);
        }, pollName);
    }

    /**
     * 批量插入多个相同类型对象到数据库，aka-dbutils根据此对象里的非null值的属性反射生成insert语句
     *
     * @param pollName 对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                 如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param objs     待插入数据库的对象数组
     * @param <T>
     * @return 返回每个对象是否插入成功数组，数组里的某索引位置int值反映objs里对应对象是否插入成功的标志，1：表示成功  0：不是没有插入任何值
     * @throws DbException
     */
    public static <T> int[] insertBy(String pollName, T[] objs) throws DbException {
        return execute(db -> {
            return db.insertBy(objs);
        }, pollName);
    }

    /**
     * 批量插入多个相同类型对象到数据库，aka-dbutils根据此对象里包含insertProperties里指定的属性反射生成insert语句，如果insertProperties
     * 里包含值为null的属性，也会插入数据库。
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param objs             待插入数据库的对象数组
     * @param insertProperties 指定插入对象里哪些属性需要插入
     * @param <T>
     * @return 返回每个对象是否插入成功数组，数组里的某索引位置int值反映objs里对应对象是否插入成功的标志，1：表示成功  0：不是没有插入任何值
     * @throws DbException
     */
    public static <T> int[] insertBy(String pollName, T[] objs, Object[] insertProperties)
            throws DbException {
        return execute(db -> {
            return db.insertBy(objs, insertProperties);
        }, pollName);
    }

    /**
     * 批量插入多个相同类型对象到数据库，aka-dbutils根据此对象里的属性反射生成insert语句的values()语句部分，根据includeNull属性决定是否插入为
     * null值的属性。
     *
     * @param pollName     对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                     如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject 指定的插入对象，根据此对象里的属性反射生成insert语句
     * @param includeNull  决定是否插入insertObject对象里为null值的属性。
     * @param <T>
     * @return 返回插入成功后数据库生成的自增主键id。
     * @throws DbException
     */
    public static <T> long insertReturnKeyBy(String pollName, T insertObject, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject, includeNull);
        }, pollName);
    }


    /**
     * 根据对象里的insertProperties指定的属性反射生成insert语句，includeNull决定是否插入insertProperties里为null值的属性。
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject     指定的插入对象，根据此对象里的insertProperties指定的属性反射生成insert语句
     * @param insertProperties 指定插入对象里哪些属性需要插入
     * @param includeNull      决定是否插入insertProperties里为null值的属性。
     * @param <T>
     * @return 返回插入记录后生成的主键id
     * @throws DbException
     */
    public static <T> long insertReturnKeyBy(String pollName, T insertObject, Object[] insertProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject, insertProperties, includeNull);
        }, pollName);
    }

    /**
     * 插入指定对象所有值不会null属性到数据库并返回生成的主键id
     *
     * @param pollName     对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                     如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject 指定的插入对象，根据此对象里的属性反射生成insert语句，会忽略为null值的属性
     * @param <T>
     * @return 返回生成的主键id
     * @throws DbException
     */
    public static <T> long insertReturnKeyBy(String pollName, T insertObject) throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject);
        }, pollName);
    }

    /**
     * 插入指定对象里insertProperties指定的属性到数据库并返回生成的主键id，insertProperties里为null值的属性也会插入数据库
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param insertObject     指定的插入对象，根据此对象里insertProperties指定的属性反射生成insert语句，insertProperties里为null值的属性也会插入数据库
     * @param insertProperties 指定哪些属性插入奥数据库
     * @param <T>
     * @return 返回生成的主键id
     * @throws DbException
     */
    public static <T> long insertReturnKeyBy(String pollName, T insertObject, Object[] insertProperties)
            throws DbException {
        return execute(db -> {
            return db.insertReturnKeyBy(insertObject, insertProperties);
        }, pollName);
    }


    /**
     * 批量插入对象里的属性到数据库，includeNull决定是否插入null值的属性到数据库
     *
     * @param pollName    对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                    如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param objs        批量插入的同类型对象数组
     * @param includeNull includeNull决定是否插入对象里为null值的属性到数据库
     * @param <T>
     * @return 返回objs数组里每个对象是否插入成功的标志，1：插入成功 0：没有插入任何记录
     * @throws DbException
     */
    public static <T> int[] insertBy(String pollName, T[] objs, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.insertBy(objs, includeNull);
        }, pollName);
    }

    /**
     * 批量插入对象里insertProperties指定属性到数据库，includeNull决定insertProperties里为null值的属性是否插入到数据库
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param objs             批量插入的同类型对象数组
     * @param insertProperties 指定对象里哪些属性插入奥数据库
     * @param includeNull      includeNull决定insertProperties里为null值的属性是否插入到数据库
     * @param <T>
     * @return 返回objs数组里每个对象是否插入成功的标志，1：插入成功 0：没有插入任何记录
     * @throws DbException
     */
    public static <T> int[] insertBy(String pollName, T[] objs, Object[] insertProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.insertBy(objs, insertProperties, includeNull);
        }, pollName);
    }


    /**
     * 根据selectObject的非空属性生成select语句的where条件部分，每个属性构成的条件之间是and关系，如：
     * <blockquote><pre><code>select * from course  where name='course33' and class_hours=13</code>
     * </pre></blockquote><p>
     *
     * @param pollName     对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                     如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param selectObject 根据传入的对象反射生成select语句，非空属性组成了where的条件部分。
     * @param <T>
     * @return 返回查询的表记录所填充的对象列表，对象的类型与selectObject的类型一致。
     * @throws DbException
     */
    public static <T> List<T> queryListBy(String pollName, T selectObject) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject);
        }, pollName);
    }

    /**
     * 根据selectObject的非空属性生成select语句的where条件部分，每个属性构成的条件之间是and关系，本方法为分页查询，参数page为页码（从1开始），
     * 参数perPage为每页多少行记录，aka-dbutils会根据这些信息生成分页的select语句，例如，如果当前数据库类型为mysql，请求第2页（page=2），
     * 每页10条（perPage=10），则生成的分页查询语句如下：
     * <blockquote><pre>
     *     select * from course  where name='course_page' limit 20,10
     *     (由于 mysql的limit后面的索引是从0开始，所以这里的20表示从第21条开始往后的10条记录，即第二页数据)
     * </pre></blockquote>
     * aka-dbutils在生成上面的分页select语句之前，会首先自动生成查询总数的select语句，如果当前数据库类型为mysql，生成的语句如下：
     * <blockquote><pre>
     *      select count(1) from (select * from course  where name='course_page') t
     *  </pre></blockquote>
     *
     * @param pollName     对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                     如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param selectObject 根据此对象反射生成select语句，非空属性组成了where的条件部分
     * @param page         页码，从1开始
     * @param perPage      每页多少行记录
     * @param pageBean     返回的分页信息存入此对象，包括总记录号，最大页数，这些信息可以用于前端UI展示
     * @param <T>
     * @return 返回查询的记录填充的对象列表，对象的类型与selectObject的类型一致
     * @throws DbException
     */
    public static <T> List<T> queryListBy(String pollName, T selectObject, int page, int perPage, PageBean pageBean) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject, page, perPage, pageBean);
        }, pollName);
    }

    /**
     * 根据selectObject存在于whereProperties里的属性生成select语句的where条件部分，每个属性构成的条件之间是and关系，whereProperties指定
     * 的属性即使在selectObject里值为空，也不会忽略，即生成形如"xxx=null"的条件。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param selectObject    此对象反射生成select语句，whereProperties指定了哪些属性用于组成where的条件部分，不会其中忽略值为空的属性。
     * @param whereProperties whereProperties指定了哪些属性用于组成where的条件部分，不会其中忽略值为空的属性。
     * @param <T>
     * @return 返回查询的记录填充的对象列表，对象的类型与selectObject的类型一致
     * @throws DbException
     */
    public static <T> List<T> queryListBy(String pollName, T selectObject,
                                          Object[] whereProperties) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject,
                    whereProperties);
        }, pollName);
    }

    /**
     * 据selectObject存在于whereProperties里的属性生成select语句的where条件部分，每个属性构成的条件之间是and关系，whereProperties指定
     * 的属性即使在selectObject里值为空，也不会忽略，即生成形如"xxx=null"的条件。本方法为分页查询，参数page为页码（从1开始），
     * 参数perPage为每页多少行记录，aka-dbutils会根据这些信息生成每页的select语句，
     * 例如，如果当前数据库类型为mysql，请求第2页（page=2）， 每页10条（perPage=10），则生成的分页查询语句如下：
     * <blockquote><pre>
     *         `select  *  from  course  where name='course_page'  limit  10,10`
     *         (由于 mysql的limit后面的索引是从0开始，所以这里的10表示从第11条开始往后的10条记录，即第二页数据)
     * </pre></blockquote>
     * aka-dbutils在生成上面的分页select语句之前，会首先自动生成查询总数的select语句，如果当前数据库类型为mysql，生成的语句如下：
     * <blockquote><pre>
     *         `select  count(1)   from  (select  *  from  course  where  name='course_page')
     * </pre></blockquote>
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param selectObject    此对象反射生成select语句，whereProperties指定了哪些属性用于组成where的条件部分，不会其中忽略值为空的属性。
     * @param whereProperties whereProperties指定了哪些属性用于组成where的条件部分，不会其中忽略值为空的属性。
     * @param page
     * @param perPage
     * @param pb
     * @param <T>
     * @return 返回查询的记录填充的对象列表，对象的类型与selectObject的类型一致
     * @throws DbException
     */
    public static <T> List<T> queryListBy(String pollName, T selectObject, Object[] whereProperties, int page,
                                          int perPage, PageBean pb) throws DbException {
        return execute(db -> {
            return db.queryListBy(selectObject, whereProperties, page, perPage, pb);
        }, pollName);
    }

    /**
     * 根据selectObject的非空属性生成select语句的where条件部分，每个属性构成的条件之间是and关系，例如：如果为mysql数据库，
     * 则生成的SQL形如：
     * <blockquote><pre>
     * <code>select * from course  where name='course33' and class_hours=13 limit 1</code>
     *   </pre></blockquote><p>
     * aka-dbutils生成sql语句时进行了优化处理，使之从数据库只取一条。
     *
     * @param pollName     对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                     如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param selectObject 根据传入的对象反射生成select语句，非空属性组成了where的条件部分
     * @param <T>
     * @return 查询一条记录并返回填充，对象的类型与selectObject的类型一致。
     * @throws DbException
     */
    public static <T> T queryOneBy(String pollName, T selectObject) throws DbException {

        return execute(db -> {
            return db.queryOneBy(selectObject);
        }, pollName);
    }

    /**
     * 据selectObject存在于whereProperties里的属性生成select语句的where条件部分，每个属性构成的条件之间是and关系，例如：如果为mysql数据库，
     * 则生成的SQL形如：
     * <blockquote><pre>
     * <code>select * from course  where name='course33' and class_hours=13 limit 1</code>
     *   </pre></blockquote><p>
     * aka-dbutils生成sql语句时进行了优化处理，使之从数据库只取一条。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param selectObject    此对象反射生成select语句，whereProperties指定了哪些属性用于组成where的条件部分，不会其中忽略值为空的属性。
     * @param whereProperties whereProperties指定了哪些属性用于组成where的条件部分，不会其中忽略值为空的属性。
     * @param <T>
     * @return 查询一条记录并返回填充，对象的类型与selectObject的类型一致。
     * @throws DbException
     */

    public static <T> T queryOneBy(String pollName, T selectObject,
                                   Object[] whereProperties) throws DbException {

        return execute(db -> {
            return db.queryOneBy(selectObject, whereProperties);
        }, pollName);

    }

    /**
     * 根据指定对象改新对应的表记录，aka-dbutils会根据对象生成update语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分（不要忽略whereProperties里值为null的属性），去除whereProperties里的属性，对象里的其它属性会生成update语句里的set语句部分，
     * 但会忽略其中值为null的属性。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param updateObject    根据此对象生成update语句。去除whereProperties里的属性，对象里的其它属性会生成update语句里的set语句部分， 但会忽略其中值为null的属性。
     * @param whereProperties 指定对象里哪些属性来生成where条件部分（不要忽略whereProperties里值为null的属性）
     * @param <T>
     * @return 返回更新记录的条数
     * @throws DbException
     */
    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties)
            throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties);
        }, pollName);
    }

    /**
     * 根据指定对象更改新对应的表记录，aka-dbutils会根据对象生成更新语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分（不会忽略whereProperties里值为null的属性），对象里除whereProperties里的其它属性会生成set子句部分，但会根据includeNull决定是否包含null值的属性。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param updateObject    根据对象生成update语句
     * @param whereProperties 指定对象里哪些属性来生成where子句的条件部分（不会忽略whereProperties里值为null的属性）
     * @param includeNull     根据includeNull决定set子句里是否包含null值的属性
     * @param <T>
     * @return 返回更新记录的条数
     * @throws DbException
     */
    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties, boolean includeNull)
            throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties, includeNull);
        }, pollName);
    }

    /**
     * 根据指定对象改新对应的表记录，aka-dbutils会根据对象生成update语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分（不会忽略whereProperties里值为null的属性），通过updateProperties指定对象里哪些属性生成update语句里的set语句部分，
     * 但会忽略updateProperties里存在值为null的属性。
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param updateObject     根据对象生成update语句
     * @param whereProperties  指定对象里哪些属性来生成where子句的条件部分（不会忽略whereProperties里值为null的属性）
     * @param updateProperties 指定对象里哪些属性来生成set子句部分，会忽略值为null的属性
     * @param <T>
     * @return 返回更新记录的条数
     * @throws DbException
     */
    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties,
                                   Object[] updateProperties) throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties,
                    updateProperties);
        }, pollName);
    }


    /**
     * 根据指定对象更改新对应的表记录，aka-dbutils会根据对象生成更新语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分（不要忽略whereProperties里值为null的属性），通过updateProperties里的属性会生成set子句部分，但会根据includeNull决定是否包含null值的属性。
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param updateObject     根据对象生成update语句
     * @param whereProperties  指定对象里哪些属性来生成where子句的条件部分（不会忽略whereProperties里值为null的属性）
     * @param updateProperties 通过updateProperties指定对象里哪些属性生成update语句里的set语句部分。
     * @param includeNull      根据includeNull决定set子句里是否包含null值的属性
     * @param <T>
     * @return 返回更新记录的条数
     * @throws DbException
     */
    public static <T> int updateBy(String pollName, T updateObject, Object[] whereProperties,
                                   Object[] updateProperties, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.updateBy(updateObject, whereProperties,
                    updateProperties, includeNull);
        }, pollName);
    }

    /**
     * 根据指定对象数组批量更新对应的表记录，aka-dbutils会根据对象数组生成批量更新语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分，对象里除了whereProperties里属性之外的其它属性会生成update语句里的set语句部分，
     * 但会忽略存在值为null的属性。批量更新操作本身在一个事务里，只要一个对象更新失败，整个事务会回滚。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param objects         根据对象数组生成批量update语句
     * @param whereProperties 指定对象里哪些属性来生成where子句的条件部分（不会忽略whereProperties里值为null的属性）
     * @param <T>
     * @return 返回更新记录的条数
     * @throws DbException
     */
    public static <T> int[] updateBy(String pollName, T[] objects, Object[] whereProperties) throws DbException {
        return execute(db -> {
            return db.updateBy(objects, whereProperties);
        }, pollName);
    }

    /**
     * 根据指定对象数组更新对应的表记录，aka-dbutils会根据对象数组生成批量更新语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分（不会忽略whereProperties里值为null的属性），通过updateProperties指定对象里哪些属性生成update语句里的set语句部分，但会忽略存在值为null的属性。
     * 注意：批量更新操作本身在一个事务里，只要一个对象更新失败，整个事务会回滚。
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @param objects          根据对象数组生成批量update语句
     * @param whereProperties  指定对象里哪些属性来生成where子句的条件部分（不会忽略whereProperties里值为null的属性）
     * @param updateProperties 通过updateProperties指定对象里哪些属性生成update语句里的set语句部分。
     * @param <T>
     * @return 返回更新记录的条数
     * @throws DbException
     */
    public static <T> int[] updateBy(String pollName, T[] objects, Object[] whereProperties,
                                     Object[] updateProperties) throws DbException {
        return execute(db -> {
            return db
                    .updateBy(objects, whereProperties, updateProperties);
        }, pollName);
    }


    /**
     * 根据指定对象数组更新对应的表记录，aka-dbutils会根据对象数组生成批量的更新语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分（不会忽略whereProperties里值为null的属性），除whereProperties里属性的其它属性会生成set子句部分，但会根据includeNull决定是否包含null值的属性。
     * 注意：批量更新操作本身在一个事务里，只要一个对象更新失败，整个事务会回滚。
     *
     * @param pollName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param updateObjects   根据对象数组生成update语句
     * @param whereProperties 指定对象里哪些属性来生成where子句的条件部分（不会忽略whereProperties里值为null的属性）
     * @param includeNull     根据includeNull决定set子句里是否包含null值的属性
     * @param <T>
     * @return 返回更新记录的条数
     * @throws DbException
     */
    public static <T> int[] updateBy(String pollName, T[] updateObjects, Object[] whereProperties,
                                     boolean includeNull) throws DbException {
        return execute(db -> {
            return db.updateBy(updateObjects, whereProperties, includeNull);
        }, pollName);
    }

    /**
     * 根据指定对象数组更新对应的表记录，aka-dbutils会根据对象数组生成批量的更新语句，需要通过whereProperties指定对象里哪些
     * 属性来生成where条件部分（不会忽略whereProperties里值为null的属性），updateProperties里属性会生成set子句部分，但会根据includeNull决定是否包含null值的属性。
     * 注意：批量更新操作本身在一个事务里，只要一个对象更新失败，整个事务会回滚。
     *
     * @param pollName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param updateObjects    根据对象数组生成update语句
     * @param whereProperties  指定对象里哪些属性来生成where子句的条件部分（不会忽略whereProperties里值为null的属性）
     * @param updateProperties 通过updateProperties指定对象里哪些属性生成update语句里的set语句部分。
     * @param includeNull      根据includeNull决定set子句里是否包含null值的属性
     * @param <T>
     * @return
     * @throws DbException
     */
    public static <T> int[] updateBy(String pollName, T[] updateObjects, Object[] whereProperties,
                                     Object[] updateProperties, boolean includeNull) throws DbException {
        return execute(db -> {
            return db.updateBy(updateObjects, whereProperties, updateProperties, includeNull);
        }, pollName);
    }


}
