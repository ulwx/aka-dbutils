package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.DbException.CODE;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.CachedRowSetImpl;
import com.github.ulwx.aka.dbutils.database.dbpool.DBPoolFactory;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.database.dialect.DialectClient;
import com.github.ulwx.aka.dbutils.database.nsql.NSQL;
import com.github.ulwx.aka.dbutils.database.sql.BeanUtils;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.BaseDao;
import com.github.ulwx.aka.dbutils.tool.PageBean;
import com.github.ulwx.aka.dbutils.tool.support.*;
import com.github.ulwx.aka.dbutils.tool.support.reflect.CGetFun;
import com.github.ulwx.aka.dbutils.tool.support.reflect.GetFun;
import com.github.ulwx.aka.dbutils.tool.support.reflect.ReflectionUtil;
import com.github.ulwx.aka.dbutils.tool.support.type.TInteger;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import com.github.ulwx.aka.dbutils.tool.support.type.TString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DataBaseImpl implements DataBase {
    private static Logger log = LoggerFactory.getLogger(DataBase.class);
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private boolean mainSlaveMode = false;
    private DataSource dataSource;
    private boolean isAutoCommit = true;
    private SQLType sqlType = SQLType.OTHER;
    private MainSlaveModeConnectMode mainSlaveModeConnectMode = MainSlaveModeConnectMode.Connect_MainServer;
    private DBMS dataBaseType;
    private int dataBaseMajorVersion = 0;
    private int dataBaseMiniVersion = 0;
    private String dbPoolName = "";
    private boolean externalControlConClose = false;
    private ConnectType connectType = null;
    private Map<String, Savepoint> savePointMap = new LinkedHashMap<>();
    private Boolean connectedToMaster=true;
    public DataBaseImpl() {
    }

    @Override
    public Boolean connectedToMaster() {
        return connectedToMaster;
    }

    private static Map<Integer, Object> toMap(Object... vParameters) {

        Map<Integer, Object> map = new HashMap<Integer, Object>();
        for (int i = 0; i < vParameters.length; i++) {
            map.put(i, vParameters[i]);
        }
        return map;

    }

    private static String getOnePName(Object p) {
        if (p == null) return null;
        if (p instanceof String) {
            return p.toString();
        } else if (p instanceof GetFun) {
            return ReflectionUtil.getFieldName((GetFun) p);
        } else if (p instanceof CGetFun) {
            return ReflectionUtil.getFieldName((CGetFun) p);
        } else {
            throw new DbException("参数[" + p + "]只支持String类型和GetFun类型");
        }
    }

    private static String[] getPNames(Object[] ps) {
        if (ps == null) {
            return null;
        }
        if (ps.length == 0) {
            if (ps.getClass().getComponentType() == String.class
                    || ps.getClass().getComponentType() == GetFun.class ||
                    ps.getClass().getComponentType() == CGetFun.class) {
                return new String[0];
            }
            throw new DbException("参数[" + ps + "]只支持String类型、GetFun类型、CGetFun类型");
        }
        String[] strs = new String[ps.length];
        for (int i = 0; i < ps.length; i++) {
            strs[i] = getOnePName(ps[i]);
        }
        return strs;
    }

    private static String[][] getPNamess(Object[][] ps) {
        if (ps == null) {
            return null;
        }
        String[][] strs = new String[ps.length][];
        for (int i = 0; i < ps.length; i++) {
            strs[i] = getPNames(ps[i]);
        }
        return strs;
    }

    public static String getCallerInf() {

        int fromLevel = 3, upLevelNum = 0;
        StackTraceElement stack[] = (new Throwable()).getStackTrace();// ;
        // 获取线程运行栈信息
        String str = "";
        if (stack.length > fromLevel) {
            for (int i = fromLevel; i < stack.length; i++) {
                if (stack[i].getClassName().startsWith(DataBase.class.getPackage().getName())
                        || stack[i].getClassName().startsWith(BaseDao.class.getPackage().getName())) {

                    continue;
                }else if(stack[i].getClassName().startsWith("jdk.internal.")
                        ||stack[i].getClassName().startsWith("java.lang.")
                        ||stack[i].getClassName().contains("CGLIB$$")
                        ||stack[i].getClassName().startsWith("org.springframework")
                ){

                    continue;
                }

                if (upLevelNum == 0) {
                    String tempInfo = "at " + stack[i].getClassName() + "." + stack[i].getMethodName() + "(" +
                            stack[i].getFileName() +
                            ":"
                            + stack[i].getLineNumber()+ "";
                    upLevelNum++;
                    str = tempInfo;
                } else {
                    String tempInfo = "" + getLastTwoClassName(stack[i].getClassName()) + "." + stack[i].getMethodName() + "(" +
                            stack[i].getFileName() +
                            ":"
                            + stack[i].getLineNumber() + ")";
                    if (upLevelNum < 3) {
                        str = tempInfo + "=>" + str;
                        upLevelNum++;
                    } else {
                        break;
                    }
                }


            }
        }
        return str;
    }

    public static String getLastTwoClassName(String srcClassName) {
        String className = srcClassName;
        int lastIndex = className.lastIndexOf(".");
        if (lastIndex < 0) {
            lastIndex = 0;
        } else {
            if (lastIndex - 1 >= 0) {
                int lastSecIndex = className.lastIndexOf(".", lastIndex - 1);
                if (lastSecIndex >= 0) {
                    lastIndex = lastSecIndex;
                } else {
                    lastIndex = 0;
                }
            }
        }
        className = className.substring(lastIndex);
        return className;
        // return srcClassName;
    }

    @Override
    public String getDbPoolName() {
        return dbPoolName;
    }

    @Override
    public boolean isMainSlaveMode() {
        return mainSlaveMode;
    }

    @Override
    public void setMainSlaveMode(boolean mainSlaveMode) {
        this.mainSlaveMode = mainSlaveMode;
    }

    public MainSlaveModeConnectMode getMainSlaveModeConnectMode() {
        return this.mainSlaveModeConnectMode;
    }

    @Override
    public boolean getInternalConnectionAutoCommit() throws DbException {
        try {

            return this.conn.getAutoCommit();
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }
    }

    private void setInternalConnectionAutoCommit(boolean autoCommit) throws DbException {
        try {

            this.conn.setAutoCommit(autoCommit);
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }
    }

    @Override
    public DBMS getDataBaseType() {
        return this.dataBaseType;
    }

    private void setDataBaseType(Connection conn) throws DbException {

        try {
            String databaseName;
            String driverName;
            int majorVersion;
            int minorVersion;
            try {
                DatabaseMetaData meta = conn.getMetaData();
                driverName = meta.getDriverName();
                databaseName = meta.getDatabaseProductName();
                majorVersion = meta.getDatabaseMajorVersion();
                minorVersion = meta.getDatabaseMinorVersion();
                this.dataBaseMajorVersion = majorVersion;
                this.dataBaseMiniVersion = minorVersion;
            } catch (Exception e) {
                if (e instanceof DbException) throw (DbException) e;
                throw new DbException(e);
            }
            this.dataBaseType = DialectClient.decideDialect(driverName, databaseName, majorVersion, minorVersion);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("get pool connection error!", e);
        }
    }

    public DataSource getDataSourceFromPool(String dbPoolName) throws DbException {

        Map<String, String> map = new HashMap<String, String>();
        String[] strs=DBPoolFactory.parseRefDbPoolName(dbPoolName);
        DataSource datasource = DBPoolFactory.getInstance(strs[0]).getDBPool(strs[1], map);

        return datasource;
    }

    @Override
    public void connectDb(Connection connection, boolean externalControlConClose) {
        this.connectType = ConnectType.CONNECTION;
        this.externalControlConClose = externalControlConClose;
        if (!this.isColsed())
            throw new DbException("原有数据库连接没有关闭,不能重新连接！");
        long start0 = System.currentTimeMillis();
        this.setMainSlaveMode(false);
        this.mainSlaveModeConnectMode = MainSlaveModeConnectMode.Connect_MainServer;
        String msg = "";
        msg = "获得数据库库链接";
        this.conn = connection;
        this.dataSource = null;
        this.dbPoolName = "";
        this.setDataBaseType(conn);
    }

    @Override
    public boolean isExternalControlConClose() {
        return this.externalControlConClose;
    }

    public void connectDb(DataSource dataSource) {
        this.connectType = ConnectType.DATASOURCE;
        this.dbPoolName = "";
        try {
            if (conn != null)
                return;
            this.setMainSlaveMode(false);
            this.mainSlaveModeConnectMode = MainSlaveModeConnectMode.Connect_MainServer;
            this.dataSource = dataSource;
            this.dbPoolName = "";

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("get pool connection error!", e);
        }
    }


    public ConnectType getConnectionType() {
        return this.connectType;
    }

    private void fetchConnection() throws DbException {
        long start0 = System.currentTimeMillis();
        String msg = "";
        this.connectedToMaster=true;
        try {
            if (this.isColsed()) {
                conn = null;
                if (this.connectType == ConnectType.POOL) {
                    if (this.mainSlaveMode) {
                        if (mainSlaveModeConnectMode == MainSlaveModeConnectMode.Connect_SlaveServer) {// 如果是主从模式，并将是连接从库
                            if (!this.getAutoCommit()) {//事务性操作
                                throw new DbException("当前操作为事务性，不能在从库上执行!!");
                            }
                            if (this.sqlType != SQLType.SELECT) {
                                throw new DbException("从库只能执行select语句执行！");
                            }
                            msg = "获取从库链接";
                            this.connectedToMaster=false;
                            TResult<Connection> tResult = new TResult<>();
                            TResult<String> tslaveName=new TResult<>();
                            String[] strs=DBPoolFactory.parseRefDbPoolName(dbPoolName);
                            DataSource ds = DBPoolFactory.getInstance(strs[0]).selectSlaveDbPool(strs[1],tslaveName, tResult);
                            this.conn = tResult.getValue();
                            this.dataSource = ds;

                        } else if (mainSlaveModeConnectMode == MainSlaveModeConnectMode.Connect_MainServer) {
                            msg = "获取主库链接";
                            this.connectedToMaster=true;
                            DataSource datasource = this.getDataSourceFromPool(dbPoolName);
                            this.dataSource = datasource;
                        } else { //Connect_Auto
                            if (this.sqlType == SQLType.SELECT) {
                                if (!this.getAutoCommit()) {//事务性操作
                                    msg = "获取主库链接";
                                    this.connectedToMaster=true;
                                    DataSource ds = this.getDataSourceFromPool(dbPoolName);
                                    this.dataSource = ds;
                                } else {
                                    msg = "获取从库链接";
                                    this.connectedToMaster=false;

                                    String[] strs=DBPoolFactory.parseRefDbPoolName(dbPoolName);
                                    TResult<Connection> tResult = new TResult<>();
                                    TResult<String> tslaveName=new TResult<>();
                                    DataSource ds = DBPoolFactory.getInstance(strs[0]).selectSlaveDbPool(strs[1],tslaveName, tResult);
                                    this.conn = tResult.getValue();
                                    this.dataSource = ds;
                                    ///
                                }
                            } else { //update、delete、存储过程,脚本在主库上执行
                                msg = "获取主库链接";
                                this.connectedToMaster=true;
                                DataSource ds = this.getDataSourceFromPool(dbPoolName);
                                this.dataSource = ds;
                            }

                        }
                    } else { //非主从库方式
                        msg = "获取主库链接";
                        this.connectedToMaster=true;
                        DataSource datasource = this.getDataSourceFromPool(dbPoolName);
                        this.dataSource = datasource;
                    }
                    if (this.isColsed()) {
                        conn = this.dataSource.getConnection();
                    }

                } else if (this.connectType == ConnectType.CONNECTION) {
                    this.connectedToMaster=null;
                    if (this.isColsed()) {
                        msg = "数据库连接为空或已经关闭！";
                    } else {
                        msg = "获取数据库链接前面已获取";
                    }
                } else if (this.connectType == ConnectType.DATASOURCE) {
                    this.connectedToMaster=null;
                    msg = "获取数据库库链接";
                    conn = dataSource.getConnection();
                } else {
                    throw new DbException("不支持的连接方式！" + this.connectType);
                }
                this.setInternalConnectionAutoCommit(this.getAutoCommit());
                this.setDataBaseType(conn);
                if (DbContext.permitDebugLog()) {
                    log.debug("fetch a new connection from [" + this.getConnectInfo()
                            + "]" + msg + ",connect time:"
                            + (System.currentTimeMillis() - start0) + " 毫秒");
                }

            }

            for (String key : this.savePointMap.keySet()) {
                if (this.savePointMap.get(key) == null) {
                    this.setRealSavepoint(key);
                }
            }
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);

        }
    }

    /**
     * 从dbpool.xml里设置的连接池获得连接,如果想指定非dbpool.xml文件里的连接池，可以按如下格式：
     * <pre>
     * 格式为：配置xml文件名称#连接池名称
     * mydbpool.xml#sysdb
     * </pre>
     * @param dbPoolName 对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                   如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @throws DbException 异常
     */
    @Override
    public void connectDb(String dbPoolName) throws DbException {

        this.connectType = ConnectType.POOL;
        this.dbPoolName = dbPoolName;
        try {
            if (conn != null)
                return;
            // 设置是否是主从模式
            String[] strs=DBPoolFactory.parseRefDbPoolName(dbPoolName);
            this.setMainSlaveMode(DBPoolFactory.getInstance(strs[0]).isMainSlaveMode(strs[1]));
            this.mainSlaveModeConnectMode = DbContext.getMainSlaveModeConnectMode();

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("get pool connection error!", e);
        }

    }

    /**
     * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用close()方法关闭
     *
     * @param sqlQuery    sql语句
     * @param vParameters 参数
     * @return 返回查询到的结果集
     * @throws DbException 异常
     */

    private DataBaseSet doCachedQuery(String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        return new DataBaseSet(doCachedQuery(sqlQuery, args));
    }

    /**
     * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用close()方法关闭 连接
     *
     * @param sqlQuery  SQL语句
     * @param vParameters 参数
     * @param page        当前页
     * @param perPage     每页多少行
     * @param pageBean    返回的分页信息
     * @param countSql    查询总数的sql语句，根据它查询总数；也可以指定一个整数字符串，用于指定总数；
     *                    如果指定null或""字符串，那么系统会根据sqlQuery自动生成查询总数sql语句
     * @return 返回DataBaseSet对象
     * @throws DbException 异常
     */
    @Override
    public DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                                         PageBean pageBean, String countSql) throws DbException {
        DataBaseSet ret = null;
        this.configInterceptorForMehtod("queryForResultSet", String.class, Map.class,
                int.class, int.class, PageBean.class, String.class);
        try {
            Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
            return ret = new DataBaseSet(
                    this.doCachedPageQuery(sqlQuery, args, page, perPage, pageBean, countSql));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        DataBaseSet ret = null;
        this.configInterceptorForMehtod("queryForResultSet", String.class, Map.class);
        try {
            Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
            return ret = new DataBaseSet(doCachedQuery(sqlQuery, args));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用close()方法关闭 连接
     *
     * @param <T>       需映射的类
     * @param sqlQuery  sql查询语句
     * @param args      sql查询语句里的参数
     * @param page      当前页，从第一页开始
     * @param perPage   每页多少行
     * @param pageBean  返回分页信息
     * @param rowMapper 映射接口，用户可以通过此接口的回调函数来执行映射
     * @param countSql  查询总数的sql语句，根据它查询总数；也可以指定一个整数字符串，用于指定总数；
     *                  如果指定null或""字符串，那么系统会根据sqlQuery自动生成查询总数sql语句
     * @return
     * @throws DbException
     */

    private <T> List<T> doPageQueryObject(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
                                          PageBean pageBean, RowMapper<T> rowMapper, String countSql) throws DbException {

        Collection<Object> vParameters = CollectionUtils.getSortedValues(args);

        String pageSql = this.getPagedSql(sqlQuery, vParameters, page, perPage, pageBean, countSql);
        if (StringUtils.isEmpty(pageSql)) {
            return new ArrayList<T>();
        }
        return this.doQueryObject(pageSql, vParameters, rowMapper);

    }

    @Override
    public <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageBean,
                                 RowMapper<T> rowMapper, String countSql) throws DbException {
        this.configInterceptorForMehtod("queryList", String.class, Map.class,
                int.class, int.class, PageBean.class, RowMapper.class, String.class);
        List<T> ret = null;
        try {
            return ret = this.doPageQueryObject(sqlQuery, args, page, perPage, pageBean, rowMapper, countSql);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    private List<Map<String, Object>> doPageQueryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
                                                     PageBean pageBean, String countSql) throws DbException {

        List<Map<String, Object>> list = doPageQueryObject(sqlQuery, args, page, perPage, pageBean,
                new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(DataBaseSet rs) throws Exception {
                        return ObjectUtils.getMapFromResultSet(rs.getResultSet());

                    }
                }, countSql);
        return list;

    }

    @Override
    public List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
                                              PageBean pageBean, String countSql) throws DbException {
        this.configInterceptorForMehtod("queryMap", Map.class,
                int.class, int.class, PageBean.class, String.class);
        List<Map<String, Object>> ret = null;
        try {
            return ret = this.doPageQueryMap(sqlQuery, args, page, perPage, pageBean, countSql);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    private CachedRowSet doCachedPageQuery(String sqlQuery, Collection<Object> vParameters, int page, int perPage,
                                           PageBean pageBean, String countSql) throws DbException {

        CachedRowSet crs = null;
        try {
            String pageSql = this.getPagedSql(sqlQuery, vParameters, page, perPage,
                    pageBean, countSql);
            if (StringUtils.isEmpty(pageSql)) {
                return new CachedRowSetImpl();
            }
            ResultSet rs = (this.doQuery(pageSql, vParameters)).getResultSet();
            this.rs = rs;

            crs = this.getCachedRowSet(rs);

        } catch (Exception e) {
            // log.error("", e);
            crs = null;
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            try {
                if (this.conn != null) {
                    if (this.getAutoCommit()) {
                        this.close();
                    } else {
                        this.closer();
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return crs;

    }

    private CachedRowSet doCachedQuery(String sqlQuery, Collection<Object> vParameters)
            throws DbException {
        return this.doCachedQuery(sqlQuery, vParameters, false);
    }

    /**
     * @param sqlQuery
     * @param vParameters
     * @param internalUse:是否是内部使用，如果是内部使用则不关闭连接
     * @return
     * @throws DbException
     */
    private CachedRowSet doCachedQuery(String sqlQuery, Collection<Object> vParameters, boolean internalUse)
            throws DbException {

        CachedRowSet crs = null;
        try {

            crs = new CachedRowSetImpl();
            ResultSet rs = (this.doQuery(sqlQuery, vParameters)).getResultSet();
            this.rs = rs;

            crs.populate(rs);

        } catch (Exception e) {
            // log.error("", e);
            crs = null;
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            try {
                if (this.conn != null) {
                    if (internalUse) {
                        this.closer();
                    } else {
                        if (this.getAutoCommit()) {
                            this.close();
                        } else {
                            this.closer();
                        }
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return crs;

    }

    /**
     * 调用此方法不会关闭底层连接和结果集,需要手动调用close()方法关闭
     *
     * @param sqlQuery    sql语句
     * @param vParameters 参数
     * @return 返回在线结果集
     */
    private DataBaseSet doQuery(String sqlQuery, Map<Integer, Object> vParameters) throws DbException {

        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        return this.doQuery(sqlQuery, args);
    }

    /**
     * 调用此方法会默认自动关闭底层数据库连接，返回查询到的T对象列表
     *
     * @param clazz       需映射的类
     * @param sqlQuery    sql语句
     * @param vParameters 参数
     * @return
     */

    private <T> List<T> doQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters)
            throws DbException {
        return this.doQueryClassOne2One(clazz, sqlQuery, vParameters, (One2OneMapNestOptions) null);

    }

    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        List<T> ret = null;
        this.configInterceptorForMehtod("queryList", Class.class, String.class, Map.class);
        try {
            return ret = this.doQueryClass(clazz, sqlQuery, vParameters);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    private <T> T doQueryClassOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {

        List<T> list = this.doQueryClass(clazz, sqlQuery, vParameters);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;

    }

    @Override
    public <T> T queryOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        this.configInterceptorForMehtod("queryOne", Class.class, String.class, Map.class);
        T ret = null;
        try {
            return ret = this.doQueryClassOne(clazz, sqlQuery, vParameters);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    private <T> List<T> doQueryClassForObject(Class<T> clazz, T selectObject, String[] whereProperteis, QueryOptions queryOptions) throws DbException {
        String sql = "";
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = selectObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.SELECT;
            this.fetchConnection();
            sql = SqlUtils.generateSelectSql(this.dbPoolName, selectObject, reflectClass, whereProperteis, vParameters,
                    queryOptions, this.getDataBaseType());
            DbContext.clearReflectClass();
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
        return doQueryClass(clazz, sql, vParameters);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> doQueryClassForObject(T selectObject, QueryOptions options) throws DbException {
        String sql = "";
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = selectObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.SELECT;
            this.fetchConnection();
            sql = SqlUtils.generateSelectSqlBySelectObject(this.dbPoolName, selectObject, reflectClass, vParameters,
                    options, this.getDataBaseType());
            DbContext.clearReflectClass();
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
        return doQueryClass((Class<T>) selectObject.getClass(), sql, vParameters);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> doQueryClassForObject(T selectObject, String[] whereProperteis, QueryOptions queryOptions) throws DbException {

        return this.doQueryClassForObject((Class<T>) selectObject.getClass(), selectObject, whereProperteis, queryOptions);
    }

    /**
     * 一到一关联映射查询，调用此方法会默认自动关闭底层数据库连接
     * <blockquote><pre>
     * String sql = "select n.*,p.*,a.* from news n left outer join photos p on
     * n.id=p.newsid  left outer join archives a  on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc";
     * </pre></blockquote>
     * 如果sqlPrefix指定"n."，clazz指定New.class，则对n.*所属的字段会映射到New类对象
     *
     * @param clazz                 指定映射父对象所属的类
     * @param sqlQuery              sql语句
     * @param vParameters           参数
     * @param one2OneMapNestOptions 一到一映射关系配置对象，可以有多个映射关系，一个映射关系对应一个QueryMapNestOne2One对象
     * @param <T>
     * @return 返回查询到的列表
     * @throws DbException
     */
    private <T> List<T> doQueryClassOne2One(Class<T> clazz, String sqlQuery,
                                            Map<Integer, Object> vParameters,
                                            One2OneMapNestOptions one2OneMapNestOptions) throws DbException {

        if (one2OneMapNestOptions != null) {
            Assert.hasText(one2OneMapNestOptions.getSqlPrefix());
            Assert.notEmpty(one2OneMapNestOptions.getQueryMapNestOne2Ones());
            QueryMapNestOne2One[] queryMapNestList = one2OneMapNestOptions.getQueryMapNestOne2Ones();
            String sqlPrefix = one2OneMapNestOptions.getSqlPrefix();
            return this.doQueryClass(clazz, sqlQuery, vParameters, queryMapNestList, sqlPrefix, null);
        } else {
            return this.doQueryClass(clazz, sqlQuery, vParameters, (QueryMapNestOne2One[]) null,
                    null, null);
        }

    }

    @Override
    public <T> List<T> queryListOne2One(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                        One2OneMapNestOptions one2OneMapNestOptions) throws DbException {
        this.configInterceptorForMehtod("queryListOne2One",
                Class.class, String.class, Map.class, One2OneMapNestOptions.class);
        List<T> ret = null;
        try {
            return ret = this.doQueryClassOne2One(clazz, sqlQuery, vParameters, one2OneMapNestOptions);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用close()方法关闭连接
     *
     * @param clazz       指定映射父对象所属的类
     * @param sqlQuery    sql语句
     * @param vParameters 参数
     * @param page        当前页，从第一页开始
     * @param perPage     每页多少行
     * @param pageBean    返回分页信息的类
     * @param countSql    总行数的查询语句，也可以为一个整数字符串，表明总行数是多少，
     *                    如果为null或""，系统为自动根据sqlQuery字符串生产总行数的查询语句
     * @return
     * @throws DbException
     */
    private <T> List<T> doPageQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page,
                                         int perPage, PageBean pageBean, String countSql) throws DbException {
        return this.doPageQueryClass(clazz, null, sqlQuery, vParameters, (QueryMapNestOne2One[]) null, page, perPage,
                pageBean, countSql);
    }

    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlQuery,
                                 Map<Integer, Object> vParameters,
                                 int page,
                                 int perPage,
                                 PageBean pageBean,
                                 String countSql) throws DbException {

        List<T> ret = null;
        this.configInterceptorForMehtod("queryList", Class.class, String.class, Map.class,
                int.class, int.class, PageBean.class, String.class);
        try {
            return ret = this.doPageQueryClass(clazz, sqlQuery, vParameters, page, perPage, pageBean, countSql);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用close()方法关闭 连接
     *
     * @param <T>
     * @param clazz            指定映射父对象所属的类
     * @param sqlPrefix        主类对应的sql前缀
     * @param sqlQuery         sql语句
     * @param vParameters      参数
     * @param queryMapNestList 一到一映射关系
     * @param page             当前页，从第一页开始
     * @param perPage          每页多少行
     * @param pageBean         返回分页信息的类
     * @param countSql         总行数的查询语句，也可以为一个整数字符串，表明总行数是多少，
     *                         如果为null或""，系统为自动根据sqlQuery字符串生产总行数的查询语句
     * @return
     * @throws DbException
     */
    private <T> List<T> doPageQueryClass(Class<T> clazz, String sqlPrefix, String sqlQuery,
                                         Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                                         PageBean pageBean, String countSql) throws DbException {
        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);

        String pageSql = this.getPagedSql(sqlQuery, args, page, perPage, pageBean, countSql);
        if (StringUtils.isEmpty(pageSql)) {
            return new ArrayList<T>();
        }
        return this.doQueryClass(clazz, pageSql, vParameters, queryMapNestList, sqlPrefix, null);

    }

    @Override
    public <T> List<T> queryListOne2One(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                        One2OneMapNestOptions one2OneMapNestOptions,
                                        int page, int perPage, PageBean pageBean, String countSql)
            throws DbException {
        Assert.notNull(one2OneMapNestOptions);
        Assert.hasText(one2OneMapNestOptions.getSqlPrefix());
        Assert.notEmpty(one2OneMapNestOptions.getQueryMapNestOne2Ones());
        this.configInterceptorForMehtod("queryListOne2One",
                Class.class, String.class, Map.class, One2OneMapNestOptions.class,
                int.class, int.class, PageBean.class, String.class);
        List<T> ret = null;
        try {
            return ret = this.doPageQueryClass(clazz, one2OneMapNestOptions.getSqlPrefix(),
                    sqlQuery, vParameters, one2OneMapNestOptions.getQueryMapNestOne2Ones(), page, perPage,
                    pageBean, countSql);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用close()方法关闭 连接
     *
     * @param <T>
     * @param clazz            指定映射父对象所属的类
     * @param sqlPrefix        主类对应的sql前缀
     * @param sqlQuery         sql语句
     * @param vParameters      参数
     * @param queryMapNestList 一到一映射关系
     * @param page             当前页，从第一页开始
     * @param perPage          每页多少行
     * @param pageBean         返回分页信息的类
     * @param countSql         总行数的查询语句，也可以为一个整数字符串，表明总行数是多少，
     *                         如果为null或""，系统为自动根据sqlQuery字符串生产总行数的查询语句
     * @return
     * @throws DbException
     */
    private <T> List<T> doPageQueryClassOne2One(Class<T> clazz, String sqlPrefix, String sqlQuery,
                                                Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                                                PageBean pageBean, String countSql) throws DbException {
        return this.doPageQueryClass(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList, page, perPage,
                pageBean, countSql);

    }

    /**
     * 一到多关联映射查询。
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param clazz            指定映射父对象所属的类
     * @param sqlPrefix        sql前缀
     * @param parentBeanKeys   对象对应的主键属性名。
     * @param sqlQuery         sql语句
     * @param vParameters      参数
     * @param queryMapNestList 关联子类关系
     * @return 返回查询结果
     * @throws DbException
     */
    private <T> List<T> doQueryClassOne2Many(Class<T> clazz, String sqlPrefix, String[] parentBeanKeys, String sqlQuery,
                                             Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException {

        List<T> list = this.doQueryClass(clazz, sqlQuery, vParameters, queryMapNestList, sqlPrefix, parentBeanKeys);
        // 过滤，并进行子类赋值
        for (int m = 0; m < list.size(); m++) {
            Object bean = list.get(m);
            String parentKeys = BeanUtils.getKeyValue(bean, parentBeanKeys);

            for (int i = 0; i < queryMapNestList.length; i++) {

                QueryMapNestOne2Many query2Many = queryMapNestList[i];

                String toPropertyName = query2Many.getToPropertyName();
                Map<String, LinkedHashMap<String, Object>> result = query2Many.getResult();

                LinkedHashMap<String, Object> childList = result.get(parentKeys);
                if (childList != null) {
                    List<Object> nlist = new ArrayList<>();
                    nlist.addAll(childList.values());
                    PropertyUtil.setSimpleProperty(bean, toPropertyName, nlist);
                }

            }

        }

        return list;

    }

    @Override
    public <T> List<T> queryListOne2Many(Class<T> clazz, String sqlQuery,
                                         Map<Integer, Object> vParameters,
                                         One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException {
        Assert.notNull(one2ManyMapNestOptions);
        Assert.hasText(one2ManyMapNestOptions.getSqlPrefix());
        Assert.notEmpty(one2ManyMapNestOptions.getQueryMapNestOne2Manys());
        Assert.notEmpty(one2ManyMapNestOptions.getParentBeanKeys());
        List<T> ret = null;
        this.configInterceptorForMehtod("queryListOne2Many",
                Class.class, String.class, Map.class, One2ManyMapNestOptions.class);
        try {
            return ret = this.doQueryClassOne2Many(clazz, one2ManyMapNestOptions.getSqlPrefix(),
                    one2ManyMapNestOptions.getParentBeanKeys(), sqlQuery, vParameters, one2ManyMapNestOptions.getQueryMapNestOne2Manys());
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    private <T> List<T> doQueryObject(String sqlQuery, Collection<Object> vParameters,
                                      RowMapper<T> rowMapper) throws DbException {

        boolean close = this.getAutoCommit();

        List<T> results = new ArrayList();
        try {

            DataBaseSet dbset = this.getResultSet(sqlQuery, vParameters);
            this.rs = dbset.getResultSet();
            int index = 0;
            T obj = null;

            while (dbset.next()) {
                obj = rowMapper.mapRow(dbset);
                results.add(obj);
            }
            return results;
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            try {
                if (this.conn != null) {
                    if (close) {
                        this.close();
                    } else {
                        this.closer();
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
    }

    /**
     * 自定义映射查询。此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>       需映射的类
     * @param sqlQuery  sql语句
     * @param args      参数
     * @param rowMapper 映射接口，用户可以通过此接口的回调函数来执行映射
     * @return
     * @throws DbException
     */
    private <T> List<T> doQueryObject(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper)
            throws DbException {

        Collection<Object> vParameters = CollectionUtils.getSortedValues(args);

        return this.doQueryObject(sqlQuery, vParameters, rowMapper);

    }

    @Override
    public <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException {
        this.configInterceptorForMehtod("queryList", String.class, Map.class, RowMapper.class);
        List<T> ret = null;
        try {
            return ret = this.doQueryObject(sqlQuery, args, rowMapper);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    private List<Map<String, Object>> doQueryMap(String sqlQuery, Map<Integer, Object> args) throws DbException {

        List<Map<String, Object>> list = doQueryObject(sqlQuery, args, new RowMapper<Map<String, Object>>() {

            @Override
            public Map<String, Object> mapRow(DataBaseSet rs) throws Exception {

                return ObjectUtils.getMapFromResultSet(rs.getResultSet());

            }
        });

        return list;

    }

    @Override
    public List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args) throws DbException {
        this.configInterceptorForMehtod("queryMap", Map.class);
        List<Map<String, Object>> ret = null;
        try {
            return ret = this.doQueryMap(sqlQuery, args);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    private <T> List<T> doQueryClass(Class<T> clazz,
                                     String sqlQuery,
                                     Map<Integer, Object> vParameters,
                                     QueryMapNestOne2One[] queryMapNestList,
                                     String sqlPrefix,
                                     String[] parentBeanKeys) throws DbException {

        ArrayList<T> list = new ArrayList<>();
        Map<String, T> keyMapList = new LinkedHashMap<>();
        if (sqlPrefix == null)
            sqlPrefix = "";
        try {
            DataBaseSet rs = this.doQuery(sqlQuery, vParameters);
            this.rs = rs.getResultSet();
            int index = 0;
            while (rs.next()) {
                try {
                    T bean = clazz.getDeclaredConstructor().newInstance();
                    Map<String, TResult2<Class, Object>> map = PropertyUtil.describeForTypes(bean, bean.getClass());
                    Set<?> set = map.keySet();
                    Iterator<?> i = set.iterator();
                    while (i.hasNext()) {

                        String name = (String) i.next();
                        TResult2<Class, Object> tr2 = map.get(name);
                        Class<?> t = tr2.getFirstValue();
                        Object value = null;
                        // name为javabean属性名
                        if (SqlUtils.checkedSimpleType(t)) {// 简单类型
                            value = SqlUtils.getValueFromResult(this.dbPoolName,clazz, t, sqlPrefix, name, rs.getResultSet(),
                                    DataBaseKeyMap.getMap());

                            PropertyUtil.setProperty(bean, name, value);

                        } else {

                            // 映射类型
                            for (int m = 0; queryMapNestList != null && m < queryMapNestList.length; m++) {

                                MapNest queryMapNest = queryMapNestList[m];
                                int mapRelation = queryMapNest.getMapRelation();
                                String toPropertyName = queryMapNest.getToPropertyName();
                                String prefix = queryMapNest.getPrefix();
                                if (prefix == null)
                                    prefix = "";
                                String[] toPros = queryMapNest.getToChildPros();

                                if (mapRelation == MapNest.ONE_TO_ONE) {

                                    if (name.equals(toPropertyName)) {
                                        Class<?> nestedClass = PropertyUtil.getPropertyType(bean, toPropertyName);

                                        Object nestedObj = nestedClass.getDeclaredConstructor().newInstance();

                                        nestedObj = BeanUtils.setOne2One(this.dbPoolName, nestedObj, toPros, prefix,
                                                rs);
                                        PropertyUtil.setProperty(bean, toPropertyName, nestedObj);

                                    } else {// 表明不进行映射的属性

                                    }
                                } else {// 表明不进行映射的属性

                                    throw new DbException("此方法不支持一到多管理映射！");
                                }

                            }

                        }

                    } // while

                    if (parentBeanKeys == null) {
                        list.add(bean);
                    } else {
                        String kv = BeanUtils.getKeyValue(bean, parentBeanKeys);
                        keyMapList.put(kv, bean);
                    }

                } catch (Exception e) {
                    if (e instanceof DbException) throw (DbException) e;
                    throw new DbException(e);

                }
            } // while

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);

        } finally {
            try {
                if (this.conn != null) {
                    if (this.getAutoCommit()) {
                        this.close();
                    } else {
                        this.closer();
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        if (parentBeanKeys == null) {
            return list;
        } else {
            Collection<T> coValues = keyMapList.values();
            list.clear();
            list.addAll(coValues);
            return list;
        }

    }

    private <T> List<T> doQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                     QueryMapNestOne2Many[] queryMapNestList, String sqlPrefix, String[] parentBeanKeys)
            throws DbException {

        ArrayList<T> list = new ArrayList<T>();
        Map<String, T> keyList = new LinkedHashMap<>();
        if (sqlPrefix == null)
            sqlPrefix = "";
        try {

            DataBaseSet rs = this.doQuery(sqlQuery, vParameters);
            this.rs = rs.getResultSet();
            while (rs.next()) {
                try {

                    T bean = clazz.getDeclaredConstructor().newInstance();
                    Map<String, TResult2<Class, Object>> map = PropertyUtil.describeForTypes(bean, bean.getClass());
                    Set<?> set = map.keySet();
                    Iterator<?> i = set.iterator();

                    while (i.hasNext()) {

                        String name = (String) i.next();
                        TResult2<Class, Object> re2 = map.get(name);

                        Class<?> t = re2.getFirstValue();
                        Object value = null;

                        // name为javabean属性名
                        if (SqlUtils.checkedSimpleType(t)) {// 简单类型

                            value = SqlUtils.getValueFromResult(this.dbPoolName,clazz, t, sqlPrefix, name, rs.getResultSet(),
                                    DataBaseKeyMap.getMap());
                            PropertyUtil.setProperty(bean, name, value);
                        } else {

                            // 映射类型
                            for (int m = 0; queryMapNestList != null && m < queryMapNestList.length; m++) {

                                MapNest queryMapNest = queryMapNestList[m];
                                int mapRelation = queryMapNest.getMapRelation();
                                String toPropertyName = queryMapNest.getToPropertyName();
                                String prefix = queryMapNest.getPrefix();
                                if (prefix == null)
                                    prefix = "";
                                String[] toPros = queryMapNest.getToChildPros();

                                if (mapRelation == MapNest.ONE_TO_MANY) {
                                    if (name.equals(toPropertyName)) {
                                        QueryMapNestOne2Many queryMapNestMany = (QueryMapNestOne2Many) queryMapNest;
                                        Map<String, LinkedHashMap<String, Object>> result = queryMapNestMany
                                                .getResult();
                                        String[] nestedBeanPropertyKeys = queryMapNestMany.getNestedBeanPropertyKeys();

                                        Class<?> nestedClass = queryMapNestMany.getNestedClassType();
                                        Object nestedObj = nestedClass.getDeclaredConstructor().newInstance();

                                        nestedObj = BeanUtils.setOne2One(this.dbPoolName, nestedObj, toPros, prefix,
                                                rs);

                                        String parentKeyValue = BeanUtils.getKeyValue(this.dbPoolName, bean, sqlPrefix,
                                                parentBeanKeys, rs);
                                        LinkedHashMap<String, Object> nestlist = result.get(parentKeyValue);

                                        if (nestlist == null) {
                                            nestlist = new LinkedHashMap<String, Object>();
                                            result.put(parentKeyValue, nestlist);
                                        }
                                        if (nestedBeanPropertyKeys != null && nestedBeanPropertyKeys.length > 0) {// //过滤重复的值
                                            String nestKeyValueStr = BeanUtils.getKeyValue(nestedObj,
                                                    nestedBeanPropertyKeys);
                                            if (nestKeyValueStr != null) {
                                                Object find = nestlist.get(nestKeyValueStr);
                                                if (find == null) {
                                                    nestlist.put(nestKeyValueStr, nestedObj);
                                                }
                                            }
                                        } else {
                                            nestlist.put(nestlist.size() + "", nestedObj);
                                        }

                                    }
                                } else {// 表明不进行映射的属性

                                    throw new DbException("此方法不支持一到一关联映射！");
                                }

                            }

                        }

                    } // while

                    if (parentBeanKeys == null) {
                        list.add(bean);
                    } else {
                        String kv = BeanUtils.getKeyValue(bean, parentBeanKeys);
                        keyList.put(kv, bean);
                    }

                } catch (Exception e) {
                    if (e instanceof DbException) throw (DbException) e;
                    throw new DbException(e);

                }
            } // while
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);

        } finally {
            try {
                if (this.conn != null) {
                    if (this.getAutoCommit()) {
                        this.close();
                    } else {
                        this.closer();
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        if (parentBeanKeys == null) {
            return list;
        } else {
            Collection coValues = keyList.values();
            list.clear();
            list.addAll(coValues);
            return list;
        }

    }

    public static void main(String[] args) {

    }

    private static String trimTailOrderBy(String sql, TString orderStr) {
        sql = sql.trim();
        String newSql = "";
        String reg = "order\\s+by\\s+\\S+(\\s+(asc|desc))?";
        TInteger startPos = new TInteger();
        boolean isEnd = StringUtils.endsWith(sql, reg, true, startPos);
        if (isEnd) {
            if (orderStr != null) {
                orderStr.setValue(sql.substring(startPos.getValue()));
            }
            newSql = sql.substring(0, startPos.getValue());
        } else {
            if (orderStr != null) {
                orderStr.setValue("");
            }
            newSql = sql;
        }
        return newSql;
    }

    private String getCountSql(String sqlQuery) throws Exception {

        if (sqlQuery == null)
            return null;
        String sql = sqlQuery.trim();

        String maxSql = "";
        sqlQuery = sqlQuery.trim();

        sqlQuery = trimTailOrderBy(sqlQuery, new TString());

        StringBuilder sb = new StringBuilder(sqlQuery);
        if (!StringUtils.startsWithIgnoreCase(sqlQuery, "select")) {
            throw new DbException("语句不是select查询语句！");
        }
        if (this.dataBaseType.isSQLServerFamily()) {

            String regx = "select\\s+top";

            TInteger tend = new TInteger();
            boolean hasTop = StringUtils.startsWith(sb.toString(), regx, true);

            if (!hasTop) {
                int from = StringUtils.indexOf(sql, "from", true);
                String regx2 = "select\\s+distinct";
                if (StringUtils.startsWith(sql, regx2, true)) {
                    maxSql = "select count(1) from (" + sb.toString() + ") t";
                } else {

                    String tempStr = sb.substring(6, from);
                    maxSql = sb.replace(6, from, " count(*) ").toString();
                } // select is 6 characters
            } else {
                maxSql = "select count(1) from (" + sb.toString() + ") t";
            }

        } else if (this.dataBaseType.isMySqlFamily()) {
            maxSql = "select count(1) from (" + sb.toString() + ") t";
        } else {
            maxSql = "select count(1) from (" + sb.toString() + ") t";
        }

        return maxSql;

    }

    private String getPagedSql(String sqlQuery, Collection<Object> vParameters, int page, int perPage,
                               PageBean pageBean, String countSql) throws DbException {

        if (sqlQuery == null)
            return null;
        String sql = sqlQuery.trim();

        int total = 0;

        try {
            this.sqlType = SQLType.SELECT;
            this.fetchConnection();

            String maxSql = countSql;
            maxSql = StringUtils.trim(maxSql);
            String reg = "\\d+";
            if (maxSql.matches(reg)) {// 说明是数字
                total = Integer.valueOf(maxSql);
            } else if (maxSql.equals("-1")) {//最大行数未知
                total = -1;
            } else {
                if (StringUtils.isEmpty(maxSql)) {//自动生成查询总行数的SQL语句
                    String tsql = this.trimTailOrderBy(sqlQuery, new TString());
                    maxSql = this.getCountSql(tsql);

                } else {// 说明指定了查询总行数的sql语句
                    maxSql = this.trimTailOrderBy(maxSql, new TString());
                }

                RowSet rs = this.doCachedQuery(maxSql, vParameters, true);// 不关闭数据库连接

                while (rs != null && rs.next()) {
                    total = rs.getInt(1);
                    break;
                }
            }

            pageBean.doPage(total, page, perPage);

            if (pageBean.isEmpty()) {
                return "";
            }
            sql = SqlUtils.getPageSql(sqlQuery, pageBean.getPage(), pageBean.getPerPage(),
                    this.dataBaseType);

            return sql;

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }


    private CachedRowSet getCachedRowSet(ResultSet rs) throws Exception {

        CachedRowSet crs = new CachedRowSetImpl();
        crs.populate(rs);
        return crs;
    }

    private DataBaseSet getResultSet(String sqlQuery, Collection<Object> vParameters) throws Exception {

        PreparedStatement preStmt = this.getPreparedStatement(sqlQuery);
        String paramStr = SqlUtils.setToPreStatment(vParameters, preStmt);
        String debugSql = SqlUtils.generateDebugSql(sqlQuery, vParameters, this.getDataBaseType());
        if (DbContext.getDebugSQLListener() != null) {
            DbContext.getDebugSQLListener().accept(debugSql);
        }

        if(DbContext.getDBInterceptor()!=null){
            DbContext.getDBInterceptorInfo().setDebugSql(debugSql);
            boolean ret=DbContext.getDBInterceptor().beforeDbOperationExeute(DataBaseImpl.this,false,debugSql);
            if(!ret){
                throw new DbException("被拦截", CODE.Intercepted);
            }
        }
        if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
            String line = System.getProperty("line.separator");
            log.debug("call info[" + getConnectInfo() + "][" + getCallerInf() + "]");
            log.debug("debugSql[" + debugSql + "]");

        }
        long start = System.currentTimeMillis();
        this.rs = preStmt.executeQuery();
        if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
            log.debug("sql执行:" + (System.currentTimeMillis() - start));
        }

        return new DataBaseSet(this.rs);
    }

    private String getConnectInfo() {
        String connectInfo = "";
        if (this.connectType == ConnectType.CONNECTION) {
            connectInfo = ConnectType.CONNECTION + ":";
        } else if (this.connectType == ConnectType.DATASOURCE) {
            connectInfo = ConnectType.DATASOURCE + ":" + this.dataSource + "";
        } else if ((this.connectType == ConnectType.POOL)) {
            connectInfo = ConnectType.POOL + ":" + this.dbPoolName;
        }
        return connectInfo;
    }

    /**
     * 不关闭底层数据库连接
     *
     * @param sqlQuery
     * @param vParameters
     * @return
     * @throws DbException
     */
    private DataBaseSet doQuery(String sqlQuery, Collection<Object> vParameters) throws DbException {

        DataBaseSet result = null;
        try {

            result = this.getResultSet(sqlQuery, vParameters);

        } catch (Exception e) {
            result = null;
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);

        } finally {

        }

        return result;
    }

    /**
     * 删除
     *
     * @param sqltext     sql语句
     * @param vParameters 参数
     * @return
     * @throws DbException
     */
    private int executeBindDelete(String sqltext, Map<Integer, Object> vParameters) throws DbException {

        return this.executeBindUpdate(sqltext, vParameters);

    }

    @Override
    public int del(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        this.configInterceptorForMehtod("del", String.class, Map.class);
        Integer ret = null;
        try {
            return ret = this.executeBindDelete(sqltext, vParameters);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }
    }

    /**
     * 更新操作（包括删除）
     *
     * @param sqltext     sql语句
     * @param vParameters 参数
     * @return
     * @throws DbException
     */
    private int executeBindUpdate(String sqltext, Map<Integer, Object> vParameters) throws DbException {

        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        long[] ret = this.executeBindUpdate(sqltext, args);
        return (int) ret[0];

    }

    @Override
    public int update(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        Integer ret = null;
        this.configInterceptorForMehtod("update", String.class, Map.class);
        try {
            return ret = this.executeBindUpdate(sqltext, vParameters);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    /**
     * 调用存储过程，用法如下：
     * <code><pre>
     * //parms参数可以按如下形式添加
     * parms.put("1","中");//默认为in类型
     * parms.put("2:in","国");
     * parms.put("3:in",new Integer(3));
     * parms.put("4:out",int.class);
     * parms.put("5:out",java.util.data.class);
     * parms.put("6:inout",new Long(44));
     *
     * //outPramsValues存放输出参数的返回值，与parms(输入参数)里的out和inout类型对应，
     * //上面的例子产生的输出参数如下：
     * {
     *   4:45556,
     *   5:"2015-09-23 12:34:56"
     *   6:34456
     * }</pre></code>
     *
     * @param sqltext            sql语句
     * @param parms              用法举例如下：
     * @param outPramsValues     存放输出参数的返回值，与parms(输入参数)里的out和inout类型对应
     * @param returnDataBaseSets 需返回值的结果集
     * @return
     * @throws DbException
     */

    private void executeStoredProcedure(String sqltext, Map<String, Object> parms,
                                        Map<Integer, Object> outPramsValues,
                                        List<DataBaseSet> returnDataBaseSets) throws DbException {
        boolean close = true;
        try {

            close = this.getAutoCommit();

            Set<String> keys = new HashSet<String>();
            if (parms != null) {
                keys = parms.keySet();
            }
            List<String> keyList = new ArrayList<String>();
            keyList.addAll(keys);
            Collections.sort(keyList, new Comparator<String>() {

                public int compare(String o1, String o2) {

                    String[] ss1 = o1.split(":|：");
                    Assert.notEmpty(ss1);
                    String[] ss2 = o2.split(":|：");
                    Assert.notEmpty(ss2);

                    return Integer.valueOf(ss1[0]).compareTo(Integer.valueOf(ss2[0]));
                }

            });

            Map<Integer, Object> inParms = new HashMap<Integer, Object>();
            Map<Integer, Object> outParms = new HashMap<Integer, Object>();

            for (int i = 0; i < keyList.size(); i++) {// 分离输入参数和输出参数
                String key = keyList.get(i);
                String[] ss = key.split(":|：");

                Assert.state(ss != null && ss[0].matches("\\d+"));

                if (ss.length == 1 || ss[1].trim().equalsIgnoreCase("in")) {
                    inParms.put(Integer.valueOf(ss[0].trim()), parms.get(key));
                } else if (ss[1].trim().equalsIgnoreCase("inout")) {
                    inParms.put(Integer.valueOf(ss[0].trim()), parms.get(key));
                    outParms.put(Integer.valueOf(ss[0].trim()), parms.get(key));
                } else if (ss[1].trim().equalsIgnoreCase("out")) {
                    outParms.put(Integer.valueOf(ss[0].trim()), parms.get(key));
                } else {
                    log.error("int paramaters:" + ss[1] + " 不能解析!");
                    throw new DbException("int paramaters:" + ss[1] + " 不能解析!");
                }
            }

            CallableStatement cs = (CallableStatement) this.getPreparedStatement(sqltext);
            // 设置输入参数
            String inParamStr = SqlUtils.setToPreStatment(inParms, cs);

            // 注册输出参数
            String outParamStr = SqlUtils.registForStoredProc(outParms, cs);

            Map<Integer, Object> inOutParms = new HashMap<Integer, Object>();
            inOutParms.putAll(inParms);
            inOutParms.putAll(outParms);
            Collection<Object> args = CollectionUtils.getSortedValues(inOutParms);
            String debugSql = SqlUtils.generateDebugSql(sqltext, args, this.getDataBaseType());
            if (DbContext.getDebugSQLListener() != null) {
                DbContext.getDebugSQLListener().accept(debugSql);
            }

            if(DbContext.getDBInterceptor()!=null){
                DbContext.getDBInterceptorInfo().setDebugSql(debugSql);
                boolean ret=DbContext.getDBInterceptor().beforeDbOperationExeute(DataBaseImpl.this,false,debugSql);
                if(!ret){
                    throw new DbException("被拦截", CODE.Intercepted);
                }
            }
            if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
                String line = System.getProperty("line.separator");
                log.debug(
                        "sql [" + sqltext + "]" + " in param[ " + inParamStr + " ]" + "out param[" + outParamStr + "]");
                log.debug("[" + getConnectInfo() + "][" + getCallerInf() + "]");
                log.debug("debugSql[" + debugSql + "]");

            }
            boolean flag = cs.execute();

            ResultSet rs = null;
            int updateCount = -1;
            if (returnDataBaseSets != null) {
                do {
                    updateCount = cs.getUpdateCount();
                    if (updateCount != -1) {// 说明当前行是一个更新计数
                        // 不进行处理.
                        cs.getMoreResults();
                        continue;// 已经是更新计数了,处理完成后应该移动到下一行,不再判断是否是ResultSet
                    }
                    rs = cs.getResultSet();
                    if (rs != null) {// 如果到了这里,说明updateCount == -1
                        // 处理rs
                        CachedRowSet crs = new CachedRowSetImpl();
                        try {
                            crs.populate(rs);
                        } finally {
                            rs.close();
                        }
                        returnDataBaseSets.add(new DataBaseSet(crs));
                        cs.getMoreResults();
                        continue;
                        // 是结果集,处理完成后应该移动到下一行
                    }

                } while (!(updateCount == -1 && rs == null));
            }

            // 取输出参数

            SqlUtils.getValueFromCallableStatement(cs, outParms, outPramsValues);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            try {
                if (this.conn != null) {
                    if (close) {
                        this.close();
                    } else {
                        this.closer();
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
    }


    @Override
    public void callStoredPro(String sqltext, Map<String, Object> parms,
                              Map<Integer, Object> outPramsValues,
                              List<DataBaseSet> returnDataBaseSets) throws DbException {
        this.configInterceptorForMehtod("callStoredPro",
                String.class, Map.class, Map.class, List.class);
        try {
            this.executeStoredProcedure(sqltext, parms, outPramsValues, returnDataBaseSets);

        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(outPramsValues);
        }

    }

    private long[] executeBindUpdate(String sqltext, Collection<Object> vParameters) throws DbException {
        return this.executeBindUpdate(sqltext, vParameters, false, null);
    }

    /**
     * 执行更新操作
     *
     * @param sqltext
     * @param vParameters
     * @param innerUse    是否是内部使用，如果是内部使用，则不需要关闭连接
     * @return 返回两个元素的数组，0位置的元素为更新的个数，1位置的元素为主键（如果存在，否则为-1）
     * @throws DbException
     */
    private long[] executeBindUpdate(String sqltext, Collection<Object> vParameters, boolean innerUse, Integer index) throws DbException {

        int count = 0;
        long[] twoInt = new long[]{-1, -1};
        try {
            sqltext = sqltext.trim();
            PreparedStatement preStmt = this.getPreparedStatement(sqltext);

            String paramStr = SqlUtils.setToPreStatment(vParameters, preStmt);

            String debugSql = SqlUtils.generateDebugSql(sqltext, vParameters, this.getDataBaseType());
            if (DbContext.getDebugSQLListener() != null) {
                DbContext.getDebugSQLListener().accept(debugSql);
            }
            if(DbContext.getDBInterceptor()!=null){
                DbContext.getDBInterceptorInfo().setDebugSql(debugSql);
                boolean ret=DbContext.getDBInterceptor().beforeDbOperationExeute(DataBaseImpl.this,false,debugSql);
                if(!ret){
                    Method mehod=DbContext.getDBInterceptorInfo().getInterceptedMethod();
                    throw new DbException(mehod+"操作被拦截不能继续执行！", CODE.Intercepted);
                }
            }
            if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
                log.debug("[" + getConnectInfo() + "][" + getCallerInf() + "]");
                log.debug((index != null ? "" + index + "." : "") + "debugSql[" + debugSql + "]");

            }
            long start = System.currentTimeMillis();
            count = preStmt.executeUpdate();
            if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
                log.debug("sql执行时间:" + (System.currentTimeMillis() - start) + "，更新条数:" + count);
            }
            twoInt[0] = count;
            try {

                if (this.sqlType == SQLType.INSERT) {
                    ResultSet keys = preStmt.getGeneratedKeys();
                    if (keys.next()) {
                        twoInt[1] = keys.getLong(1);
                    } else {
                        twoInt[1] = -1;
                    }
                    keys.close();
                } else {
                    twoInt[1] = -1;
                }

            } catch (Throwable e) {
                twoInt[1] = -1;
                log.error("您的驱动程序不支持生成主键的操作（PreparedStatement.getGeneratedKeys()），建议更换您的数据库驱动程序版本"
                        + "使之支持jdbc3.0标准！返回-1");
            }

        } catch (Exception e) {
            twoInt[0] = -1;
            twoInt[1] = -1;
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);

        } finally {
            try {
                if (this.conn != null) {
                    if (innerUse) {
                        this.closer();
                    } else {
                        if (this.getAutoCommit()) {
                            this.close();
                        } else {
                            this.closer();
                        }
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return twoInt;
    }

    /**
     * 返回插入记录的行数
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param sqltext     sql语句
     * @param vParameters 参数
     * @return
     */
    private int executeBindInsert(String sqltext, Map<Integer, Object> vParameters) throws DbException {

        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        return this.executeBindInsert(sqltext, args);

    }

    @Override
    public int insert(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        this.configInterceptorForMehtod("insert", String.class, Map.class);
        Integer ret = null;
        try {
            return ret = this.executeBindInsert(sqltext, vParameters);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }


    /**
     * 使insertObject对象的所有属性插入到数据库，如果insertObject的对象某个属性值为null， 那么会忽略此属性，不会插入空值到数据库。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param insertObject 要插入的对象
     * @return
     * @throws DbException
     */
    private <T> int excuteInsertClass(T insertObject) throws DbException {

        return this.excuteInsertClass(insertObject, null, true);

    }

    /**
     * 向数据库插入在insertObject里properties数组指定的属性，如果在properties中的某
     * 个属性对应insertObject属性值为空，那么会忽略此属性，不会插入空值到数据库。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param insertObject
     * @param properties   指定insertObject里需插入的属性，如果properties指定为空，
     *                     则插入insertObject对象所有属性
     * @return
     * @throws DbException
     */
    private <T> int excuteInsertClass(T insertObject, String[] properties, boolean ignoreNull) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = insertObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.INSERT;
            this.fetchConnection();
            String sqltext = SqlUtils.generateInsertSql(this.dbPoolName, properties, insertObject, reflectClass,
                    vParameters, null, ignoreNull, this.getDataBaseType());
            DbContext.clearReflectClass();

            return this.executeBindInsert(sqltext, vParameters);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }

    }


    /**
     * 把某对象的指定的属性插入到数据库到数据库，并返回主键，有些数据库的驱动程序不会返回主键，所以要根据具体数据库而言，如mysql数据库可以返回主键。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param insertObject 插入的对象
     * @param properties   哪些属性需要插入数据库
     * @param ignoreNull   是否忽略properties指定属性中为null的属性，既为null的属性不包含在insert的values语句里
     * @param <T>
     * @return
     * @throws DbException
     */
    private <T> long excuteInsertClassReturnKey(T insertObject, String[] properties, boolean ignoreNull) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = insertObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.INSERT;
            this.fetchConnection();
            String sqltext = SqlUtils.generateInsertSql(this.dbPoolName, properties, insertObject, reflectClass,
                    vParameters, null, ignoreNull, this.getDataBaseType());

            DbContext.clearReflectClass();

            return this.executeBindInsertReturnKey(sqltext, vParameters);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }

    }


    /**
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param insertObject 要插入的对象
     * @return
     * @throws DbException
     */
    private <T> long excuteInsertClassReturnKey(T insertObject) throws DbException {

        return this.excuteInsertClassReturnKey(insertObject, null, true);

    }


    /**
     * 插入多个对象的指定属性,此方法必须保证insertObjects是相同的类型
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param insertObjects 待插入的对象
     * @param properties    对象的属性
     * @return
     * @throws DbException
     */
    private <T> int[] excuteInsertClass(T[] insertObjects, String[] properties, boolean ignoreNull) throws DbException {

        String[][] pros = new String[insertObjects.length][];
        ArrayUtils.fill(pros, properties);
        return this.excuteInsertObjects(insertObjects, pros, ignoreNull);
    }


    /**
     * 插入多个对象指定的属性到数据库，insertObjects里的每个对象对应一个属性数组，所以为二维数组，不忽略为null的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param insertObjects：待插入数据库的对象数组
     * @param properties：属性数组，用于指定对象的哪些属性需要插入数据库；每个对象对应一个属性数组
     * @return
     * @throws DbException
     */
    private int[] excuteInsertWholeObjects(Object[] insertObjects, String[][] properties) throws DbException {

        return excuteInsertObjects(insertObjects, properties, false);
    }

    /**
     * @param insertObjects：待插入数据库的对象
     * @param properties：属性数组，用于指定哪些属性需要插入数据库
     * @param ignoreNull                      ：忽略为空的属性
     * @return
     * @throws DbException
     */
    private int[] excuteInsertObjects(Object[] insertObjects, String[][] properties, boolean ignoreNull)
            throws DbException {
        int length = insertObjects.length;
        Map[] maps = new HashMap[length];
        String[] sqltexts = new String[length];
        try {
            boolean optimize = true;
            Class<?>[] handClassList = DbContext.getReflectClass();
            this.sqlType = SQLType.INSERT;
            this.fetchConnection();
            for (int i = 0; i < maps.length; i++) {

                Object obj = insertObjects[i];

                Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
                String[] pros = null;
                if (properties != null && properties.length > 0) {
                    pros = properties[i];
                }

                Class reflectClass = obj.getClass();
                if (handClassList != null && handClassList.length > 0) {
                    if (handClassList.length == 1) {
                        reflectClass = handClassList[0];
                    } else {
                        reflectClass = handClassList[i];
                    }
                }

                String sql = SqlUtils.generateInsertSql(this.dbPoolName, pros, obj, reflectClass, vParameters,
                        null, ignoreNull, this.getDataBaseType());

                maps[i] = vParameters;
                sqltexts[i] = sql;

                if (i > 0) {
                    if (!sql.equals(sqltexts[0])) {
                        optimize = false;
                    }
                    if (!ArrayUtils.isEquals(pros, properties[0])) {
                        optimize = false;
                    }
                }
            }
            DbContext.clearReflectClass();

            if (optimize) {// 可以优化成批量更新
                @SuppressWarnings("unchecked")
                List<Map<Integer, Object>> list = Arrays.asList(maps);
                int[] res = this.executeBindBatch(sqltexts[0], list);
                return res;
            } else {
                int[] res = this.executeBindBatch(sqltexts, maps);

                return res;
            }

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }

    }

    /**
     * 插入多个对象的所有属性到数据库，如果对象里某个属性为空，会忽略此属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param insertObjects 待插入的对象，可以为不同类型的类
     * @return
     * @throws DbException
     */
    private int[] excuteInsertWholeObjects(Object[] insertObjects) throws DbException {
        return this.excuteInsertWholeObjects(insertObjects, (String[][]) null);
    }

    /**
     * 插入多个对象的所有属性到数据库，不忽略为null的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param insertObjects 待插入的对象，可以为不同类型的类
     * @return
     * @throws DbException
     */
    private int[] excuteInsertObjects(Object[] insertObjects) throws DbException {
        return this.excuteInsertObjects(insertObjects, (String[][]) null, true);
    }

    /**
     * 插入多个对象到数据库，如果对象里某个属性为空，会忽略此属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param objs 待插入的对象，所有对象的类型必须相同
     * @return
     * @throws DbException
     */
    private <T> int[] excuteInsertClass(T[] objs) throws DbException {
        return this.excuteInsertClass(objs, (String[]) null, true);
    }


    /**
     * 更新对象
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param updateObject       待更新的对象
     * @param whereForProperties 待更新对象的主键属性名称，复合主键属性用逗号隔开
     * @param properties         待插入的属性， 如果指定的属性在upateObject对象里的值为null，则忽略
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateClass(T updateObject, String[] whereForProperties, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = updateObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.UPDATE;
            this.fetchConnection();
            String sqltext = SqlUtils.generateUpdateSql(this.dbPoolName, properties, updateObject, reflectClass,
                    whereForProperties, vParameters, null, true, this.getDataBaseType());
            DbContext.clearReflectClass();
            return this.executeBindUpdate(sqltext, vParameters);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }

    }

    /**
     * 更新对象
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param updateObject       待更新的对象
     * @param whereForProperties 待更新对象的主键属性名称，复合主键属性用逗号隔开
     * @param properties         待插入的属性，不会忽略为NULL的属性
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateWholeClass(T updateObject, String[] whereForProperties, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = updateObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.UPDATE;
            this.fetchConnection();
            String sqltext = SqlUtils.generateUpdateSql(this.dbPoolName, properties, updateObject, reflectClass,
                    whereForProperties, vParameters, null
                    , false, this.getDataBaseType());
            DbContext.clearReflectClass();

            return this.executeBindUpdate(sqltext, vParameters);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }

    }

    /**
     * 删除多个对象，所有对象都以whereProperteis里指定的属性的值来定位删除
     *
     * @param <T>
     * @param deleteObject
     * @param whereProperteis 指定的多个属性组成where的条件，各个条件是and关系，例如whereProperteis为[name,id,age]，
     *                        则产生的where部分内容为<code>where name=? and id=? and age=?</code>
     * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。 如果返回为null，表示执行失败
     * @throws DbException
     */
    private <T> int[] excuteDeleteClass(T[] deleteObject, String[] whereProperteis) throws DbException {
        String[][] strArray = new String[deleteObject.length][];
        strArray = ArrayUtils.fill(strArray, whereProperteis);
        return this.excuteDeleteObjects(deleteObject, strArray);

    }

    /**
     * 删除一个对象，所有对象都以whereProperteis属性的值来定位删除
     *
     * @param <T>
     * @param deleteObject    待删除的对象
     * @param whereProperteis 指定的多个属性组成where的条件，各个条件是and关系，例如whereProperteis为[name,id,age]，
     *                        则产生的where部分内容为<code>where name=? and id=? and age=?</code>
     * @return 返回实际更新的行数
     * @throws DbException
     */
    private <T> int excuteDeleteClass(T deleteObject, String[] whereProperteis) throws DbException {
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = deleteObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.DELETE;
            this.fetchConnection();
            String sql = SqlUtils.generateDeleteSql(this.dbPoolName, deleteObject, reflectClass, whereProperteis,
                    vParameters, null, this.getDataBaseType());
            DbContext.clearReflectClass();
            return this.executeBindDelete(sql, vParameters);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }

    /**
     * 删除一个对象，待删除对象根据有值的属性来生成where的删除条件
     *
     * @param <T>
     * @param deleteObject 待删除的对象 待删除对象根据有值的属性来生成where的删除条件。
     * @return 返回实际更新的行数
     * @throws DbException
     */
    @SuppressWarnings("unused")
    private <T> int excuteDeleteClass(T deleteObject) throws DbException {
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = deleteObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            this.sqlType = SQLType.DELETE;
            this.fetchConnection();
            String sql = SqlUtils.generateDeleteSqlByObject(this.dbPoolName, deleteObject, reflectClass, vParameters,
                    null, this.getDataBaseType());
            DbContext.clearReflectClass();
            return this.executeBindDelete(sql, vParameters);

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }

    /**
     * 把对象所有属性更新到数据库，如果某个属性值为null，则忽略。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param updateObject       待更新到数据库的对象
     * @param whereForProperties 指定的多个属性组成where的条件，各个条件是and关系，例如whereProperteis为[name,id,age]，
     *                           则产生的where部分内容为<code>where name=? and id=? and age=?</code>
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateClass(T updateObject, String[] whereForProperties) throws DbException {
        return this.excuteUpdateClass(updateObject, whereForProperties, (String[]) null);
    }

    /**
     * 把对象所有属性更新到数据库，不会忽略为null的属性
     * <p>
     * whereForProperties为对象主键属性（复合主键属性以逗号分开）
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param updateObject       待更新到数据库的对象
     * @param whereForProperties 指定的多个属性组成where的条件，各个条件是and关系，例如whereProperteis为[name,id,age]，
     *                           则产生的where部分内容为<code>where name=? and id=? and age=?</code>
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateWholeClass(T updateObject, String[] whereForProperties) throws DbException {
        return this.excuteUpdateWholeClass(updateObject, whereForProperties, (String[]) null);
    }

    private <T> int[] excuteUpdateClass(T[] updateObject, String[] whereForProperties) throws DbException {
        return this.excuteUpdateClass(updateObject, whereForProperties, (String[]) null);
    }

    private <T> int[] excuteUpdateWholeClass(T[] updateObject, String[] whereForProperties) throws DbException {
        return this.excuteUpdateWholeClass(updateObject, whereForProperties, (String[]) null);
    }

    /**
     * 把对象指定属性更新到数据库
     * <p>
     * whereForProperties为对象主键属性（复合主键属性以逗号分开）
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param objects            待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组。
     * @param whereForProperties 指定的多个属性组成where的条件，各个条件是and关系，例如whereProperteis为[name,id,age]，
     *                           则产生的where部分内容为<code>where name=? and id=? and age=?</code>。
     * @param updateProperties   指定需更新的属性，如果指定的某个属性在对应对象里值为null，则忽略。
     * @return
     * @throws DbException
     */
    private <T> int[] excuteUpdateClass(T[] objects, String[] whereForProperties, String[] updateProperties) throws DbException {

        int length = objects.length;
        String[][] whereProperteis = new String[length][];
        ArrayUtils.fill(whereProperteis, whereForProperties);
        String[][] pros = new String[length][];
        ArrayUtils.fill(pros, updateProperties);
        return excuteUpdateObjects(objects, whereProperteis, pros, true);

    }

    /**
     * 把对象指定属性更新到数据库,不会忽略为NULL的属性
     * <p>
     * whereForProperties为对象主键属性（复合主键属性以逗号分开）
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param <T>
     * @param objects            待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组
     * @param whereForProperties 指定的多个属性组成where的条件，各个条件是and关系，例如whereProperteis为[name,id,age]，
     *                           则产生的where部分内容为<code>where name=? and id=? and age=?</code>。
     * @param properties         指定需更新的属性，不会忽略为NULL的属性
     * @return
     * @throws DbException
     */
    private <T> int[] excuteUpdateWholeClass(T[] objects, String[] whereForProperties, String[] properties) throws DbException {

        int length = objects.length;
        String[][] whereProperteis = new String[length][];
        ArrayUtils.fill(whereProperteis, whereForProperties);
        String[][] pros = new String[length][];
        ArrayUtils.fill(pros, properties);
        return this.excuteUpdateObjects(objects, whereProperteis, pros, false);

    }


    /**
     * 把多个对象插入数据库，各个对象的类型可以不一样
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param objects          待更新到数据库的多个对象
     * @param whereProperteis  指定的多个属性组成where的条件，各个条件是and关系，例如whereProperteis为[name,id,age]，
     *                         则产生的where部分内容为<code>where name=? and id=? and age=?</code>。
     * @param updateProperties 每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
     *                         不忽略为null的属性。
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateWholeObjects(Object[] objects, String[][] whereProperteis, String[][] updateProperties)
            throws DbException {

        return excuteUpdateObjects(objects, whereProperteis, updateProperties, false);
    }

    /**
     * 把多个对象插入数据库，各个对象的类型可以不一样，不会忽略为null的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param objects         待更新到数据库的多个对象
     * @param whereProperteis 生成where条件的对象属性
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateWholeObjects(Object[] objects, String[][] whereProperteis) throws DbException {

        return excuteUpdateObjects(objects, whereProperteis, null, false);
    }

    /**
     * @param objects         待更新的对象
     * @param whereProperteis 生成where条件的对象属性
     * @param updateProperties      待更新的属性，可以为空，表明更新主键以外的属性
     * @param ignoreNull      是否忽略空
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateObjects(Object[] objects, String[][] whereProperteis, String[][] updateProperties, boolean ignoreNull)
            throws DbException {
        int length = objects.length;
        Map[] maps = new HashMap[length];
        String[] sqltexts = new String[length];
        boolean optimize = true;
        try {
            Class[] handClassList = DbContext.getReflectClass();
            this.sqlType = SQLType.UPDATE;
            this.fetchConnection();
            for (int i = 0; i < maps.length; i++) {
                Object obj = objects[i];

                String[] whereps = whereProperteis[i];
                Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
                String[] pros = null;
                if (updateProperties != null)
                    pros = updateProperties[i];

                Class reflectClass = obj.getClass();
                if (handClassList != null && handClassList.length > 0) {
                    if (handClassList.length == 1) {
                        reflectClass = handClassList[0];
                    } else {
                        reflectClass = handClassList[i];
                    }
                }
                String sql = SqlUtils.generateUpdateSql(this.dbPoolName, pros, obj, reflectClass, whereps, vParameters,
                        null, ignoreNull, this.getDataBaseType());

                maps[i] = vParameters;
                sqltexts[i] = sql;

                if (i > 0) {
                    if (!sql.equals(sqltexts[0])) {
                        optimize = false;
                    }
                    if (!ArrayUtils.isEquals(pros, updateProperties[0])) {
                        optimize = false;
                    }
                }
            }
            DbContext.clearReflectClass();

            if (optimize) {// 可以优化成批量更新
                @SuppressWarnings("unchecked")
                List<Map<Integer, Object>> list = Arrays.asList(maps);
                int[] res = this.executeBindBatch(sqltexts[0], list);
                return res;
            } else {
                int[] res = this.executeBindBatch(sqltexts, maps);

                return res;
            }

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }

    /**
     * 删除多个对象
     *
     * @param deleteObjects   待删除的多个对象，数组里的每个对象类型可以不相同
     * @param whereProperties 生成where条件的对象属性
     * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。 如果返回为null，表示执行失败
     * @throws DbException
     */
    private int[] excuteDeleteObjects(Object[] deleteObjects, String[][] whereProperties) throws DbException {

        int length = deleteObjects.length;
        Map[] maps = new HashMap[length];
        String[] sqltexts = new String[length];
        boolean optimize = true;
        try {
            Class[] handClassList = DbContext.getReflectClass();
            this.sqlType = SQLType.DELETE;
            this.fetchConnection();
            for (int i = 0; i < maps.length; i++) {
                Object obj = deleteObjects[i];
                String[] oneWhereProperties = whereProperties[i];
                Map<Integer, Object> vParameters = new HashMap<Integer, Object>();

                Class reflectClass = obj.getClass();
                if (handClassList != null && handClassList.length > 0) {
                    if (handClassList.length == 1) {
                        reflectClass = handClassList[0];
                    } else {
                        reflectClass = handClassList[i];
                    }
                }
                String sql = SqlUtils.generateDeleteSql(this.dbPoolName, obj, reflectClass, oneWhereProperties,
                        vParameters, null, this.getDataBaseType());

                maps[i] = vParameters;
                sqltexts[i] = sql;

                if (i > 0) {
                    if (!sql.equals(sqltexts[0])) {
                        optimize = false;
                    }
                }
            }
            DbContext.clearReflectClass();

            if (optimize) {// 可以优化成批量更新
                @SuppressWarnings("unchecked")
                List<Map<Integer, Object>> list = Arrays.asList(maps);
                int[] res = this.executeBindBatch(sqltexts[0], list);
                return res;
            } else {
                int[] res = this.executeBindBatch(sqltexts, maps);
                return res;
            }

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }

    /**
     * 根据sql语句和参数，插入记录到数据库，返回插入记录的行数
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param sqltext
     * @param vParameters
     * @return 返回插入记录的行数，-1表明插入出错
     */
    private int executeBindInsert(String sqltext, Collection<Object> vParameters) throws DbException {
        long[] twoInt = this.executeBindUpdate(sqltext, vParameters);
        return (int) twoInt[0];
    }

    /**
     * 如果insert一条语句用此函数，并返回插入数据库后返回此记录的主键
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param sqltext
     * @param vParameters
     * @return
     */
    private long executeBindInsertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException {

        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        return this.executeBindOneInsert(sqltext, args);
    }

    @Override
    public long insertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException {
        this.configInterceptorForMehtod("insertReturnKey", String.class, Map.class);
        Long ret = null;
        try {
            return ret = this.executeBindInsertReturnKey(sqltext, vParameters);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    private long executeBindOneInsert(String sqltext, Collection<Object> vParameters) throws DbException {
        long[] twoInt = this.executeBindUpdate(sqltext, vParameters);
        return twoInt[1];
    }

    /**
     * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过调用setAutoCommit(false)方法
     * 可以设置为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过close()方法关闭数据库连接
     *
     * @return 是否自动提交，非自动提交说明开启了事务
     * @throws DbException
     */
    @Override
    public boolean getAutoCommit() throws DbException {
        return this.isAutoCommit;
    }

    /**
     * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过
     * setAutoCommit(false)方法可以设置为事务操作，如果为事务操作，那么DataBase里所有方法都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过close()方法关闭数据库连接
     *
     * @throws DbException
     */
    @Override
    public void setAutoCommit(boolean b) throws DbException {
        this.isAutoCommit = b;
        try {
            if (this.conn != null && !this.conn.isClosed()) {
                this.setInternalConnectionAutoCommit(b);
            }
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }

    }

    /**
     * 用于事务性操作的回滚，如果事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    @Override
    public void rollback() throws DbException {
        try {
            if (conn != null) {
                conn.rollback();
                clearSavePoint();
                if (DbContext.permitDebugLog())
                    log.debug(this.getConnectInfo() + ":rollbacked done!");
            }
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }

    @Override
    public Map<String, Savepoint> getSavepoint() {
        return this.savePointMap;
    }

    /**
     * 设置保存点
     *
     * @param savepointName 保存点的名称
     * @throws DbException
     */
    @Override
    public void setSavepoint(String savepointName) throws DbException {
        Assert.hasText(savepointName);
        if (this.savePointMap.containsKey(savepointName)) {
            return;
        } else {
            this.savePointMap.put(savepointName, null);
            if (!this.isColsed()) {
                this.setRealSavepoint(savepointName);
            }
        }
    }

    @Override
    public void releaseSavepoint(String savepointName) throws DbException {
        if (!this.isColsed()) {
            Savepoint savepoint = this.savePointMap.get(savepointName);
            try {
                if (savepoint != null) {
                    this.conn.releaseSavepoint(savepoint);
                }
            } catch (SQLException exception) {
                throw new DbException(exception);
            }

        }
    }

    private void setRealSavepoint(String savepointName) throws DbException {
        try {
            if (savepointName != null && !savepointName.isEmpty() && !this.isColsed()) {
                if (savePointMap.containsKey(savepointName) && savePointMap.get(savepointName) == null) {
                    Savepoint savepoint = conn.setSavepoint(savepointName);
                    if (DbContext.permitDebugLog()) {
                        log.debug(this.getConnectInfo() + ": Savepoint:" + savepointName + "  ok!");
                    }
                    this.savePointMap.put(savepointName, savepoint);
                }
            }

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }

    @Override
    public void rollbackToSavepoint(String savepointName) throws DbException {
        if (!savePointMap.containsKey(savepointName)
                || savePointMap.get(savepointName) == null) {
            return;
        } else {
            try {
                if (!this.isColsed()) {
                    conn.rollback(savePointMap.get(savepointName));
                    savePointMap.remove(savepointName);
                    if (DbContext.permitDebugLog())
                        log.debug("[" + this.getConnectInfo() + "]:rollbacked..to savepoint done!" + savepointName);
                }
            } catch (Exception e) {
                if (e instanceof DbException) throw (DbException) e;
                throw new DbException(e);
            }
        }
    }

    /**
     * 判断资源和底层数据库连接是否关闭
     *
     * @return
     * @throws DbException
     */
    @Override
    public boolean isColsed() throws DbException {
        boolean result = false;
        try {
            if (this.conn == null || this.conn.isClosed()) {
                return true;
            }
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("error in isClosed!");
        }
        return result;
    }

    private void clearSavePoint() {
        this.savePointMap.clear();
    }

    /**
     * 事务性操作的事务的提交，当 {@link #setAutoCommit(boolean)}设为false， 会用到此方法，一般对于事务性操作会用到，如果
     * 事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    @Override
    public void commit() throws DbException {
        try {
            if (conn != null) {
                conn.commit();
                clearSavePoint();
                if (DbContext.permitDebugLog())
                    log.debug("[" + this.getConnectInfo() + "]commited done!");
            }
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }


    /**
     * @param sqltext
     * @param vParametersArray
     * @return 返回每条语句更新记录的条数，执行错误，会抛出异常
     * @throws DbException
     */
    private int[] executeBindBatchBy(String sqltext, List<Collection<Object>> vParametersArray)
            throws DbException {
        Boolean backAutoCommit = null;
        int[] count = null;
        try {
            backAutoCommit = this.getAutoCommit();
            if (this.getAutoCommit()) {
                this.setAutoCommit(false);//暂时设置为false，使之成为事务性操作
            }
            sqltext = sqltext.trim();
            PreparedStatement preStmt = this.getPreparedStatement(sqltext);
            String callInfo = getCallerInf();
            for (int m = 0; m < vParametersArray.size(); m++) {

                Collection<Object> vParameters = vParametersArray.get(m);
                String debugSql = SqlUtils.generateDebugSql(sqltext, vParameters, this.getDataBaseType());
                if (DbContext.getDebugSQLListener() != null) {
                    DbContext.getDebugSQLListener().accept(debugSql);
                }
                if(DbContext.getDBInterceptor()!=null){
                    DbContext.getDBInterceptorInfo().setDebugSql(debugSql);
                    boolean ret=DbContext.getDBInterceptor().beforeDbOperationExeute(DataBaseImpl.this,true,debugSql);
                    if(!ret){
                        throw new DbException("被拦截", CODE.Intercepted);
                    }
                }
                if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
                    log.debug("[" + this.getConnectInfo() + "][" + callInfo + "]");
                    log.debug("" + m + ".debugSql[" + debugSql + "]");
                }
                String paramStr = SqlUtils.setToPreStatment(vParameters, preStmt);
                preStmt.addBatch();
            } // for

            boolean error = false;
            count = preStmt.executeBatch();
            int i = 0;
            for (i = 0; i < count.length; i++) {
                if (count[i] < 0) {
                    error = true;
                    break;
                }
            }
            if (backAutoCommit) {
                if (error) {
                    this.rollback();
                    count = null;
                    throw new DbException("执行错误,!第[" + i + "]条语句更新失败!");
                } else {
                    this.commit();
                }
            }

        } catch (Exception e) {
            count = null;
            if (backAutoCommit) {
                this.rollback();
            }
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);

        } finally {
            try {
                if (this.conn != null) {
                    if (backAutoCommit) {
                        this.close();
                        this.setAutoCommit(true);
                    } else {
                        this.closer();
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return count;
    }

    /**
     * 返回的数组里每个值对应返回对应sql语句执行后更新的行数，如果为null表明执行失败，内部会自动回滚。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用close()方法
     *
     * @param sqltxts
     * @param vParametersArray
     * @return 如果为null表明执行失败
     * @throws DbException
     */
    private int[] executeBindBatch(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {

        int[] res = null;
        if (ArrayUtils.isNotEmpty(vParametersArray)) {
            if (sqltxts.length != vParametersArray.length) {
                throw new DbException("sqltexs的长度应该和vParametersArray的长度相等！");
            }
        } else {

        }
        res = new int[sqltxts.length];

        boolean bkCommit = true;
        boolean error = false;
        try {
            bkCommit = this.getAutoCommit();
            if (bkCommit) {
                this.setAutoCommit(false);
            }

            for (int i = 0; i < sqltxts.length; i++) {
                Collection<Object> args = null;
                if (ArrayUtils.isNotEmpty(vParametersArray)) {
                    if (vParametersArray[i] != null) {
                        args = CollectionUtils.getSortedValues(vParametersArray[i]);
                    }
                }
                long[] twoInt = this.executeBindUpdate(sqltxts[i], args, true, i);
                int ret = (int) twoInt[0];// 返回记录的行数
                res[i] = ret;
            }

            if (bkCommit) {
                if (error) {
                    this.rollback();
                    res = null;
                } else {
                    this.commit();
                }
            }
            return res;
        } catch (Exception ex) {
            res = null;
            ;
            try {
                if (bkCommit) {
                    this.rollback();
                }
            } catch (DbException e) {
            }
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);

        } finally {
            try {
                if (bkCommit) {
                    this.close();
                    this.setAutoCommit(true);
                } else {
                    this.closer();
                }
            } catch (DbException e) {
                //
            }

        }

    }

    @Override
    public int[] update(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
        int[] ret = null;
        this.configInterceptorForMehtod("update", String[].class, Map[].class);
        try {
            return ret = this.executeBindBatch(sqltxts, vParametersArray);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public int[] insert(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {
        this.configInterceptorForMehtod("insert", String[].class, Map[].class);
        int[] ret = null;
        try {
            return ret = this.executeBindBatch(sqltxts, vParametersArray);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    /**
     * 批量更新操作（增，删，改），返回每条语句更新记录的行数
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要调用close()方法
     *
     * @param sqltxt          执行的sql语句
     * @param vParametersList 每条语句所携带的参数，每条语句对应一个Map，每个Map存放相应语句的参数
     * @return 返回每条语句更新记录的行数，执行错误，会抛出异常
     * @throws DbException
     */
    private int[] executeBindBatch(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {

        List<Collection<Object>> argsList = new ArrayList<Collection<Object>>();
        for (int i = 0; i < vParametersList.size(); i++) {
            Map<Integer, Object> map = vParametersList.get(i);
            Collection<Object> args = CollectionUtils.getSortedValues(map);
            argsList.add(args);
        }

        return executeBindBatchBy(sqltxt, argsList);
    }

    @Override
    public int[] update(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
        int[] ret = null;
        this.configInterceptorForMehtod("update", String.class, List.class);
        try {
            return ret = this.executeBindBatch(sqltxt, vParametersList);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public int[] insert(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {

        int[] ret = null;
        this.configInterceptorForMehtod("insert", String.class, List.class);
        try {
            return ret = this.executeBindBatch(sqltxt, vParametersList);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    /**
     * 不带参数的批量处理
     *
     * @param sqltxt
     * @return
     * @throws DbException
     */
    private int[] executeBatch(ArrayList<String> sqltxt) throws DbException {
        return updateBatch(sqltxt);
    }


    @Override
    public int[] update(ArrayList<String> sqltxts) throws DbException {
        int[] ret = null;
        this.configInterceptorForMehtod("update", ArrayList.class);
        try {
            return ret = this.executeBatch(sqltxts);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    public void configInterceptorForMehtod(String mname, Class<?>... parameterTypes) {

        try {
            if(DbContext.getDBInterceptor()!=null) {
                Method method = this.getClass().getMethod(mname, parameterTypes);
                DBInterceptorInfo dbInterceptorInfo = new DBInterceptorInfo();
                dbInterceptorInfo.setDataBase(this);
                dbInterceptorInfo.setInterceptedMethod(method);
                DbContext.setDBInterceptorInfo(dbInterceptorInfo);
            }
        } catch (NoSuchMethodException e) {
            log.error("" + e, e);
        }
    }

    private void configInterceptorForException(Exception e) {

        if(DbContext.getDBInterceptor()!=null) {
            DBInterceptorInfo dbInterceptorInfo = DbContext.getDBInterceptorInfo();
            dbInterceptorInfo.setException(e);
        }
    }

    private void configInterceptorForPostExecute(Object result) {

        if (DbContext.getDBInterceptor()!= null ) {
            DBInterceptorInfo dbInterceptorInfo = DbContext.getDBInterceptorInfo();
            dbInterceptorInfo.setResult(result);
            DBInterceptor dbInterceptor = DbContext.getDBInterceptor();
            dbInterceptor.postDbOperationExeute(dbInterceptorInfo.getDataBase(),
                    dbInterceptorInfo.getInterceptedMethod(),
                    dbInterceptorInfo.getResult(),
                    dbInterceptorInfo.getException(),
                    dbInterceptorInfo.getDebugSql());
            DbContext.removeDBInterceptorInfo();
        }

    }

    @Override
    public int[] insert(ArrayList<String> sqltxts) throws DbException {

        int[] ret = null;
        this.configInterceptorForMehtod("insert", ArrayList.class);
        try {
            return ret = this.executeBatch(sqltxts);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    private int[] updateBatch(ArrayList<String> sqltxt) throws DbException {
        return this.executeBindBatch(sqltxt.toArray(new String[0]), null);
    }

    private PreparedStatement getPreparedStatement(String sqltxt) throws DbException {
        try {
            this.sqlType = SqlUtils.decideSqlType(sqltxt);

            if (this.sqlType == SQLType.SELECT) {
                this.fetchConnection();
                ps = conn.prepareStatement(sqltxt, ResultSet.CONCUR_READ_ONLY);
            } else if (this.sqlType == SQLType.INSERT) {
                this.fetchConnection();
                ps = conn.prepareStatement(sqltxt, Statement.RETURN_GENERATED_KEYS);
            } else if (this.sqlType == SQLType.UPDATE) {
                this.fetchConnection();
                ps = conn.prepareStatement(sqltxt);
            } else if (this.sqlType == SQLType.DELETE) {
                this.fetchConnection();
                ps = conn.prepareStatement(sqltxt);
            } else if (this.sqlType == SQLType.STORE_DPROCEDURE) {
                this.fetchConnection();
                ps = this.conn.prepareCall(sqltxt);
            } else if (this.sqlType == SQLType.OTHER) { //create,drop等
                this.fetchConnection();
                ps = conn.prepareStatement(sqltxt);

            } else {
                throw new DbException("不能决定sql语句的类型！");
            }

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
        return ps;
    }

    /**
     * 关闭底层资源，但不关闭数据库连接
     */
    public void closer() throws DbException {

        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }

            if (ps != null) {
                ps.close();
                ps = null;

            }

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }

    /**
     * 关闭数据库连接，释放底层占用资源
     */
    @Override
    public void close() {
        try {
            this.forceClose();

        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void forceClose() {

        try {

            if (rs != null) {
                rs.close();
                rs = null;
            }

            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                if (!conn.isClosed()) {
                    try {
                        if (!this.externalControlConClose) {
                            this.setInternalConnectionAutoCommit(true);
                            conn.close();
                            conn = null;
                            clearSavePoint();
                            if (DbContext.permitDebugLog())
                                log.debug("[" + this.getConnectInfo() + "]:closed!");
                        }
                    } catch (Exception ex) {
                        log.error(this.getConnectInfo() + ":close fail!", ex);
                    }


                }

            }

        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public Connection getConnection(boolean force) {
        if (this.isColsed() && force) {
            this.fetchConnection();
        }
        return this.conn;
    }

    @Override
    public <T> int insertBy(T insertObject) throws DbException {
        this.configInterceptorForMehtod("insertBy", Object.class);
        Integer ret = null;
        try {
            return ret = this.excuteInsertClass(insertObject);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject) throws DbException {
        Long ret = null;
        this.configInterceptorForMehtod("insertReturnKeyBy", Object.class);
        try {
            return ret = this.excuteInsertClassReturnKey(insertObject);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> int[] insertBy(T[] objs) throws DbException {
        this.configInterceptorForMehtod("insertBy", Object[].class);
        int[] ret = null;
        try {
            return ret = this.excuteInsertClass(objs);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties) throws DbException {
        this.configInterceptorForMehtod("insertBy", Object.class, Object[].class);
        Integer ret = null;
        try {
            return ret = this.excuteInsertClass(insertObject, getPNames(insertProperties), false);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties) throws DbException {
        Long ret = null;
        this.configInterceptorForMehtod("insertReturnKeyBy", Object.class, Object[].class);
        try {
            return ret = this.excuteInsertClassReturnKey(insertObject, getPNames(insertProperties), false);

        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties) throws DbException {
        this.configInterceptorForMehtod("insertBy", Object[].class, Object[].class);
        int[] ret = null;
        try {
            return ret = this.excuteInsertClass(objs, getPNames(insertProperties), false);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, boolean includeNull) throws DbException {
        Long ret = null;
        this.configInterceptorForMehtod("insertReturnKeyBy", Object.class, boolean.class);
        try {
            return ret = this.excuteInsertClassReturnKey(insertObject, null, !includeNull);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        this.configInterceptorForMehtod("insertBy", Object.class, Object[].class, boolean.class);
        Integer ret = null;
        try {
            return ret = this.excuteInsertClass(insertObject, getPNames(insertProperties), !includeNull);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        Long ret = null;
        this.configInterceptorForMehtod("insertReturnKeyBy", Object.class, Object[].class, boolean.class);
        try {
            return ret = this.excuteInsertClassReturnKey(insertObject, getPNames(insertProperties), !includeNull);

        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> int[] insertBy(T[] objs, boolean includeNull) throws DbException {
        this.configInterceptorForMehtod("insertBy", Object[].class, boolean.class);
        int[] ret = null;
        try {
            return ret = this.excuteInsertClass(objs, (String[]) null, !includeNull);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties, boolean includeNull) throws DbException {

        int[] ret = null;
        this.configInterceptorForMehtod("insertBy", Object[].class, Object[].class, boolean.class);
        try {
            return ret = this.excuteInsertClass(objs, getPNames(insertProperties), !includeNull);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis) throws DbException {
        Integer ret = null;
        this.configInterceptorForMehtod("updateBy", Object.class, Object[].class);
        try {
            return ret = this.excuteUpdateClass(updateObject, getPNames(whereProperteis));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis, Object[] properties) throws DbException {
        this.configInterceptorForMehtod("updateBy", Object.class, Object[].class, Object[].class);
        Integer ret = null;
        try {
            return ret = this.excuteUpdateClass(updateObject, getPNames(whereProperteis), getPNames(properties));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int[] updateBy(T[] objects, Object[] whereProperteis, Object[] properties) throws DbException {
        this.configInterceptorForMehtod("updateBy", Object[].class, Object[].class, Object[].class);
        int[] ret = null;
        try {
            return ret = this.excuteUpdateClass(objects, getPNames(whereProperteis), getPNames(properties));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int[] updateBy(T[] objects, Object[] whereProperteis) throws DbException {
        int[] ret = null;
        this.configInterceptorForMehtod("updateBy", Object[].class, Object[].class);
        try {
            return ret = this.excuteUpdateClass(objects, getPNames(whereProperteis));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, boolean includeNull) throws DbException {
        this.configInterceptorForMehtod("updateBy", Object.class, Object[].class, boolean.class);
        Integer ret = null;
        try {
            if (includeNull) {
                return ret = this.excuteUpdateWholeClass(updateObject, getPNames(whereProperties));
            } else {
                return ret = this.updateBy(updateObject, whereProperties);
            }
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        this.configInterceptorForMehtod("updateBy", Object.class, Object[].class, Object[].class, boolean.class);
        Integer ret = null;
        try {
            if (includeNull) {
                return ret = this.excuteUpdateWholeClass(updateObject, getPNames(whereProperties), getPNames(updateProperties));
            } else {
                return ret = this.excuteUpdateClass(updateObject, getPNames(whereProperties), getPNames(updateProperties));
            }
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        this.configInterceptorForMehtod("updateBy", Object[].class, Object[].class, Object[].class,
                boolean.class);
        int[] ret = null;
        try {
            if (includeNull) {
                return ret = this.excuteUpdateWholeClass(updateObjects, getPNames(whereProperties), getPNames(updateProperties));
            } else {
                return ret = this.excuteUpdateClass(updateObjects, getPNames(whereProperties), getPNames(updateProperties));
            }
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, boolean includeNull) throws DbException {
        this.configInterceptorForMehtod("updateBy", Object[].class, Object[].class, boolean.class);
        int[] ret = null;
        try {
            if (includeNull) {
                return ret = this.excuteUpdateWholeClass(updateObjects, getPNames(whereProperties));
            } else {
                return ret = this.excuteUpdateClass(updateObjects, getPNames(whereProperties));
            }
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> T queryOneBy(T selectObject, Object[] whereProperteis) throws DbException {
        this.configInterceptorForMehtod("queryOneBy", Object.class, Object[].class);
        T ret = null;
        try {
            QueryOptions queryOptions = new QueryOptions();
            queryOptions.setLimitOne(true);
            List<T> list = this.doQueryClassForObject(selectObject, getPNames(whereProperteis), queryOptions);
            if (list.size() > 0) {
                return ret = list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis) throws DbException {
        this.configInterceptorForMehtod("queryListBy", Object.class, Object[].class);
        List<T> ret = null;
        try {
            return ret = this.doQueryClassForObject((Class<T>) selectObject.getClass(), selectObject, getPNames(whereProperteis), null);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis,
                                   int page, int perPage, PageBean pb)
            throws DbException {
        List<T> ret = null;
        this.configInterceptorForMehtod("queryListBy", Object.class, Object[].class,
                int.class, int.class, PageBean.class);
        try {

            String sql = "";
            Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
            try {
                Class<?>[] handClassList = DbContext.getReflectClass();
                Class<?> reflectClass = selectObject.getClass();
                if (handClassList != null && handClassList.length > 0) {
                    reflectClass = handClassList[0];
                }
                this.sqlType = SQLType.SELECT;
                this.fetchConnection();
                sql = SqlUtils.generateSelectSql(this.dbPoolName, selectObject, reflectClass, getPNames(whereProperteis), vParameters,
                        null, this.getDataBaseType());
                DbContext.clearReflectClass();

            } catch (Exception e) {
                if (e instanceof DbException) throw (DbException) e;
                throw new DbException(e);
            }
            return ret = this.doPageQueryClass((Class<T>) selectObject.getClass(), sql, vParameters, page, perPage, pb, null);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException {
        this.configInterceptorForMehtod("queryListBy", Object.class, int.class, int.class, PageBean.class);
        List<T> ret = null;
        try {
            if (selectObject instanceof MdbOptions) {
                MdbOptions mm = (MdbOptions) selectObject;
                if (mm.selectOptions() != null) {
                    if (mm.selectOptions().limit() != null && mm.selectOptions().limit() >= 0) {
                        throw new DbException("根据对象分页查询不能通过selectObject#selectOptions()#limit()指定limit！");
                    }
                }
            }
            String sql = "";
            Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
            try {
                Class<?>[] handClassList = DbContext.getReflectClass();
                Class<?> reflectClass = selectObject.getClass();
                if (handClassList != null && handClassList.length > 0) {
                    reflectClass = handClassList[0];
                }
                this.sqlType = SQLType.SELECT;
                this.fetchConnection();
                sql = SqlUtils.generateSelectSqlBySelectObject(this.dbPoolName, selectObject, reflectClass, vParameters,
                        null, this.getDataBaseType());

                DbContext.clearReflectClass();
            } catch (Exception e) {
                if (e instanceof DbException) throw (DbException) e;
                throw new DbException(e);
            }
            return ret = this.doPageQueryClass((Class<T>) selectObject.getClass(), sql, vParameters, page, perPage, pb, null);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> List<T> queryListBy(T selectObject) throws DbException {
        this.configInterceptorForMehtod("queryListBy", Object.class);
        List<T> ret = null;
        try {
            return ret = this.doQueryClassForObject(selectObject, (QueryOptions) null);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> T queryOneBy(T selectObject) throws DbException {
        this.configInterceptorForMehtod("queryOneBy", Object.class);
        T ret = null;
        try {
            QueryOptions queryOptions = new QueryOptions();
            queryOptions.setLimitOne(true);
            List<T> list = this.doQueryClassForObject(selectObject, queryOptions);

            if (list.size() > 0) {
                return ret = list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public <T> int delBy(T deleteObject, Object[] whereProperteis) throws DbException {
        this.configInterceptorForMehtod("delBy",
                Object.class, Object[].class);
        Integer ret = null;
        try {
            return ret = this.excuteDeleteClass(deleteObject, getPNames(whereProperteis));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }
    }

    @Override
    public <T> int[] delBy(T[] deleteObjects, Object[] whereProperteis) throws DbException {
        this.configInterceptorForMehtod("delBy",
                Object[].class, Object[].class);
        int[] ret = null;
        try {
            return ret = this.excuteDeleteClass(deleteObjects, getPNames(whereProperteis));
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }

    }

    @Override
    public <T> int insertBy(T insertObject, boolean includeNull) throws DbException {
        this.configInterceptorForMehtod("insertBy", Object.class, boolean.class);
        Integer ret = null;
        try {
            return ret = this.excuteInsertClass(insertObject, null, !includeNull);
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    @Override
    public String exeScript(Reader reader, boolean throwWarning, Map<String, Object> args) throws DbException {

        this.configInterceptorForMehtod("exeScript", Reader.class, boolean.class, Map.class);
        String ret = null;
        try {
            ret = this.exeBatchSQLTemplate(() -> {
                try {
                    this.sqlType = SQLType.SCRIPT;
                    this.fetchConnection();
                    ScriptRunner scriptRunner = new ScriptRunner(this.getConnection(false));
                    scriptRunner.setThrowWarning(throwWarning);
                    scriptRunner.setRemoveCRs(true);
                    scriptRunner.setArgs(args);
                    return scriptRunner.runScriptByLine(reader);
                } catch (Exception e) {
                    if (e instanceof DbException) throw (DbException) e;
                    throw new DbException(e);
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e2) {
                        log.error("", e2);
                    }
                }
            });
            return ret;
        } catch (Exception e) {
            this.configInterceptorForException(e);
            if (e instanceof DbException) throw e;
            throw new DbException(e);
        } finally {
            this.configInterceptorForPostExecute(ret);
        }


    }

    private <R> R exeBatchSQLTemplate(Supplier<R> function) {
        Boolean backAutoCommit = null;
        boolean error = true;
        R ret = null;
        try {
            backAutoCommit = this.getAutoCommit();
            if (this.getAutoCommit()) {
                this.setAutoCommit(false);//暂时设置为false，使之成为事务性操作
            }
            ret = function.get();
            error = false;
            if (backAutoCommit) {
                if (error) {
                    this.rollback();
                    throw new DbException("执行错误!");
                } else {
                    this.commit();
                }
            }

        } catch (Exception e) {
            if (backAutoCommit) {
                this.rollback();
            }
            throw e;

        } finally {
            try {
                if (this.conn != null) {
                    if (backAutoCommit) {
                        this.close();
                        this.setAutoCommit(true);
                    } else {
                        this.closer();
                    }
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return ret;
    }

    public class ScriptRunner {

        private final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

        private final String DEFAULT_DELIMITER = ";";
        private final String DELIMITER_LINE_START = "DELIMITER ";
        private final Pattern DELIMITER_PATTERN = Pattern
                .compile("^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE);

        private final Connection connection;
        private boolean throwWarning = true;
        private boolean removeCRs = true;
        private boolean escapeProcessing = true;

        private StringWriter resultWriter = new StringWriter();
        private PrintWriter resultPrintWriter = new PrintWriter(resultWriter);
        private String transactionId = "";
        private Map<String, Object> args = null;
        private String delimiter = DEFAULT_DELIMITER;

        public ScriptRunner(Connection connection) {
            this.connection = connection;
            this.transactionId = System.currentTimeMillis() + "-" + RandomUtils.getRandomNumberString(6);
        }

        public Map<String, Object> getArgs() {
            return args;
        }

        public void setArgs(Map<String, Object> args) {
            this.args = args;
        }

        public void setThrowWarning(boolean throwWarning) {
            this.throwWarning = throwWarning;
        }


        public void setRemoveCRs(boolean removeCRs) {
            this.removeCRs = removeCRs;
        }

        public void setEscapeProcessing(boolean escapeProcessing) {
            this.escapeProcessing = escapeProcessing;
        }


        public void setDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }

        public String runScriptByLine(Reader reader) {
            return executeLineByLine(reader);
        }

        private String executeLineByLine(Reader reader) {

            StringBuilder command = new StringBuilder();
            try {
                BufferedReader lineReader = new BufferedReader(reader);
                String line;
                while ((line = lineReader.readLine()) != null) {
                    if(!handleLine(command, line)){
                        return null;
                    };
                }
                checkForMissingLineTerminator(command);
                return this.resultWriter.toString();
            } catch (Exception e) {
                if (e instanceof DbException) throw (DbException) e;
                throw new DbException(e);
            }
        }

        private void checkForMissingLineTerminator(StringBuilder command) {
            if (command != null && command.toString().trim().length() > 0) {
                throw new DbException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
            }
        }

        private boolean handleLine(StringBuilder command, String line) throws Exception {
            String trimmedLine = line.trim();
            if (lineIsComment(trimmedLine)) {
                Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
                if (matcher.find()) {
                    delimiter = matcher.group(5);
                }
            } else if (delimiterLine(trimmedLine)) {
                String delimiterStr = StringUtils.substring(trimmedLine, DELIMITER_LINE_START.length()).trim();
                this.delimiter = delimiterStr;
            } else if (commandReadyToExecute(trimmedLine)) {
                int lastDelimiterPos = line.lastIndexOf(delimiter);
                command.append(line.substring(0, lastDelimiterPos));
                command.append(LINE_SEPARATOR);
                executePreparedStatement(command.toString());
                command.setLength(0);

            } else if (trimmedLine.length() > 0) {
                command.append(line);
                command.append(LINE_SEPARATOR);
            }
            return true;
        }

        private boolean lineIsComment(String trimmedLine) {
            return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
        }

        private boolean delimiterLine(String trimmedLine) {
            if (DataBaseImpl.this.getDataBaseType().isMySqlFamily()) {
                return StringUtils.startsWithIgnoreCase(trimmedLine, DELIMITER_LINE_START);
            } else {
                return false;
            }

        }

        private boolean commandReadyToExecute(String trimmedLine) {
            if (trimmedLine.endsWith(delimiter)) return true;
            return false;
        }

        private void executePreparedStatement(String command) throws Exception {

            PreparedStatement statement = null;
            try {
                boolean handArgs = false;
                String source = "";
                String sqltext = command.trim();
                if (removeCRs) {
                    sqltext = sqltext.replaceAll("\r\n", "\n");
                }

                if (this.args != null && args.size() > 0) {
                    ScriptOption scriptOption = (ScriptOption) this.args.get(ScriptOption.class.getName());
                    if (scriptOption != null) {
                        source = scriptOption.getSource();
                        if (scriptOption.isFromMDMethod()) {
                            handArgs = true;
                        }
                    }
                }
                String preparedSql = sqltext;
                NSQL nsql = null;
                if (handArgs) {
                    nsql = NSQL.getNSQL(sqltext, source, this.args);
                    preparedSql = nsql.getExeSql();
                }
                statement = DataBaseImpl.this.getPreparedStatement(preparedSql);
                if (handArgs) {
                    String paramStr = SqlUtils.setToPreStatment(nsql.getArgs(), statement);
                }
                statement.setEscapeProcessing(escapeProcessing);
                String debugSql = "";
                if (handArgs) {
                    Collection<Object> vParameters = CollectionUtils.getSortedValues(nsql.getArgs());
                    debugSql = SqlUtils.generateDebugSql(preparedSql, vParameters, DataBaseImpl.this.getDataBaseType());
                } else {
                    debugSql = preparedSql;
                }
                if (DbContext.getDebugSQLListener() != null) {
                    DbContext.getDebugSQLListener().accept(debugSql);
                }

                if(DbContext.getDBInterceptor()!=null){
                    DbContext.getDBInterceptorInfo().setDebugSql(debugSql);
                    boolean ret=DbContext.getDBInterceptor().beforeDbOperationExeute(DataBaseImpl.this,true,debugSql);
                    if(!ret){
                        throw new DbException("被拦截", CODE.Intercepted);
                    }
                }
                if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
                    if (handArgs) {
                        log.debug("transId:" + transactionId + ":debugSql[" + debugSql + "]");
                    } else {
                        log.debug("transId:" + transactionId + ":debugSql[" + debugSql + "]");
                    }
                }
                long start = System.currentTimeMillis();
                boolean hasResults = statement.execute();
                if (log.isDebugEnabled() && DbContext.permitDebugLog()) {

                    log.debug("sql执行:" + (System.currentTimeMillis() - start) + (!hasResults ? ",更新条数:" + statement.getUpdateCount() : ""));
                }
                while (!(!hasResults && statement.getUpdateCount() == -1)) {
                    checkWarnings(statement);
                    printResults(statement, hasResults);
                    hasResults = statement.getMoreResults();
                }

            } catch (SQLWarning e) {
                throw e;
            } catch (SQLException e) {
                throw e;
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (Exception e) {
                    log.error("" + e, e);
                }
            }
        }


        private void checkWarnings(Statement statement) throws SQLException {
            // In Oracle, CREATE PROCEDURE, FUNCTION, etc. returns warning
            // instead of throwing exception if there is compilation error.
            SQLWarning warning = statement.getWarnings();
            if (warning != null) {
                if (!throwWarning) {
                    log.warn("" + warning);
                } else {
                    throw warning;
                }
            }
        }

        private void printResults(Statement statement, boolean hasResults) {
            if (!hasResults) {
                return;
            }
            try (ResultSet rs = statement.getResultSet()) {
                if (rs == null) {
                    return;
                }
                ResultSetPrinter.printResultSet(rs, resultPrintWriter);

            } catch (Exception e) {
                if (e instanceof DbException) throw (DbException) e;
                throw new DbException("Error printing results: " + e, e);
            }
        }

        private void resultPrint(Object o) {
            if (resultPrintWriter != null) {
                resultPrintWriter.print(o);
                resultPrintWriter.flush();
            } else {
            }
        }

        private void resultPrintln(Object o) {
            if (resultPrintWriter != null) {
                resultPrintWriter.println(o);
                resultPrintWriter.flush();
            } else {
            }
        }


    }
}
