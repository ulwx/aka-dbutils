package com.github.ulwx.aka.dbutils.database.dbpool;

import javax.sql.DataSource;

public interface DBPool {
    public DataSource getNewDataSource(DBPoolAttr dbPoolAttr) throws Exception;

    void close(DataSource dataSource) throws Exception ;

    public String getType();
}
