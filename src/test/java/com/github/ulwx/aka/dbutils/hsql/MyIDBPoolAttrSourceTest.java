package com.github.ulwx.aka.dbutils.hsql;

import com.github.ulwx.aka.dbutils.database.IDBPoolAttrSource;
import com.github.ulwx.aka.dbutils.hsql.domain.db.db_teacher.Teacher;
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
            MDbUtils.queryOneBy("hsql/dbpool.xml#db_teacher", new Teacher());
        }catch (Exception e){
            log.error("",e);
        }

        masterProperties.put("driverClassName","org.hsqldb.jdbc.JDBCDriver");
        masterProperties.put("url","jdbc:hsqldb:file:D:/suncj/hsqldb/db/db_teacher");
        masterProperties.put("username","root");
        masterProperties.put("password","123456");
        masterProperties.put("encrypt","0");
        masterProperties.put("checkoutTimeout","6000");
        masterProperties.put("idleConnectionTestPeriod",""+new Random().nextInt(10));
        masterProperties.put("maxIdleTime","60");
        masterProperties.put("minPoolSize","2");
        masterProperties.put("maxStatements","20");

        Map<String, String> slave1=new HashMap<>();
        slave1.put("driverClassName","org.hsqldb.jdbc.JDBCDriver");
        slave1.put("url","jdbc:hsqldb:file:D:/suncj/hsqldb/db/db_teacher_slave1");
        slave1.put("username","root");
        slave1.put("password","123456");
        slave1.put("encrypt","0");
        slave1.put("checkoutTimeout","6000");
        slave1.put("idleConnectionTestPeriod","30");
        slave1.put("maxIdleTime","60");
        slave1.put("minPoolSize","2");
        slave1.put("maxStatements","20");
        slaveServerProperties.put("external_slave_1",slave1);
        Map<String, String> slave2=new HashMap<>();
        slave2.put("driverClassName","org.hsqldb.jdbc.JDBCDriver");
        slave2.put("url","jdbc:hsqldb:file:D:/suncj/hsqldb/db/db_teacher_slave2");
        slave2.put("username","root");
        slave2.put("password","123456");
        slave2.put("encrypt","0");
        slave2.put("checkoutTimeout","6000");
        slave2.put("idleConnectionTestPeriod","30");
        slave2.put("maxIdleTime","60");
        slave2.put("minPoolSize","2");
        slave2.put("maxStatements","20");
        slaveServerProperties.put("external_slave_2",slave2);


    }
}