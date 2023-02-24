package com.github.ulwx.aka.dbutils.database.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;

public interface AKaDistributedTransactionManager extends  AKaTransactionManager{

    public  void begin(int timeout,AkaPropagationType propagationType);
    void init(final Collection<DataSource> resourceDataSources);
    Connection getConnection(DataSource dataSource) throws Exception ;
    public boolean isInDistributedTransaction();
    void close();
}
