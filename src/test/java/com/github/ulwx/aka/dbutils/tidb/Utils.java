package com.github.ulwx.aka.dbutils.tidb;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        //1.首先在linux下载tidb后，运行tiup playground --host  192.168.137.211 --tag akadb2 （本机使用的是192.168.137.211)
        //2.再手动新建db_student,db_teacher,db_teacher_slave1,db_teacher_slave2数据库
        //3.执行下面语句
        inportDbTeacher();
        importDbStudent();

        SqlUtils.exportTables("tidb/dbpool.xml#db_student", "db_student",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.tidb.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("tidb/dbpool.xml#db_teacher", "db_teacher",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.tidb.domain.db.db_teacher",
                "utf-8",true);

        System.out.println("ok!");
    }

    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("tidb/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.tidb",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("tidb/dbpool.xml#db_teacher",
                "com.github.ulwx.aka.dbutils.tidb",
                "db_teacher.sql", false,null);
        MDbUtils.exeScript("tidb/dbpool.xml#db_teacher_slave1",
                "com.github.ulwx.aka.dbutils.tidb",
                "db_teacher_slave1.sql", false,null);
        MDbUtils.exeScript("tidb/dbpool.xml#db_teacher_slave2",
                "com.github.ulwx.aka.dbutils.tidb",
                "db_teacher_slave2.sql", false,null);
        DbContext.permitDebugLog(true);
    }

}
