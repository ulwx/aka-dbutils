package com.github.ulwx.database;

import com.github.ulwx.tool.support.*;
import com.github.ulwx.database.cacherowset.com.sun.rowset.CachedRowSetImpl;
import com.github.ulwx.database.dbpool.DBPoolFactory;
import com.github.ulwx.database.page.interceptor.SQLHelp;
import com.github.ulwx.database.page.model.DBMS;
import com.github.ulwx.database.sql.BeanUtils;
import com.github.ulwx.database.sql.SqlUtils;
import com.github.ulwx.database.utils.DbConst;
import com.github.ulwx.tool.PageBean;
import com.github.ulwx.tool.support.type.TInteger;
import com.github.ulwx.tool.support.type.TResult2;
import com.github.ulwx.tool.support.type.TString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.*;
import java.util.*;

/**
 * <code>DataBase</code>类用于封装了执行数据库的一些操作，功能强大，具有从sql语句到对象的映射功能，<br/>
 * 如果使用对象映射功能的方法，所有的映射对象的类可以通过 {@link SqlUtils}类的
 * exportTables方法生成 <br/>
 * <p>
 * DataBase对象不是线程安全的，这就意味着多个线程通常使用一个DataBase对象会出现不安全隐患。记住，你要自己处理线程
 * 安全问题，你可以通过ThreadLocal或线程同步来解决多个线程访问一个DataBase对象
 * </p>
 * <br/>
 * <p>
 * 用法1：
 * <p>
 * <blockquote>
 *
 * <pre>
 * String sql = &quot;select * from photos p where p.newsid = ?&quot; + &quot; and (p.audi_stat = ? or p.audi_stat = ?)&quot;;
 *
 * List list = new ArrayList();
 *
 * try {
 * 	Map&lt;Integer, Object&gt; args = new HashMap&lt;Integer, Object&gt;();
 * 	args.put(1, newsid);
 * 	args.put(2, 1);
 * 	args.put(3, 4);
 * 	list = db.doQueryClass(Photos.class, sql, args);
 * } catch (DbException e) {
 * 	//
 * 	log.error(&quot;&quot;, e);
 * }
 * </pre>
 *
 * </blockquote>
 * </p>
 * 用法2：
 * <p>
 * <blockquote>
 *
 * <pre>
 * DataBase db = DataBaseFactory.getDataBase();
 * String sql = "select * from news n where n.type =? and id=? and audi=1 order by n.id desc ";
 *
 * Map<Integer, Object> args = new HashMap<Integer, Object>();
 *
 * args.put(1, Integer.valueOf(100));
 * args.put(2, 234);
 * int page=2//当前第几页
 * int perpage=20//每页20条
 * PageBean pageBean = new PageBean();//用于存放分页信息，包括总页数，当前多少页，总共多少行等
 * list = db.doPageQueryClass(News.class, sql, args, page, perpage, pageBean, null);
 * int maxPage=pageBean.getMaxPage();//最大页
 * int curPage =pageBean.getPage();//当前页
 * int prePage =pageBean.getPrevPage();//前一页
 * </pre>
 *
 * </blockquote>
 * </p>
 * 用法3:
 * <p>
 * <blockquote>
 *
 * <pre>
 * DataBase db = DataBaseFactory.getDataBase();
 * String sqlx = &quot;select * from news where id=? and name=?&quot;;
 * Map&lt;Integer, Object&gt; vparms = new HashMap&lt;Integer, Object&gt;();
 * vparms.put(1, 23);
 * vparms.put(2, &quot;娱乐&quot;);
 * PageBean pageBean = new PageBean();// 用于存放分页信息，包括总页数，当前多少页，总共多少行等
 * int page = 2;// 当前页
 * int perPage = 20;// 每页多少行
 * DataBaseSet rs = db.doCachedPageQuery(sqlx, vparms, page, perPage, pageBean, null);
 *
 * while (rs.next()) {
 * 	String name = rs.getString(&quot;name&quot;);
 * }
 * int maxPage = pageBean.getMaxPage();// 最大页
 * int curPage = pageBean.getPage();// 当前页
 * int prePage = pageBean.getPrevPage();// 前一页
 * </pre>
 *
 * </blockquote>
 * </p>
 * 用法4:
 * <p>
 * <blockquote>
 *
 * <pre>
 * String sql = &quot;update product_version set action=? where id=?&quot;;
 * DataBase db = null;
 *
 * db = DataBaseFactory.getDataBase();
 * List&lt;Map&lt;Integer, Object&gt;&gt; list = new ArrayList&lt;Map&lt;Integer, Object&gt;&gt;();
 * for (int i = 0; i &lt; ids.length; i++) {
 * 	Map&lt;Integer, Object&gt; map = new HashMap&lt;Integer, Object&gt;();
 * 	map.put(1, action);
 * 	map.put(2, ids[i]);
 * 	list.add(map);
 * }
 * int[] results = db.executeBindBatch(sql, list);
 *
 * </pre>
 *
 * </blockquote>
 * </p>
 * 用法5:
 * <p>
 * <blockquote>
 *
 * <pre>
 * public static int deleteBy(int[] ids) {
 *
 * 	StringBuilder sql = new StringBuilder(
 * 			&quot;delete from platform where id=? and id not in (select platformid &quot; + &quot; from productinfo)&quot;);
 * 	DataBase db = null;
 *
 * 	try {
 * 		List&lt;Map&lt;Integer, Object&gt;&gt; vParametersList = new ArrayList&lt;Map&lt;Integer, Object&gt;&gt;();
 *
 * 		for (int i = 0; i &lt; ids.length; i++) {
 * 			Map&lt;Integer, Object&gt; args = new HashMap&lt;Integer, Object&gt;();
 * 			args.put(1, ids[i]);
 * 			vParametersList.add(args);
 *        }
 * 		db = DataBaseFactory.getDataBase();
 * 		int[] is = db.executeBindBatch(sql.toString(), vParametersList);
 * 		if (is == null) {
 * 			return 0;
 *        } else {
 * 			for (int i = 0; i &lt; is.length; i++) {
 * 				if (is[i] == 0) {
 * 					return 0;
 *                }
 *            }
 *        }
 * 		return 1;
 *
 *    } catch (DbException e) {
 * 		//
 * 		// e.printStackTrace();
 * 		log.error(&quot;&quot;, e);
 *    }
 * 	return 0;
 * }
 * </pre>
 *
 * </blockquote>
 * </p>
 * 用法6: <br/>
 * 如果涉及到事务性操作，可以设置db.setAutoCommit(false)见下面的例子，用户可以显式控制事务性操作，事务性操作完成后一定要在最后通过db
 * .close()关闭数据库连接，<br/>
 * 否则会用完连接；如果不是事务性操作，不需要通过db.close()，因为每次调用后都
 * <p>
 * <blockquote>
 *
 * <pre>
 * public static int add(Version p) {
 *
 * 	String sql = &quot;insert into versions(version) values(?)&quot;;
 * 	Map&lt;Integer, Object&gt; args = new HashMap&lt;Integer, Object&gt;();
 * 	args.put(1, p.getVersion());
 * 	DataBase db = null;
 * 	try {
 * 		db = DataBaseFactory.getDataBase();
 * 		db.setAutoCommit(false);
 * 		int i = db.executeBindInsert(sql, args);
 * 		String sql1 = &quot;update versions set bakid=&quot; + i + &quot; where id=&quot; + i;
 * 		i = db.executeBindUpdate(sql1, null);
 * 		db.commit();
 * 		return i;
 *    } catch (DbException e) {
 * 		//
 * 		e.printStackTrace();
 * 		try {
 * 			db.rollback();
 *        } catch (DbException e1) {
 *        }
 * 		log.error(&quot;&quot;, e);
 *    } finally {
 * 		db.close();
 *    }
 * 	return 0;
 * }
 * </pre>
 *
 * </blockquote>
 * </p>
 * 用法7:<br/>
 * <p>
 * <blockquote>
 *
 * <pre>
 * try {
 *
 * 	String sql = &quot;select n.*,p.*,a.* from news n left outer join photos p&quot;
 * 			+ &quot; on   n.id=p.newsid  left outer join archives a &quot;
 * 			+ &quot; on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc&quot;;
 *
 * 	Map&lt;Integer, Object&gt; vParameters = new HashMap&lt;Integer, Object&gt;();
 * 	vParameters.put(1, 1232);
 * 	DataBase db = DataBaseFactory.getDataBase();
 * 	QueryMapNestOne2Many query1 = new QueryMapNestOne2Many();
 * 	query1.set(Photos.class, &quot;photosList&quot;, &quot;id&quot;, &quot;p.&quot;, null);
 * 	QueryMapNestOne2Many query2 = new QueryMapNestOne2Many();
 * 	query2.set(Archives.class, &quot;archivesList&quot;, &quot;id&quot;, &quot;a.&quot;, null);
 *
 * 	list = db.doQueryClassOne2Many(News.class, &quot;n.&quot;, &quot;id&quot;, sql, vParameters,
 * 			new QueryMapNestOne2Many[] { query1, query2 });
 *
 * } catch (Exception ex) {
 * 	ex.printStackTrace();
 * }
 * </pre>
 *
 * </blockquote>
 * </p>
 */
public class DataBaseImpl implements DataBase {
    private static Logger log = LoggerFactory.getLogger(DataBase.class);
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    private CallableStatement cs = null;
    private boolean mainSlaveMode = false;
    private boolean needAutoReconnect = false;
    private DataSource dataSource;

    @Override
    public boolean isAutoReconnect() {

        return needAutoReconnect;
    }

    private boolean isAutoCommit = true;

    @Override
    public void setAutoReconnect(boolean autoReconnect) throws DbException {
        this.needAutoReconnect = autoReconnect;
    }

    private SQLType sqlType = SQLType.SELECT;
    private MainSlaveModeConnectMode mainSlaveModeConnectMode = MainSlaveModeConnectMode.Try_Connect_MainServer;

