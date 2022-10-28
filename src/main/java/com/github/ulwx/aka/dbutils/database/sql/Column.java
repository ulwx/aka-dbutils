package com.github.ulwx.aka.dbutils.database.sql;

import com.github.ulwx.aka.dbutils.database.dialect.DBMS;

public class Column {

    private DBMS dbms;

    public DBMS getDbms() {
        return dbms;
    }

    public void setDbms(DBMS dbms) {
        this.dbms = dbms;
    }

    public String getTable_cat() {
        return table_cat;
    }

    public void setTable_cat(String table_cat) {
        this.table_cat = table_cat;
    }

    public String getTable_schem() {
        return table_schem;
    }

    public void setTable_schem(String table_schem) {
        this.table_schem = table_schem;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public int getData_type() {
        return data_type;
    }

    public void setData_type(int data_type) {
        this.data_type = data_type;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getColumn_size() {
        return column_size;
    }

    public void setColumn_size(int column_size) {
        this.column_size = column_size;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getIs_nullable() {
        return is_nullable;
    }

    public void setIs_nullable(String is_nullable) {
        this.is_nullable = is_nullable;
    }

    public String getIs_autoincrement() {
        return is_autoincrement;
    }

    public void setIs_autoincrement(String is_autoincrement) {
        this.is_autoincrement = is_autoincrement;
    }

    public int getDecimal_digits() {
        return decimal_digits;
    }

    public void setDecimal_digits(int decimal_digits) {
        this.decimal_digits = decimal_digits;
    }

    public String getColumn_def() {
        return column_def;
    }

    public void setColumn_def(String column_def) {
        this.column_def = column_def;
    }

    public String getIs_generatedcolumn() {
        return is_generatedcolumn;
    }

    public void setIs_generatedcolumn(String is_generatedcolumn) {
        this.is_generatedcolumn = is_generatedcolumn;
    }

    public short getSource_data_type() {
        return source_data_type;
    }

    public void setSource_data_type(short source_data_type) {
        this.source_data_type = source_data_type;
    }

    private String table_cat = "";
    private String table_schem = "";
    private String table_name = "";
    private String column_name = "";
    private int data_type;// SQL type from java.sql.Types
    private String type_name;
    private int column_size;
    private String remarks;
    private String is_nullable;// "YES","NO" or ""
    private String is_autoincrement;// "YES","NO" or ""
    private int decimal_digits;
    private String column_def;
    private String is_generatedcolumn;// "YES" "NO" or ""
    private short source_data_type;


}

