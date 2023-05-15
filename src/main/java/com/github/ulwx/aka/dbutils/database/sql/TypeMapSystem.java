package com.github.ulwx.aka.dbutils.database.sql;

import com.github.ulwx.aka.dbutils.database.DataBaseSet;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.tool.support.CTime;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.*;

public class TypeMapSystem {
    private static Set<Class> simpleType = new HashSet<Class>();
    private static Set<Class> sqlType = new HashSet<Class>();
    private static Map<Integer, Class> sql2javaType = new HashMap<Integer, Class>();
    private static Map<Class, Integer> javaType2sql = new HashMap<Class, Integer>();

    static {
        simpleType.add(boolean.class);
        simpleType.add(Boolean.class);
        simpleType.add(String.class);
        simpleType.add(char.class);
        simpleType.add(Character.class);
        simpleType.add(byte.class);
        simpleType.add(Byte.class);
        simpleType.add(Integer.class);
        simpleType.add(int.class);
        simpleType.add(Long.class);
        simpleType.add(long.class);
        simpleType.add(Short.class);
        simpleType.add(short.class);
        simpleType.add(Float.class);
        simpleType.add(float.class);
        simpleType.add(Double.class);
        simpleType.add(double.class);
        simpleType.add(Date.class);
        simpleType.add(LocalDate.class);
        simpleType.add(LocalDateTime.class);
        simpleType.add(LocalTime.class);//java.sql.Date
        simpleType.add(java.math.BigDecimal.class);
        simpleType.add(java.math.BigInteger.class);
        simpleType.add(java.sql.Date.class);
        simpleType.add(java.sql.Timestamp.class);
        simpleType.add(java.sql.Time.class);

        sqlType.add(java.sql.Date.class);
        sqlType.add(Time.class);
        sqlType.add(Timestamp.class);//java.sql.Date
        sqlType.add(Blob.class);
        sqlType.add(Clob.class);

        sqlType.add(Struct.class);
        sqlType.add(Array.class);
        sqlType.add(NClob.class);
        sqlType.add(Struct.class);

        simpleType.addAll(sqlType);

        // ////////////////
        sql2javaType.put(Types.BIGINT, Long.class);
        sql2javaType.put(Types.INTEGER, Integer.class);
        sql2javaType.put(Types.ARRAY, Array.class);
        sql2javaType.put(Types.BLOB, Blob.class);
        sql2javaType.put(Types.CLOB, Clob.class);
        sql2javaType.put(Types.BOOLEAN, Boolean.class);
        sql2javaType.put(Types.CHAR, String.class);
        sql2javaType.put(Types.DATE, LocalDate.class);
        sql2javaType.put(Types.DECIMAL, Double.class);
        sql2javaType.put(Types.SMALLINT, Integer.class);
        sql2javaType.put(Types.TINYINT, Integer.class);
        sql2javaType.put(Types.TIME, LocalTime.class);
        sql2javaType.put(Types.TIMESTAMP, LocalDateTime.class);
        sql2javaType.put(Types.VARCHAR, String.class);
        sql2javaType.put(Types.NVARCHAR, String.class);
        sql2javaType.put(Types.LONGNVARCHAR, String.class);
        sql2javaType.put(Types.NCHAR, String.class);
        sql2javaType.put(Types.NUMERIC, Double.class);
        sql2javaType.put(Types.STRUCT, Struct.class);
        sql2javaType.put(Types.REAL, Float.class);
        sql2javaType.put(Types.LONGVARCHAR, String.class);
        sql2javaType.put(Types.FLOAT, Float.class);
        sql2javaType.put(Types.DOUBLE, Double.class);
        sql2javaType.put(Types.BINARY, byte[].class);
        sql2javaType.put(Types.BIT, Integer.class);
        sql2javaType.put(Types.REF, Object.class);
        sql2javaType.put(Types.VARBINARY, byte[].class);
        sql2javaType.put(Types.LONGVARBINARY, byte[].class);
        javaType2sql.put(Long.class, Types.BIGINT);
        javaType2sql.put(long.class, Types.BIGINT);
        javaType2sql.put(Integer.class, Types.INTEGER);
        javaType2sql.put(int.class, Types.INTEGER);
        javaType2sql.put(Array.class, Types.ARRAY);
        javaType2sql.put(Blob.class, Types.BLOB);
        javaType2sql.put(Clob.class, Types.CLOB);
        javaType2sql.put(Boolean.class, Types.BOOLEAN);
        javaType2sql.put(boolean.class, Types.BOOLEAN);
        javaType2sql.put(Date.class, Types.TIMESTAMP);

        javaType2sql.put(LocalDateTime.class, Types.TIMESTAMP);
        javaType2sql.put(LocalTime.class, Types.TIME);
        javaType2sql.put(LocalDate.class, Types.DATE);

        javaType2sql.put(Double.class, Types.DOUBLE);//java.math.BigDecimal.class
        javaType2sql.put(double.class, Types.DOUBLE);
        javaType2sql.put(java.math.BigDecimal.class, Types.DOUBLE);
        javaType2sql.put(java.math.BigInteger.class, Types.BIGINT);
        javaType2sql.put(String.class, Types.VARCHAR);
        javaType2sql.put(Struct.class, Types.STRUCT);
        javaType2sql.put(Float.class, Types.FLOAT);
        javaType2sql.put(float.class, Types.FLOAT);
        javaType2sql.put(byte[].class, Types.BINARY);
        javaType2sql.put((Class) DataBaseSet.class, Types.REF);
        javaType2sql.put(ResultSet.class, Types.REF);


    }
    public static Date sqlTimestampTojavaDate(Timestamp value) {
        return CTime.sqlTimestampTojavaDate(value);

    }

    public static Date sqlDateTojavaDate(java.sql.Date value) {
        return CTime.sqlDateTojavaDate(value);
    }

    public static LocalDate sqlDateToLocalDate(java.sql.Date value) {
        return CTime.sqlDateToLocalDate(value);
    }

    public static LocalDateTime sqlTimestampToLocalDateTime(java.sql.Timestamp value) {
        return CTime.sqlTimestampToLocalDateTime(value);
    }

    public static LocalTime sqlTimeToLocalTime(java.sql.Time value) {
        return CTime.sqlTimeToLocalTime(value);
    }

    public static boolean checkedSimpleType(Class t) {
        if (simpleType.contains(t)) {
            return true;
        }
        return false;
    }
    public static boolean checkSqlType(Class t) {
        if (sqlType.contains(t)) {
            return true;
        }
        return false;
    }
    public static Class  sql2javaType( Column column){

        if(column.getDbms().isOracleFamily()){
            if(column.getData_type()== Types.DECIMAL &&
                    column.getType_name().equals("NUMBER")){
                if(column.getDecimal_digits()==0){ //说明是整形
                    if(column.getColumn_size()<=11) return Integer.class;
                    else {
                        return Long.class;
                    }
                }
            }else if(column.getData_type()==Types.OTHER &&
                     column.getType_name().equals("NVARCHAR2")){
                return String.class;
            }
        }
        return sql2javaType.get(column.getData_type());

    }
    static final int ORACLE_CURSOR = -10;
    public static Integer javaType2sql(Class javaType,DBMS dbms){
        if(dbms.isOracleFamily()){
            if(javaType==ResultSet.class){
                return ORACLE_CURSOR;
            }
        }
        return javaType2sql.get(javaType);
    }
}
