package com.github.ulwx.aka.dbutils.database.sql;

import com.github.ulwx.aka.dbutils.database.*;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.CachedRowSetImpl;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.database.dialect.DBType;
import com.github.ulwx.aka.dbutils.database.utils.DbConst;
import com.github.ulwx.aka.dbutils.database.utils.Table2JavaNameUtils;
import com.github.ulwx.aka.dbutils.tool.support.*;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import com.github.ulwx.aka.dbutils.tool.support.type.TString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.time.*;
import java.util.Date;
import java.util.*;


class Column {
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

    public int getNum_prec_radix() {
        return num_prec_radix;
    }

    public void setNum_prec_radix(int num_prec_radix) {
        this.num_prec_radix = num_prec_radix;
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

    private String table_cat = "";
    private String table_schem = "";
    private String table_name = "";
    private String column_name = "";
    private int data_type;// SQL type from java.sql.Types
    private String type_name;
    private int column_size;
    private int num_prec_radix;
    private String remarks;
    private String is_nullable;// "YES","NO" or ""
    private String is_autoincrement;// "YES","NO" or ""
}

public class SqlUtils {
    private static Logger log = LoggerFactory.getLogger(SqlUtils.class);
    public static Set<Class> simpleType = new HashSet<Class>();

    public static Set<Class> sqlType = new HashSet<Class>();
    public static Map<Integer, Class> sql2javaType = new HashMap<Integer, Class>();

    public static Map<DBType, String> dbEscapeLefCharMap = new HashMap<DBType, String>();

    static {
        dbEscapeLefCharMap.put(DBType.MYSQL, "`");
        dbEscapeLefCharMap.put(DBType.MS_SQL_SERVER, "[");
        dbEscapeLefCharMap.put(DBType.ORACLE, "\"");
    }

    public static String dbEscapeLef(DBMS dbms) {
        String chars = dbEscapeLefCharMap.get(dbms.getDbType());
        if (chars != null) {
            return chars;
        } else {
            return "";
        }
    }

    public static Map<DBType, String> dbEscapeRightCharMap = new HashMap<DBType, String>();

    static {
        dbEscapeRightCharMap.put(DBType.MYSQL, "`");
        dbEscapeRightCharMap.put(DBType.MS_SQL_SERVER, "]");
        dbEscapeRightCharMap.put(DBType.ORACLE, "\"");
    }

    public static String dbEscapeRight(DBMS dbms) {
        String chars = dbEscapeRightCharMap.get(dbms.getDbType());
        if (chars != null) {
            return chars;
        } else {
            return "";
        }
    }

    public static Map<Class, Integer> javaType2sql = new HashMap<Class, Integer>();

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


        // //////
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
        Timestamp time = value;
        Date dateTime = new Date();
        dateTime.setTime(time.getTime());
        return dateTime;

    }

    public static Date sqlDateTojavaDate(java.sql.Date value) {
        Date dateTime = new Date();
        dateTime.setTime(value.getTime());
        return dateTime;

    }

