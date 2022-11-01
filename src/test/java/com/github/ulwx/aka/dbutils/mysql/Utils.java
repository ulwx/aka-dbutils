package com.github.ulwx.aka.dbutils.mysql;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        importDbStudent();
        inportDbTeacher();
        SqlUtils.exportTables("mysql/dbpool.xml#db_student", "db_student",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.mysql.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("mysql/dbpool.xml#db_teacher", "db_teacher",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.mysql.domain.db.db_teacher",
                "utf-8",true);

        System.out.println("ok!");
    }

    public static void importDbStudentFromFile(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScriptInDir("mysql/dbpool.xml#db_student",
                "D:\\suncj\\jydsource\\common\\common\\aka-dbutils\\src\\test\\java\\com\\github\\ulwx\\aka\\dbutils\\mysql",
                "db_student.sql", false,null,"utf-8");
        DbContext.permitDebugLog(true);
    }
    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("mysql/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("mysql/dbpool.xml#db_teacher",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_teacher.sql", false,null);
        MDbUtils.exeScript("mysql/dbpool.xml#db_teacher_slave1",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_teacher_slave1.sql", false,null);
        MDbUtils.exeScript("mysql/dbpool.xml#db_teacher_slave2",
                "com.github.ulwx.aka.dbutils.mysql",
                "db_teacher_slave2.sql", false,null);
        DbContext.permitDebugLog(true);
    }

}
