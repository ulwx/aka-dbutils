package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.tool.support.reflect.ReflectionUtil;

import javax.sql.DataSource;

public class HikariDBPoolImpl implements DBPool{
    public static HikariDBPoolImpl instance=new HikariDBPoolImpl();



    @Override
    public DataSource getNewDataSource(String url, String user, String password, String checkoutTimeout,
                                       String maxPoolSize, String minPoolSize,
                                       String maxStatements, String maxIdleTime,
                                       String idleConnectionTestPeriod, String driverClassName,
                                       String removeAbandoned,
                                       String removeAbandonedTimeout) throws Exception {

        //com.zaxxer.hikari.HikariDataSource dataSource=new com.zaxxer.hikari.HikariDataSource();
        Object p = Class.forName("com.zaxxer.hikari.HikariDataSource").getDeclaredConstructor().newInstance();
        ReflectionUtil.invoke(p, "setJdbcUrl", String.class, url);
        ReflectionUtil.invoke(p, "setUsername", String.class, user);
        ReflectionUtil.invoke(p, "setPassword", String.class, password);
        ReflectionUtil.invoke(p, "setDriverClassName", String.class, driverClassName);

        ReflectionUtil.invoke(p, "setMaximumPoolSize", int.class, Integer.valueOf(maxPoolSize));
        ReflectionUtil.invoke(p, "setMinimumIdle", int.class, Integer.valueOf(minPoolSize));
        ReflectionUtil.invoke(p, "setIdleTimeout", long.class, Long.valueOf(maxIdleTime)*1000);
        ReflectionUtil.invoke(p, "setKeepaliveTime", long.class,
                        Integer.valueOf(idleConnectionTestPeriod)*1000);



        return (DataSource)p;

    }

   public void close(DataSource dataSource) throws Exception {
       ReflectionUtil.invoke(dataSource, "close");

    }
}
