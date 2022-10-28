package com.github.ulwx.aka.dbutils.oracle;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        SqlUtils.exportTables("oracle/dbpool.xml#db_student", "DB_STUDENT",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.oracle.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("oracle/dbpool.xml#db_teacher", "DB_TEACHER",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.oracle.domain.db.db_teacher",
                "utf-8",true);

         importDbStudent();
         inportDbTeacher();
        System.out.println("ok!");

    }

    public static void testImportDbStudentFromFile(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScriptInDir("oracle/dbpool.xml#db_student",
                "D:\\suncj\\jydsource\\common\\common\\aka-dbutils\\src\\test\\java\\com\\github\\ulwx\\aka\\dbutils\\oracle",
                "db_student.sql", false,null,"utf-8");
        DbContext.permitDebugLog(true);
    }
    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("oracle/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.oracle",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("oracle/dbpool.xml#db_teacher",
                "com.github.ulwx.aka.dbutils.oracle",
                "db_teacher.sql", false,null);
        MDbUtils.exeScript("oracle/dbpool.xml#db_teacher_slave1",
                "com.github.ulwx.aka.dbutils.oracle",
                "db_teacher_slave1.sql", false,null);
        MDbUtils.exeScript("oracle/dbpool.xml#db_teacher_slave2",
                "com.github.ulwx.aka.dbutils.oracle",
                "db_teacher_slave2.sql", false,null);
        DbContext.permitDebugLog(true);
    }

}
