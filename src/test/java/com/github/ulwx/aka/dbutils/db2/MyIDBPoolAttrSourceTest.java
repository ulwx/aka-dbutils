package com.github.ulwx.aka.dbutils.db2;

import com.github.ulwx.aka.dbutils.database.IDBPoolAttrSource;
import com.github.ulwx.aka.dbutils.db2.domain.db.db_teacher.Teacher;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyIDBPoolAttrSourceTest implements IDBPoolAttrSource {
    private final static Logger log = LoggerFactory.getLogger(MyIDBPoolAttrSourceTest.class);
    @Override
    public void configProperties(Map<String, String> masterProperties,
                                 Map<String, Map<String, String>> slaveServerProperties) {

        try {
            MDbUtils.queryOneBy("db2/dbpool.xml#db_teacher", new Teacher());
        }catch (Exception e){
            log.error(""+e);
        }

        masterProperties.put("driverClassName","com.ibm.db2.jcc.DB2Driver");
        masterProperties.put("url","jdbc:db2://192.168.137.200:50000/teacher");
        masterProperties.put("username","Administrator");
        masterProperties.put("password","scj_123123");
        masterProperties.put("encrypt","0");
        masterProperties.put("checkoutTimeout","6000");
        masterProperties.put("idleConnectionTestPeriod",""+new Random().nextInt(10));
        masterProperties.put("maxIdleTime","60");
        masterProperties.put("minPoolSize","2");
        masterProperties.put("maxStatements","20");

        Map<String, String> slave1=new HashMap<>();
        slave1.put("driverClassName","com.ibm.db2.jcc.DB2Driver");
        slave1.put("url","jdbc:db2://192.168.137.200:50000/slave1");
        slave1.put("username","Administrator");
        slave1.put("password","scj_123123");
        slave1.put("encrypt","0");
        slave1.put("checkoutTimeout","6000");
        slave1.put("idleConnectionTestPeriod","30");
        slave1.put("maxIdleTime","60");
        slave1.put("minPoolSize","2");
        slave1.put("maxStatements","20");
        slaveServerProperties.put("external_slave_1",slave1);
        Map<String, String> slave2=new HashMap<>();
        slave2.put("driverClassName","com.ibm.db2.jcc.DB2Driver");
        slave2.put("url","jdbc:db2://192.168.137.200:50000/slave2");
        slave2.put("username","Administrator");
        slave2.put("password","scj_123123");
        slave2.put("encrypt","0");
        slave2.put("checkoutTimeout","6000");
        slave2.put("idleConnectionTestPeriod","30");
        slave2.put("maxIdleTime","60");
        slave2.put("minPoolSize","2");
        slave2.put("maxStatements","20");
        slaveServerProperties.put("external_slave_2",slave2);


    }
}