    private String dataBaseType = DataBaseType.OTHER;
    private int dataBaseMajorVersion = 0;

    private String dbPoolName = "";

    private boolean externalControlConClose = false;

    @Override
    public String getDbPoolName() {
        return dbPoolName;
    }

    /*
     * POOL：从连接池里获得连接
     */
    public static enum ConnectType {
        POOL, DATASOURCE, CONNECTION
    }

    ;

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

    // private
    private ConnectType connectType = null;

    private void setInteralConnectionAutoCommit(boolean autoCommit) throws DbException {
        try {

            this.conn.setAutoCommit(autoCommit);
        } catch (Exception ex) {
            throw new DbException(ex);
        }
    }

    @Override
    public boolean getInternalConnectionAutoCommit() throws DbException {
        try {

            return this.conn.getAutoCommit();
        } catch (Exception ex) {
            throw new DbException(ex);
        }
    }

    public DataBaseImpl() {
    }

    /**
     * 如果数据库是主从式模式，则语句为查询语句并且是非事务性的时候，则选择从库查询
     */
    @Override
    public void selectSlaveDb() throws DbException {
        long start = System.currentTimeMillis();
        if (this.connectType == ConnectType.POOL && this.isMainSlaveMode()) {
            this.close();
            log.debug("1:" + (System.currentTimeMillis() - start));
            DataSource ds = DBPoolFactory.getInstance().getSlaveDbPool(this.dbPoolName);
            log.debug("2:" + (System.currentTimeMillis() - start));
            if (ds != null) {
                log.debug("3:" + (System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
                try {
                    conn = ds.getConnection();
                } catch (SQLException throwables) {
                    throw new DbException(throwables.getMessage(), throwables);
                }
                this.dataSource = ds;
                log.debug("use Slave Server!,connect time:" + (System.currentTimeMillis() - start) + " 毫秒");
            }

        }

    }

    @Override
    public String getDataBaseType() {
        return this.dataBaseType;
    }

    private void setDataBaseType(Connection conn) throws DbException {

        try {
            DatabaseMetaData dm = conn.getMetaData();
            String url = dm.getURL().trim().toLowerCase();

            String databaseProductName = "";

            databaseProductName = dm.getDatabaseProductName().toLowerCase();

            try {
                this.dataBaseMajorVersion = dm.getDatabaseMajorVersion();

            } catch (Throwable e) {

                this.dataBaseMajorVersion = 0;
                log.error("++++++");
            }
            if (databaseProductName.contains(DataBaseType.MYSQL)) {

                this.dataBaseType = DataBaseType.MYSQL;
            } else if (databaseProductName.contains(DataBaseType.MS_SQL_SERVER)) {

                if (dataBaseMajorVersion <= 8)
                    this.dataBaseType = DataBaseType.MS_SQL_SERVER;
                else {
                    this.dataBaseType = DataBaseType.MS_SQL_SERVER_2005;
                }

            } else if (databaseProductName.contains(DataBaseType.ORACLE)) {

                this.dataBaseType = DataBaseType.ORACLE;
            } else {

                this.dataBaseType = DataBaseType.OTHER;

            }

        } catch (Exception e) {
            throw new DbException("get pool connection error!", e);
        }
    }

    public DataSource getDataSourceFromPool(String dbPoolName) throws DbException {

        Map<String, String> map = new HashMap<String, String>();
        DataSource datasource = DBPoolFactory.getInstance().getDBPool(dbPoolName, map);

        return datasource;
    }

    @Override
    public void connectDb(Connection connection, boolean externalControlConClose) {
        this.connectType = ConnectType.CONNECTION;
        this.externalControlConClose = externalControlConClose;
        if (conn != null)
            return;
        long start0 = System.currentTimeMillis();
        this.setMainSlaveMode(false);
        this.mainSlaveModeConnectMode = MainSlaveModeConnectMode.Connect_MainServer;
        String msg = "";
        msg = "获得数据库库链接";
        this.conn = connection;
        log.debug(Thread.currentThread().getId() + ":" + ":connect:" + msg + ",connect time:"
                + (System.currentTimeMillis() - start0) + " 毫秒");
        this.setDataBaseType(conn);
        this.dataSource = dataSource;
        this.dbPoolName = "";
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
            //非主从模式
            this.setMainSlaveMode(false);
            this.mainSlaveModeConnectMode = MainSlaveModeConnectMode.Connect_MainServer;
            long start0 = System.currentTimeMillis();
            String msg = "";
            msg = "获得数据库库链接";
            conn = dataSource.getConnection();
            log.debug(Thread.currentThread().getId() + ":" + dataSource.toString() + ":connect:" + msg + ",connect time:"
                    + (System.currentTimeMillis() - start0) + " 毫秒");
            this.setDataBaseType(conn);
            this.dataSource = dataSource;
            this.dbPoolName = "";

        } catch (Exception e) {
            throw new DbException("get pool connection error!", e);
        }
    }

    @Override
    public void connectDb(String dbPoolName) throws DbException {
        this.connectDb(dbPoolName, MainSlaveModeConnectMode.Try_Connect_MainServer);
    }

    public ConnectType getConnectionType() {
        return this.connectType;
    }


    /**
     * 从dbpool.xml里设置的连接池获得连接
     *
     * @param dbPoolName 对应于dbpool.xml里的元素dbpool的name属性值
     * @throws DbException
     */
    @Override
    public void connectDb(String dbPoolName, MainSlaveModeConnectMode mainSlaveModeConnectMode) throws DbException {

        this.connectType = ConnectType.POOL;
        this.dbPoolName = dbPoolName;
        try {
            if (conn != null)
                return;
            // 设置是否是主从模式
            this.setMainSlaveMode(DBPoolFactory.getInstance().isMainSlaveMode(dbPoolName));
            this.mainSlaveModeConnectMode = mainSlaveModeConnectMode;

            long start0 = System.currentTimeMillis();
            String msg = "";
            if (this.isMainSlaveMode() && mainSlaveModeConnectMode == MainSlaveModeConnectMode.Connect_SlaveServer) {// 如果是主从模式，并将是连接从库
                this.selectSlaveDb();
                msg = "获得从库链接";
            } else {
                msg = "获得主库链接";
                DataSource datasource = this.getDataSourceFromPool(dbPoolName);
                conn = datasource.getConnection();
                this.dataSource = datasource;
            }
            log.debug(Thread.currentThread().getId() + ":create a new db:" + this.dbPoolName + ":connect:" + msg + ",connect time:"
                    + (System.currentTimeMillis() - start0) + " 毫秒");
            // System.out.println("----------"+conn.getTransactionIsolation());
            this.setDataBaseType(conn);

        } catch (Exception e) {
            throw new DbException("get pool connection error!", e);
        }

    }

    /**
     * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭
     *
     * @param sqlQuery    sql语句
     * @param vParameters 参数
     * @return 返回查询到的结果集
     * @throws DbException
     */

    private DataBaseSet doCachedQuery(String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        return new DataBaseSet(doCachedQuery(sqlQuery, args));
    }

    private static Map<Integer, Object> toMap(Object... vParameters) {

        Map<Integer, Object> map = new HashMap<Integer, Object>();
        for (int i = 0; i < vParameters.length; i++) {
            map.put(i, vParameters[i]);
        }
        return map;

    }

    /**
     * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
     *
     * @param sqlQuery
     * @param vParameters
     * @param page        当前页
     * @param perPage     每页多少行
     * @param pageBean    返回的分页信息
     * @param countSql    查询总数的sql语句，根据它查询总数；也可以指定一个整数字符串，用于指定总数；
     *                    如果指定null或""字符串，那么系统会根据sqlQuery自动生成查询总数sql语句
     * @return
     * @throws DbException
     */
    @Override
    public DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                                         PageBean pageBean, String countSql) throws DbException {

        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        return new DataBaseSet(
                this.doCachedPageQuery(sqlQuery, args, page, perPage, pageBean, this.getAutoCommit(), countSql));
    }