    public static LocalDate sqlDateToLocalDate(java.sql.Date value) {
        Instant instant = value.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    public static LocalDateTime sqlTimestampToLocalDateTime(java.sql.Timestamp value) {
        Instant instant = value.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    public static LocalTime sqlTimeToLocalTime(java.sql.Time value) {
        Instant instant = value.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalTime localTime = instant.atZone(zoneId).toLocalTime();
        return localTime;
    }

    public static boolean checkedSimpleType(Class t) {
        if (simpleType.contains(t)) {
            return true;
        }
        return false;
    }

    private static String exportJavaBean(String dbpool, String tableName, String tableRemark,
                                         Map map, String toPackage, boolean propertyLowcaseFirstChar, TString tclassName) {
        Set set = map.keySet();
        Iterator i = set.iterator();
        StringBuilder sb = new StringBuilder();
        StringBuilder sm = new StringBuilder();
        if (toPackage != null && !toPackage.trim().equals("")) {
            sb.append("package " + toPackage + ";\n");
        }

        sb.append("import java.util.*;\n");
        sb.append("import java.sql.*;\n");
        sb.append("import java.time.*;\n");
        sb.append("import " + MdbOptions.class.getName() + ";\n");
        sb.append("import " + ObjectUtils.class.getName() + ";\n");
        sb.append("\n/*********************************************\n");
        sb.append(tableRemark);
        sb.append("\n***********************************************/\n");

        String className = "";
        String tableNameRule = DbConst.getTableNameRule(dbpool);
        if (StringUtils.isEmpty(tableNameRule)) {
            tableNameRule = DbConst.getTableNameRule();
        }
        if (tableNameRule.equals(DbConst.TableNameRules.underline_to_camel)) {
            className = Table2JavaNameUtils.underLineToCamel(tableName);
        } else if (tableNameRule.equals(DbConst.TableNameRules.normal)) {
            className = tableName;
        } else if (tableNameRule.equals(DbConst.TableNameRules.first_letter_upcase)) {
            className = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
        } else {
            className = tableName;
        }
        tclassName.setValue(className);
        sb.append("public class " + className + " extends " + MdbOptions.class.getSimpleName() +
                " implements java.io.Serializable {\n\n");


        int n = 0;
        while (i.hasNext()) {
            String name = (String) i.next();
            Column co = (Column) map.get(name);
            Integer type = co.getData_type();
            if (name.trim().equals("class")) {
                name = "$class";
            }

            n++;
            Class typeClass = (Class) SqlUtils.sql2javaType.get(type);
            String typeName = typeClass.getSimpleName();
            if (SqlUtils.checkedSimpleType(typeClass)) {
                typeName = typeClass.getSimpleName();
            } else {
                typeName = typeClass.getName();
            }
            String tableColumRule = DbConst.getTableColumRule(dbpool);
            if (StringUtils.isEmpty(tableColumRule)) {
                tableColumRule = DbConst.getTableColumRule();
            }
            if (tableColumRule.equals(DbConst.TableColumRules.underline_to_camel)) {
                name = Table2JavaNameUtils.underLineToCamel(name);
            }

            if (propertyLowcaseFirstChar) {
                name = StringUtils.firstCharLowCase(name);
                sb.append("\t" + "private " + typeName + " " + name + ";/*"
                        + co.getRemarks() + ";len:" + co.getColumn_size()
                        + "*/\n");
            } else {
                sb.append("\t" + "private " + typeName + " " + name + ";/*"
                        + co.getRemarks() + ";len:" + co.getColumn_size()
                        + "*/\n");
            }

            sm.append("\t" + "public void set"
                    + (name.charAt(0) + "").toUpperCase() + name.substring(1)
                    + "(" + typeName + " " + name + "){\n");
            sm.append("\t\tthis." + name + " = " + name + ";\n\t}");
            sm.append("\n\t" + "public " + typeName + " get"
                    + (name.charAt(0) + "").toUpperCase() + name.substring(1)
                    + "(){\n");
            sm.append("\t\treturn " + name + ";\n\t}\n");
        }
        String toStringMethod = "\n\tpublic String toString(){\n";
        toStringMethod = toStringMethod + "\t\treturn  ObjectUtils.toString(this);\n\t}\n";

        sb.append("\n" + sm);
        sb.append(toStringMethod);
        int hashCode = (tableName + toPackage).hashCode();
        String to = "\n\tprivate static final long serialVersionUID =" + hashCode + "L;\n";
        sb.append(to);
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * 从某个数据库中导出javabean类，每个表的字段对应javabean相应的属性<br>
     * 例子如下：
     * <blockquote>
     * SqlUtils.exportTables("tt1", "feecenter", "e:/okok3", "u6.hwfee.domain.db","utf-8",false);
     * </blockquote>
     *
     * @param pool                     dbpool.xml里连接池名称，如 tt1
     * @param schema                   导出的数据库名称
     * @param toFolder                 导出的javabean类存放的目录
     * @param toPackage                导出的javabean类所属的包名
     * @param remarkEncoding           导出的javabean类文件(.java)编码
     * @param propertyLowcaseFirstChar 导出的属性第一个字符小写
     */
    public static void exportTables(String pool, String schema,
                                    String toFolder, String toPackage, String remarkEncoding, boolean propertyLowcaseFirstChar) {

        DataBase db = null;
        try {
            db = DataBaseFactory.getDataBase(pool);
            db.setAutoCommit(false);
            Connection conn = db.getConnection(true);
            DatabaseMetaData dd = conn.getMetaData();
            if (StringUtils.isEmpty(schema)) {
                schema = conn.getCatalog();
            }
            ResultSet rs = dd.getTables(conn.getCatalog(), schema, "%",
                    new String[]{"TABLE"});

            ArrayList tablelist = new ArrayList();
            ArrayList tableCommentList = new ArrayList();
            while (rs.next()) {
                // System.out.println(rs.getString("TABLE_NAME"));
                String tableName = rs.getString("TABLE_NAME");
                tablelist.add(tableName);

                String tableComment = rs.getString("REMARKS");
                if (StringUtils.isEmpty(tableComment)) {
                    if (db.getDataBaseType().isMySqlFamily()) {
                        String sql = "SELECT TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES "
                                + "WHERE TABLE_NAME=? AND TABLE_SCHEMA='" + schema + "'";
                        try {
                            Map<Integer, Object> args = new HashMap<Integer, Object>();
                            args.put(1, tableName);
                            DataBaseSet dbrs = db.queryForResultSet(sql, args);
                            if (dbrs.next()) {
                                tableComment = dbrs.getString("TABLE_COMMENT");
                            }
                        } finally {
                        }

                    }
                }
                tableCommentList.add(tableComment);
            }

            rs.close();
            for (int i = 0; i < tablelist.size(); i++) {

                rs = dd.getColumns(conn.getCatalog(), schema, (String) tablelist.get(i), "%");
                String tableName = (String) tablelist.get(i);
                String tableRemark = (String) tableCommentList.get(i);

                Map columMap = new LinkedHashMap();

                while (rs.next()) {
                    Column co = new Column();
                    co.setColumn_name(rs.getString("COLUMN_NAME"));
                    co.setColumn_size(rs.getInt("COLUMN_SIZE"));
                    co.setData_type(rs.getInt("DATA_TYPE"));
                    co.setRemarks(new String(rs.getBytes("REMARKS"),
                            remarkEncoding));
                    co.setTable_cat(rs.getString("TABLE_CAT"));
                    co.setTable_name(rs.getString("TABLE_NAME"));
                    co.setTable_schem(rs.getString("TABLE_SCHEM"));
                    co.setType_name(rs.getString("TYPE_NAME"));

                    columMap.put(rs.getString("COLUMN_NAME"), co);


                }
                TString tclassName = new TString("");
                String content = exportJavaBean(pool, (String) tablelist.get(i), tableRemark,
                        columMap, toPackage, propertyLowcaseFirstChar, tclassName);
                FileUtils.write(
                        toFolder + "/" + tclassName + ".java", content
                );
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void main(String[] args) throws Exception {
        String sqltxt = "{call query_course_cnt_func(#{name})}";
        System.out.println(
                StringUtils.indexOf(sqltxt, "[{=]\\s*call\\s+", true));
    }

    /**
     * 判断查询结果集中是否存在某列
     *
     * @param rs         查询结果集
     * @param columnName 列名
     * @return true 存在; false 不存咋
     */
    public static boolean isExistColumn(ResultSet rs, String columnName) {

        try {
            if (rs.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }

        return false;

    }

    public static boolean checkSqlType(Class t) {
        if (sqlType.contains(t)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static Object getValueFromResult(String dbpoolName, Class t, String prefix,
                                            String name, ResultSet rs, Map<String, String> map) {

        Object value = null;
        try {

            long start = System.currentTimeMillis();
            if (map != null) {
                String mapName = map.get(name);
                if (mapName != null) {
                    name = mapName;
                }
            }
            String columName = getColumName(dbpoolName, name);
            String labelName = prefix + columName;
            String prefixName = prefix + name;//根据属性名称
            if (!isExistColumn(rs, labelName)) { //优先使用labelName
                if (labelName.equals(prefixName)) {
                    return null;
                } else {
                    if (!isExistColumn(rs, prefixName)) {
                        return null;
                    } else {
                        labelName = prefixName;
                    }
                }
            }

            if (rs.getObject(labelName) == null) return null;

            if (t == String.class) {

                value = rs.getString(labelName);


            } else if (t == Integer.class || t == int.class) {

                value = rs.getInt(labelName);

            } else if (t == boolean.class || t == Boolean.class) {

                value = rs.getBoolean(labelName);

            } else if (t == Long.class || t == long.class) {
                value = rs.getLong(labelName);

            } else if (t == Short.class || t == short.class) {
                value = rs.getShort(labelName);

            } else if (t == Float.class || t == float.class) {
                value = rs.getFloat(labelName);

            } else if (t == Double.class || t == double.class) {
                value = rs.getDouble(labelName);

            } else if (t == Date.class) {
                value = rs.getTimestamp(labelName);
                value = SqlUtils
                        .sqlTimestampTojavaDate((Timestamp) value);
            } else if (t == java.sql.Date.class) {
                value = rs.getDate(labelName);
            } else if (t == Timestamp.class) {
                value = rs.getTimestamp(labelName);
            } else if (t == Time.class) {
                value = rs.getTime(labelName);
            } else if (t == LocalDate.class) {
                value = rs.getDate(labelName);
                value = ((java.sql.Date) value).toLocalDate();
            } else if (t == LocalDateTime.class) {
                value = rs.getTimestamp(labelName);
                value = ((Timestamp) value).toLocalDateTime();
            } else if (t == LocalTime.class) {
                value = rs.getTime(labelName);
                value = ((Time) value).toLocalTime();
            } else if (t == Byte.class || t == byte.class) {
                value = rs.getByte(labelName);
            } else if (t == char.class || t == Character.class) {
                value = rs.getString(labelName);
            } else if (t == java.math.BigDecimal.class) {
                value = rs.getBigDecimal(labelName);
            } else if (t == java.math.BigInteger.class) {
                value = rs.getLong(labelName);
                value = new java.math.BigInteger(value.toString());
            } else {
                value = rs.getObject(labelName, t);
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
        return value;
    }

    public static void getValueFromCallableStatement(CallableStatement rs,
                                                     Map<Integer, Object> outParms, Map<Integer, Object> returnKeyValues)
            throws Exception {

        Object value = null;
        try {

            Set<Integer> keys = outParms.keySet();
            for (Integer key : keys) {
                Object val = outParms.get(key);
                if (!(val instanceof Class)) {
                    val = val.getClass();
                }
                Class t = (Class) val;
                if (t == String.class) {

                    value = rs.getString(key);

                } else if (t == Integer.class || t == int.class) {

                    value = rs.getInt(key);

                } else if (t == boolean.class || t == Boolean.class) {

                    value = rs.getBoolean(key);

                } else if (t == Long.class || t == long.class) {
                    value = rs.getLong(key);

                } else if (t == Short.class || t == short.class) {
                    value = rs.getShort(key);

                } else if (t == Float.class || t == float.class) {
                    value = rs.getFloat(key);

                } else if (t == Double.class || t == double.class) {
                    value = rs.getDouble(key);

                } else if (t == Date.class) {
                    value = rs.getTimestamp(key);
                    value = SqlUtils
                            .sqlTimestampTojavaDate((Timestamp) value);
                } else if (t == java.sql.Date.class) {
                    value = rs.getDate(key);
                } else if (t == Timestamp.class) {
                    value = rs.getTimestamp(key);
                } else if (t == Time.class) {
                    value = rs.getTime(key);
                } else if (t == LocalDate.class) {
                    value = rs.getDate(key);
                    value = ((java.sql.Date) value).toLocalDate();
                } else if (t == LocalDateTime.class) {
                    value = rs.getTimestamp(key);
                    value = ((Timestamp) value).toLocalDateTime();
                } else if (t == LocalTime.class) {
                    value = rs.getTime(key);
                    value = ((Time) value).toLocalTime();
                } else if (t == Byte.class || t == byte.class) {
                    value = rs.getByte(key);
                } else if (t == char.class || t == Character.class) {
                    value = rs.getString(key);
                } else if (t == java.math.BigDecimal.class) {
                    value = rs.getBigDecimal(key);
                } else if (t == java.math.BigInteger.class) {
                    value = rs.getLong(key);
                    value = new java.math.BigInteger(value.toString());
                } else if (t == DataBaseSet.class) {
                    value = rs.getObject(key);
                    ResultSet rss = null;
                    CachedRowSet crs = null;
                    try {
                        rss = (ResultSet) value;
                        crs = new CachedRowSetImpl();
                        rss.beforeFirst();
                        crs.populate(rss);
                    } finally {
                        if (rss != null) {
                            rss.close();
                        }
                    }
                    Object temp = new DataBaseSet(crs);
                } else {

                    value = rs.getObject(key);
                    if (value != null && value instanceof ResultSet) {
                        //value = rs.getObject(key);
                        ResultSet rss = null;
                        CachedRowSet crs = null;
                        try {
                            rss = (ResultSet) value;
                            crs = new CachedRowSetImpl();
                            rss.beforeFirst();
                            crs.populate(rss);
                        } finally {
                            if (rss != null) {
                                rss.close();
                            }
                        }
                        Object temp = new DataBaseSet(crs);
                        value = temp;
                    } else {

                        throw new Exception("position[" + key + "] class["
                                + t.getName() + "] can't be decided!");
                    }

                }
                if (returnKeyValues != null)
                    returnKeyValues.put(key, value);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        }

    }

    public static String generateSelectSqlBySelectObject(String dbpoolName, Object selectObject, Class reflectClass,
                                                         Map<Integer, Object> returnvParameters, QueryOptions options,
                                                         DBMS dataBaseType)
            throws Exception {

        String sql = "";
        String className = reflectClass.getSimpleName();
        String select = "select *";
        if (selectObject instanceof MdbOptions) {
            MdbOptions mm = (MdbOptions) selectObject;
            String selectPartString = handSelectPartString(mm.selectOptions(), dbpoolName, dataBaseType);
            if (!selectPartString.isEmpty()) {
                select = selectPartString;
            }
        }
        sql = select + " from " + dbEscapeLef(dataBaseType) + getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType) + " ";

        try {
            int index = 1;

            String where = " where ";
            Map<String, TResult2<Class, Object>> map = PropertyUtil.describeForTypes(selectObject, reflectClass);
            Set<?> set = map.keySet();

            Iterator<?> inames = set.iterator();
            while (inames.hasNext()) {
                String name = (String) inames.next();

                TResult2<Class, Object> tr2 = map.get(name);
                Class<?> t = tr2.getFirstValue();
                Object value = null;

                // name为javabean属性名
                if (SqlUtils.checkedSimpleType(t)) {// 简单类型

                    value = tr2.getSecondValue();
                    if (value != null) {
                        where = where + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, name) + dbEscapeRight(dataBaseType) + "=? and ";
                        returnvParameters.put(index++, value);
                    }
                } else {
                    continue;
                }
            }
            where = StringUtils.trim(where);
            where = StringUtils.trimTailString(where, "and");
            if (!where.equals("where")) {
                sql = sql + " " + where + " ";
            }
            if (selectObject instanceof MdbOptions) {
                MdbOptions mm = (MdbOptions) selectObject;
                if (mm.selectOptions() != null) {
                    String tailPartString = handTailPartString(mm.selectOptions(), dbpoolName, dataBaseType);
                    if (!tailPartString.isEmpty()) {
                        sql = sql + tailPartString;
                    }
                    if (mm.selectOptions().limit() != null && mm.selectOptions().limit() >= 0) {
                        sql = dataBaseType.pageSQL(sql, 1, mm.selectOptions().limit());

                    } else {
                        if (options != null && options.isLimitOne()) {
                            sql = dataBaseType.pageSQL(sql, 1, 1);
                        }
                    }
                }
            }
            return sql;
        } catch (Exception e) {
            throw e;
        }

    }


    public static String handSelectPartString(SelOp so, String dbpoolName, DBMS dataBaseType) {
        if (so == null) {
            return "select * ";
        }
        if (StringUtils.isEmpty(so.select())) {
            return "select * ";
        }
        return "select " + so.select() + " ";
    }

    public static String handTailPartString(SelOp so, String dbpoolName, DBMS dataBaseType) {
        if (so == null) {
            return "";
        }
        String orderStr = "";
        if (so.orderBy() != null && !so.orderBy().isEmpty()) {
            String[] strs = so.orderBy().split(",");

            for (int i = 0; i < strs.length; i++) {
                String s = strs[i].trim();
                String[] temps = s.split(" +");

                String part = SqlUtils.dbEscapeLef(dataBaseType)
                        + SqlUtils.getColumName(dbpoolName, temps[0]) + SqlUtils.dbEscapeRight(dataBaseType);
                if (temps.length >= 2) {
                    part = part + " " + temps[1];
                }
                if (i == 0) {
                    orderStr = orderStr + "" + part;
                } else {
                    orderStr = orderStr + "," + part;
                }
            }
        }
        String ret = "";

        if (!orderStr.isEmpty()) {
            ret = ret + " order by " + orderStr;
        }

        return ret;
    }

    public static String getPageSql(String sqlQuery, int pageNum, int pageSize, DBMS dataBaseType) {

        sqlQuery = sqlQuery.trim();
        String sql = dataBaseType.pageSQL(sqlQuery, pageNum, pageSize);
        if (StringUtils.hasText(sql)) {
            return sql;
        } else {
            throw new DbException("数据库不支持分页！");
        }
    }

    public static String generateSelectSql(String dbpoolName, Object selectObject, Class reflectClass,
                                           String[] whereProperteis, Map<Integer, Object> returnvParameters,
                                           QueryOptions options, DBMS dataBaseType) throws Exception {

        String sql = "";
        String className = reflectClass.getSimpleName();
        String select = "select *";
        if (selectObject instanceof MdbOptions) {
            MdbOptions mm = (MdbOptions) selectObject;
            String selectPartString = handSelectPartString(mm.selectOptions(), dbpoolName, dataBaseType);
            if (!selectPartString.isEmpty()) {
                select = selectPartString;
            }
        }
        sql = select + " from " + dbEscapeLef(dataBaseType) +
                getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType) + " ";

        if (whereProperteis == null || whereProperteis.length == 0) {
            return sql;
        }
        String[] keys = whereProperteis;
        if (ArrayUtils.isEmpty(keys)) {
            return sql;
        }
        try {

            int index = 1;
            String where = " where ";
            for (int n = 0; n < keys.length; n++) {
                Object value = PropertyUtil.getProperty(selectObject, keys[n]);

                where = where + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, keys[n]) + dbEscapeRight(dataBaseType) + "=?";
                if (n < keys.length - 1) {
                    where = where + " and ";
                }
                returnvParameters.put(index++, value);
            }
            sql = sql + where;
            if (selectObject instanceof MdbOptions) {
                MdbOptions mm = (MdbOptions) selectObject;
                if (mm.selectOptions() != null) {
                    String tailPartString = handTailPartString(mm.selectOptions(), dbpoolName, dataBaseType);
                    if (!tailPartString.isEmpty()) {
                        sql = sql + tailPartString;
                    }
                    if (mm.selectOptions().limit() != null && mm.selectOptions().limit() >= 0) {
                        sql = dataBaseType.pageSQL(sql, 1, mm.selectOptions().limit());

                    } else {
                        if (options != null && options.isLimitOne()) {
                            sql = dataBaseType.pageSQL(sql, 1, 1);
                        }
                    }
                }
            }
            return sql;
        } catch (Exception e) {
            throw e;
        }

    }


    public static String generateDeleteSqlByObject(String dbpoolName, Object deleteObject,
                                                   Class reflectClass,
                                                   Map<Integer, Object> returnvParameters,
                                                   UpdateOptions options, DBMS dataBaseType) throws Exception {

        List<String> whereProperteis = new ArrayList<>();

        Map<String, TResult2<Class, Object>> map = PropertyUtil.describeForTypes(deleteObject, reflectClass);
        Set<?> set = map.keySet();

        Iterator<?> inames = set.iterator();
        int i = 0;
        while (inames.hasNext()) {
            String name = (String) inames.next();
            TResult2<Class, Object> tr2 = map.get(name);
            Class<?> t = tr2.getFirstValue();
            Object value = null;

            // name为javabean属性名
            if (SqlUtils.checkedSimpleType(t)) {// 简单类型
                value = tr2.getSecondValue();
                if (value != null) {
                    whereProperteis.add(name);
                }
            } else {
                continue;
            }

        }
        if (whereProperteis.size() == 0) {
            throw new RuntimeException("对象所有属性值为空，获取不了属性！");
        }

        return SqlUtils.generateDeleteSql(dbpoolName, deleteObject, reflectClass, whereProperteis.toArray(new String[0]), returnvParameters, options, dataBaseType);

    }

    public static String getColumName(String dbStr, String proName) {
        String colName = proName;
        String tableColumRule = DbConst.getTableColumRule(dbStr);
        if (StringUtils.isEmpty(tableColumRule)) {
            tableColumRule = DbConst.getTableColumRule();
        }
        if (tableColumRule.equals(DbConst.TableColumRules.underline_to_camel)) {
            colName = Table2JavaNameUtils.camelToUnderLine(proName);
        }
        return colName;
    }

    public static String getTableName(String dbpoolName, String className) {
        String tableName = "";

        String tableNameRule = "";
        tableNameRule = DbConst.getTableNameRule(dbpoolName);
        if (StringUtils.isEmpty(tableNameRule)) {
            tableNameRule = DbConst.getTableNameRule();
        }
        if (tableNameRule.equals(DbConst.TableNameRules.underline_to_camel)) {
            tableName = Table2JavaNameUtils.camelToUnderLine(className);
        } else if (tableNameRule.equals(DbConst.TableNameRules.normal)) {
            tableName = className;
        } else if (tableNameRule.equals(DbConst.TableNameRules.first_letter_upcase)) {
            tableName = className.toLowerCase();
        } else {
            tableName = className;
        }
        return tableName;
    }

    public static String generateDeleteSql(String dbpoolName, Object deleteObject, Class reflectClass,
                                           String[] whereProperteis, Map<Integer, Object> returnvParameters,
                                           UpdateOptions options, DBMS dataBaseType) throws Exception {

        String sql = "";
        String className = reflectClass.getSimpleName();

        sql = "delete from " + dbEscapeLef(dataBaseType) + getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType);

        if (whereProperteis == null || whereProperteis.length == 0) {
            throw new RuntimeException("deleteProperteis为空");
        }
        String[] keys = whereProperteis;

        try {

            int index = 1;

            String where = " where ";

            for (int n = 0; n < keys.length; n++) {
                Object value = PropertyUtil.getProperty(deleteObject, keys[n]);
                where = where + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, keys[n]) + dbEscapeRight(dataBaseType) + "=?";
                if (n < keys.length - 1) {
                    where = where + " and ";
                }
                returnvParameters.put(index++, value);
            }
            sql = sql + where;
            //log.debug("generated sql:" + sql);
            return sql;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     *  根据对象生成更新的SQL语句
     * @param dbpoolName 数据库连接池
     * @param properties        需更新的属性，如果为空，表明更新所有主键属性以外的属性
     * @param updateObject      需更新的对象
     * @param reflectClass      如果不为null，则使用它生成表名，用于javaBean存在继承时指定哪个父类用于生成表名
     * @param whereProperteis   updateObject对象里为主键属性名，复合主键以","隔开，主键属性用于唯一标示一个对象，通过它生成where语句
     * @param returnvParameters 生成的参数，每个参数对应于生成语句的一个"?"字符
     * @param options           更新选项
     * @param ignoreNull        是否忽略为空的属性，即如果属性为空，则不生成对应属性的sql
     * @param dataBaseType  DBMS对象
     * @return 生成的SQL语句
     * @throws Exception 异常
     */
    public static String generateUpdateSql(String dbpoolName, String[] properties,
                                           Object updateObject, Class reflectClass, String[] whereProperteis,
                                           Map<Integer, Object> returnvParameters,
                                           UpdateOptions options, boolean ignoreNull,
                                           DBMS dataBaseType)
            throws Exception {

        if (whereProperteis == null || whereProperteis.length == 0) {
            throw new RuntimeException("whereProperteis为空");
        }

        String sql = "";
        String className = reflectClass.getSimpleName();
        sql = "update " + dbEscapeLef(dataBaseType) + getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType) + " ";
        String colPart = "set ";
        String[] keys = whereProperteis;
        try {
            Map<String, TResult2<Class, Object>> map = PropertyUtil.describeForTypes(updateObject, reflectClass);
            Set<?> set = map.keySet();
            Iterator<?> i = set.iterator();
            int index = 1;

            while (i.hasNext()) {

                String name = (String) i.next();

                if (properties != null) {

                    if (!ArrayUtils.contains(properties, name)) {
                        continue;
                    }
                }
                if (DataBaseKeyMap.getMap() != null) {
                    String mapName = DataBaseKeyMap.getMap().get(name);
                    if (mapName != null) {
                        name = mapName;
                    }
                }

                if (ArrayUtils.containsIgnoreCase(keys, name)) {
                    continue;
                }

                Class<?> t = map.get(name).getFirstValue();

                // name为javabean属性名
                if (SqlUtils.checkedSimpleType(t)) {// 简单类型
                    Object colValue = map.get(name).getSecondValue();

                    if (colValue != null) {
                        colPart = colPart + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, name) + dbEscapeRight(dataBaseType) + "=?,";

                        returnvParameters.put(index++, colValue);
                    } else {
                        if (!ignoreNull) {
                            colPart = colPart + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, name) + dbEscapeRight(dataBaseType) + "=null,";
                        }
                    }

                } else {
                    String error = "update a illegal type:[" + t + "]";
                    log.warn(error);
                }

            }// while

            colPart = colPart.substring(0, colPart.length() - 1);

            sql = sql + " " + colPart;
            String where = " where ";

            for (int n = 0; n < keys.length; n++) {
                Object value = PropertyUtil.getProperty(updateObject, keys[n]);
                where = where + getColumName(dbpoolName, keys[n]) + "=?";
                if (n < keys.length - 1) {
                    where = where + " and ";
                }
                returnvParameters.put(index++, value);
            }
            sql = sql + where;
            // log.debug("generated sql:" + sql);
            return sql;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 根据对象生成插入sql语句
     * @param dbpoolName 数据库连接池名称
     * @param properties  哪些属性需要更新
     * @param insertObject 需插入的对象
     * @param reflectClass 真实插入对象的class，用于在javaBean存在继承时，使用哪个继承层次的类名作为表名
     * @param returnvParameters 生成的参数，每个参数对应于生成语句的相应位置的"?"字符
     * @param options    更新选项
     * @param ignoreNull  是否忽略properties属性里空值的属性更新到数据库
     * @param dataBaseType DBMS对象
     * @return  回根据对象生成的插入SQL语句
     * @throws Exception  异常
     */
    public static String generateInsertSql(String dbpoolName, String[] properties,
                                           Object insertObject, Class reflectClass, Map<Integer, Object> returnvParameters,
                                           UpdateOptions options, boolean ignoreNull, DBMS dataBaseType) throws Exception {
        String sql = "";

        String className = reflectClass.getSimpleName();

        sql = "insert into " + dbEscapeLef(dataBaseType) + getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType);
        String colPart = "(";
        String values = "values(";

        try {

            Map<String, TResult2<Class, Object>> map = PropertyUtil.describeForTypes(insertObject, reflectClass);
            Set<?> set = map.keySet();
            Iterator<?> i = set.iterator();
            int index = 1;

            while (i.hasNext()) {

                String name = (String) i.next();

                if (properties != null
                        && !ArrayUtils.containsIgnoreCase(properties, name)) {
                    continue;
                }
                if (DataBaseKeyMap.getMap() != null) {
                    String mapName = DataBaseKeyMap.getMap().get(name);
                    if (mapName != null) {
                        name = mapName;
                    }
                }
                TResult2<Class, Object> tr2 = map.get(name);
                Class<?> t = tr2.getFirstValue();

                // name为javabean属性名
                if (SqlUtils.checkedSimpleType(t)) {// 简单类型

                    Object colValue = tr2.getSecondValue();
                    if (colValue == null) {
                        if (ignoreNull) {
                            continue;
                        } else {
                            colPart = colPart + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, name) + dbEscapeRight(dataBaseType);
                            values = values + "null";
                            colPart = colPart + ",";
                            values = values + ",";
                            continue;
                        }

                    }

                    colPart = colPart + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, name) + dbEscapeRight(dataBaseType);
                    values = values + "?";
                    colPart = colPart + ",";
                    values = values + ",";

                    returnvParameters.put(index++, colValue);

                } else {
                    String error = "insert a illegal type:[" + t + "]";
                    log.warn(error);
                }

            }// while

            colPart = colPart.substring(0, colPart.length() - 1) + ")";
            values = values.substring(0, values.length() - 1) + ")";
            sql = sql + " " + colPart + " " + values;

            //log.debug("generated sql:" + sql);
            return sql;
        } catch (Exception e) {
            throw e;
        }

    }

    public static String registForStoredProc(Map<Integer, Object> vParameters,
                                             CallableStatement preStmt) throws SQLException {
        String paramStr = ""; // add by jda at 2007/12/15

        if (vParameters != null && vParameters.size() > 0) {

            Set<Integer> keys = vParameters.keySet();
            List<Integer> keyList = new ArrayList<Integer>(keys);
            Collections.sort(keyList);

            for (Integer key : keyList) {

                Object val = vParameters.get(key);
                if (!(val instanceof Class)) {
                    val = val.getClass();
                }

                preStmt.registerOutParameter(key, javaType2sql.get(val));
                paramStr = paramStr + "[" + key + ":" + val + "]";

            }
        }
        return paramStr;

    }

    /**
     * 用于设置执行存储过程PreparedStatement的参数
     * @param vParameters 参数
     * @param preStmt  PreparedStatement对象
     * @return  返回调试的字符串
     * @throws SQLException 异常
     */
    public static String setToPreStatment(Map<Integer, Object> vParameters,
                                          PreparedStatement preStmt) throws SQLException {

        String paramStr = "";
        Set<Integer> keys = vParameters.keySet();
        List<Integer> keyList = new ArrayList<Integer>(keys);
        Collections.sort(keyList);// 排序
        if (vParameters != null && vParameters.size() > 0) {
            for (Integer key : keyList) {

                Object obj = vParameters.get(key);
                if (obj instanceof Character) {
                    preStmt.setString(key, (String) obj);
                    paramStr = paramStr + "[" + key + ":\"" + (String) obj + "\"]";
                } else if (obj instanceof Boolean) {
                    preStmt.setBoolean(key, (Boolean) obj);
                    paramStr = paramStr + "[" + key + ":"
                            + ((Boolean) obj).toString() + "]";
                } else if (obj instanceof String) {
                    // preStmt.setString(j, (String) obj);
                    preStmt.setString(key, (String) obj);
                    paramStr = paramStr + "[" + key + ":\"" + (String) obj + "\"]";
                } else if (obj instanceof Integer) {
                    preStmt.setInt(key, ((Integer) obj).intValue());
                    paramStr = paramStr + "[" + key + ":"
                            + ((Integer) obj).toString() + "]";
                } else if (obj instanceof Long) {
                    preStmt.setLong(key, ((Long) obj).longValue());
                    paramStr = paramStr + "[" + key + ":"
                            + ((Long) obj).toString() + "]";
                } else if (obj instanceof Float) {
                    preStmt.setFloat(key, ((Float) obj).floatValue());
                    paramStr = paramStr + "[" + key + ":"
                            + ((Float) obj).toString() + "]";
                } else if (obj instanceof Double) {
                    preStmt.setDouble(key, ((Double) obj).doubleValue());
                    paramStr = paramStr + "[" + key + ":"
                            + ((Double) obj).toString() + "]";
                } else if (obj instanceof java.sql.Date) {
                    preStmt.setDate(key, (java.sql.Date) obj);
                    paramStr = paramStr + "[" + key + ":"
                            + (CTime.formatWholeDate((java.sql.Date) obj)) + "]";
                } else if (obj instanceof Date) {
                    Timestamp tmsp = new Timestamp(
                            ((Date) obj).getTime());
                    preStmt.setTimestamp(key, tmsp);
                    paramStr = paramStr + "[" + key + ":"
                            + CTime.formatWholeDate(tmsp) + "]";
                } else if (obj instanceof LocalDate) {
                    java.sql.Date sqlDate = java.sql.Date.valueOf((LocalDate) obj);
                    preStmt.setDate(key, sqlDate);
                    paramStr = paramStr + "[" + key + ":" + sqlDate.toString() + "]";

                } else if (obj instanceof LocalDateTime) {
                    Timestamp tmsp = Timestamp.valueOf((LocalDateTime) obj);
                    preStmt.setTimestamp(key, tmsp);
                    paramStr = paramStr + "[" + key + ":" + tmsp.toString() + "]";
                } else if (obj instanceof LocalTime) {
                    Time tmsp = Time.valueOf((LocalTime) obj);
                    preStmt.setTime(key, tmsp);
                    paramStr = paramStr + "[" + key + ":" + tmsp.toString() + "]";
                } else if (obj instanceof java.math.BigDecimal) {
                    preStmt.setBigDecimal(key, (java.math.BigDecimal) obj);

                    paramStr = paramStr + "[" + key + ":"
                            + ((java.math.BigDecimal) obj).doubleValue() + "]";
                } else if (obj instanceof java.math.BigInteger) {
                    preStmt.setLong(key, ((java.math.BigInteger) obj).longValue());
                    paramStr = paramStr + "[" + key + ":"
                            + ((java.math.BigInteger) obj).longValue() + "]";
                } else {
                    preStmt.setObject(key, obj);
                    paramStr = paramStr + "[" + key + ":" + obj + "]";

                }

            }
        }
        return paramStr;
    }

    /**
     * 生产预处理的sql的debug语句，也就是把?字符替换成实际的参数，主要用于调试
     * @param sql  带?的sql语句
     * @param vParameters  参数
     * @param dbms  数据方言
     * @return 返回debug sql。带?的sql里所有?都会填入参数
     */
    public static String generateDebugSql(String sql,
                                          Collection<Object> vParameters, DBMS dbms) {

        String paramStr = "";
        StringBuilder sb = new StringBuilder(sql);
        if (vParameters != null && vParameters.size() > 0) {
            Iterator it = vParameters.iterator();
            int i = 0;
            int start = 0;

            while (it.hasNext()) {
                Object obj = it.next();
                int blen = sb.length();
                int m = sb.indexOf("?", start);
                if (m == -1)
                    break;
                sb.replace(m, m + 1, dbms.javaObjToSqlValue(obj));
                int alen = sb.length();
                start = m + (alen - blen) + 1;

            }
        }
        return sb.toString();

    }


    public static String setToPreStatment(Collection<Object> vParameters,
                                          PreparedStatement preStmt) throws SQLException {

        String paramStr = ""; // add by jda at 2007/12/15
        if (vParameters != null && vParameters.size() > 0) {
            Iterator it = vParameters.iterator();
            int i = 0, j = 0;
            while (it.hasNext()) {
                Object obj = it.next();
                j = ++i;
                if (obj instanceof Character) {
                    preStmt.setString(j, (String) obj);
                    paramStr = paramStr + "[" + j + ":" + (String) obj + "]";
                } else if (obj instanceof String) {
                    // preStmt.setString(j, (String) obj);
                    preStmt.setString(j, (String) obj);
                    paramStr = paramStr + "[" + j + ":" + (String) obj + "]";
                } else if (obj instanceof Boolean) {
                    preStmt.setBoolean(j, (Boolean) obj);
                    paramStr = paramStr + "[" + j + ":"
                            + ((Boolean) obj).toString() + "]";
                } else if (obj instanceof Integer) {
                    preStmt.setInt(j, ((Integer) obj).intValue());
                    paramStr = paramStr + "[" + j + ":"
                            + ((Integer) obj).toString() + "]";
                } else if (obj instanceof Long) {
                    preStmt.setLong(j, ((Long) obj).longValue());
                    paramStr = paramStr + "[" + j + ":"
                            + ((Long) obj).toString() + "]";
                } else if (obj instanceof java.math.BigInteger) {
                    preStmt.setLong(j, ((java.math.BigInteger) obj).longValue());
                    paramStr = paramStr + "[" + j + ":"
                            + ((java.math.BigInteger) obj).longValue() + "]";
                } else if (obj instanceof java.math.BigDecimal) {
                    preStmt.setBigDecimal(j, (java.math.BigDecimal) obj);
                    paramStr = paramStr + "[" + j + ":"
                            + ((java.math.BigDecimal) obj).doubleValue() + "]";
                } else if (obj instanceof Float) {
                    preStmt.setFloat(j, ((Float) obj).floatValue());
                    paramStr = paramStr + "[" + j + ":"
                            + ((Float) obj).toString() + "]";
                } else if (obj instanceof Double) {
                    preStmt.setDouble(j, ((Double) obj).doubleValue());
                    paramStr = paramStr + "[" + j + ":"
                            + ((Double) obj).toString() + "]";
                } else if (obj instanceof java.sql.Date) {
                    preStmt.setDate(j, (java.sql.Date) obj);
                    paramStr = paramStr + "[" + j + ":"
                            + ((java.sql.Date) obj).toString() + "]";
                } else if (obj instanceof Date) {
                    Timestamp tmsp = new Timestamp(
                            ((Date) obj).getTime());
                    preStmt.setTimestamp(j, tmsp);
                    paramStr = paramStr + "[" + j + ":" + tmsp.toString() + "]";
                } else if (obj instanceof LocalDate) {
                    java.sql.Date sqlDate = java.sql.Date.valueOf((LocalDate) obj);
                    preStmt.setDate(j, sqlDate);
                    paramStr = paramStr + "[" + j + ":" + sqlDate.toString() + "]";

                } else if (obj instanceof LocalDateTime) {
                    Timestamp tmsp = Timestamp.valueOf((LocalDateTime) obj);
                    preStmt.setTimestamp(j, tmsp);
                    paramStr = paramStr + "[" + j + ":" + tmsp.toString() + "]";
                } else if (obj instanceof LocalTime) {
                    Time tmsp = Time.valueOf((LocalTime) obj);
                    preStmt.setTime(j, tmsp);
                    paramStr = paramStr + "[" + j + ":" + tmsp.toString() + "]";
                } else {
                    preStmt.setObject(j, obj);
                    paramStr = paramStr + "[" + j + ":" + obj + "]";


                }
            }
        }
        return paramStr;
    }

    public static void clearDbDriver() {

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                log.error("", e);

            }
        }

    }


    /**
     *把rs的一行转换成一个javabean
     * @param dbpoolName 数据库连接池
     * @param clazz ResultSet的每行数据映射到类型为clazz的对象
     * @param rs  需要抽取数据的ResultSet
     * @param <T> 泛型
     * @return  返回行数据映射的对象
     */
    public static <T> T getBeanFromResultSet(String dbpoolName, Class<T> clazz, ResultSet rs) {

        try {

            T bean = clazz.newInstance();

            Map<String, TResult2<Class, Object>> map = PropertyUtil.describeForTypes(bean, bean.getClass());
            Set<?> set = map.keySet();
            Iterator<?> i = set.iterator();

            while (i.hasNext()) {

                String name = (String) i.next();
                Class<?> t = map.get(name).getFirstValue();
                Object value = null;
                // name为javabean属性名
                if (SqlUtils.checkedSimpleType(t)) {// 简单类型

                    try {
                        value = SqlUtils.getValueFromResult(dbpoolName, t, "", name, rs,
                                DataBaseKeyMap.getMap());
                        PropertyUtil.setProperty(bean, name, value);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                } else {
                    log.info("不识别的类型[" + t.getSimpleName() + "]");
                }

            }// while

            return bean;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;

    }

    public static String encodeSQLStr(String sqlStr) {
        if (sqlStr == null) {
            return null;
        }
        return StringUtils.replace(sqlStr, "'", "''");
    }


    public static DataBase.SQLType decideSqlType(String sqltxt) throws DbException {
        sqltxt = StringUtils.trim(sqltxt);
        if (StringUtils.startsWithIgnoreCase(sqltxt, "select")) {
            return DataBase.SQLType.SELECT;
        } else if (StringUtils.startsWithIgnoreCase(sqltxt, "insert")) {
            return DataBase.SQLType.INSERT;
        } else if (StringUtils.startsWithIgnoreCase(sqltxt, "update")
                || StringUtils.startsWithIgnoreCase(sqltxt, "create")
                || StringUtils.startsWithIgnoreCase(sqltxt, "drop")
                || StringUtils.startsWithIgnoreCase(sqltxt, "alter")) {
            return DataBase.SQLType.UPDATE;
        } else if (StringUtils.startsWithIgnoreCase(sqltxt, "delete")) {
            return DataBase.SQLType.DELETE;
        } else if (StringUtils.startsWithIgnoreCase(sqltxt, "{")
                && StringUtils.endsWithIgnoreCase(sqltxt, "}")
                && StringUtils.indexOf(sqltxt, "[{=]\\s*call\\s+", true) >= 0) {
            return DataBase.SQLType.STORE_DPROCEDURE;

        } else {
            return DataBase.SQLType.OTHER;
        }
    }
}
