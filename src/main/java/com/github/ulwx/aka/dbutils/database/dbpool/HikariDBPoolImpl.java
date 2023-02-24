package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.tool.support.reflect.ReflectionUtil;

import javax.sql.DataSource;

public class HikariDBPoolImpl implements DBPool{
    public static HikariDBPoolImpl instance=new HikariDBPoolImpl();



    @Override
    public DataSource getNewDataSource(DBPoolAttr dbPoolAttr) throws Exception {

        //com.zaxxer.hikari.HikariDataSource dataSource=new com.zaxxer.hikari.HikariDataSource();
        Object p = Class.forName("com.zaxxer.hikari.HikariDataSource").getDeclaredConstructor().newInstance();
        ReflectionUtil.invoke(p, "setJdbcUrl", String.class, dbPoolAttr.getUrl());
        ReflectionUtil.invoke(p, "setUsername", String.class, dbPoolAttr.getUser());
        ReflectionUtil.invoke(p, "setPassword", String.class, dbPoolAttr.getPassword());
        ReflectionUtil.invoke(p, "setDriverClassName", String.class,dbPoolAttr.getDriverClassName() );

        ReflectionUtil.invoke(p, "setMaximumPoolSize", int.class, Integer.valueOf(dbPoolAttr.getMaxPoolSize()));
        ReflectionUtil.invoke(p, "setMinimumIdle", int.class, Integer.valueOf(dbPoolAttr.getMinPoolSize()));
        ReflectionUtil.invoke(p, "setIdleTimeout", long.class, Long.valueOf(dbPoolAttr.getMaxIdleTime())*1000);
        ReflectionUtil.invoke(p, "setKeepaliveTime", long.class,
                        Integer.valueOf(dbPoolAttr.getIdleConnectionTestPeriod())*1000);

        return (DataSource)p;

    }

   public void close(DataSource dataSource) throws Exception {
       ReflectionUtil.invoke(dataSource, "close");

    }

    @Override
    public String getType() {
        return PoolType.HikariCP;
    }
}
