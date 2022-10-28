package com.github.ulwx.aka.dbutils.postgres;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        SqlUtils.exportTables("postgres/dbpool.xml#db_student", "public",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.postgres.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("postgres/dbpool.xml#db_teacher", "public",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.postgres.domain.db.db_teacher",
                "utf-8",true);

        importDbStudent();
        inportDbTeacher();
        System.out.println("ok!");

    }

    public static void testImportDbStudentFromFile(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScriptInDir("postgres/dbpool.xml#db_student",
                "D:\\suncj\\jydsource\\common\\common\\aka-dbutils\\src\\test\\java\\com\\github\\ulwx\\aka\\dbutils\\postgres",
                "db_student.sql", false,null,"utf-8");
        DbContext.permitDebugLog(true);
    }
    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("postgres/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.postgres",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("postgres/dbpool.xml#db_teacher",
                "com.github.ulwx.aka.dbutils.postgres",
                "db_teacher.sql", false,null);
        MDbUtils.exeScript("postgres/dbpool.xml#db_teacher_slave1",
                "com.github.ulwx.aka.dbutils.postgres",
                "db_teacher_slave1.sql", false,null);
        MDbUtils.exeScript("postgres/dbpool.xml#db_teacher_slave2",
                "com.github.ulwx.aka.dbutils.postgres",
                "db_teacher_slave2.sql", false,null);
        DbContext.permitDebugLog(true);
    }

}
