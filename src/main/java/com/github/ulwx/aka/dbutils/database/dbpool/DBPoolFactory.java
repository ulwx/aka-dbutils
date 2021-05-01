package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.IDBPoolAttrSource;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.database.dialect.DialectClient;
import com.github.ulwx.aka.dbutils.tool.support.EncryptUtil;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.RandomUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBPoolFactory {
    private volatile static Logger log = LoggerFactory.getLogger(DBPoolFactory.class);
    private String dbPoolXmlFileName = null;
    private ReadConfig readConfig;
    private static String KEY = "S$T$#@QR@GHODFP$R";
    // 0 表示没有初始化， 1：表示正在初始化 2：初始化完成  3：代表部分初始化开始  4：部分初始化完成  134：获取信息
    private volatile int status = STATUS.uninit;

    static class STATUS {
        public static int uninit = 0;
        public static int init_doing = 1;
        public static int init_finished = 2;
        public static int part_init_doing = 3;
        public static int part_init_finished = 4;
    }

    private volatile ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    static class PoolType {
        public static String TOMCAT_DB_POOL = "tomcatdbpool";
    }

    public ReadConfig getReadConfig() {
        return readConfig;
    }

    private volatile Map<String, DataSource> poollist = new ConcurrentHashMap<String, DataSource>();

    /**
     * 主库和从库的关系
     */
    private volatile Map<String, ConcurrentHashMap<String, DataSource>> poolSlaveList = new ConcurrentHashMap<String, ConcurrentHashMap<String, DataSource>>();

    private static volatile Map<String, DBPoolFactory> dbpoolFactoryMap = new ConcurrentHashMap<>();

    private DBPoolFactory(String dbPoolXmlFileName) {
        this.dbPoolXmlFileName = dbPoolXmlFileName;

    }

    public String getDbPoolXmlFileName() {
        return dbPoolXmlFileName;
    }

    public static String getEncryptPassword(String password) {
        return aesEncrypt(password);
    }

    /**
     * 解析具有外部xml引用的连接池名称，如 myDbpool.xml#sysdb
     *
     * @param dbPoolXmlFileNameAndDbPoolName
     * @return
     */
    public static String[] parseRefDbPoolName(String dbPoolXmlFileNameAndDbPoolName) {
        String dbPoolKey = null;
        String dbPoolXmlName = null;
        String[] strs = dbPoolXmlFileNameAndDbPoolName.split("\\#");
        if (strs.length == 1) {
            dbPoolXmlName = ReadConfig.DEFAULT;
            dbPoolKey = dbPoolXmlFileNameAndDbPoolName;

        } else if (strs.length == 2) {
            dbPoolXmlName = strs[0];
            dbPoolKey = strs[1];
        } else {

        }
        return new String[]{dbPoolXmlName, dbPoolKey};
    }

    public static DBPoolFactory getInstance(String dbPoolXmlFileName) {
        DBPoolFactory dbPoolFactory = dbpoolFactoryMap.get(dbPoolXmlFileName);
        if (dbPoolFactory == null) {
            synchronized (DBPoolFactory.class) {
                dbPoolFactory = dbpoolFactoryMap.get(dbPoolXmlFileName);
                if (dbPoolFactory == null) {
                    dbPoolFactory = new DBPoolFactory(dbPoolXmlFileName);
                    dbpoolFactoryMap.put(dbPoolXmlFileName, dbPoolFactory);
                    dbPoolFactory.initDbPoolFactory();

                }
            }
        }
        return dbPoolFactory;

    }


    public boolean isMainSlaveMode(String poolName) {
        try {
            rwLock.readLock().lock();
            if (poolSlaveList != null && poolSlaveList.get(poolName) != null && poolSlaveList.get(poolName).size() > 0)
                return true;
            return false;
        } finally {
            rwLock.readLock().unlock();
            ;
        }
    }

    public static DBPoolFactory findDBPoolFactory(String poolName) throws DbException {
        Set<String> dbPoolXmlFileNameSet = ReadConfig.findDBPoolXmlNames(poolName);
        //dbPoolXmlFileNameSet存放的原始最多一个
        if (dbPoolXmlFileNameSet != null && dbPoolXmlFileNameSet.size() == 1) {
            for (String dbPoolXmlFileName : dbPoolXmlFileNameSet) {
                return DBPoolFactory.getInstance(dbPoolXmlFileName);
            }
        }
        return null;
    }

    ;

    private void initOnePool(String dbPoolXmlFileName, String poolName, TResult<DataSource> masterDataSoruce,
                             TResult<ConcurrentHashMap<String, DataSource>> slaverDataSourceMap) {
        try {

            ReadConfig readConfig = ReadConfig.getInstance(dbPoolXmlFileName);
            ConcurrentHashMap<String, Map<String, String>> maps = readConfig.getProperties();
            ConcurrentHashMap<String, Map<String, Map<String, String>>> slaveProperites = readConfig.getSlaveProperites();
            Map<String, String> masterMap = maps.get(poolName);
            Map<String, Map<String, String>> slaveServersMap = slaveProperites.get(poolName);
            initOnePool(masterMap, slaveServersMap, masterDataSoruce, slaverDataSourceMap);
        } catch (Exception ex) {
            if (ex instanceof DbException) throw ex;
            throw new DbException(ex);
        }
    }

    private void initOnePool(Map<String, String> masterMap,
                             Map<String, Map<String, String>> slaveServersMap,
                             TResult<DataSource> masterDataSoruce,
                             TResult<ConcurrentHashMap<String, DataSource>> slaverDataSourceMap) {
        try {

            Map<String, String> map = masterMap;
            Map<String, Map<String, String>> slaveServer = slaveServersMap;
            String driverClassName = map.get("driverClassName");
            String url = map.get("url");
            String user = map.get("username");
            String password = map.get("password");
            String encrypt = StringUtils.trim(map.get("encrypt"));

            if (encrypt.equals("1")) {
                password = EncryptUtil.aesUnEncrypt(password, KEY);
            }
            String maxStatements = StringUtils.trim(map.get("maxStatements"), "30");
            String checkoutTimeout = StringUtils.trim(map.get("checkoutTimeout"), "60000");// 60000毫秒
            String idleConnectionTestPeriod = StringUtils.trim(map.get("idleConnectionTestPeriod"), "60");// 40秒
            String type = StringUtils.trim(map.get("type"), PoolType.TOMCAT_DB_POOL);
            String maxIdleTime = StringUtils.trim(map.get("maxIdleTime"), "600");// 以秒为单位，默认空隙600秒后回收
            String maxPoolSize = StringUtils.trim(map.get("maxPoolSize"), "30");// 默认30个
            String minPoolSize = StringUtils.trim(map.get("minPoolSize"), "10");// 默认20个

            DataSource ds = masterDataSoruce.getValue();
            ConcurrentHashMap<String, DataSource> slaveDataSources = new ConcurrentHashMap<String, DataSource>();
            if (type.equals(PoolType.TOMCAT_DB_POOL)) {
                if (ds == null) {
                    ds = getNewDataSourceFromTomcatDb(url, user, password, checkoutTimeout, maxPoolSize, minPoolSize,
                            maxStatements, maxIdleTime, idleConnectionTestPeriod, driverClassName);
                } else {
                    ds = restartDataSourceFromTomcatDb(ds, url, user, password, checkoutTimeout, maxPoolSize, minPoolSize,
                            maxStatements, maxIdleTime, idleConnectionTestPeriod, driverClassName);
                }
                //判断ds是否可以获得连接
                boolean res = DBPoolFactory.startDataSource(ds);
                if (!res) {
                    close(ds);
                    return;
                }
                slaveDataSources = getSlaveServerConfig(slaveServer, PoolType.TOMCAT_DB_POOL, driverClassName);
            }

            masterDataSoruce.setValue(ds);
            slaverDataSourceMap.setValue(slaveDataSources);
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }
    }

    static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * @throws DbException
     */
    private void initAllPool() throws DbException {
        try {
            //关闭所有连接池
            status = STATUS.init_doing;
            close(dbPoolXmlFileName);
            ReadConfig readConfig = ReadConfig.getInstance(dbPoolXmlFileName);
            this.readConfig = readConfig;
            ConcurrentHashMap<String, Map<String, String>> poolNameMasterMap = readConfig.getProperties();
            ConcurrentHashMap<String, Map<String, Map<String, String>>> slaveNameMap = readConfig.getSlaveProperites();
            Iterator<String> iter = poolNameMasterMap.keySet().iterator();
            Map<String, String> refMap = new TreeMap<>();
            Map<String, Class> refClassMap = new TreeMap<>();
            while (iter.hasNext()) {
                try {
                    String poolName = iter.next();

                    Map<String, String> map = poolNameMasterMap.get(poolName);
                    String ref = StringUtils.trim(map.get("ref"));
                    String refClass = StringUtils.trim(map.get("ref-class"));
                    if (ref.isEmpty() && refClass.isEmpty()) {
                        TResult<DataSource> tmasterDataSoruce = new TResult<>();
                        TResult<ConcurrentHashMap<String, DataSource>> tslaverDataSourceMap = new TResult<>();
                        initOnePool(dbPoolXmlFileName, poolName, tmasterDataSoruce, tslaverDataSourceMap);
                        poollist.put(poolName, tmasterDataSoruce.getValue());
                        poolSlaveList.put(poolName, tslaverDataSourceMap.getValue());
                    } else {
                        if (!ref.isEmpty()) {
                            refMap.put(poolName, ref);
                        } else if (!refClass.isEmpty()) {
                            refClassMap.put(poolName, Class.forName(refClass));
                        }

                    }
                } catch (Exception ex) {
                    throw ex;
                }
            } // while
            //处理RefClass引用
            for (String poolName : refClassMap.keySet()) {
                Class refClass = refClassMap.get(poolName);
                if (IDBPoolAttrSource.class.isAssignableFrom(refClass)) {
                    IDBPoolAttrSource idbPoolAttrSource = (IDBPoolAttrSource) refClass.getConstructor().newInstance();
                    Map<String, String> masterProperties = new HashMap<>();
                    Map<String, Map<String, String>> slaveProperties = new HashMap<>();
                    boolean debug = DbContext.permitDebugLog();
                    DbContext.permitDebugLog(true);
                    idbPoolAttrSource.configProperties(masterProperties, slaveProperties);
                    DbContext.permitDebugLog(debug);
                    Map<String, String> masterMap = poolNameMasterMap.get(poolName);
                    HashMap<String, String> newMasterMap = new HashMap<>();
                    newMasterMap.putAll(masterMap);
                    newMasterMap.putAll(masterProperties);
                    poolNameMasterMap.put(poolName, newMasterMap);
                    slaveNameMap.put(poolName, slaveProperties);
                    TResult<DataSource> tmasterDataSoruce = new TResult<>();
                    TResult<ConcurrentHashMap<String, DataSource>> tslaverDataSourceMap = new TResult<>();

                    initOnePool(dbPoolXmlFileName, poolName, tmasterDataSoruce, tslaverDataSourceMap);
                    poollist.put(poolName, tmasterDataSoruce.getValue());
                    poolSlaveList.put(poolName, tslaverDataSourceMap.getValue());
                    long checkTime = 0;
                    try {
                        String str = StringUtils.trim(masterMap.get("check-time"));
                        if (!str.isEmpty()) {
                            checkTime = Integer.valueOf(str);
                        }
                    } catch (Exception e) {

                    }
                    if (checkTime > 1000) {
                        executorService.scheduleAtFixedRate(() -> {
                            if (status == STATUS.init_doing) {
                                return;
                            }
                            Map<String, String> lmasterProperties = new HashMap<>();
                            Map<String, Map<String, String>> lslaveProperties = new HashMap<>();
                            DbContext.permitDebugLog(true);
                            idbPoolAttrSource.configProperties(lmasterProperties, lslaveProperties);
                            DbContext.permitDebugLog(debug);
                            if (ObjectUtils.deepEquals(lmasterProperties, masterProperties) &&
                                    ObjectUtils.deepEquals(lslaveProperties, slaveProperties)) {
                                //不执行
                            } else { //重新构造连接池
                                //先关闭连接池
                                try {
                                    status = STATUS.part_init_doing;
                                    rwLock.writeLock().lock();
                                    log.debug("get write lock!");
                                    close(dbPoolXmlFileName, poolName);
                                    HashMap<String, String> lnewMasterMap = new HashMap<>();
                                    Map<String, String> lmasterMap = poolNameMasterMap.get(poolName);
                                    lnewMasterMap.putAll(lmasterMap);
                                    lnewMasterMap.putAll(lmasterProperties);
                                    poolNameMasterMap.put(poolName, lnewMasterMap);
                                    slaveNameMap.put(poolName, lslaveProperties);
                                    TResult<DataSource> ltmasterDataSoruce = new TResult<>();
                                    //如果使用现有连接池设置
                                    ltmasterDataSoruce.setValue(this.getDBPool(poolName));
                                    TResult<ConcurrentHashMap<String, DataSource>> ltslaverDataSourceMap = new TResult<>();
                                    initOnePool(dbPoolXmlFileName, poolName, ltmasterDataSoruce, ltslaverDataSourceMap);
                                    poollist.put(poolName, ltmasterDataSoruce.getValue());
                                    poolSlaveList.put(poolName, ltslaverDataSourceMap.getValue());
                                } finally {
                                    status = STATUS.part_init_finished;
                                    rwLock.writeLock().unlock();
                                }
                            }
                        }, checkTime, checkTime, TimeUnit.SECONDS);


                    }
                } else {
                    throw new DbException(refClass.getName() + "没有继承" + IDBPoolAttrSource.class.getName() + "！");
                }
            }

            //处理Ref引用
            for (String poolName : refMap.keySet()) {
                String ref = refMap.get(poolName);
                String refDbPoolName = ref;
                String refFileName = dbPoolXmlFileName;
                if (ref.contains("#")) {//判断是否存在外部引用
                    String[] strs = ref.split("\\#");
                    refFileName = strs[0].trim();
                    refDbPoolName = strs[1].trim();
                    if (!refFileName.equals(dbPoolXmlFileName)) {//外部引用
                        DBPoolFactory outerDbPoolFactory = DBPoolFactory.getInstance(refFileName);
                        DataSource masterDataSource = outerDbPoolFactory.getDBPool(refDbPoolName);
                        if (masterDataSource == null) {
                            throw new DbException(refFileName + "里无法找到" + refDbPoolName + "的定义！");
                        }
                        ConcurrentHashMap<String, DataSource> slaveDataSources = outerDbPoolFactory.
                                getSlaveDataSources(refDbPoolName);
                        poollist.put(poolName, masterDataSource);
                        poolSlaveList.put(poolName, slaveDataSources);
                        synSomeProperties(poolName, outerDbPoolFactory, refDbPoolName);
                        continue;
                    }
                }
                //本地引用
                DataSource masterDataSource = poollist.get(refDbPoolName);
                if (masterDataSource == null) {
                    throw new DbException(refFileName + "里无法找到" + refDbPoolName + "的定义！");
                }
                ConcurrentHashMap<String, DataSource> slaveDataSources = poolSlaveList.get(refDbPoolName);
                poollist.put(poolName, masterDataSource);
                poolSlaveList.put(poolName, slaveDataSources);
                synSomeProperties(poolName, this, refDbPoolName);

            }

        } catch (Exception e) {
            // log.error("", e);

            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("add database pool error!", e);
        } finally {
            status = STATUS.init_finished;
        }
    }

    private void synSomeProperties(String poolName, DBPoolFactory outerDbPoolFactory, String refDbPoolName) {
        Map<String, String> newMasterProperties = new HashMap<>();
        Map<String, String> masterProperties =
                this.getReadConfig().getProperties().get(poolName);
        Map<String, String> refMasterProperties = outerDbPoolFactory.getReadConfig().getProperties().get(refDbPoolName);
        newMasterProperties.put("table-name-rule", refMasterProperties.get("table-name-rule"));
        newMasterProperties.put("table-colum-rule", refMasterProperties.get("table-colum-rule"));
        newMasterProperties.putAll(masterProperties);
        this.getReadConfig().getProperties().put(poolName, newMasterProperties);
    }

    private ConcurrentHashMap<String, DataSource> getSlaveServerConfig(Map<String, Map<String, String>> slaveServer, String poolType,
                                                                       String driverClassName) throws Exception {

        ConcurrentHashMap<String, DataSource> slaveDataSources = new ConcurrentHashMap<String, DataSource>();
        if (slaveServer != null) {
            for (String slaveServerName : slaveServer.keySet()) {
                Map<String, String> slaveConfig = slaveServer.get(slaveServerName);
                String s_url = slaveConfig.get("url");
                String s_user = slaveConfig.get("username");
                String s_password = slaveConfig.get("password");
                String encrypt = StringUtils.trim(slaveConfig.get("encrypt"));
                if (encrypt.equals("1")) {
                    s_password = EncryptUtil.aesUnEncrypt(s_password, KEY);
                }
                String s_maxStatements = StringUtils.trim(slaveConfig.get("maxStatements"), "30");
                String s_checkoutTimeout = StringUtils.trim(slaveConfig.get("checkoutTimeout"), "60000");// 60000毫秒
                String s_idleConnectionTestPeriod = StringUtils.trim(slaveConfig.get("idleConnectionTestPeriod"), "60");// 40秒
                String s_maxIdleTime = StringUtils.trim(slaveConfig.get("maxIdleTime"), "600");// 以秒为单位，默认空隙600秒后回收
                String s_maxPoolSize = StringUtils.trim(slaveConfig.get("maxPoolSize"), "30");// 默认30个
                String s_minPoolSize = StringUtils.trim(slaveConfig.get("minPoolSize"), "10");// 默认20个

                DataSource s_ds = null;
                if (poolType.equals(PoolType.TOMCAT_DB_POOL)) {
                    s_ds = getNewDataSourceFromTomcatDb(s_url, s_user, s_password, s_checkoutTimeout, s_maxPoolSize,
                            s_minPoolSize, s_maxStatements, s_maxIdleTime, s_idleConnectionTestPeriod, driverClassName);
                } else {
                    continue;
                }
                if (s_ds == null) {
                    log.error("无法从库连接池" + slaveServerName + "获取连接！数据源返回为空,忽略...");
                    continue;
                }
                // 判断ds是否可以获得连接
                boolean res = DBPoolFactory.startDataSource(s_ds);
                if (!res) {// 不能获得连接
                    log.error("无法从库连接池" + slaveServerName + "获取连接！忽略....");
                    close(s_ds);
                    continue;
                } else {
                    slaveDataSources.put(slaveServerName, s_ds);
                }
            }
        }

        return slaveDataSources;
    }

    public static DataSource getNewDataSourceFromTomcatDb(
            String url,
            String user,
            String password,
            String checkoutTimeout,
            String maxPoolSize,
            String minPoolSize,
            String maxStatements,
            String maxIdleTime,
            String idleConnectionTestPeriod,
            String driverClassName) throws Exception {

        PoolProperties poolProperties = configTomcatJdbcPoolProperties(url, user, password, checkoutTimeout,
                maxPoolSize, minPoolSize, maxStatements,
                maxIdleTime, idleConnectionTestPeriod, driverClassName);
        org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
        datasource.setPoolProperties(poolProperties);
        DataSource ds = datasource;
        return ds;

        // 从库的设置

    }

    public static DataSource restartDataSourceFromTomcatDb(DataSource ds,
                                                           String url,
                                                           String user,
                                                           String password,
                                                           String checkoutTimeout,
                                                           String maxPoolSize,
                                                           String minPoolSize,
                                                           String maxStatements,
                                                           String maxIdleTime,
                                                           String idleConnectionTestPeriod,
                                                           String driverClassName) throws Exception {

        PoolProperties poolProperties = configTomcatJdbcPoolProperties(url, user, password, checkoutTimeout,
                maxPoolSize, minPoolSize, maxStatements,
                maxIdleTime, idleConnectionTestPeriod, driverClassName);
        org.apache.tomcat.jdbc.pool.DataSource datasource = (org.apache.tomcat.jdbc.pool.DataSource) ds;
        datasource.setPoolProperties(poolProperties);
        return datasource;

        // 从库的设置

    }

    public static PoolProperties configTomcatJdbcPoolProperties(String url,
                                                                String user,
                                                                String password,
                                                                String checkoutTimeout,
                                                                String maxPoolSize,
                                                                String minPoolSize,
                                                                String maxStatements,
                                                                String maxIdleTime,
                                                                String idleConnectionTestPeriod,
                                                                String driverClassName) {
        PoolProperties p = new PoolProperties();
        p.setUrl(url);
        p.setDriverClassName(driverClassName);
        p.setUsername(user);
        p.setPassword(password);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(true);
        p.setTestOnBorrow(false);
        p.setTestOnReturn(false);
        if (StringUtils.containsIgnoreCase(driverClassName, "oracle")) {
            p.setValidationQuery("select 1 from dual");
        } else {
            p.setValidationQuery("SELECT 1");//
        }

        int test = Integer.valueOf(idleConnectionTestPeriod);// 秒

        p.setValidationInterval(test * 1000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(Integer.valueOf(maxPoolSize));
        p.setInitialSize(Integer.valueOf(minPoolSize));
        p.setMaxWait(Integer.valueOf(checkoutTimeout));

        p.setMinEvictableIdleTimeMillis(Integer.valueOf(maxIdleTime) * 1000);
        p.setMinIdle(Integer.valueOf(minPoolSize));
        p.setMaxIdle(Integer.valueOf(maxPoolSize));
        p.setLogAbandoned(true);
        p.setRemoveAbandonedTimeout(300 * 10);//
        p.setRemoveAbandoned(true);//
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return p;

    }

    private static boolean startDataSource(DataSource ds) {
        Connection con = null;
        try {
            ds.setLoginTimeout(20);
            con = ds.getConnection();
        } catch (Exception e) {
            log.error("" + e, e);
            return false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    log.error("" + ex, ex);
                }
            }
        }
        return true;
    }


    private void initDbPoolFactory() {

        try {
            this.initAllPool();
            checkSlaveDataSourceAlive();
        } catch (Exception e) {
            log.error("", e);
            new Exception("add database pool error!", e);
        }
    }

    public DataSource getDBPool(String dbpoolName) throws DbException {
        return getDBPool(dbpoolName, new HashMap<>());
    }


    /**
     * @param dbpoolName dbpoolxml里的连接池名
     * @param pros       存放返回的属性
     * @return 返回DataSource对象
     * @throws DbException 异常
     */
    public DataSource getDBPool(String dbpoolName, Map<String, String> pros) throws DbException {

        try {
            rwLock.readLock().lock();
            if (dbpoolName == null)
                throw new DbException("DBPool 'key' CANNOT be null");

            ConcurrentHashMap<String, Map<String, String>> maps = this.readConfig.getProperties();
            if (pros != null) {
                pros.putAll(maps.get(dbpoolName));
            }

            DataSource ds = (DataSource) poollist.get(dbpoolName);
            return ds;
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            rwLock.readLock().unlock();
        }
    }


    public static class DataSourceStat {
        private long lastCheckTime;
        private int errCnt;

        public long getLastCheckTime() {
            return lastCheckTime;
        }

        public void setLastCheckTime(long lastCheckTime) {
            this.lastCheckTime = lastCheckTime;
        }

        public int getErrCnt() {
            return errCnt;
        }

        public void setErrCnt(int errCnt) {
            this.errCnt = errCnt;
        }
    }

    private volatile Map<DataSource, DataSourceStat> errorDataSourceStat = new ConcurrentHashMap<>();

    public boolean checkIsNormalDataSource(DataSource dss) {
        Connection connection = null;
        try {
            connection = dss.getConnection();
            DBMS dbms = DialectClient.decideDialect(connection);
            PreparedStatement preparedStatement = connection.prepareStatement(dbms.getCheckSql());
            preparedStatement.execute();
        } catch (Exception e) {
            log.error("" + e, e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exception) {
                }
            }
        }
        return true;
    }

    public void checkSlaveDataSourceAlive() {
        for (String poolName : poolSlaveList.keySet()) {
            Map<String, DataSource> slaveDss = poolSlaveList.get(poolName);
            if (slaveDss.size() > 0) {
                Thread thread = new Thread(() -> {
                    while (true) {
                        try {
                            rwLock.readLock().lock();
                            for (String slaveServerName : slaveDss.keySet()) {
                                try {
                                    DataSource slaveDataSource = slaveDss.get(slaveServerName);
                                    int errCnt = 0;
                                    do {
                                        boolean ret = checkIsNormalDataSource(slaveDataSource);
                                        if (!ret) {
                                            errCnt++;
                                        } else {
                                            errCnt = 0;
                                            break;
                                        }
                                        Thread.sleep(2000);
                                    } while (errCnt >= 3);
                                    if (errCnt >= 3) { //说明存在错误
                                        DataSourceStat dataSourceStat = new DataSourceStat();
                                        dataSourceStat.setErrCnt(errCnt);
                                        dataSourceStat.setLastCheckTime(System.currentTimeMillis());
                                        errorDataSourceStat.put(slaveDataSource, dataSourceStat);
                                    } else {
                                        //已经正常，移除错误统计
                                        errorDataSourceStat.remove(slaveDataSource);
                                    }

                                } catch (Exception exception) {
                                    log.error(exception + "", exception);
                                }

                            }
                        } finally {
                            rwLock.readLock().unlock();
                        }
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {

                        }
                    }
                });
                thread.setDaemon(true);
                thread.setName("dataSource-check-thread[" + poolName + "]");
                thread.start();
            }
        }
    }

    public ConcurrentHashMap<String, DataSource> getSlaveDataSources(String poolName) {

        try {
            rwLock.readLock().lock();
            return this.poolSlaveList.get(poolName);
        } finally {
            rwLock.readLock().unlock();
        }

    }

    /**
     * 根据连接池的名称选择从库
     *
     * @param poolName 连接池名称
     * @param result   返回从库的数据库连接
     * @return 返回DataSource对象
     */
    public DataSource selectSlaveDbPool(String poolName, TResult<String> slaveName, TResult<Connection> result) {
        try {
            rwLock.readLock().lock();
            Map<String, DataSource> slaveDss = poolSlaveList.get(poolName);
            List<DataSource> availableDss = new ArrayList<DataSource>();
            List<String> availableDssNames = new ArrayList<>();
            // 选取
            for (String slaveServerName : slaveDss.keySet()) {
                DataSource dataSource = slaveDss.get(slaveServerName);
                if (errorDataSourceStat.get(dataSource) == null) {
                    availableDss.add(dataSource);
                    availableDssNames.add(slaveServerName);
                }
            }
            // 随机选一条
            if (availableDss.size() > 0) {
                int index = RandomUtils.nextInt(availableDss.size());
                int cnt = 0;
                while (true) {
                    if (cnt >= availableDss.size()) { //说明所有可用数据源都不能获取连接，则退出
                        throw new DbException("所有可用数据源都不能获取连接！");
                    }
                    if (DbContext.permitDebugLog())
                        log.debug("slave server.size=" + availableDss.size() + ",select[" + index + "]");
                    index = index % availableDss.size();
                    DataSource dss = availableDss.get(index);
                    if (errorDataSourceStat.get(dss) != null) { //说明是错误数据源
                        index++;
                        cnt++;
                        continue;
                    }
                    if (result != null) {
                        Connection connection = null;
                        try {
                            connection = dss.getConnection();
                            result.setValue(connection);
                            slaveName.setValue(availableDssNames.get(index));
                            return dss;
                        } catch (Exception exception) {
                            if (connection != null) {
                                try {
                                    connection.close();
                                    result.setValue(null);
                                } catch (SQLException throwables) {
                                }
                            }
                            log.error("获取从库连接失败！" + exception + ",继续尝试选择其它从库获取！", exception);
                            index++;
                            cnt++;

                        }
                    } else {
                        slaveName.setValue(availableDssNames.get(index));
                        return dss;
                    }
                }
            } else {
                throw new DbException("所有从库连接无法获取，可能从库出现故障！");
            }
        } finally {
            rwLock.readLock().unlock();
        }

    }


    public static void main(String[] args) {
    }

    public static void close() {
        synchronized (DBPoolFactory.class) {
            try {
                // 清空常规连接池
                Set<String> dbpoolFactoryMapKeys = dbpoolFactoryMap.keySet();
                for (String xmlFileName : dbpoolFactoryMapKeys) {
                    close(xmlFileName);
                }

            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private static void close(DataSource dataSource) {
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            org.apache.tomcat.jdbc.pool.DataSource dss =
                    (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
            dss.close();
        } else {
        }
    }

    public static void close(String xmlFileName, String poolName) {
        synchronized (DBPoolFactory.class) {
            try {
                // 关闭主库连接池
                DBPoolFactory dbPoolFactory = dbpoolFactoryMap.get(xmlFileName);
                if (dbPoolFactory == null) return;
                try {
                    DataSource ds = dbPoolFactory.poollist.get(poolName);
                    close(ds);
                } catch (Exception e) {
                    log.error("", e);
                }

                // 关闭从库的连接池
                Map<String, DataSource> slaveMap = dbPoolFactory.poolSlaveList.get(poolName);
                for (String slaveServerName : slaveMap.keySet()) {
                    try {
                        DataSource dss = slaveMap.get(slaveServerName);
                        if (dss instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                            org.apache.tomcat.jdbc.pool.DataSource odss = (org.apache.tomcat.jdbc.pool.DataSource) dss;
                            odss.close();
                        } else {
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }

            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static void close(String xmlFileName) {
        synchronized (DBPoolFactory.class) {
            try {
                // 清空常规连接池
                DBPoolFactory dbPoolFactory = dbpoolFactoryMap.get(xmlFileName);
                if (dbPoolFactory == null) return;
                Set<String> sets = dbPoolFactory.poollist.keySet();
                Iterator<String> iter = sets.iterator();
                while (iter.hasNext()) {
                    try {
                        String poolName = iter.next();
                        close(xmlFileName, poolName);
                    } catch (Exception e) {
                        log.error("", e);
                    }

                }

            } catch (Exception e) {
                log.error("", e);
            }
        }

    }

    public static String aesUnEncrypt(String str) {
        return EncryptUtil.aesUnEncrypt(str, KEY);
    }

    public static String aesEncrypt(String str) {
        return EncryptUtil.aesEncrypt(str, KEY);
    }
}
