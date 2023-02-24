package com.github.ulwx.aka.dbutils.database.transaction;

public interface AKaTransactionManager {

    public  void begin(AkaPropagationType propagationType) ;
    void commit() throws Exception;

    void rollback(Throwable forExcpetion);

    void end();

    AkaTransactionType getTransactionType();

    public String getDbPoolXmlFileName();

}
