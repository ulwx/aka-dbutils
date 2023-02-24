package com.github.ulwx.aka.dbutils.sharding_jdbc;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.AccountServiceImpl;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.OrderServiceImpl;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.OrderStatisticsInfoServiceImpl;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) throws Exception {

        String dbPoolName="sharding_jdbc/dbpool.xml#demo-ds-0";
        new OrderServiceImpl(dbPoolName).initEnvironment();
        new AccountServiceImpl(dbPoolName).initEnvironment();
        new OrderServiceImpl(dbPoolName).initEnvironment();
        new OrderStatisticsInfoServiceImpl(dbPoolName).initEnvironment();

        SqlUtils.exportTables(dbPoolName, "demo_ds_0",
                "c:/dbutils_demo/demo-ds-0",
                "com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds",
                "utf-8",true);


        System.out.println("ok!");

    }

    public static void initSql(){

        DbContext.permitDebugLog(true);
        String dbPoolName="sharding_jdbc/dbpool.xml#demo";
        MDbUtils.exeScript(dbPoolName,
                "com.github.ulwx.aka.dbutils.sharding_jdbc",
                "manual_schema.sql", false,true,null);
        DbContext.permitDebugLog(true);
    }




}
