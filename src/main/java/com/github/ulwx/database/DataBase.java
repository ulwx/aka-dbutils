package com.github.ulwx.database;

import com.github.ulwx.tool.PageBean;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DataBase extends DBObjectOperation, AutoCloseable {

    public static enum SQLType {
        NONE,INSERT, UPDATE, SELECT, STORE_DPROCEDURE,SCRIPT
    }

    public static class DataBaseType {
        public static String MYSQL = "mysql";
        public static String MS_SQL_SERVER = "microsoft sql server";
        public static String MS_SQL_SERVER_2005 = "microsoft sql server 2005";
        public static String ORACLE = "oracle";
        public static String DB2 = "db2";
        public static String H2 = "h2";
        public static String HSQL = "hsql";
        public static String POSTGRE = "postgre";
        public static String SYBASE = "sybase";
        public static String SQLITE = "sqlite";
        public static String OTHER = "other";
    }

    public static enum MainSlaveModeConnectMode {
        // 如果是主从模式，并且是非事务性操作,如果是这种模式，则只去获取主库连接
        Connect_MainServer,
        // 如果是主从模式，并且是非事务性操作，如果是这种模式，则只去获取从库连接
        Connect_SlaveServer,
        // 根据语句或是否含有事务来判断自动获取主库连接还是从宽连接。如果是执行语句包含在事务里或者是insert，update，delete语句，则获取主库连接;
        // 如果为查询语句，并且不包含在事务里，在获取从库连接
        Connect_Auto
    }

    public MainSlaveModeConnectMode getMainSlaveModeConnectMode();

    String getDbPoolName();

    boolean isMainSlaveMode();

    void setMainSlaveMode(boolean mainSlaveMode);

    boolean getInternalConnectionAutoCommit() throws DbException;

    String getDataBaseType();

    default void connectDb(DataSource dataSource) {
    }



    DataBaseImpl.ConnectType getConnectionType();

    default void connectDb(Connection connection, boolean externalControlConClose) {
    }

    default boolean isExternalControlConClose() {
        return false;
    }
    void connectDb(String dbPoolName) throws DbException;
    /**
     * 从dbpool.xml里设置的连接池获得连接
     *
     * @param dbPoolName 对应于dbpool.xml里的元素<dbpool>name属性值
     * @throws DbException
     */
    void connectDb(String dbPoolName, MainSlaveModeConnectMode mainSlaveModeConnectMode) throws DbException;


    DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage, PageBean pageUtils,
                                  String countSql) throws DbException;

    DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageUtils,
                          RowMapper<T> rowMapper, String countSql) throws DbException;

    List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
                                       PageBean pageUtils, String countSql) throws DbException;

    List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    <T> T queryOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
                          QueryMapNestOne2One[] queryMapNestList) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                          PageBean pageUtils, String countSql) throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
                          QueryMapNestOne2One[] queryMapNestList, int page, int perPage, PageBean pageUtils, String countSql)
            throws DbException;

    <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
                          Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException;

    <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException;

    int del(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    int update(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    int callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
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
     * @param reader    sql脚本输入reader
     * @param logWriter 日志打印writer
     * @return 返回执行成功的结果，出错返回异常
     * @throws DbException
     */
    String exeScript(Reader reader, PrintWriter logWriter) throws DbException;

    /**
     * @param reader sql脚本输入reader
     * @return 返回执行成功的结果，出错返回异常
     * @throws DbException
     */
    String exeScript(Reader reader) throws DbException;

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
     * 用于事务性操作的回滚，如果事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    void rollback() throws DbException;


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

    Connection getConnection();
}