package com.github.ulwx.aka.dbutils.database.transaction;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.dbpool.DBPoolFactory;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;

import java.util.Map;

public class TransactionManagerFactory {
    public final static String enable = "enable";
    public final static String type = "type";
    public static class SEATA_AT{
        public final static String seata_application_id = "seata.application-id";
        public final static String seata_tx_service_group = "seata.tx-service-group";
        public final static String seata_client_tm_defaultGlobalTransactionTimeout = "seata.client.tm.defaultGlobalTransactionTimeout";
        public final  static String seata_registry_conf_path ="seata.registry-conf.path";
    }

    public final static TransactionManagerFactory instance = new TransactionManagerFactory();

    public AKaTransactionManager build(String poolXmlFileName, String type,Map<String, String> transactionProperties) {
        if(type.equals(AkaTransactionType.LOCAL.name())){
            AKaTransactionManager manager=new MDbTransactionManager(poolXmlFileName);
            return manager;
        }else if (type.equals(AkaTransactionType.SEATA_AT.name())) {
            boolean enableSeataAT = true;
            String applicationId = "";
            int globalTXTimeout = 60;
            String registryConfPath="";
            String transactionServiceGroup = "";
            try {
                if (StringUtils.hasText(transactionProperties.get(enable)) ) {
                    enableSeataAT = Boolean.valueOf(transactionProperties.get(enable)).booleanValue();
                }
                if (StringUtils.hasText(transactionProperties.get(SEATA_AT.seata_application_id))) {
                    applicationId = transactionProperties.get(SEATA_AT.seata_application_id).trim();
                }
                if (StringUtils.hasText(transactionProperties.get(SEATA_AT.seata_client_tm_defaultGlobalTransactionTimeout))) {
                    globalTXTimeout =
                            Integer.valueOf(transactionProperties.get(SEATA_AT.seata_client_tm_defaultGlobalTransactionTimeout));
                }
                if (StringUtils.hasText(transactionProperties.get(SEATA_AT.seata_tx_service_group) )) {
                    transactionServiceGroup = transactionProperties.get(SEATA_AT.seata_tx_service_group);
                }else{
                    throw new RuntimeException("必须指定seata.tx-service-group属性！");
                }
                if (StringUtils.hasText(transactionProperties.get(SEATA_AT.seata_registry_conf_path) )) {
                    registryConfPath =
                            transactionProperties.get(SEATA_AT.seata_registry_conf_path);
                }
            } catch (Exception e) {
                throw new DbException(e);
            }

            SeataAtAkaDistributedTransactionManager manager = new SeataAtAkaDistributedTransactionManager(
                    poolXmlFileName,
                    enableSeataAT,
                    applicationId,
                    transactionServiceGroup,
                    globalTXTimeout,
                    registryConfPath);
            if(StringUtils.hasText(registryConfPath)){
                System.setProperty("seata.config.name",registryConfPath);
            }
            manager.init(null);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    manager.close();
                }
            }));
            return manager;

        }

        return null;
    }

    public static AKaTransactionManager getTransactionManager(
            String dbPoolXmlFileName,
            AkaTransactionType transactionType){
        AKaTransactionManager manager=DBPoolFactory.getInstance(dbPoolXmlFileName).getTransactionManager(transactionType.name());
        return manager;

    }
    public static void main(String[] args) {
    }
}
