package com.github.ulwx.aka.dbutils;

import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;

public class Export {
    public static void main(String[] args) {
        SqlUtils.exportTables("mysql/dbpool.xml#db_student",
                "common-frame", "c:/ok4/testa",
                "com.github.ulwx.database.model",
                "utf-8", true);
    }
}
