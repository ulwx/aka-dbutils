package com.github.ulwx.database.dbpool;

import com.github.ulwx.database.DbException;
import com.github.ulwx.tool.support.EncryptUtil;
import com.github.ulwx.tool.support.RandomUtils;
import com.github.ulwx.tool.support.StringUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DBPoolFactory {
	private volatile static Logger log = LoggerFactory.getLogger(DBPoolFactory.class);

	private static String KEY = "S$T$#@QR@GHODFP$R";
	private volatile int status = 0; // 0 表示没有初始化， 1：表示正在初始化 2：表示完成

	static class PoolType {
		public static String TOMCAT_DB_POOL = "tomcatdbpool";
	};

	private volatile Map<String, DataSource> poollist = new ConcurrentHashMap<String, DataSource>();

	/**
	 * 主库和从库的关系
	 */
	private volatile Map<String, Map<String, DataSource>> poolSlaveList = new ConcurrentHashMap<String, Map<String, DataSource>>();

	private static volatile DBPoolFactory dbpoolFactory = new DBPoolFactory();

	private DBPoolFactory() {
		// System.out.println(poolSlaveExceptionTimes);
		initPool();
	}

	public static String getEncryptPassword(String password) {
		return EncryptUtil.aesEncrypt(password, KEY);
	}

	public static DBPoolFactory getInstance() {
		if (dbpoolFactory == null) {
			synchronized (DBPoolFactory.class) {
				if (dbpoolFactory == null)
					dbpoolFactory = new DBPoolFactory();
			}
		}
		return dbpoolFactory;
	}

	/**
	 * 向连接池列表添加一个新的池化（Pooled）数据源
	 * 
	 * @param key
	 * @param configFile 配置文件名。为了简化参数输入与便于外部维护，可以把配置好的文件传入这里分析
	 */
	public void add(String key, String configFile) {
		// read config from file, and build a dbpool
	}

	public boolean isMainSlaveMode(String poolName) {
		if (poolSlaveList != null && poolSlaveList.get(poolName) != null && poolSlaveList.get(poolName).size() > 0)
			return true;

		return false;
	}

	synchronized public void setPoollist() throws DbException {

		try {
			if (status != 0) {
				return;
			} else {
				status = 1;
			}
			clearAll();
			Map<String, Map<String, String>> maps = ReadConfig.getInstance().getProperties();
			Map<String, Map<String, Map<String, String>>> slaveProperites = ReadConfig.getInstance()
					.getSlaveProperites();
			Set<String> sets = maps.keySet();
			Iterator<String> iter = sets.iterator();
			while (iter.hasNext()) {
				try {
					String poolName = iter.next();
					Map<String, String> map = maps.get(poolName);
					Map<String, Map<String, String>> slaveServer = slaveProperites.get(poolName);
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

					Class.forName(driverClassName);

					DataSource ds = null;
					Map<String, DataSource> slaveDataSources = new HashMap<String, DataSource>();

					if (type.equals(PoolType.TOMCAT_DB_POOL)) {
					ds = getDataSourceFromTomcatDb(url, user, password, checkoutTimeout, maxPoolSize, minPoolSize,
								maxStatements, maxIdleTime, idleConnectionTestPeriod, driverClassName);
						// 判断ds是否可以获得连接
						boolean res = DBPoolFactory.startDataSource(ds);
						if (!res) {
							continue;
						}
						slaveDataSources = getSlaveServerConfig(slaveServer, PoolType.TOMCAT_DB_POOL, driverClassName);
					}

					poollist.put(poolName, ds);
					poolSlaveList.put(poolName, slaveDataSources);
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					log.error("", ex);
					throw ex;
				}
			} // while
			status = 2;

		} catch (Exception e) {
			// log.error("", e);

			clearAll();
			status = 0;
			new DbException("add database pool error!", e);
		} finally {

		}

	}

	public Map<String, DataSource> getSlaveServerConfig(Map<String, Map<String, String>> slaveServer, String poolType,
			String driverClassName) throws Exception {

		Map<String, DataSource> slaveDataSources = new HashMap<String, DataSource>();
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
					s_ds = getDataSourceFromTomcatDb(s_url, s_user, s_password, s_checkoutTimeout, s_maxPoolSize,
							s_minPoolSize, s_maxStatements, s_maxIdleTime, s_idleConnectionTestPeriod, driverClassName);
				} else {
					continue;
				}
				// 判断ds是否可以获得连接
				boolean res = DBPoolFactory.startDataSource(s_ds);
				if (!res) {// 不能获得连接
					if (poolType.equals(PoolType.TOMCAT_DB_POOL)) {
						org.apache.tomcat.jdbc.pool.DataSource old_pooled = (org.apache.tomcat.jdbc.pool.DataSource) s_ds;
						old_pooled.close();
					}
					continue;
				} else {
					slaveDataSources.put(slaveServerName, s_ds);
				}
			}
		}

		return slaveDataSources;
	}

	public DataSource getDataSourceFromTomcatDb(String url, String user, String password, String checkoutTimeout,
			String maxPoolSize, String minPoolSize, String maxStatements, String maxIdleTime,
			String idleConnectionTestPeriod, String driverClassName) throws Exception {

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
		org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
		datasource.setPoolProperties(p);

		DataSource ds = datasource;

		return ds;

		// 从库的设置

	}

	public static boolean startDataSource(DataSource ds) {
		Connection con = null;
		try {
			ds.setLoginTimeout(60);
			ds.getConnection();
		} catch (Exception e) {
			log.error("", e);
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
		}
		return true;
	}


	synchronized public void initPool() {

		try {

			this.setPoollist();
//			// 初始化poolSlaveLiveTimes
//			for (String poolName : poolSlaveList.keySet()) {
//				Map<String, Date> curmap = new ConcurrentHashMap<String, Date>();
//
//				Map<String, DataSource> dss = poolSlaveList.get(poolName);
//
//			}

		} catch (Exception e) {
			log.error("", e);
			new Exception("add database pool error!", e);
		}
	}

	synchronized public static void init() {
		DBPoolFactory.getInstance();
	}

	/**
	 * 
	 * @param key  连接池名
	 * @param pros 返回的属性
	 * @return
	 * @throws DbException
	 */
	public DataSource getDBPool(String key, Map<String, String> pros) throws DbException {
		try {
			Map<String, Map<String, String>> maps = ReadConfig.getInstance().getProperties();
			if (pros != null) {
				pros.putAll(maps.get(key));
			}

			if (key == null)
				throw new DbException("DBPool 'key' CANNOT be null");
			DataSource ds = (DataSource) poollist.get(key);

			return ds;
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	/**
	 * 根据连接池的名称选择从库
	 * 
	 * @param poolName
	 * @return
	 */
	public DataSource getSlaveDbPool(String poolName) {
		Map<String, DataSource> slaveDss = poolSlaveList.get(poolName);

		List<DataSource> availableDss = new ArrayList<DataSource>();
		// 选取
		for (String slaveServerName : slaveDss.keySet()) {

			availableDss.add(slaveDss.get(slaveServerName));
		}

		// 随机选一条
		if (availableDss.size() > 0) {
			int index = RandomUtils.nextInt(availableDss.size());
			log.debug("slave server.size:" + availableDss.size() + ",select[" + index + "]");
			return availableDss.get(index);
		}
		return null;

	}

	public static void main(String[] args) {
	}

	synchronized public void clearAll() {
		try {
			// 清空常规连接池
			Set<String> sets = poollist.keySet();
			Iterator<String> iter = sets.iterator();
			while (iter.hasNext()) {
				try {
					String poolName = iter.next();
					DataSource ds = poollist.get(poolName);
					if (ds instanceof org.apache.tomcat.jdbc.pool.DataSource) {
						org.apache.tomcat.jdbc.pool.DataSource dss = (org.apache.tomcat.jdbc.pool.DataSource) ds;
						dss.close();
					} else {
					}
				} catch (Exception e) {
					log.error("", e);
				}

			}
			poollist.clear();

			// 清除从库的连接池
			for (String poolName : poolSlaveList.keySet()) {
				Map<String, DataSource> slaveMap = poolSlaveList.get(poolName);
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
			}
			poolSlaveList.clear();

		} catch (Exception e) {
			log.error("", e);
		}
	}

}
