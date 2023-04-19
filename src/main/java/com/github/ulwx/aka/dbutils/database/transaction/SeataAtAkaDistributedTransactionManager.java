package com.github.ulwx.aka.dbutils.database.transaction;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.model.GlobalStatus;
import io.seata.core.rpc.netty.*;
import io.seata.rm.RMClient;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.tm.TMClient;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 分布式事务管理器，通过集成seata的AT模式事务来实现
 *
 */
public class SeataAtAkaDistributedTransactionManager implements  AKaDistributedTransactionManager{

    private static final Logger log = LoggerFactory.getLogger(SeataAtAkaDistributedTransactionManager.class);

    private  Map<DataSource, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    private  String applicationId;

    private  String transactionServiceGroup;

    private  boolean enableSeataAT=true;

    private  int globalTXTimeout=60;

    private String registryConfPath;

    private String dbPoolXmlFileName;

    public String getDbPoolXmlFileName() {
        return dbPoolXmlFileName;
    }

    public void setDbPoolXmlFileName(String dbPoolXmlFileName) {
        this.dbPoolXmlFileName = dbPoolXmlFileName;
    }

    public int getGlobalTXTimeout() {
        return globalTXTimeout;
    }

    public Map<DataSource, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public String getTransactionServiceGroup() {
        return transactionServiceGroup;
    }

    public boolean isEnableSeataAT() {
        return enableSeataAT;
    }

    public String getRegistryConfPath() {
        return registryConfPath;
    }

    public SeataAtAkaDistributedTransactionManager(
            String poolXmlFileName,
            boolean enableSeataAT,
            String applicationId,
            String transactionServiceGroup,
            int globalTXTimeout,
            String registryConfPath) {
        this.enableSeataAT = enableSeataAT;
        this.applicationId = applicationId;
        this.transactionServiceGroup = transactionServiceGroup;
        this.globalTXTimeout = globalTXTimeout;
        this.registryConfPath = registryConfPath;
        this.dbPoolXmlFileName=poolXmlFileName;

    }

    @Override
    public void init(final Collection<DataSource> resourceDataSources) {
        if (enableSeataAT) {
            initSeataRPCClient();
            try {
                Thread.sleep(2*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }

        }
    }


    private void initSeataRPCClient() {
        Assert.notNull(applicationId, "please config application id within seata.conf file.");
        TMClient.init(applicationId, transactionServiceGroup);
        RMClient.init(applicationId, transactionServiceGroup);


    }



    @Override
    public boolean isInDistributedTransaction() {
        Assert.isTrue(enableSeataAT, "seata-at transaction has been disabled.");
        return null != RootContext.getXID();
    }


    @Override
    public Connection getConnection(DataSource dataSource) throws Exception {

        if(dataSource instanceof DataSourceProxy){
            return dataSource.getConnection();
        }

        //"org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource"
        if(dataSource.getClass().getName().
                startsWith("org.apache.shardingsphere")){//判断ShardingSphereDataSource
            return dataSource.getConnection();
        }
        Assert.isTrue(enableSeataAT, "seata-at transaction has been disabled.");
        DataSource proxyDatasource=dataSourceMap.get(dataSource);
        if(proxyDatasource!=null) {
            return proxyDatasource.getConnection();
        }else{
            synchronized (this){
                proxyDatasource=dataSourceMap.get( dataSource);
                if(proxyDatasource==null){
                    dataSourceMap.put(dataSource,new DataSourceProxy(dataSource));
                    proxyDatasource=dataSourceMap.get( dataSource);

                }
                return proxyDatasource.getConnection();

            }

        }
    }

    @Override
    public void begin(AkaPropagationType propagationType) {
        begin(globalTXTimeout,propagationType);
    }

    @Override
    public void begin(int timeout,AkaPropagationType propagationType) {
        GlobalTransaction globalTransaction=null;
        try {
            AkaTransactionManagerHolder.set(this);
            if (timeout < 0) {
                throw new TransactionException("timeout should more than 0s");
            }
            Assert.isTrue(enableSeataAT, "seata-at transaction has been disabled.");
            globalTransaction = GlobalTransactionContext.getCurrentOrCreate();

            globalTransaction.begin(timeout * 1000);

        }catch(Exception e){
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }finally {
            AkaSeataTransactionHolder.set(globalTransaction);
        }
    }

    @Override
    public void commit() {
        Assert.isTrue(enableSeataAT, "seata-at transaction has been disabled.");
        try {
            AkaSeataTransactionHolder.get().commit();

        }catch(Exception e){
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {

        }
    }

    @Override
    public void rollback(Throwable forExcpetion) {
        Assert.isTrue(enableSeataAT, "seata-at transaction has been disabled.");
        try {
            AkaSeataTransactionHolder.get().rollback();
        }catch(Throwable e){
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
        }
    }

    @Override
    public void close() {
        RmNettyRemotingClient.getInstance().destroy();
        TmNettyRemotingClient.getInstance().destroy();
    }


    @Override
    public void end() {
        AkaTransactionManagerHolder.clear();
        AkaSeataTransactionHolder.clear();
        RootContext.unbind();
    }

    @Override
    public AkaTransactionType getTransactionType() {
        return AkaTransactionType.SEATA_AT;
    }

}