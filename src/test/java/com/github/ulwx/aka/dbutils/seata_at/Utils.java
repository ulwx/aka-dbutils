package com.github.ulwx.aka.dbutils.seata_at;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class Utils {
    public static void main(String[] args) {
        inportDbTeacher();
        importDbStudent();

        SqlUtils.exportTables("seata_at/dbpool.xml#db_student", "db_student",
                "c:/dbutils_demo/db_student",
                "com.github.ulwx.aka.dbutils.seata_at.domain.db.db_student",
                "utf-8",true);
        SqlUtils.exportTables("seata_at/dbpool.xml#db_teacher", "db_teacher",
                "c:/dbutils_demo/db_teacher",
                "com.github.ulwx.aka.dbutils.seata_at.domain.db.db_teacher",
                "utf-8",true);

        System.out.println("ok!");
    }

    public static void importDbStudent(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("seata_at/dbpool.xml#db_student",
                "com.github.ulwx.aka.dbutils.seata_at",
                "db_student.sql", false,null);
        DbContext.permitDebugLog(true);
    }

    public static void inportDbTeacher(){
        DbContext.permitDebugLog(false);
        MDbUtils.exeScript("seata_at/dbpool.xml#db_teacher",
                "com.github.ulwx.aka.dbutils.seata_at",
                "db_teacher.sql", false,null);
        DbContext.permitDebugLog(true);
    }


}
