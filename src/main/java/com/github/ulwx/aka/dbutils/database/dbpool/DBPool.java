package com.github.ulwx.aka.dbutils.database.dbpool;

import javax.sql.DataSource;

public interface DBPool {
    public DataSource getNewDataSource(String url, String user, String password, String checkoutTimeout,
                                       String maxPoolSize, String minPoolSize, String maxStatements,
                                       String maxIdleTime, String idleConnectionTestPeriod,
                                       String driverClassName,
                                       String removeAbandoned,
                                       String removeAbandonedTimeout) throws Exception;


    void close(DataSource dataSource) throws Exception ;
}
