package com.github.ulwx.aka.dbutils.mysql_shardingjdbc;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {

        importDbStudent();

        System.out.println("ok!");
    }

    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("mysql_shardingjdbc/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.mysql_shardingjdbc",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }



}
