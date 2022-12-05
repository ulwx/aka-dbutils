package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.IDBPoolAttrSource;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.database.dialect.DialectClient;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.support.*;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import com.github.ulwx.aka.dbutils.tool.support.reflect.ReflectionUtil;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult;
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
    private final static Logger log = LoggerFactory.getLogger(DBPoolFactory.class);
    private volatile String dbPoolXmlFileName = null;
    private volatile ReadConfig readConfig;
    private final static String KEY = "S$T$#@QR@GHODFP$R";
    // 0 表示没有初始化， 1：表示正在初始化 2：初始化完成  3：代表部分初始化开始  4：部分初始化完成  134：获取信息
    private volatile int status = INIT_STATUS.uninit;

    static class INIT_STATUS {
        public static int uninit = 0;
        public static int doing = 1;
        public static int finished = 2;

    }

    private volatile int partUpdateStatus = PART_UPDATE_STATUS.uninit;

    static class PART_UPDATE_STATUS {
        public static int uninit = 0;
        public static int doing = 1;
        public static int finished = 2;

    }

    private static volatile ThreadLocal<Boolean> DO_INI_SAME_THREAD = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    private volatile ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    static class PoolType {
        public final static String TOMCAT_DB_POOL = "tomcatdbpool";
        public final static String ALiBABA_DRUID="druid";
        public final static String HikariCP="HikariCP";
        public final static Map<String,DBPool> map = new HashMap<>();
        static{
            map.put(TOMCAT_DB_POOL,TomcatDBPoolImpl.instance);
            map.put(ALiBABA_DRUID,DruidDBPoolImpl.instance);
            map.put(HikariCP,HikariDBPoolImpl.instance);
        }
       public static DBPool getDBPool(String PoolType){
           return map.get(PoolType);
       }
    }

    public ReadConfig getReadConfig() {
        return readConfig;
    }

    private volatile Map<String, DataSource> poollist = new ConcurrentHashMap<String, DataSource>();
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
        return MD.parsePoolName(dbPoolXmlFileNameAndDbPoolName);
    }

    public static DBPoolFactory getInstance(String dbPoolXmlFileName) {
        try {
            DBPoolFactory dbPoolFactory = dbpoolFactoryMap.get(dbPoolXmlFileName);
            if (dbPoolFactory == null) {
                synchronized (DBPoolFactory.class) {
                    dbPoolFactory = dbpoolFactoryMap.get(dbPoolXmlFileName);
                    if (dbPoolFactory == null) {
                        Resource[] resources = ReadConfig.getResource(dbPoolXmlFileName);
                        ReadConfig.checkResource(resources, dbPoolXmlFileName);
                        dbPoolFactory = new DBPoolFactory(dbPoolXmlFileName);
                        dbpoolFactoryMap.put(dbPoolXmlFileName, dbPoolFactory);
                        DO_INI_SAME_THREAD.set(true);
                        dbPoolFactory.initDbPoolFactory();
                        DO_INI_SAME_THREAD.set(false);

                    }
                }
            }
            //检测dbPoolFactory对象是否构造完成
            dbPoolFactory.checkInitIsFinished();
            return dbPoolFactory;
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }

    }


    public boolean isMainSlaveMode(String poolName) {
        //checkInitIsFinished();
        try {
            rwLock.readLock().lock();
            if (poolSlaveList != null && poolSlaveList.get(poolName) != null && poolSlaveList.get(poolName).size() > 0)
                return true;
            return false;
        } finally {
            rwLock.readLock().unlock();

        }
    }


    private static void initOnePool(String dbPoolXmlFileName, String poolName, TResult<DataSource> masterDataSoruce,
                                    TResult<ConcurrentHashMap<String, DataSource>> slaverDataSourceMap) {
        try {

            ReadConfig readConfig = ReadConfig.getInstance(dbPoolXmlFileName);
            ConcurrentHashMap<String, Map<String, String>> maps = readConfig.getProperties();
            ConcurrentHashMap<String, Map<String, Map<String, String>>> slaveProperites = readConfig.getSlaveProperites();

            Map<String, String> masterMap = maps.get(poolName);
            Map<String, Map<String, String>> slaveServersMap = slaveProperites.get(poolName);

            initOnePool(masterMap, slaveServersMap, masterDataSoruce, slaverDataSourceMap);
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }
    }

    private static void initOnePool(Map<String, String> masterMap,
                                    Map<String, Map<String, String>> slaveServersMap,
                                    TResult<DataSource> masterDataSoruce,
                                    TResult<ConcurrentHashMap<String, DataSource>> slaverDataSourceMap) {
        try {

            Map<String, String> map = masterMap;
            Map<String, Map<String, String>> slaveServer = slaveServersMap;
            String driverClassName = map.get("driverClassName");
            String url = StringUtils.trim(map.get("url"));
            String user = StringUtils.trim(map.get("username"));
            String password = StringUtils.trim(map.get("password"));
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

            String removeAbandoned = StringUtils.trim(map.get("removeAbandoned"), "false");
            String removeAbandonedTimeout = StringUtils.trim(map.get("removeAbandonedTimeout"), "1800");

            ConcurrentHashMap<String, DataSource> slaveDataSources = new ConcurrentHashMap<String, DataSource>();
            DataSource ds = null;
            log.debug("driverClassName=" + driverClassName);

            DBPool dbPool=PoolType.getDBPool(type);
            ds = dbPool.getNewDataSource(url, user, password, checkoutTimeout, maxPoolSize, minPoolSize,
                    maxStatements, maxIdleTime, idleConnectionTestPeriod, driverClassName,removeAbandoned,removeAbandonedTimeout);
            //判断ds是否可以获得连接
            boolean res = DBPoolFactory.startDataSource(ds);
            if (!res) {
                dbPool.close(ds);
                return;
            }
            slaveDataSources = getSlaveServerConfig(slaveServer, PoolType.TOMCAT_DB_POOL, driverClassName);

            masterDataSoruce.setValue(ds);
            slaverDataSourceMap.setValue(slaveDataSources);
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }
    }

    final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * @throws DbException
     */
    private void initAllPool() throws DbException {
        try {
            //关闭所有连接池
            status = INIT_STATUS.doing;
            close(dbPoolXmlFileName);
            ReadConfig readConfig = ReadConfig.getInstance(dbPoolXmlFileName);
            this.readConfig = readConfig;
            ConcurrentHashMap<String, Map<String, String>> poolNameMasterMap = readConfig.getProperties();
            ConcurrentHashMap<String, Map<String, Map<String, String>>> slaveNameMap = readConfig.getSlaveProperites();
            Iterator<String> iter = poolNameMasterMap.keySet().iterator();
            Map<String, String> refMap = new TreeMap<>();
            Map<String, Class> refClassMap = new TreeMap<>();
            while (iter.hasNext()) {
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
                    }
                    if (!refClass.isEmpty()) {
                        if (!ref.isEmpty()) {
                            throw new DbException(dbPoolXmlFileName + "文件里" + poolName
                                    + "定义的属性不能同时存在ref和ref-class属性！");
                        }
                        refClassMap.put(poolName, Class.forName(refClass));
                    }

                }

            } // while
            //处理RefClass引用
            for (String poolName : refClassMap.keySet()) {
                Class refClass = refClassMap.get(poolName);
                if (IDBPoolAttrSource.class.isAssignableFrom(refClass)) {
                    IDBPoolAttrSource idbPoolAttrSource = (IDBPoolAttrSource) refClass.getConstructor().newInstance();
                    Map<String, String> masterMap = poolNameMasterMap.get(poolName);
                    this.refClassPropertyRefresh(poolName, idbPoolAttrSource);
                    long checkTime = 0;
                    try {
                        String str = StringUtils.trim(masterMap.get("check-time"));
                        if (!str.isEmpty()) {
                            checkTime = Integer.valueOf(str);
                        }
                    } catch (Exception e) {

                    }
                    if (checkTime > 2) {
                        executorService.scheduleWithFixedDelay(() -> {
                            if (this.status != INIT_STATUS.finished) {
                                return;
                            }
                            this.refClassPropertyRefresh(poolName, idbPoolAttrSource);
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
                //本地引用
                DataSource masterDataSource = poollist.get(refDbPoolName);
                if (masterDataSource == null) {
                    throw new DbException(refFileName + "里无法找到" + refDbPoolName + "的定义！");
                }
                ConcurrentHashMap<String, DataSource> slaveDataSources = poolSlaveList.get(refDbPoolName);
                poollist.put(poolName, masterDataSource);
                poolSlaveList.put(poolName, slaveDataSources);

            }

        } catch (Exception e) {

            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("add database pool error!", e);
        } finally {
            status = INIT_STATUS.finished;
        }
    }

    private void refClassPropertyRefresh(String poolName, IDBPoolAttrSource attrSource) {

        Map<String, String> lmasterProperties = new HashMap<>();
        Map<String, Map<String, String>> newlaveProperties = new HashMap<>();
        boolean debug = DbContext.permitDebugLog();
        DbContext.permitDebugLog(true);
        try {
            attrSource.configProperties(lmasterProperties, newlaveProperties);
            if(lmasterProperties.isEmpty() && newlaveProperties.isEmpty()){
                return ;
            }
        } catch (Exception e) {
            throw new DbException("获取数据源信息异常！",e);
        }
        DbContext.permitDebugLog(debug);
        ConcurrentHashMap<String, Map<String, String>> poolNameMasterMap = readConfig.getProperties();
        Map<String, String> masterMap = poolNameMasterMap.get(poolName);
        ConcurrentHashMap<String, Map<String, Map<String, String>>> slaveNameMap = readConfig.getSlaveProperites();
        Map<String, Map<String, String>> slaveProperties = slaveNameMap.get(poolName);
        HashMap<String, String> newMasterMap = new HashMap<>();
        newMasterMap.put("name", masterMap.get("name"));
        newMasterMap.put("ref-class", masterMap.get("ref-class"));
        newMasterMap.put("table-name-rule", masterMap.get("table-name-rule"));
        newMasterMap.put("table-colum-rule", masterMap.get("table-colum-rule"));
        newMasterMap.put("check-time", masterMap.get("check-time"));
        newMasterMap.putAll(lmasterProperties);

        if (ObjectUtils.deepEquals(newMasterMap, masterMap) &&
                ObjectUtils.deepEquals(newlaveProperties, slaveProperties)) {
            //说明配置没有更新，不用更新连接池
            return;
        } else { //重新构造连接池
            //checkInitIsFinished();
            try {
                partUpdateStatus = PART_UPDATE_STATUS.doing;
                rwLock.writeLock().lock();
                log.debug("get write lock!");
                String type = StringUtils.trim(masterMap.get("type"), PoolType.TOMCAT_DB_POOL);
                close(dbPoolXmlFileName, poolName, type);
                poolNameMasterMap.put(poolName, newMasterMap);
                slaveNameMap.put(poolName, newlaveProperties);
                TResult<DataSource> ltmasterDataSoruce = new TResult<>();
                TResult<ConcurrentHashMap<String, DataSource>> ltslaverDataSourceMap = new TResult<>();
                initOnePool(dbPoolXmlFileName, poolName, ltmasterDataSoruce, ltslaverDataSourceMap);
                poollist.put(poolName, ltmasterDataSoruce.getValue());
                poolSlaveList.put(poolName, ltslaverDataSourceMap.getValue());
            } finally {
                rwLock.writeLock().unlock();
                log.debug("release write lock!");
                partUpdateStatus = PART_UPDATE_STATUS.finished;
            }
        }

    }

    private static ConcurrentHashMap<String, DataSource> getSlaveServerConfig(Map<String, Map<String, String>> slaveServer, String poolType,
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
                String s_removeAbandoned = StringUtils.trim(slaveConfig.get("removeAbandoned"), "false");
                String s_removeAbandonedTimeout = StringUtils.trim(slaveConfig.get("removeAbandonedTimeout"), "1800");

                DataSource s_ds = null;
                DBPool dbPool=PoolType.getDBPool(poolType);
                s_ds = dbPool.getNewDataSource(s_url, s_user, s_password, s_checkoutTimeout, s_maxPoolSize,
                        s_minPoolSize, s_maxStatements, s_maxIdleTime, s_idleConnectionTestPeriod, driverClassName,s_removeAbandoned,s_removeAbandonedTimeout);

                if (s_ds == null) {
                    log.error("无法从库连接池" + slaveServerName + "获取连接！数据源返回为空,忽略...");
                    continue;
                }
                // 判断ds是否可以获得连接
                boolean res = DBPoolFactory.startDataSource(s_ds);
                if (!res) {// 不能获得连接
                    log.error("无法从库连接池" + slaveServerName + "获取连接！忽略....");
                    dbPool.close(s_ds);
                } else {
                    slaveDataSources.put(slaveServerName, s_ds);
                }
            }
        }

        return slaveDataSources;
    }


    private static boolean startDataSource(DataSource ds) {
        Connection con = null;
        try {
            ds.setLoginTimeout(30);
            con = ds.getConnection();
        } catch (Exception e) {
            log.error("" + e+",ds="+ds, e);
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
        synchronized (this) {
            this.initAllPool();
            this.notifyAll();
        }
        checkSlaveDataSourceAlive();
    }

    public DataSource getDBPool(String dbpoolName) throws DbException {
        return getDBPool(dbpoolName, null);
    }


    /**
     * @param dbpoolName dbpoolxml里的连接池名
     * @param pros       存放返回的属性
     * @return 返回DataSource对象
     * @throws DbException 异常
     */
    public DataSource getDBPool(String dbpoolName, Map<String, String> pros) throws DbException {

        //checkInitIsFinished();
        try {
            rwLock.readLock().lock();
            if (dbpoolName == null)
                throw new DbException("DBPool 'key' CANNOT be null");

            if (pros != null) {
                ConcurrentHashMap<String, Map<String, String>> maps = this.readConfig.getProperties();
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

    private void checkInitIsFinished() {
        try {
            if (this.status != INIT_STATUS.finished) {
                while (true) {
                    if (this.status != INIT_STATUS.finished
                            && !DO_INI_SAME_THREAD.get().booleanValue()) {
                        synchronized (this) {
                            this.wait();
                        }

                    } else {
                        return;
                    }
                }

            }
        } catch (Exception e) {
            throw new DbException(e);
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

    private void checkSlaveDataSourceAlive() {
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
     * @return 返回DataSource对象
     */
    public DataSource selectSlaveDbPool(String poolName, TResult<String> slaveName) {
        //checkInitIsFinished();
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
                    slaveName.setValue(availableDssNames.get(index));
                    return dss;
                }
            } else {
                throw new DbException("所有从库连接无法获取，可能从库出现故障！");
            }
        } finally {
            rwLock.readLock().unlock();
        }

    }


    public static void main(String[] args) throws Exception {
        executorService.scheduleWithFixedDelay(() -> {
            System.out.println(123);
        }, 2, 2, TimeUnit.SECONDS);
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

    private static void close(DataSource dataSource, String poolType) throws Exception {
        PoolType.getDBPool(poolType).close(dataSource);
    }

    public static void close(String xmlFileName, String poolName, String poolType) {
        synchronized (DBPoolFactory.class) {
            try {
                // 关闭主库连接池
                DBPoolFactory dbPoolFactory = dbpoolFactoryMap.get(xmlFileName);
                if (dbPoolFactory == null) return;
                try {
                    DataSource ds = dbPoolFactory.poollist.get(poolName);
                    if (ds == null) return;
                    close(ds, poolType);
                } catch (Exception e) {
                    log.error("", e);
                }

                // 关闭从库的连接池
                Map<String, DataSource> slaveMap = dbPoolFactory.poolSlaveList.get(poolName);
                for (String slaveServerName : slaveMap.keySet()) {
                    try {
                        DataSource dss = slaveMap.get(slaveServerName);
                        close(dss, poolType);
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
                        ConcurrentHashMap<String, Map<String, String>> maps = dbPoolFactory.readConfig.getProperties();
                        Map<String, String> masterMap = maps.get(poolName);
                        close(xmlFileName, poolName, masterMap.get("type"));
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
