package com.github.ulwx.aka.dbutils.derby;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        inportDbTeacher();
        importDbStudent();
        SqlUtils.exportTables("derby/dbpool.xml#db_student", "APP",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.derby.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("derby/dbpool.xml#db_teacher", "APP",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.derby.domain.db.db_teacher",
                "utf-8",true);


        System.out.println("ok!");
    }


    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("derby/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.derby",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("derby/dbpool.xml#db_teacher",
                "com.github.ulwx.aka.dbutils.derby",
                "db_teacher.sql", false,null);
        MDbUtils.exeScript("derby/dbpool.xml#db_teacher_slave1",
                "com.github.ulwx.aka.dbutils.derby",
                "db_teacher_slave1.sql", false,null);
        MDbUtils.exeScript("derby/dbpool.xml#db_teacher_slave2",
                "com.github.ulwx.aka.dbutils.derby",
                "db_teacher_slave2.sql", false,null);
        DbContext.permitDebugLog(true);
    }

}
