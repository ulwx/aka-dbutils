package com.github.ulwx.aka.dbutils.sqlserver;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        SqlUtils.exportTables("sqlserver/dbpool.xml#db_student", "dbo",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("sqlserver/dbpool.xml#db_teacher", "dbo",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_teacher",
                "utf-8",true);
        importDbStudent();
        inportDbTeacher();
        System.out.println("ok!");

    }
    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("sqlserver/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.sqlserver",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("sqlserver/dbpool.xml#db_teacher",
                "com.github.ulwx.aka.dbutils.sqlserver",
                "db_teacher.sql", false,null,null);
        MDbUtils.exeScript("sqlserver/dbpool.xml#db_teacher_slave1",
                "com.github.ulwx.aka.dbutils.sqlserver",
                "db_teacher_slave1.sql", false,null);
        MDbUtils.exeScript("sqlserver/dbpool.xml#db_teacher_slave2",
                "com.github.ulwx.aka.dbutils.sqlserver",
                "db_teacher_slave2.sql", false,null);
        DbContext.permitDebugLog(true);
    }

}
