package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.reflect.ReflectionUtil;

import javax.sql.DataSource;

public class DruidDBPoolImpl implements DBPool{
    public static DruidDBPoolImpl instance=new DruidDBPoolImpl();



    @Override
    public DataSource getNewDataSource(String url, String user, String password, String checkoutTimeout,
                                       String maxPoolSize, String minPoolSize,
                                       String maxStatements, String maxIdleTime,
                                       String idleConnectionTestPeriod, String driverClassName,
                                       String removeAbandoned,
                                       String removeAbandonedTimeout) throws Exception {

      //  com.alibaba.druid.pool.DruidDataSource druidDataSource = new com.alibaba.druid.pool.DruidDataSource();
        Object p = Class.forName("com.alibaba.druid.pool.DruidDataSource").getDeclaredConstructor().newInstance();
        ReflectionUtil.invoke(p, "setUrl", String.class, url);
        ReflectionUtil.invoke(p, "setUsername", String.class, user);
        ReflectionUtil.invoke(p, "setPassword", String.class, password);
        ReflectionUtil.invoke(p, "setDriverClassName", String.class, driverClassName);
        //druidDataSource.setInitialSize(
        ReflectionUtil.invoke(p, "setInitialSize", int.class, Integer.valueOf(minPoolSize));
        ReflectionUtil.invoke(p, "setMaxActive", int.class, Integer.valueOf(maxPoolSize));
        ReflectionUtil.invoke(p, "setMinIdle", int.class, Integer.valueOf(minPoolSize));
        ReflectionUtil.invoke(p, "setMaxWait", long.class, Long.valueOf(checkoutTimeout));
        if(StringUtils.containsIgnoreCase(url,"oracle")){
            ReflectionUtil.invoke(p, "setPoolPreparedStatements", boolean.class,true);
            ReflectionUtil.invoke(p, "setMaxPoolPreparedStatementPerConnectionSize", int.class,100);
            ReflectionUtil.invoke(p, "setValidationQuery", String.class,"select 1 from dual");
        }else{
            ReflectionUtil.invoke(p, "setValidationQuery", String.class,"select 1");
        }

        //ReflectionUtil.invoke(p, "setValidationQueryTimeout", int.class, test);
        ReflectionUtil.invoke(p, "setTestOnBorrow", boolean.class, true);
        ReflectionUtil.invoke(p, "setTestOnReturn", boolean.class, true);
        ReflectionUtil.invoke(p, "setTestWhileIdle", boolean.class, true);
        ReflectionUtil.invoke(p, "setKeepAlive", boolean.class, true);
        int test = Integer.valueOf(idleConnectionTestPeriod);// 秒
        ReflectionUtil.invoke(p, "setTimeBetweenEvictionRunsMillis", long.class, test*1000l);//40秒
        ReflectionUtil.invoke(p, "setMinEvictableIdleTimeMillis", long.class,
                Integer.valueOf(maxIdleTime) * 1000);
        ReflectionUtil.invoke(p, "setFilters", String.class,"stat");
        ReflectionUtil.invoke(p, "setRemoveAbandoned", boolean.class,Boolean.valueOf(removeAbandoned));
        ReflectionUtil.invoke(p, "setRemoveAbandonedTimeout", int.class,
                Integer.valueOf(removeAbandonedTimeout));
        ReflectionUtil.invoke(p, "setLogAbandoned", boolean.class, true);
        ReflectionUtil.invoke(p, "setConnectionProperties", String.class, "druid.stat.slowSqlMillis=3000");
        //druid.stat.mergeSql=false;
        return (DataSource)p;

    }

   public void close(DataSource dataSource) throws Exception {
       ReflectionUtil.invoke(dataSource, "close");

    }
}
