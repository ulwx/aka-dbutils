package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;

class DataBaseToJavaBean {

    public static void main(String[] args) {
        ////System.out.println(Path.getClassPath());
        SqlUtils.exportTables("test", "test", "c:/ok4/testa", "com.github.ulwx.database.transactiontest.bean.testa", "utf-8", true);

    }
}