    @Override
    public DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        Collection<Object> args = CollectionUtils.getSortedValues(vParameters);
        return new DataBaseSet(doCachedQuery(sqlQuery, args));
    }


    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
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

        TInteger rsStart = new TInteger();
        TInteger rsEnd = new TInteger();
        String pageSql = this.getPagedSql(sqlQuery, vParameters, page, perPage, pageBean, countSql, rsStart, rsEnd);
        if (StringUtils.isEmpty(pageSql)) {
            return new ArrayList<T>();
        }
        return this.doQueryObject(pageSql, vParameters, rowMapper, true, rsStart.getValue(), rsEnd.getValue());

    }

    @Override
    public <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageBean,
                                 RowMapper<T> rowMapper, String countSql) throws DbException {
        return this.doPageQueryObject(sqlQuery, args, page, perPage, pageBean, rowMapper, countSql);
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
        return this.doPageQueryMap(sqlQuery, args, page, perPage, pageBean, countSql);
    }

    private RowSet doCachedQuery(String sqlQuery, Collection<Object> vParameters) throws DbException {
        return doCachedQuery(sqlQuery, vParameters, this.getAutoCommit());
    }

    private CachedRowSet doCachedPageQuery(String sqlQuery, Collection<Object> vParameters, int page, int perPage,
                                           PageBean pageBean, boolean close, String countSql) throws DbException {

        CachedRowSet crs = null;
        try {
            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }

            TInteger rsStart = new TInteger();
            TInteger rsEnd = new TInteger();
            String pageSql = this.getPagedSql(sqlQuery, vParameters, page, perPage, pageBean, countSql, rsStart,
                    rsEnd);
            if (StringUtils.isEmpty(pageSql)) {
                return new CachedRowSetImpl();
            }
            ResultSet rs = (this.doQuery(pageSql, vParameters)).getResultSet();
            this.rs = rs;

            crs = this.getCachedRowSet(rs, true, rsStart.getValue(), rsEnd.getValue());

        } catch (Exception e) {
            // log.error("", e);
            crs = null;
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
        return crs;

    }

    private CachedRowSet doCachedQuery(String sqlQuery, Collection<Object> vParameters, boolean close)
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
        return crs;

    }

    /**
     * 调用此方法不会关闭底层连接和结果集,需要调用DataBase.close()方法关闭它们
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

        return this.doQueryClassOne2One(clazz, null, sqlQuery, vParameters, (QueryMapNestOne2One[]) null);

    }

    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException {
        return this.doQueryClass(clazz, sqlQuery, vParameters);
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
        return this.doQueryClassOne(clazz, sqlQuery, vParameters);
    }


    private <T> List<T> doQueryClassNoSql(Class<T> clazz, T selectObject, String selectProperties) throws DbException {
        String sql = "";
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = selectObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            sql = SqlUtils.generateSelectSql(this.dbPoolName, selectObject, reflectClass, selectProperties, vParameters,
                    DataBaseKeyMap.getMap(), this.getDataBaseType());
            DbContext.clearReflectClass();
        } catch (Exception e) {
            throw new DbException(e);
        }
        return doQueryClass(clazz, sql, vParameters);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> doQueryClassNoSql(T selectObject) throws DbException {
        String sql = "";
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = selectObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            sql = SqlUtils.generateSelectSqlBySelectObject(this.dbPoolName, selectObject, reflectClass, vParameters,
                    DataBaseKeyMap.getMap(), this.getDataBaseType());
            DbContext.clearReflectClass();
        } catch (Exception e) {
            throw new DbException(e);
        }
        return doQueryClass((Class<T>) selectObject.getClass(), sql, vParameters);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> doQueryClassNoSql(T selectObject, String selectProperties) throws DbException {

        return this.doQueryClassNoSql((Class<T>) selectObject.getClass(), selectObject, selectProperties);
    }

    /**
     * 一到一关联映射查询，调用此方法会默认自动关闭底层数据库连接，
     *
     * @param <T>              需映射的主类的Class对象
     * @param clazz            需映射到类的Class对象
     * @param sqlPrefix        sql前缀
     *                         <p>
     *                         <blockquote>
     *
     *                         <pre>
     *                                                 String sql = &quot;select n.*,p.*,a.* from news n left outer join photos p&quot;
     *                                                 		+ &quot; on   n.id=p.newsid  left outer join archives a &quot;
     *                                                 		+ &quot; on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc&quot;;
     *                                                 </pre>
     *
     *                         </blockquote>
     *                         </p>
     *                         <br/>
     *                         如果主类对应于n表(news)，那么sql前缀为"n."
     * @param sqlQuery         sql语句
     * @param vParameters      参数
     * @param queryMapNestList 一到一映射关系，可以有多个映射关系，一个映射关系对应一个QueryMapNestOne2One对象
     * @return 返回查询到的列表
     * @throws DbException
     */
    private <T> List<T> doQueryClassOne2One(Class<T> clazz, String sqlPrefix, String sqlQuery,
                                            Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList) throws DbException {
        return this.doQueryClass(clazz, sqlQuery, vParameters, queryMapNestList, sqlPrefix, null, this.getAutoCommit(),
                false, 0, 0);

    }

    public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
                                 QueryMapNestOne2One[] queryMapNestList) throws DbException {
        return this.doQueryClassOne2One(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList);

    }

    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
     *
     * @param clazz       需映射类的Class对象
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
    public <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                                 PageBean pageBean, String countSql) throws DbException {
        return this.doPageQueryClass(clazz, sqlQuery, vParameters, page, perPage, pageBean, countSql);
    }

    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
     *
     * @param <T>
     * @param clazz            需映射主类的Class对象
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

        TInteger rsStart = new TInteger();
        TInteger rsEnd = new TInteger();
        String pageSql = this.getPagedSql(sqlQuery, args, page, perPage, pageBean, countSql, rsStart, rsEnd);
        if (StringUtils.isEmpty(pageSql)) {
            return new ArrayList<T>();
        }
        return this.doQueryClass(clazz, pageSql, vParameters, queryMapNestList, sqlPrefix, null, this.getAutoCommit(),
                true, rsStart.getValue(), rsEnd.getValue());

    }

    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
                                 QueryMapNestOne2One[] queryMapNestList, int page, int perPage, PageBean pageBean, String countSql)
            throws DbException {
        return this.doPageQueryClass(clazz, sqlPrefix, sqlQuery, vParameters, queryMapNestList, page, perPage,
                pageBean, countSql);
    }

    /**
     * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
     *
     * @param <T>
     * @param clazz            需映射主类的Class对象
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
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param clazz            需映射的主类Class对象
     * @param sqlPrefix        sql前缀
     * @param beanKey          为主类对应的主键属性名， 如果主键为复合主键，以英文逗号隔开。
     * @param sqlQuery         sql语句
     *                         <p>
     *                         <blockquote>
     *
     *                         <pre>
     *                                                 String sql = &quot;select n.*,p.*,a.* from news n left outer join photos p&quot;
     *                                                 		+ &quot; on   n.id=p.newsid  left outer join archives a &quot;
     *                                                 		+ &quot; on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc&quot;;
     *                                                 </pre>
     *
     *                         </blockquote>
     *                         </p>
     *                         <br/>
     *                         如果主类对应于n表(news)，那么sql前缀为"n."
     * @param vParameters      参数
     * @param queryMapNestList 关联子类关系
     * @return 返回查询结果
     * @throws DbException
     */
    private <T> List<T> doQueryClassOne2Many(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
                                             Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException {

        List<T> list = this.doQueryClass(clazz, sqlQuery, vParameters, queryMapNestList, sqlPrefix, beanKey,
                this.getAutoCommit());
        // 过滤，并进行子类赋值
        for (int m = 0; m < list.size(); m++) {
            Object bean = list.get(m);
            String parentKeys = BeanUtils.getKeyValue(bean, beanKey);

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
    public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
                                 Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
        return this.doQueryClassOne2Many(clazz, sqlPrefix, beanKey, sqlQuery, vParameters, queryMapNestList);
    }

    private <T> List<T> doQueryObject(String sqlQuery, Collection<Object> vParameters, RowMapper<T> rowMapper,
                                      boolean isPageQuery, int rsStart, int rsEnd) throws DbException {

        boolean close = this.getAutoCommit();

        List<T> results = new ArrayList();
        try {
            // Collection<Object> vParameters
            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }
            DataBaseSet dbset = this.getResultSet(sqlQuery, vParameters);
            this.rs = dbset.getResultSet();

            if (isPageQuery) {
                if (rsStart <= 0)
                    rs.beforeFirst();
                else
                    rs.absolute(rsStart);
            } else {
                rs.beforeFirst(); // 如果还要用结果集，就把指针再移到初始化的位置
            }
            int index = 0;
            int count = rsEnd - rsStart;
            T obj = null;

            while (dbset.next()) {
                if (isPageQuery) {
                    if (index++ >= count) {
                        break;
                    }

                }
                obj = rowMapper.mapRow(dbset);
                results.add(obj);
            }
            return results;
        } catch (Exception e) {
            // log.error("", e);
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
     * 自定义映射查询
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
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

        return this.doQueryObject(sqlQuery, vParameters, rowMapper, false, 0, 0);

    }

    @Override
    public <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException {
        return this.doQueryObject(sqlQuery, args, rowMapper);
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
        return this.doQueryMap(sqlQuery, args);
    }

    private <T> List<T> doQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                     QueryMapNestOne2One[] queryMapNestList, String sqlPrefix, String beanKey, boolean close,
                                     boolean isPageQuery, int rsStart, int rsEnd) throws DbException {

        ArrayList<T> list = new ArrayList<>();
        Map<String, T> keyMapList = new LinkedHashMap<>();
        if (sqlPrefix == null)
            sqlPrefix = "";
        try {

            DataBaseSet rs = this.doQuery(sqlQuery, vParameters);

            this.rs = rs.getResultSet();

            if (isPageQuery) {

                if (rsStart <= 0)
                    rs.beforeFirst();
                else
                    rs.absolute(rsStart);
            } else {
                rs.beforeFirst(); // 如果还要用结果集，就把指针再移到初始化的位置
            }
            int index = 0;
            int count = rsEnd - rsStart;
            while (rs.next()) {
                try {
                    if (isPageQuery) {

                        if (index++ >= count) {
                            break;
                        }

                    }

                    T bean = clazz.newInstance();
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
                            value = SqlUtils.getValueFromResult(this.dbPoolName, t, sqlPrefix, name, rs.getResultSet(),
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
                                String[] toPros = queryMapNest.getToPros();

                                if (mapRelation == MapNest.ONE_TO_ONE) {

                                    if (name.equals(toPropertyName)) {
                                        Class<?> nestedClass = PropertyUtil.getPropertyType(bean, toPropertyName);

                                        Object nestedObj = nestedClass.newInstance();

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

                    if (beanKey == null) {
                        list.add(bean);
                    } else {
                        String kv = BeanUtils.getKeyValue(bean, beanKey);
                        keyMapList.put(kv, bean);
                    }

                } catch (Exception e) {
                    // log.error("", e);
                    throw new DbException(e);

                }
            } // while

        } catch (Exception e) {
            // log.error("", e);
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
        if (beanKey == null) {
            return list;
        } else {
            Collection<T> coValues = keyMapList.values();
            list.clear();
            list.addAll(coValues);
            return list;
        }

    }

    private <T> List<T> doQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                     QueryMapNestOne2Many[] queryMapNestList, String sqlPrefix, String beanKey, boolean close)
            throws DbException {

        ArrayList<T> list = new ArrayList<T>();
        Map<String, T> keyList = new LinkedHashMap<>();
        if (sqlPrefix == null)
            sqlPrefix = "";
        try {

            DataBaseSet rs = this.doQuery(sqlQuery, vParameters);

            this.rs = rs.getResultSet();

            rs.beforeFirst(); // 如果还要用结果集，就把指针再移到初始化的位置

            while (rs.next()) {
                try {

                    T bean = clazz.newInstance();
                    // Map<String, Object> nestedObjs = new HashMap<String,
                    // Object>();
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

                            value = SqlUtils.getValueFromResult(this.dbPoolName, t, sqlPrefix, name, rs.getResultSet(),
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
                                String[] toPros = queryMapNest.getToPros();

                                if (mapRelation == MapNest.ONE_TO_MANY) {
                                    if (name.equals(toPropertyName)) {
                                        QueryMapNestOne2Many queryMapNestMany = (QueryMapNestOne2Many) queryMapNest;
                                        String parentKey = beanKey;
                                        Map<String, LinkedHashMap<String, Object>> result = queryMapNestMany
                                                .getResult();
                                        String nestedBeanPropertyKeys = queryMapNestMany.getNestedBeanPropertyKeys();

                                        Class<?> nestedClass = queryMapNestMany.getClassType();
                                        Object nestedObj = nestedClass.newInstance();

                                        nestedObj = BeanUtils.setOne2One(this.dbPoolName, nestedObj, toPros, prefix,
                                                rs);

                                        String parentKeyValue = BeanUtils.getKeyValue(this.dbPoolName, bean, sqlPrefix,
                                                parentKey, rs);
                                        LinkedHashMap<String, Object> nestlist = result.get(parentKeyValue);

                                        if (nestlist == null) {
                                            nestlist = new LinkedHashMap<String, Object>();
                                            result.put(parentKeyValue, nestlist);
                                        }
                                        if (nestedBeanPropertyKeys != null && !nestedBeanPropertyKeys.equals("")) {// //过滤重复的值

                                            String nestKeyValueStr = BeanUtils.getKeyValue(nestedObj,
                                                    nestedBeanPropertyKeys);

                                            if (nestKeyValueStr != null) {
                                                Object find = nestlist.get(nestKeyValueStr);
                                                //

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

                    if (beanKey == null) {
                        list.add(bean);
                    } else {
                        String kv = BeanUtils.getKeyValue(bean, beanKey);
                        keyList.put(kv, bean);
                    }

                } catch (Exception e) {
                    // log.error("", e);
                    throw new DbException(e);

                }
            } // while
        } catch (Exception e) {
            // log.error("", e);
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
        if (beanKey == null) {
            return list;
        } else {
            Collection coValues = keyList.values();
            list.clear();
            list.addAll(coValues);
            return list;
        }

    }

    private String trimTailOrderBy(String sql, TString orderStr) {
        sql = sql.trim();
        String newSql = "";
        String reg = "order\\s+by\\s+\\w+(\\s+(asc|desc))?";
        TInteger startPos = new TInteger();
        boolean isEnd = StringUtils.endsWith(sql, reg, true, startPos);
        if (isEnd) {
            orderStr.setValue(sql.substring(startPos.getValue()));
            newSql = sql.substring(0, startPos.getValue());
        } else {
            orderStr.setValue("");
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
        if (this.dataBaseType == DataBaseType.MS_SQL_SERVER) {

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

        } else {

            maxSql = "select count(1) from (" + sb.toString() + ") t";
        }

        return maxSql;

    }

    private String getPagedSql(String sqlQuery, Collection<Object> vParameters, int page, int perPage,
                               PageBean pageBean, String countSql, TInteger rsStart, TInteger rsEnd) throws DbException {

        if (sqlQuery == null)
            return null;
        String sql = sqlQuery.trim();

        int total = 0;

        try {
            String maxSql = countSql;
            TString orderStr = new TString("");
            String tsql = this.trimTailOrderBy(sqlQuery, orderStr);
            //
            maxSql = StringUtils.trim(maxSql);
            String reg = "\\d+";
            if (maxSql.matches(reg)) {// 说明是数字
                total = Integer.valueOf(maxSql);
            } else {// 说明是查询总行数的sql语句
                if (StringUtils.isEmpty(maxSql)) {
                    maxSql = this.getCountSql(tsql);

                } else {
                    maxSql = this.trimTailOrderBy(maxSql, new TString());
                }

                RowSet rs = this.doCachedQuery(maxSql, vParameters, false);// 不关闭数据库连接

                while (rs != null && rs.next()) {
                    total = rs.getInt(1);
                    break;
                }
            }

            pageBean.doPage(total, page, perPage);

            if (pageBean.isEmpty()) {
                return "";
            }
            int begin = 0;
            int end = 0;
            if (this.dataBaseType == DataBaseType.MYSQL) {
                sql = sqlQuery.trim() + " limit " + pageBean.getStart() + "," + pageBean.getPerPage();
                begin = 0;
                end = pageBean.getEnd() - pageBean.getStart();
            } else if (this.dataBaseType == DataBaseType.MS_SQL_SERVER) {
                // microsoft sql server
                // 2000或以下版本
                sqlQuery = sqlQuery.trim();
                if (StringUtils.startsWithIgnoreCase(sqlQuery, "select")) {
                    StringBuilder sb = new StringBuilder(sqlQuery);
                    String regx = "select\\s+top";
                    if (!StringUtils.startsWith(sb.toString(), regx, true)) {
                        sqlQuery = sb.insert(6, " top 100 percent").toString();
                    }

                } else {
                    return "";
                }
                sql = "select top " + pageBean.getEnd() + " t.* from (" + sqlQuery + ") t";
                begin = pageBean.getStart();
                end = pageBean.getEnd();

            } else if (this.dataBaseType == DataBaseType.MS_SQL_SERVER_2005) {
                // microsoft sql server 2005或以上版本

                /*
                 * select * from( select top 3 ROW_NUMBER() OVER (order by id desc ,var1 desc)
                 * as row, * from test ) t where t.row>=2 and t.row<=3;
                 */

                sql = SQLHelp.generatePageSql(sqlQuery, pageBean, DBMS.SQL_SERVER_2005);

                begin = 0;
                end = pageBean.getEnd();

            } else if (this.dataBaseType == DataBaseType.DB2) {
                sql = SQLHelp.generatePageSql(sqlQuery, pageBean, DBMS.DB2);
                begin = 0;
                end = pageBean.getEnd();
            } else if (this.dataBaseType == DataBaseType.H2) {
                sql = SQLHelp.generatePageSql(sqlQuery, pageBean, DBMS.H2);
                begin = 0;
                end = pageBean.getEnd();
            } else if (this.dataBaseType == DataBaseType.HSQL) {
                sql = SQLHelp.generatePageSql(sqlQuery, pageBean, DBMS.HSQL);
                begin = 0;
                end = pageBean.getEnd();
            } else if (this.dataBaseType == DataBaseType.POSTGRE) {
                sql = SQLHelp.generatePageSql(sqlQuery, pageBean, DBMS.POSTGRE);
                begin = 0;
                end = pageBean.getEnd();
            } else if (this.dataBaseType == DataBaseType.SQLITE) {
                sql = SQLHelp.generatePageSql(sqlQuery, pageBean, DBMS.SQLITE);
                begin = 0;
                end = pageBean.getEnd();
            } else if (this.dataBaseType == DataBaseType.SYBASE) {
                sql = SQLHelp.generatePageSql(sqlQuery, pageBean, DBMS.SYBASE);
                begin = 0;
                end = pageBean.getEnd();
            } else if (this.dataBaseType == DataBaseType.ORACLE) {

                sqlQuery = sqlQuery.trim();

                if (StringUtils.isEmpty(orderStr.getValue())) {
                    orderStr.setValue("order by null");
                }
                sql = "select xx.* from ( select t.*,row_number() over" + "(" + orderStr.getValue()
                        + ")as rownumer from (" + sqlQuery + ")t )xx where rownumer>" + pageBean.getStart()
                        + " and rownumer<=" + pageBean.getEnd();
                begin = 0;
                end = pageBean.getEnd();
            } else {

                begin = pageBean.getStart();
                end = pageBean.getEnd();

            }

            rsStart.setValue(begin);
            rsEnd.setValue(end);

            return sql;

        } catch (Exception e) {
            // log.error("", e);
            throw new DbException(e);
        }
    }

    private CachedRowSet getCachedRowSet(ResultSet rs, boolean isPageQuery, int rsStart, int rsEnd) throws Exception {

        CachedRowSet crs = new CachedRowSetImpl();

        if (isPageQuery) {
            int begin = rsStart, end = rsEnd;
            crs.setMaxRows(end - begin);
            crs.populate(rs, begin + 1);
        } else {
            rs.beforeFirst();
            crs.populate(rs);
        }

        return crs;
    }

    private DataBaseSet getResultSet(String sqlQuery, Collection<Object> vParameters) throws Exception {

        PreparedStatement preStmt = this.getPreparedStatement(sqlQuery);

        String paramStr = SqlUtils.setToPreStatment(vParameters, preStmt);

        if (log.isDebugEnabled()) {
            String line = System.getProperty("line.separator");
            // log.debug("sql [" + sqlQuery + "]" + " sql param[ " + paramStr + " ]");
            String poolName = this.dbPoolName;
            if (poolName == null) {
                poolName = "";
            }
            log.debug("[" + poolName + "][" + getCallerInf() + "]");
            log.debug("debugSql[" + SqlUtils.generateDebugSql(sqlQuery, vParameters) + "]");

        }
        long start = System.currentTimeMillis();
        this.rs = preStmt.executeQuery();
        if (log.isDebugEnabled()) {
            log.debug("sql执行:" + (System.currentTimeMillis() - start));
        }

        return new DataBaseSet(this.rs);
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

            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }
            result = this.getResultSet(sqlQuery, vParameters);

        } catch (Exception e) {
            // log.error("", e);
            result = null;
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
        return this.executeBindDelete(sqltext, vParameters);
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
        return this.executeBindUpdate(sqltext, args);

    }

    @Override
    public int update(String sqltext, Map<Integer, Object> vParameters) throws DbException {

        return this.executeBindUpdate(sqltext, vParameters);
    }

    /**
     * 调用存储过程
     *
     * @param sqltext            sql语句
     * @param parms              用法举例如下： <code>
     *                           <pre>
     *                           用法：
     *                           parms.put("1","中");//默认为in类型
     *                           parms.put("2:in","孙");
     *                           parms.put("3:in",new Integer(3));
     *                           parms.put("4:out",int.class);
     *                           parms.put("5:out",java.util.data.class);
     *                           parms.put("6:inout",new Long(44));
     *                           </pre>
     *                           </code>
     * @param outPramsValues     存放输出参数的返回值，根据parms(输入法参数)里的out,inout对应，如果输入参数为上面的例子所示，那么outPramsValues可能输入如下：
     *
     *                           <pre>
     *                             {
     *                               4:45556,
     *                               5:"2015-09-23 12:34:56"
     *                               6:34456
     *                              }
     *                                                     </pre>
     * @param returnDataBaseSets 需返回值的结果集
     * @return
     * @throws DbException
     */

    private int executeStoredProcedure(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
                                       List<DataBaseSet> returnDataBaseSets) throws DbException {
        boolean close = true;
        try {

            close = this.getAutoCommit();

            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }
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

            CallableStatement cs = this.getCallableStatement(sqltext);
            // 设置输入参数
            String inParamStr = SqlUtils.setToPreStatment(inParms, cs);

            // 注册输出参数
            String outParamStr = SqlUtils.registForStoredProc(outParms, cs);
            if (log.isDebugEnabled()) {
                String line = System.getProperty("line.separator");
                log.debug(
                        "sql [" + sqltext + "]" + " in param[ " + inParamStr + " ]" + "out param[" + outParamStr + "]");
                if (outParamStr.equals("")) {
                    Collection<Object> args = CollectionUtils.getSortedValues(inParms);
                    log.debug("[" + this.dbPoolName + "][" + getCallerInf() + "]");
                    log.debug("debugSql[" + SqlUtils.generateDebugSql(sqltext, args) + "]");
                }

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
                            rs.beforeFirst();
                            crs.populate(rs);
                        } finally {
                            rs.close();
                        }
                        returnDataBaseSets.add(new DataBaseSet(crs));
                        cs.getMoreResults();
                        continue;
                        // 是结果集,处理完成后应该移动到下一行
                    }
                    // 如果到了这里,说明updateCount == -1 && rs == null,什么也没的了

                } while (!(updateCount == -1 && rs == null));
            }

            // 取输出参数

            SqlUtils.getValueFromCallableStatement(cs, outParms, outPramsValues);

        } catch (Exception e) {
            log.error("", e);
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
        return 0;
    }

    @Override
    public int callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
                             List<DataBaseSet> returnDataBaseSets) throws DbException {

        return this.executeStoredProcedure(sqltext, parms, outPramsValues, returnDataBaseSets);

    }

    /**
     * 返回更新的记录行的个数
     *
     * @param sqltext
     * @param vParameters
     * @return
     */
    private int executeBindUpdate(String sqltext, Collection<Object> vParameters) throws DbException {

        long[] twoInt = this.executeBindUpdate(sqltext, vParameters, this.getAutoCommit());
        return (int) twoInt[0];
    }

    private long[] executeBindUpdate(String sqltext, Collection<Object> vParameters, boolean close) throws DbException {

        int count = 0;
        long[] twoInt = new long[]{-1, -1};
        try {
            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }

            boolean isInsert = false;
            PreparedStatement preStmt = null;

            sqltext = sqltext.trim();
            String head = sqltext.substring(0, 6);
            if ("insert".equalsIgnoreCase(head.trim())) {
                isInsert = true;
            }

            preStmt = this.getPreparedStatement(sqltext);

            String paramStr = SqlUtils.setToPreStatment(vParameters, preStmt);

            if (log.isDebugEnabled()) {
                String line = System.getProperty("line.separator");
                // log.debug("sql [" + sqltext + "] " + "sql param[ " + paramStr + " ] ");
                log.debug("[" + this.dbPoolName + "][" + getCallerInf() + "]");
                log.debug("debugSql[" + SqlUtils.generateDebugSql(sqltext, vParameters) + "]");

            }
            long start = System.currentTimeMillis();
            count = preStmt.executeUpdate();
            if (log.isDebugEnabled()) {
                log.debug("sql执行:" + (System.currentTimeMillis() - start));
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
            // log.error("", e);

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
        return twoInt;
    }

    /**
     * 返回插入记录的行数
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
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
        return this.executeBindInsert(sqltext, vParameters);
    }

    /**
     * 使insertObject对象的所有属性插入到数据库，不会忽略为NULL的属性。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObject 要插入的对象
     * @return
     * @throws DbException
     */
    private <T> int excuteInsertWholeClass(T insertObject) throws DbException {

        return this.excuteInsertWholeClass(insertObject, null);

    }

    /**
     * 使insertObject对象的所有属性插入到数据库，如果insertObject的对象某个属性值为null， 那么会忽略此属性，不会插入空值到数据库。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObject 要插入的对象
     * @return
     * @throws DbException
     */
    private <T> int excuteInsertClass(T insertObject) throws DbException {

        return this.excuteInsertClass(insertObject, null);

    }

    /**
     * 向数据库插入在insertObject里properties数组指定的属性，如果在properties中的某
     * 个属性对应insertObject属性值为空，那么会忽略此属性，不会插入空值到数据库。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObject
     * @param properties   指定insertObject里需插入的属性，如果properties指定为空，
     *                     则插入insertObject对象所有属性
     * @return
     * @throws DbException
     */
    private <T> int excuteInsertClass(T insertObject, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = insertObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }

            String sqltext = SqlUtils.generateInsertSql(this.dbPoolName, properties, insertObject, reflectClass,
                    vParameters, DataBaseKeyMap.getMap(), true, this.getDataBaseType());
            DbContext.clearReflectClass();

            return this.executeBindInsert(sqltext, vParameters);

        } catch (Exception e) {
            throw new DbException(e);
        }

    }

    /**
     * 向数据库插入在insertObject里properties数组指定的属性，如果属性的值为空，也会插入到数据库。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObject
     * @param properties   指定insertObject里需插入的属性，不会忽略为NULL的属性
     * @return
     * @throws DbException
     */
    private <T> int excuteInsertWholeClass(T insertObject, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = insertObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }

            String sqltext = SqlUtils.generateInsertSql(this.dbPoolName, properties, insertObject, reflectClass,
                    vParameters, DataBaseKeyMap.getMap(), false, this.getDataBaseType());
            DbContext.clearReflectClass();

            return this.executeBindInsert(sqltext, vParameters);

        } catch (Exception e) {
            throw new DbException(e);
        }

    }

    /**
     * 把某对象的指定的属性插入到数据库到数据库，并返回主键，有些数据库的驱动程序不会返回主键，所以 要根据具体数据库而言，mysql数据可以返回主键。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param type
     * @param insertObject
     * @return 返回插入的主键
     * @throws DbException
     */
    private <T> long excuteInsertClassReturnKey(T insertObject, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = insertObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            String sqltext = SqlUtils.generateInsertSql(this.dbPoolName, properties, insertObject, reflectClass,
                    vParameters, DataBaseKeyMap.getMap(), true, this.getDataBaseType());

            DbContext.clearReflectClass();

            return this.executeBindInsertReturnKey(sqltext, vParameters);

        } catch (Exception e) {
            // log.error("", e);
            throw new DbException(e);
        }

    }

    /**
     * 把某对象的指定的属性插入到数据库到数据库，并返回主键，有些数据库的驱动程序不会返回主键，所以 要根据具体数据库而言，mysql数据可以返回主键。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObject
     * @param properties   :插入指定的属性，不会忽略为null的属性
     * @return 返回插入的主键
     * @throws DbException
     */
    private <T> long excuteInsertWholeClassReturnKey(T insertObject, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = insertObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }

            String sqltext = SqlUtils.generateInsertSql(this.dbPoolName, properties, insertObject, reflectClass,
                    vParameters, DataBaseKeyMap.getMap(), false, this.getDataBaseType());

            DbContext.clearReflectClass();

            return this.executeBindInsertReturnKey(sqltext, vParameters);

        } catch (Exception e) {
            // log.error("", e);
            throw new DbException(e);
        }

    }

    /**
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObject 要插入的对象
     * @return
     * @throws DbException
     */
    private <T> long excuteInsertClassReturnKey(T insertObject) throws DbException {

        return this.excuteInsertClassReturnKey(insertObject, null);

    }

    /**
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法，不会忽略为null的属性
     *
     * @param <T>
     * @param insertObject 要插入的对象
     * @return
     * @throws DbException
     */
    private <T> long excuteInsertWholeClassReturnKey(T insertObject) throws DbException {

        return this.excuteInsertWholeClassReturnKey(insertObject, null);

    }

    /**
     * 插入多个对象的指定属性,此方法必须保证insertObjects是相同的类型
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObjects 待插入的对象
     * @param properties    对象的属性
     * @return
     * @throws DbException
     */
    private <T> int[] excuteInsertClass(T[] insertObjects, String[] properties) throws DbException {

        String[][] pros = new String[insertObjects.length][];
        ArrayUtils.fill(pros, properties);
        return this.excuteInsertObjects(insertObjects, pros);
    }

    /**
     * 插入多个对象的指定属性,此方法必须保证insertObjects是相同的类型,并且不会忽略为NULL的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param insertObjects 待插入的对象
     * @param properties    对象的属性，即待插入对象的哪些属性需要插入
     * @return
     * @throws DbException
     */
    private <T> int[] excuteInsertWholeClass(T[] insertObjects, String[] properties) throws DbException {

        String[][] pros = new String[insertObjects.length][];
        ArrayUtils.fill(pros, properties);
        return this.excuteInsertObjects(insertObjects, pros, false);
    }

    /**
     * 插入多个对象指定的属性到数据库，insertObjects里的每个对象对应一个属性数组，所以为二维数组，每个对象里的属性值为null的会忽略
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param insertObjects：待插入的对象数组
     * @param properties：属性数组，每个待插入对象对应一个属性数组，表示要插入此对象的哪些属性
     * @return
     * @throws DbException
     */
    private int[] excuteInsertObjects(Object[] insertObjects, String[][] properties) throws DbException {

        return excuteInsertObjects(insertObjects, properties, true);
    }

    /**
     * 插入多个对象指定的属性到数据库，insertObjects里的每个对象对应一个属性数组，所以为二维数组，不忽略为null的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
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
                        DataBaseKeyMap.getMap(), ignoreNull, this.getDataBaseType());

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
            // log.error("", e);
            throw new DbException(e);
        }

    }

    /**
     * 插入多个对象的所有属性到数据库，如果对象里某个属性为空，会忽略此属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
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
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param insertObjects 待插入的对象，可以为不同类型的类
     * @return
     * @throws DbException
     */
    private int[] excuteInsertObjects(Object[] insertObjects) throws DbException {
        return this.excuteInsertObjects(insertObjects, (String[][]) null);
    }

    /**
     * 插入多个对象到数据库，如果对象里某个属性为空，会忽略此属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param objs 待插入的对象，所有对象的类型必须相同
     * @return
     * @throws DbException
     */
    private <T> int[] excuteInsertClass(T[] objs) throws DbException {
        return this.excuteInsertClass(objs, (String[]) null);
    }

    /**
     * 插入多个对象到数据库，不会忽略为NULL的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param objs 待插入的对象，所有对象的类型必须相同
     * @return
     * @throws DbException
     */
    private <T> int[] excuteInsertWholeClass(T[] objs) throws DbException {

        return this.excuteInsertWholeClass(objs, (String[]) null);
    }

    /**
     * 更新对象
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param updateObject 待更新的对象
     * @param beanKey      待更新对象的主键属性名称，复合主键属性用逗号隔开
     * @param properties   待插入的属性， 如果指定的属性在upateObject对象里的值为null，则忽略
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateClass(T updateObject, String beanKey, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = updateObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            String sqltext = SqlUtils.generateUpdateSql(this.dbPoolName, properties, updateObject, reflectClass,
                    beanKey, vParameters, DataBaseKeyMap.getMap(), true, this.getDataBaseType());
            DbContext.clearReflectClass();
            return this.executeBindUpdate(sqltext, vParameters);

        } catch (Exception e) {
            throw new DbException(e);
        }

    }

    /**
     * 更新对象
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param updateObject 待更新的对象
     * @param beanKey      待更新对象的主键属性名称，复合主键属性用逗号隔开
     * @param properties   待插入的属性，不会忽略为NULL的属性
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateWholeClass(T updateObject, String beanKey, String[] properties) throws DbException {

        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = updateObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            String sqltext = SqlUtils.generateUpdateSql(this.dbPoolName, properties, updateObject, reflectClass,
                    beanKey, vParameters, DataBaseKeyMap.getMap(), false, this.getDataBaseType());
            DbContext.clearReflectClass();

            return this.executeBindUpdate(sqltext, vParameters);

        } catch (Exception e) {
            throw new DbException(e);
        }

    }

    /**
     * 删除多个对象，所有对象都以deleteProperteis里指定的属性的值来定位删除
     *
     * @param <T>
     * @param deleteObject
     * @param deleteProperteis 复合属性（即以英文逗号隔开的属性）
     *                         待删除对象根据deleteProperteis字符串里的属性来定位删除。
     *                         deleteProperteis可以为多个属性，以英文逗号隔开
     *                         ，例如deleteProperteis为"name,id,age"，将会生产 "where name=?
     *                         and id=? and age=?"条件。
     * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。 如果返回为null，表示执行失败
     * @throws DbException
     */
    private <T> int[] excuteDeleteClass(T[] deleteObject, String deleteProperteis) throws DbException {
        String[] strArray = new String[deleteObject.length];
        strArray = ArrayUtils.fill(strArray, deleteProperteis);
        return this.excuteDeleteObjects(deleteObject, strArray);

    }

    /**
     * 删除一个对象，所有对象都以deleteProperteis属性的值来定位删除
     *
     * @param <T>
     * @param deleteObject     待删除的对象
     * @param deleteProperteis 待删除对象根据deleteProperteis里的属性来定位删除。
     *                         deleteProperteis可以为多个属性，以英文逗号隔开
     *                         ，例如deleteProperteis为"name,id,age"，将会生产 "where name=?
     *                         and id=? and age=?"条件。
     * @return 返回实际更新的行数
     * @throws DbException
     */
    private <T> int excuteDeleteClass(T deleteObject, String deleteProperteis) throws DbException {
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = deleteObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            String sql = SqlUtils.generateDeleteSql(this.dbPoolName, deleteObject, reflectClass, deleteProperteis,
                    vParameters, DataBaseKeyMap.getMap(), this.getDataBaseType());
            DbContext.clearReflectClass();
            return this.executeBindDelete(sql, vParameters);

        } catch (Exception e) {
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
            String sql = SqlUtils.generateDeleteSqlByObject(this.dbPoolName, deleteObject, reflectClass, vParameters,
                    DataBaseKeyMap.getMap(), this.getDataBaseType());
            DbContext.clearReflectClass();
            return this.executeBindDelete(sql, vParameters);

        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    /**
     * 把对象所有属性更新到数据库，如果某个属性值为null，则忽略
     * <p>
     * beanKey为对象主键属性（复合主键属性以逗号分开）
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param updateObject 待更新到数据库的对象
     * @param beanKey      updateObject对象的主键属性，复合主键属性以英文逗号隔开
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateClass(T updateObject, String beanKey) throws DbException {
        return this.excuteUpdateClass(updateObject, beanKey, (String[]) null);
    }

    /**
     * 把对象所有属性更新到数据库，不会忽略为null的属性
     * <p>
     * beanKey为对象主键属性（复合主键属性以逗号分开）
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param updateObject 待更新到数据库的对象
     * @param beanKey      updateObject对象的主键属性，复合主键属性以英文逗号隔开
     * @return
     * @throws DbException
     */
    private <T> int excuteUpdateWholeClass(T updateObject, String beanKey) throws DbException {
        return this.excuteUpdateWholeClass(updateObject, beanKey, (String[]) null);
    }

    private <T> int[] excuteUpdateClass(T[] updateObject, String beanKey) throws DbException {
        return this.excuteUpdateClass(updateObject, beanKey, (String[]) null);
    }

    private <T> int[] excuteUpdateWholeClass(T[] updateObject, String beanKey) throws DbException {
        return this.excuteUpdateWholeClass(updateObject, beanKey, (String[]) null);
    }

    /**
     * 把对象指定属性更新到数据库
     * <p>
     * beanKey为对象主键属性（复合主键属性以逗号分开）
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param objects    待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组
     * @param beanKey    objects对象的主键属性，复合主键属性以英文逗号隔开
     * @param properties 指定需更新的属性，如果指定的某个属性在对应对象里值为null，则忽略
     * @return
     * @throws DbException
     */
    private <T> int[] excuteUpdateClass(T[] objects, String beanKey, String[] properties) throws DbException {

        int length = objects.length;
        String[] beanKeys = new String[length];
        ArrayUtils.fill(beanKeys, beanKey);
        String[][] pros = new String[length][];
        ArrayUtils.fill(pros, properties);
        return this.excuteUpdateObjects(objects, beanKeys, pros);

    }

    /**
     * 把对象指定属性更新到数据库,不会忽略为NULL的属性
     * <p>
     * beanKey为对象主键属性（复合主键属性以逗号分开）
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param <T>
     * @param objects    待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组
     * @param beanKey    objects对象的主键属性，复合主键属性以英文逗号隔开
     * @param properties 指定需更新的属性，不会忽略为NULL的属性
     * @return
     * @throws DbException
     */
    private <T> int[] excuteUpdateWholeClass(T[] objects, String beanKey, String[] properties) throws DbException {

        int length = objects.length;
        String[] beanKeys = new String[length];
        ArrayUtils.fill(beanKeys, beanKey);
        String[][] pros = new String[length][];
        ArrayUtils.fill(pros, properties);
        return this.excuteUpdateObjects(objects, beanKeys, pros, false);

    }

    /**
     * 把多个对象插入数据库，各个对象的类型可以不一样,忽略为null的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param objects  待更新到数据库的多个对象
     * @param beanKeys 每个对象分别对应主键属性名
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateObjects(Object[] objects, String[] beanKeys) throws DbException {

        return excuteUpdateObjects(objects, beanKeys, null, true);
    }

    /**
     * 把多个对象插入数据库，各个对象的类型可以不一样
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param objects    待更新到数据库的多个对象
     * @param beanKeys   每个对象分别对应主键属性名
     * @param properties 每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
     *                   如果指定的某个属性在对应对象里值为null，则忽略。
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateObjects(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {

        return excuteUpdateObjects(objects, beanKeys, properties, true);
    }

    /**
     * 把多个对象插入数据库，各个对象的类型可以不一样
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param objects    待更新到数据库的多个对象
     * @param beanKeys   每个对象分别对应主键属性名
     * @param properties 每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
     *                   不忽略为null的属性。
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys, String[][] properties)
            throws DbException {

        return excuteUpdateObjects(objects, beanKeys, properties, false);
    }

    /**
     * 把多个对象插入数据库，各个对象的类型可以不一样，不会忽略为null的属性
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param objects  待更新到数据库的多个对象
     * @param beanKeys 每个对象分别对应主键属性名
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys) throws DbException {

        return excuteUpdateObjects(objects, beanKeys, null, false);
    }

    /**
     * @param objects    待更新的对象
     * @param beanKeys   生成where语句的key
     * @param properties 待更新的属性，可以为空，表明更新主键以外的属性
     * @param ignoreNull 是否忽略空
     * @return
     * @throws DbException
     */
    private int[] excuteUpdateObjects(Object[] objects, String[] beanKeys, String[][] properties, boolean ignoreNull)
            throws DbException {
        int length = objects.length;
        Map[] maps = new HashMap[length];
        String[] sqltexts = new String[length];
        boolean optimize = true;
        try {
            Class[] handClassList = DbContext.getReflectClass();
            for (int i = 0; i < maps.length; i++) {
                Object obj = objects[i];

                String key = beanKeys[i];
                Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
                String[] pros = null;
                if (properties != null)
                    pros = properties[i];

                Class reflectClass = obj.getClass();
                if (handClassList != null && handClassList.length > 0) {
                    if (handClassList.length == 1) {
                        reflectClass = handClassList[0];
                    } else {
                        reflectClass = handClassList[i];
                    }
                }
                String sql = SqlUtils.generateUpdateSql(this.dbPoolName, pros, obj, reflectClass, key, vParameters,
                        DataBaseKeyMap.getMap(), ignoreNull, this.getDataBaseType());

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
            // log.error("", e);
            throw new DbException(e);
        }
    }

    /**
     * 删除多个对象
     *
     * @param deleteObjects         待删除的多个对象，数组里的每个对象类型可以不相同
     * @param deletePropertiesArray 每个对象对应一个复合属性，每个对象根据一个复合属性，来生产sql删除语句，每个复合属性以英文逗号隔开。
     * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。 如果返回为null，表示执行失败
     * @throws DbException
     */
    private int[] excuteDeleteObjects(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException {

        int length = deleteObjects.length;
        Map[] maps = new HashMap[length];
        String[] sqltexts = new String[length];
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            for (int i = 0; i < maps.length; i++) {

                Object obj = deleteObjects[i];
                String deleteProperties = deletePropertiesArray[i];
                Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
                Class reflectClass = obj.getClass();
                if (handClassList != null && handClassList.length > 0) {
                    if (handClassList.length == 1) {
                        reflectClass = handClassList[0];
                    } else {
                        reflectClass = handClassList[i];
                    }
                }
                String sql = SqlUtils.generateDeleteSql(this.dbPoolName, obj, reflectClass, deleteProperties,
                        vParameters, DataBaseKeyMap.getMap(), this.getDataBaseType());

                maps[i] = vParameters;
                sqltexts[i] = sql;
            }
            DbContext.clearReflectClass();
            int[] res = this.executeBindBatch(sqltexts, maps);

            return res;
        } catch (Exception e) {
            // log.error("", e);
            throw new DbException(e);
        }
    }

    /**
     * 根据sql语句和参数，插入记录到数据库，返回插入记录的行数
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param sqltext
     * @param vParameters
     * @return 返回插入记录的行数，-1表明插入出错
     */
    private int executeBindInsert(String sqltext, Collection<Object> vParameters) throws DbException {
        long[] twoInt = this.executeBindUpdate(sqltext, vParameters, this.getAutoCommit());
        return (int) twoInt[0];
    }

    /**
     * 如果insert一条语句用此函数，并返回插入数据库后返回此记录的主键
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
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
        return this.executeBindInsertReturnKey(sqltext, vParameters);
    }

    private long executeBindOneInsert(String sqltext, Collection<Object> vParameters) throws DbException {
        long[] twoInt = this.executeBindUpdate(sqltext, vParameters, this.getAutoCommit());
        return twoInt[1];
    }

    /**
     * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过DataBase.
     * setAutoCommit()方法 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBase.close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    @Override
    public void setAutoCommit(boolean b) throws DbException {

        this.isAutoCommit = b;
        this.setInteralConnectionAutoCommit(b);
    }

    /**
     * <pre>
     *   重新连接数据库，当数据库连接关闭时，可以调用此方法进行重连。如果嫌每次重连太麻烦，你也可以通过设置
     *   {@link #setAutoReconnect(boolean)}方法，通过传入参数为true，来让操作关闭后自动重连。
     *
     * </pre>
     *
     * @throws DbException
     */
    @Override
    public void reConnectDb() throws DbException {

        if (!this.isColsed()) {
            this.close();
        }
        ConnectType ct = this.connectType;
        if (ct == ConnectType.POOL) {
            this.connectDb(this.dbPoolName, this.mainSlaveModeConnectMode);
        } else if (ct == ConnectType.DATASOURCE) {
            this.connectDb(this.dataSource);
        } else {
            throw new DbException("您的数据库连接方式不支持重连！");

        }

    }

    /**
     * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过DataBase.setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBase.close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    @Override
    public boolean getAutoCommit() throws DbException {
        try {
            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }
            return this.isAutoCommit;
        } catch (Exception e) {
            // log.error("", e);
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
                log.debug(Thread.currentThread().getId() + ":" + this.dbPoolName + ":rollback..");
            }
        } catch (SQLException e) {
            // log.error("", e);
            throw new DbException(e);
        }
    }

    /**
     * 回滚并且关闭连接
     *
     * @throws DbException
     */
    @Override
    public void rollbackAndClose() throws DbException {
        try {
            this.rollback();
        } finally {
            this.close();
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
            throw new DbException("error in isClosed!");
        }
        return result;
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
                log.debug(Thread.currentThread().getId() + ":" + this.dbPoolName + ":commit...");
            }
        } catch (SQLException e) {
            // log.error("", e);
            throw new DbException(e);
        }
    }

    @Override
    public void commitAndClose() throws DbException {

        try {
            if (conn != null)
                this.commit();
        } finally {
            this.close();
        }
    }

    /**
     * @param sqltext
     * @param vParametersArray
     * @param close            执行后是否关闭数据库
     * @return 返回每条语句更新记录的条数，执行错误，会抛出异常
     * @throws DbException
     */
    private int[] executeBindBatch(String sqltext, List<Collection<Object>> vParametersArray, boolean close)
            throws DbException {

        int[] count = null;
        try {
            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }
            if (close) {
                this.setInteralConnectionAutoCommit(false);
                // conn.setAutoCommit(false);
            }

            PreparedStatement preStmt = null;

            sqltext = sqltext.trim();

            preStmt = this.getPreparedStatement(sqltext);
            String callInfo = getCallerInf();
            for (int m = 0; m < vParametersArray.size(); m++) {

                Collection<Object> vParameters = vParametersArray.get(m);

                if (log.isDebugEnabled()) {

                    log.debug("[" + this.dbPoolName + "][" + callInfo + "]");
                    log.debug("" + m + ".debugSql[" + SqlUtils.generateDebugSql(sqltext, vParameters) + "]");
                }

                String paramStr = SqlUtils.setToPreStatment(vParameters, preStmt);
                if (log.isDebugEnabled()) {
                    // log.debug(m + ".sql [" + sqltext + "] " + "sql param[ " + paramStr + " ]");
                }

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
            if (close) {
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
            // log.error("", e);
            if (close) {
                this.rollback();
            }
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
        return count;
    }

    /**
     * 返回的数组里每个值对应返回对应sql语句执行后更新的行数，如果为null表明执行失败，内部会自动回滚。
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
     *
     * @param sqltxts
     * @param vParametersList
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
                this.setInteralConnectionAutoCommit(false);
            }

            for (int i = 0; i < sqltxts.length; i++) {
                Collection<Object> args = null;
                if (ArrayUtils.isNotEmpty(vParametersArray)) {
                    if (vParametersArray[i] != null) {
                        args = CollectionUtils.getSortedValues(vParametersArray[i]);
                    }
                }
                long[] twoInt = this.executeBindUpdate(sqltxts[i], args, false);
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
            log.error("", ex);
            try {
                if (bkCommit) {
                    this.rollback();
                }
            } catch (DbException e) {
                //
                // log.error("", e);
            }
            throw new DbException(ex);

        } finally {
            try {
                if (bkCommit) {
                    this.close();
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

        return this.executeBindBatch(sqltxts, vParametersArray);
    }

    @Override
    public int[] insert(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException {

        return this.update(sqltxts, vParametersArray);
    }

    /**
     * 批量更新操作（增，删，改），返回每条语句更新记录的行数
     * <p>
     * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
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

        return executeBindBatch(sqltxt, argsList, this.getAutoCommit());
    }

    @Override
    public int[] update(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
        return this.executeBindBatch(sqltxt, vParametersList);
    }

    @Override
    public int[] insert(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException {
        return this.update(sqltxt, vParametersList);
    }

    /**
     * 不带参数的批量处理
     *
     * @param sqltxt
     * @return
     * @throws DbException
     */
    private int[] executeBatch(ArrayList<String> sqltxt) throws DbException {
        return updateBatch(sqltxt, this.getAutoCommit());
    }

    @Override
    public int[] update(ArrayList<String> sqltxts) throws DbException {
        return this.executeBatch(sqltxts);

    }

    @Override
    public int[] insert(ArrayList<String> sqltxts) throws DbException {
        return this.update(sqltxts);

    }

    private int[] updateBatch(ArrayList<String> sqltxt, boolean close) throws DbException {

        return this.executeBindBatch(sqltxt.toArray(new String[0]), null);

    }

    private CallableStatement getCallableStatement(String sqltext) throws Exception {
        this.sqlType = SQLType.STORE_DPROCEDURE;
        this.cs = this.conn.prepareCall(sqltext);

        return this.cs;

    }

    private PreparedStatement getPreparedStatement(String sqltxt) throws DbException {
        try {
            if (this.conn == null || this.conn.isClosed()) {
                if (this.isAutoReconnect()) {
                    log.debug("reconnect database!");
                    this.reConnectDb();
                } else {
                    throw new DbException("connection is null!");
                }
            }
            if (StringUtils.startsWithIgnoreCase(sqltxt.trim(), "select")) {
                try {
                    this.sqlType = SQLType.SELECT;
                    if (this.isMainSlaveMode()) {
                        if (this.getInternalConnectionAutoCommit()) {// 如果不是事务性操作，则读从库
                            if (this.mainSlaveModeConnectMode == MainSlaveModeConnectMode.Try_Connect_MainServer) {
                                log.debug("查询sql语句，且Try_Connect_MainServer模式，获取从库连接");
                                this.selectSlaveDb();
                            }
                        }
                    }
                    ps = conn.prepareStatement(sqltxt, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } catch (Exception e) {
                    // log.error("您的数据库驱动程序版本过低，请更新成支持jdbc3.0以上版本的驱动！");
                    throw e;
                }
            } else if (StringUtils.startsWithIgnoreCase(sqltxt.trim(), "insert")) {
                try {
                    this.sqlType = SQLType.INSERT;
                    ps = conn.prepareStatement(sqltxt, Statement.RETURN_GENERATED_KEYS);
                } catch (Exception e) {
                    log.warn("您的数据库驱动程序不支持带Statement.RETURN_GENERATED_KEYS参数"
                            + "的PreparedStatement构造函数，可能由于您的数据库版本较低，建议升级您的驱动程序！");
                    // ps = conn.prepareStatement(sqltxt);
                    throw e;
                }
            } else {
                this.sqlType = SQLType.UPDATE;
                ps = conn.prepareStatement(sqltxt);
            }

        } catch (Exception e) {
            // log.error("", e);
            throw new DbException(e);
        }
        return ps;
    }

    /**
     * 关闭底层资源，但不关闭数据库连接
     */
    @Override
    public void closer() throws DbException {

        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }

            if (cs != null) {
                cs.close();
                cs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;

            }

        } catch (SQLException e) {
            // log.error("", e);
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

            if (cs != null) {
                cs.close();
                cs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (conn != null) {
                if (!conn.isClosed()) {
                    try {
                        if (!this.externalControlConClose) {
                            this.setInteralConnectionAutoCommit(true);
                            conn.close();
                            conn = null;
                            log.debug(Thread.currentThread().getId() + ":db=" + this.dbPoolName + ":closed!");
                        }
                    } catch (Exception ex) {
                        log.error(this.dbPoolName + ":close fail!", ex);
                    }


                }

            }

        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public <T> int insertBy(T insertObject) throws DbException {

        return this.excuteInsertClass(insertObject);

    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject) throws DbException {

        return this.excuteInsertClassReturnKey(insertObject);

    }

    @Override
    public <T> int insertBy(T insertObject, String[] properties) throws DbException {

        return this.excuteInsertClass(insertObject, properties);

    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, String[] properties) throws DbException {

        return this.excuteInsertClassReturnKey(insertObject, properties);

    }

    @Override
    public <T> int[] insertBy(T[] objs) throws DbException {

        return this.excuteInsertClass(objs);

    }

    @Override
    public <T> int[] insertBy(T[] objs, String[] properties) throws DbException {

        return this.excuteInsertClass(objs, properties);

    }

    @Override
    public <T> int updateBy(T updateObject, String beanKey) throws DbException {
        return this.excuteUpdateClass(updateObject, beanKey);
    }

    @Override
    public <T> int updateBy(T updateObject, String beanKey, String[] properties) throws DbException {
        return this.excuteUpdateClass(updateObject, beanKey, properties);
    }

    @Override
    public <T> int[] updateBy(T[] objects, String beanKey, String[] properties) throws DbException {
        return this.excuteUpdateClass(objects, beanKey, properties);
    }

    @Override
    public <T> int[] updateBy(T[] objects, String beanKey) throws DbException {
        return this.excuteUpdateClass(objects, beanKey);
    }

    @Override
    public <T> int[] updateBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
        return this.excuteUpdateObjects(objects, beanKeys, properties);
    }

    @Override
    public <T> T queryOneBy(T selectObject, String selectProperties) throws DbException {

        List<T> list = this.doQueryClassNoSql(selectObject, selectProperties);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }

    }

    @Override
    public <T> List<T> queryListBy(T selectObject, String selectProperties) throws DbException {

        return this.doQueryClassNoSql((Class<T>) selectObject.getClass(), selectObject, selectProperties);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, String selectProperties, int page, int perPage, PageBean pb)
            throws DbException {
        String sql = "";
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = selectObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            sql = SqlUtils.generateSelectSql(this.dbPoolName, selectObject, reflectClass, selectProperties, vParameters,
                    DataBaseKeyMap.getMap(), this.getDataBaseType());
            DbContext.clearReflectClass();

        } catch (Exception e) {
            throw new DbException(e);
        }
        return this.doPageQueryClass((Class<T>) selectObject.getClass(), sql, vParameters, page, perPage, pb, null);

    }

    @Override
    public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException {
        String sql = "";
        Map<Integer, Object> vParameters = new HashMap<Integer, Object>();
        try {
            Class<?>[] handClassList = DbContext.getReflectClass();
            Class<?> reflectClass = selectObject.getClass();
            if (handClassList != null && handClassList.length > 0) {
                reflectClass = handClassList[0];
            }
            sql = SqlUtils.generateSelectSqlBySelectObject(this.dbPoolName, selectObject, reflectClass, vParameters,
                    DataBaseKeyMap.getMap(), this.getDataBaseType());

            DbContext.clearReflectClass();
        } catch (Exception e) {
            throw new DbException(e);
        }
        return this.doPageQueryClass((Class<T>) selectObject.getClass(), sql, vParameters, page, perPage, pb, null);
    }

    ;

    @Override
    public <T> List<T> queryListBy(T selectObject) throws DbException {

        return this.doQueryClassNoSql(selectObject);
    }

    @Override
    public <T> T queryOneBy(T selectObject) throws DbException {

        List<T> list = this.doQueryClassNoSql(selectObject);

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public <T> int delBy(T deleteObject, String deleteProperteis) throws DbException {
        return this.excuteDeleteClass(deleteObject, deleteProperteis);
    }


    @Override
    public <T> int[] delBy(T[] deleteObjects, String deleteProperteis) throws DbException {
        return this.excuteDeleteClass(deleteObjects, deleteProperteis);

    }

    @Override
    public <T> int[] delBy(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException {

        return this.excuteDeleteObjects(deleteObjects, deletePropertiesArray);

    }

    @Override
    public <T> int insertWholeBy(T insertObject) throws DbException {

        return this.excuteInsertWholeClass(insertObject);

    }

    @Override
    public <T> long insertWholeReturnKeyBy(T insertObject) throws DbException {

        return this.excuteInsertWholeClassReturnKey(insertObject);

    }

    @Override
    public <T> int insertWholeBy(T insertObject, String[] properties) throws DbException {

        return this.excuteInsertWholeClass(insertObject, properties);

    }

    @Override
    public <T> long insertWholeReturnKeyBy(T insertObject, String[] properties) throws DbException {

        return this.excuteInsertWholeClassReturnKey(insertObject, properties);

    }

    @Override
    public <T> int[] insertWholeBy(T[] objs) throws DbException {

        return this.excuteInsertWholeClass(objs);

    }

    @Override
    public <T> int[] insertWholeBy(T[] objs, String[] properties) throws DbException {

        return this.excuteInsertWholeClass(objs, properties);

    }

    @Override
    public <T> int updateWholeBy(T updateObject, String beanKey) throws DbException {
        return this.excuteUpdateWholeClass(updateObject, beanKey);
    }

    @Override
    public <T> int updateWholeBy(T updateObject, String beanKey, String[] properties) throws DbException {
        return this.excuteUpdateWholeClass(updateObject, beanKey, properties);
    }

    @Override
    public <T> int[] updateWholeBy(T[] objects, String beanKey, String[] properties) throws DbException {
        return this.excuteUpdateWholeClass(objects, beanKey, properties);
    }

    @Override
    public <T> int[] updateWholeBy(T[] objects, String beanKey) throws DbException {
        return this.excuteUpdateWholeClass(objects, beanKey);
    }

    @Override
    public <T> int[] updateWholeBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
        return this.excuteUpdateWholeObjects(objects, beanKeys, properties);
    }

    public static String getCallerInf() {
        // 3, 10, DbConst.filter, 1
        int fromLevel = 3, maxLevel = 20, upLevelNum = 2;
        String filterStrEndsWith = DbConst.filter;

        StackTraceElement stack[] = Thread.currentThread().getStackTrace();// (new Throwable()).getStackTrace();
        // 获取线程运行栈信息
        if (stack.length > fromLevel) {
            for (int i = fromLevel; i < maxLevel && i < stack.length; i++) {
                if (stack[i].getClassName().endsWith("BaseDao")) {// BaseDao
                    continue;
                }
                if (stack[i].getClassName().endsWith(filterStrEndsWith)) {

                    String str = getLastTwoClassName(stack[i].getClassName()) + "." + stack[i].getMethodName() + "(:"
                            + stack[i].getLineNumber() + ")";
                    if (upLevelNum > 0 && stack.length > (i + upLevelNum) && maxLevel > (i + upLevelNum)) {
                        for (int n = 1; n <= upLevelNum; n++) {
                            str = getLastTwoClassName(stack[i + n].getClassName()) + "." + stack[i + n].getMethodName()
                                    + "(:" + stack[i + n].getLineNumber() + ")" + "=>" + str;
                        }
                        return str;
                    } else {
                        return str;
                    }

                }

            }
        }
        return "";
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
    }

    @Override
    public String exeScript(Reader reader) throws DbException {

        try {
            return this.exeScript(reader, null);
        } catch (Exception e) {
            throw e;
        } finally {
        }


    }

    @Override
    public String exeScript(Reader reader, PrintWriter logWriter) throws DbException {

        boolean close = this.getAutoCommit();
        try {
            ScriptRunner scriptRunner = new ScriptRunner(this.getConnection());
            scriptRunner.setLogWriter(logWriter);
            scriptRunner.setErrorLogWriter(logWriter);
            scriptRunner.setStopOnError(true);
            scriptRunner.setThrowWarning(true);
            scriptRunner.setRemoveCRs(true);
            scriptRunner.setSendFullScript(true);
            return scriptRunner.runScript(reader);

        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            try {
                reader.close();
                if (logWriter != null) {
                    logWriter.close();
                }
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

}
