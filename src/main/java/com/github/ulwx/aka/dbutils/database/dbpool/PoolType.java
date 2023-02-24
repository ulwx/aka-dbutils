package com.github.ulwx.aka.dbutils.database.dbpool;

import java.util.HashMap;
import java.util.Map;

public class PoolType {
    public final static String TOMCAT_DB_POOL = "tomcatdbpool";
    public final static String ALiBABA_DRUID="druid";
    public final static String HikariCP="HikariCP";
    public final static String ShardingJDBC="ShardingJDBC";
    public final static Map<String,DBPool> map = new HashMap<>();
    static{
        map.put(TOMCAT_DB_POOL,TomcatDBPoolImpl.instance);
        map.put(ALiBABA_DRUID,DruidDBPoolImpl.instance);
        map.put(HikariCP,HikariDBPoolImpl.instance);
        map.put(ShardingJDBC,ShardingJDBCDBPoolImpl.instance);
    }
    public static DBPool getDBPool(String PoolType){
        return map.get(PoolType);
    }
}
