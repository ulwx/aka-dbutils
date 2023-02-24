package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.reflect.ReflectionUtil;

import javax.sql.DataSource;

public class TomcatDBPoolImpl implements DBPool{
    public static TomcatDBPoolImpl instance=new TomcatDBPoolImpl();

    public Object configJdbcPoolProperties(String url, String user, String password,
                                           String checkoutTimeout, String maxPoolSize,
                                           String minPoolSize, String maxStatements,
                                           String maxIdleTime, String idleConnectionTestPeriod,
                                           String driverClassName,
                                           String removeAbandoned,
                                           String removeAbandonedTimeout) throws Exception {
        //PoolProperties p1=new PoolProperties();
        Object p = Class.forName("org.apache.tomcat.jdbc.pool.PoolProperties").getDeclaredConstructor().newInstance();
        ReflectionUtil.invoke(p, "setUrl", String.class, url);
        ReflectionUtil.invoke(p, "setDriverClassName", String.class, driverClassName);
        if(StringUtils.hasText(user)) {
            ReflectionUtil.invoke(p, "setUsername", String.class, user);
            ReflectionUtil.invoke(p, "setPassword", String.class, password);
        }

        ReflectionUtil.invoke(p, "setJmxEnabled", boolean.class, true);
        ReflectionUtil.invoke(p, "setTestWhileIdle", boolean.class, true);
        ReflectionUtil.invoke(p, "setTestOnBorrow", boolean.class, false);
        ReflectionUtil.invoke(p, "setTestOnReturn", boolean.class, false);
        if (StringUtils.containsIgnoreCase(url, "oracle")) {
            ReflectionUtil.invoke(p, "setValidationQuery", String.class, "select 1 from dual");
        } else {
            ReflectionUtil.invoke(p, "setValidationQuery", String.class, "SELECT 1");
        }

        int test = Integer.valueOf(idleConnectionTestPeriod);// 秒

        ReflectionUtil.invoke(p, "setValidationInterval", long.class, test * 1000);
        ReflectionUtil.invoke(p, "setTimeBetweenEvictionRunsMillis", int.class, test * 1000);

        ReflectionUtil.invoke(p, "setMaxActive", int.class, Integer.valueOf(maxPoolSize));
        ReflectionUtil.invoke(p, "setInitialSize", int.class, Integer.valueOf(minPoolSize));
        ReflectionUtil.invoke(p, "setMaxWait", int.class, Integer.valueOf(checkoutTimeout));

        ReflectionUtil.invoke(p, "setMinEvictableIdleTimeMillis", int.class,
                Integer.valueOf(maxIdleTime) * 1000);
        ReflectionUtil.invoke(p, "setMinIdle", int.class, Integer.valueOf(minPoolSize));
        ReflectionUtil.invoke(p, "setMaxIdle", int.class, Integer.valueOf(maxPoolSize));
        ReflectionUtil.invoke(p, "setLogAbandoned", boolean.class, true);
        ReflectionUtil.invoke(p, "setRemoveAbandoned", boolean.class, Boolean.valueOf(removeAbandoned));
        ReflectionUtil.invoke(p, "setRemoveAbandonedTimeout", int.class,
                Integer.valueOf(removeAbandonedTimeout));
        ReflectionUtil.invoke(p, "setJdbcInterceptors", String.class, "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return p;
    }

    @Override
    public DataSource getNewDataSource(DBPoolAttr dbPoolAttr) throws Exception {
        Object poolProperties = configJdbcPoolProperties(
                dbPoolAttr.getUrl() ,
                dbPoolAttr.getUser(),
                dbPoolAttr.getPassword(),
                dbPoolAttr.getCheckoutTimeout(),
                dbPoolAttr.getMaxPoolSize(),
                dbPoolAttr.getMinPoolSize(),
                dbPoolAttr.getMaxStatements(),
                dbPoolAttr.getMaxIdleTime(),
                dbPoolAttr.getIdleConnectionTestPeriod(),
                dbPoolAttr.getDriverClassName() ,
                dbPoolAttr.getRemoveAbandoned(),
                dbPoolAttr.getRemoveAbandonedTimeout());
        DataSource datasource = (DataSource) Class.forName("org.apache.tomcat.jdbc.pool.DataSource")
                .getDeclaredConstructor().newInstance();

        ReflectionUtil.invoke(datasource, "setPoolProperties",
                "org.apache.tomcat.jdbc.pool.PoolConfiguration"
                , poolProperties);
        DataSource ds = datasource;
        return ds;
    }

   public void close(DataSource dataSource) throws Exception {

       ReflectionUtil.invoke(dataSource, "close");

    }

    @Override
    public String getType() {
        return PoolType.TOMCAT_DB_POOL;
    }
}
