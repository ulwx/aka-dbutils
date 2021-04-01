package com.github.ulwx.aka.dbutils.mysql;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        SqlUtils.exportTables("db_student", "db_student",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.mysql.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("db_teacher", "db_teacher",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.mysql.domain.db.db_teacher",
                "utf-8",true);

    }
    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("db_student",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_student.sql", false);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("db_teacher",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_teacher.sql", false);
        MDbUtils.exeScript("db_teacher",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_teacher_slave1.sql", false);
        MDbUtils.exeScript("db_teacher",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_teacher_slave2.sql", false);
        DbContext.permitDebugLog(true);
    }

}
