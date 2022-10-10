package com.github.ulwx.aka.dbutils.sqlserver;

import com.github.ulwx.aka.dbutils.database.IDBPoolAttrSource;
import com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_teacher.Teacher;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyIDBPoolAttrSourceTest implements IDBPoolAttrSource {
    @Override
    public void configProperties(Map<String, String> masterProperties,
                                 Map<String, Map<String, String>> slaveServerProperties) {


        MDbUtils.queryOneBy("sqlserver/dbpool.xml#db_teacher",new Teacher());

        masterProperties.put("driverClassName","com.microsoft.sqlserver.jdbc.SQLServerDriver");
        masterProperties.put("url","jdbc:sqlserver://192.168.137.200:1433;databaseName=db_teacher");
        masterProperties.put("username","sa");
        masterProperties.put("password","scj_123123");
        masterProperties.put("encrypt","0");
        masterProperties.put("checkoutTimeout","6000");
        masterProperties.put("idleConnectionTestPeriod",""+new Random().nextInt(10));
        masterProperties.put("maxIdleTime","60");
        masterProperties.put("minPoolSize","2");
        masterProperties.put("maxStatements","20");

        Map<String, String> slave1=new HashMap<>();
        slave1.put("driverClassName","com.microsoft.sqlserver.jdbc.SQLServerDriver");
        slave1.put("url","jdbc:sqlserver://192.168.137.200:1433;databaseName=db_teacher_slave1");
        slave1.put("username","sa");
        slave1.put("password","scj_123123");
        slave1.put("encrypt","0");
        slave1.put("checkoutTimeout","6000");
        slave1.put("idleConnectionTestPeriod","30");
        slave1.put("maxIdleTime","60");
        slave1.put("minPoolSize","2");
        slave1.put("maxStatements","20");
        slaveServerProperties.put("external_slave_1",slave1);
        Map<String, String> slave2=new HashMap<>();
        slave2.put("driverClassName","com.microsoft.sqlserver.jdbc.SQLServerDriver");
        slave2.put("url","jdbc:sqlserver://192.168.137.200:1433;databaseName=db_teacher_slave2");
        slave2.put("username","sa");
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
