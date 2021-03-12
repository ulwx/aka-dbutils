package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.tool.PageBean;

import javax.sql.DataSource;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DataBase extends DBObjectOperation, AutoCloseable {

    public static enum SQLType {
        OTHER,INSERT, UPDATE,DELETE, SELECT, STORE_DPROCEDURE,SCRIPT
    }
    public static enum ConnectType {
        POOL, DATASOURCE, CONNECTION
    }

    public static enum MainSlaveModeConnectMode {
        // 如果是主从模式，并且是非事务性操作,如果是这种模式，则只去获取主库连接
        Connect_MainServer,
        // 如果是主从模式，并且是非事务性操作，如果是这种模式，则只去获取从库连接
        Connect_SlaveServer,
        // 根据语句或是否含有事务来判断自动获取主库连接还是从库连接。如果是执行语句包含在事务里或者是insert，update，delete语句，则获取主库连接;
        // 如果为查询语句，并且不包含在事务里，会在从库里获取连接
        Connect_Auto
    }

    public MainSlaveModeConnectMode getMainSlaveModeConnectMode();

    String getDbPoolName();

    boolean isMainSlaveMode();

    void setMainSlaveMode(boolean mainSlaveMode);

    boolean getInternalConnectionAutoCommit() throws DbException;

    DBMS getDataBaseType();

    default void connectDb(DataSource dataSource) {
    }

    ConnectType getConnectionType();

    default void connectDb(Connection connection, boolean externalControlConClose) {
    }

    default boolean isExternalControlConClose() {
        return false;
    }
    /**
     * 从dbpool.xml里设置的连接池获得连接
     *
     * @param dbPoolName 对应于dbpool.xml里的元素<dbpool>name属性值
     * @throws DbException
     */
    void connectDb(String dbPoolName) throws DbException;


    DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage, PageBean pageBean,
                                  String countSql) throws DbException;

    DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageBean,
                          RowMapper<T> rowMapper, String countSql) throws DbException;

    List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
                                       PageBean pageBean, String countSql) throws DbException;

    List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    <T> T queryOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    <T> List<T> queryListOne2One(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                 One2OneMapNestOptions one2OneMapNestOptions) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                          PageBean pageBean, String countSql) throws DbException;

    <T> List<T> queryListOne2One(Class<T> clazz,  String sqlQuery, Map<Integer, Object> vParameters,
                                 One2OneMapNestOptions one2OneMapNestOptions,
                                 int page, int perPage, PageBean pageBean, String countSql)
            throws DbException;

    <T> List<T> queryListOne2Many(Class<T> clazz,String sqlQuery,
                                  Map<Integer, Object> vParameters,One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException;

    <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException;

    int del(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    int update(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    void callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
                      List<DataBaseSet> returnDataBaseSets) throws DbException;

    int insert(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    long insertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException;


    int[] update(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException;

    int[] insert(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException;


    int[] update(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException;

    int[] insert(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException;

    int[] update(ArrayList<String> sqltxts) throws DbException;

    int[] insert(ArrayList<String> sqltxts) throws DbException;


    /**
     * 执行脚本
     * @param reader    sql脚本输入reader
     * @param throwWarning 脚本执行时如果出现warning，是否退出并回滚
     * @return 返回执行成功的结果，出错返回异常
     * @throws DbException
     */
    String exeScript(Reader reader, boolean throwWarning,Map<String, Object> args) throws DbException ;


    /**
     * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过DataBase.
     * setAutoCommit()方法 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBase.close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    void setAutoCommit(boolean b) throws DbException;


    /**
     * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过DataBase.setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBase.close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    boolean getAutoCommit() throws DbException;

    /**
     * 用于事务性操作的回滚。
     *
     * @throws DbException
     */
    void rollback() throws DbException;

    /**
     * 得到保存点信息；
     * @return
     */
    Map<String, Savepoint> getSavepoint();
    /**
     * 设置保存点
     * @param savepointName 保存点名称
     * @throws DbException
     */
    void setSavepoint(String savepointName) throws DbException;

    /**
     * 释放并删除指定名称的savepoint
     * @param savepointName
     * @throws DbException
     */
    void releaseSavepoint(String savepointName) throws DbException;
    /**
     * 用于事务回滚到保存点。
     * @throws DbException
     */
    void rollbackToSavepoint(String savepointName) throws DbException;

    /**
     * 判断资源和底层数据库连接是否关闭
     *
     * @return
     * @throws DbException
     */
    boolean isColsed() throws DbException;

    /**
     * 事务性操作的事务的提交，当 {@link #setAutoCommit(boolean)}设为false， 会用到此方法，一般对于事务性操作会用到，如果
     * 事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    void commit() throws DbException;

    /**
     * 关闭数据库连接，释放底层占用资源
     */
    void close();

    /**
     * 返回当前连接，如果force=true，若当前没有连接，则新生成一个连接；force=false，若连接
     * 不存在或已经关闭则会返回null。
     * @param force
     * @return
     */
    Connection getConnection(boolean force);
}