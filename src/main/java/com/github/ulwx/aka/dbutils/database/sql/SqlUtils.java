package com.github.ulwx.aka.dbutils.database.sql;

import com.github.ulwx.aka.dbutils.database.*;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.CachedRowSetImpl;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.database.utils.DbConst;
import com.github.ulwx.aka.dbutils.database.utils.Table2JavaNameUtils;
import com.github.ulwx.aka.dbutils.tool.support.*;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import com.github.ulwx.aka.dbutils.tool.support.type.TString;
import com.github.ulwx.aka.dbutils.tool.support.type.TType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.*;


public class SqlUtils {
    private static Logger log = LoggerFactory.getLogger(SqlUtils.class);

    public static String dbEscapeLef(DBMS dbms) {
        return dbms.escapeLeft();
    }

    public static String dbEscapeRight(DBMS dbms) {
        return dbms.escapeRight();
    }

    private static String exportJavaBean(String dbpool, String tableName, String tableRemark,
                                         Map<String, Column> columMap,
                                         String toPackage, boolean propertyLowcaseFirstChar,
                                         TString tclassName) {
        Set set = columMap.keySet();
        Iterator i = set.iterator();
        StringBuilder sb = new StringBuilder();
        StringBuilder sm = new StringBuilder();
        if (toPackage != null && !toPackage.trim().equals("")) {
            sb.append("package " + toPackage + ";\n");
        }

        sb.append("import java.util.*;\n");
        sb.append("import java.sql.*;\n");
        sb.append("import java.time.*;\n");
        // sb.append("import " + MdbOptions.class.getName() + ";\n");
        sb.append("import " + ObjectUtils.class.getName() + ";\n");
        sb.append("import " + AkaColumn.class.getName() + ";\n");
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
        sb.append("public class " + className +
                " implements java.io.Serializable {\n\n");
        int n = 0;
        while (i.hasNext()) {
            //@AkaColumn(isAutoincrement=
            String annotionStr = "";
            String name = (String) i.next();
            Column co = (Column) columMap.get(name);
            Integer type = co.getData_type();

            name = DBColum2JavaKyewordTool.toJavaPropery(name.trim());
            if (co.getIs_autoincrement().equals("YES")) {
                annotionStr = annotionStr + ",isAutoincrement=true";
            }
            if (co.getIs_nullable().equals("NO")) {
                annotionStr = annotionStr + ",isNullable=false";
            }
            if (!annotionStr.isEmpty()) {
                annotionStr = StringUtils.trimLeadingString(annotionStr, ",");
                annotionStr = "@AkaColumn(" + annotionStr + ")";
            }
            n++;
            Class typeClass = (Class) TypeMapSystem.sql2javaType(co);
            if (typeClass == null) {
                throw new DbException("类型:" + type + "无法转换！" + "type=" + type + ",co=" + ObjectUtils.toPrettyJsonString(co));
            }
            String typeName = typeClass.getSimpleName();
            if (TypeMapSystem.checkedSimpleType(typeClass)) {
                typeName = typeClass.getSimpleName();
            } else {
                if(typeClass==byte[].class ){
                    typeName = typeClass.getSimpleName();
                }else {
                    typeName = typeClass.getName();
                }
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
            sm.append("\n\t" + annotionStr);
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
                                    String toFolder, String toPackage,
                                    String remarkEncoding,
                                    boolean propertyLowcaseFirstChar) {

        DataBase db = null;
        try {
            db = DataBaseFactory.getDataBase(pool);
            db.setAutoCommit(false);
            Connection conn = db.getConnection(true);

            DatabaseMetaData dd = conn.getMetaData();
            if (StringUtils.isEmpty(schema)) {
                schema = conn.getCatalog();
            }
            ResultSet rs = null;
            ArrayList<String> tablelist = new ArrayList();
            ArrayList<String> tableCommentList = new ArrayList();
            ArrayList<Map<String, String>> tableColumCommentList = new ArrayList<>();

            if(db.getDataBaseType().isPostgresFamily()){
                String sqltable="SELECT " +
                        "    '' as REMARKS," +
                        "    t.table_name as TABLE_NAME " +
                        "FROM " +
                        "    information_schema.tables t " +
                        "LEFT JOIN  " +
                        "    pg_catalog.pg_class c ON c.relname = t.table_name " +
                        "LEFT JOIN  " +
                        "    pg_catalog.pg_inherits i ON c.oid = i.inhrelid " +
                        "WHERE " +
                        "    t.table_schema NOT IN ('information_schema', 'pg_catalog') " +
                        "    AND t.table_type = 'BASE TABLE' " +
                        "    AND i.inhrelid IS NULL  " +
                        "ORDER BY " +
                        "    t.table_schema," +
                        "    t.table_name;";
                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(sqltable);
            }else {

                rs = dd.getTables(conn.getCatalog(), schema, "%",
                        new String[]{"TABLE"});

            }
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tablelist.add(tableName);
                String tableComment = StringUtils.trim(rs.getString("REMARKS"));
                Map<String, String> colCommentMap = null;
                if (StringUtils.isEmpty(tableComment)) {
                    String sql = db.getDataBaseType().queryTableCommentSql(schema);

                    String sqlColum = db.getDataBaseType().queryColCommentSql(schema);
                    try {
                        Map<Integer, Object> args = new HashMap<Integer, Object>();
                        args.put(1, tableName);
                        DataBaseSet dbrs = null;
                        if (!sql.isEmpty()) {
                            dbrs = db.queryForResultSet(sql, args);
                            if (dbrs.next()) {
                                tableComment = dbrs.getString("TABLE_COMMENT");
                                colCommentMap = null;


                            }
                        }
                        if (!sqlColum.isEmpty()) {
                            dbrs = db.queryForResultSet(sqlColum, args);
                            if (dbrs != null) {
                                while (dbrs.next()) {
                                    if (colCommentMap == null) {
                                        colCommentMap = new HashMap<>();
                                    }
                                    String colName = dbrs.getString("COLUMN_NAME");
                                    String colComment = dbrs.getString("COLUMN_DESCRIPTION");
                                    colCommentMap.put(colName, colComment);
                                }
                            }
                        }

                    } finally {
                    }
                }
                tableCommentList.add(tableComment);
                tableColumCommentList.add(colCommentMap);
            }
            rs.close();
            for (int i = 0; i < tablelist.size(); i++) {

                rs = dd.getColumns(conn.getCatalog(), schema, (String) tablelist.get(i), "%");
                String tableName = (String) tablelist.get(i);
                String tableRemark = (String) tableCommentList.get(i);

                Map<String, Column> columMap = new LinkedHashMap<>();

                while (rs.next()) {

                    Column co = new Column();
                    co.setDbms(db.getDataBaseType());
                    co.setColumn_name(StringUtils.trimLeadingString("';",rs.getString("COLUMN_NAME")));
                    co.setColumn_size(rs.getInt("COLUMN_SIZE"));
                    co.setData_type(rs.getInt("DATA_TYPE"));
                    co.setIs_nullable(rs.getString("IS_NULLABLE"));

                    if(tableColumCommentList.get(i)!=null){
                        co.setRemarks(tableColumCommentList.get(i).get(co.getColumn_name()));
                    } else {
                        try {
                            String remark = rs.getString("REMARKS");
                            if(remark==null) remark="";
                            co.setRemarks(remark);
                        }catch (Exception ex) {
                        }
                    }

                    co.setTable_cat(rs.getString("TABLE_CAT"));
                    co.setTable_name(rs.getString("TABLE_NAME"));
                    co.setTable_schem(rs.getString("TABLE_SCHEM"));
                    co.setType_name(rs.getString("TYPE_NAME"));
                    try {
                        co.setIs_autoincrement(rs.getString("IS_AUTOINCREMENT"));
                    }catch (Exception ex) {
                    }
                    co.setColumn_def(rs.getString("COLUMN_DEF"));
                    try {
                        co.setIs_generatedcolumn(rs.getString("IS_GENERATEDCOLUMN"));
                    } catch (Exception ex) {
                    }
                    try {
                        co.setSource_data_type(rs.getShort("SOURCE_DATA_TYPE"));
                    }catch (Exception ex) {
                    }
                    try {
                        co.setDecimal_digits(rs.getInt("DECIMAL_DIGITS"));
                    }catch (Exception ex) {
                    }
                    try {
                        columMap.put(StringUtils.trimLeadingString("';",rs.getString("COLUMN_NAME")), co);
                    }catch (Exception ex) {
                    }



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


    public static boolean isTSimpleTypeWrapper(Class t) {
        if (TType.class.isAssignableFrom(t)) {
            return true;
        }
        return false;
    }

    /**
     * @param dbpoolName 数据库连接池名称
     * @param outerClass 映射对象的类
     * @param t          映射对象属性的类型
     * @param prefix     sql前缀
     * @param name       映射对象属性名称
     * @param rs         结果集对象
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object getValueFromResult(String dbpoolName, Class outerClass, Class t, String prefix,
                                            String name, ResultSet rs) {

        Object value = null;
        try {

            if (isTSimpleTypeWrapper(outerClass)) {
                Class innerType =
                        ((TType) outerClass.getConstructor().newInstance()).wrappedClass();
                t = innerType;
                long start = System.currentTimeMillis();
                value = rs.getObject(1);
                if (value == null) {
                    return null;
                } else {
                    if (NumberUtils.isNumber(value)) {
                        value = NumberUtils.convertNumberToTargetClass((Number) value, t);
                        return value;
                    }
                }
                if (t == String.class) {
                    value = rs.getString(1);
                } else if (t == Integer.class || t == int.class) {
                    value = rs.getObject(1, Integer.class);
                } else if (t == boolean.class || t == Boolean.class) {
                    value = rs.getObject(1, Boolean.class);
                } else if (t == Long.class || t == long.class) {
                    value = rs.getObject(1, Long.class);
                } else if (t == Short.class || t == short.class) {
                    value = rs.getObject(1, Short.class);
                } else if (t == Float.class || t == float.class) {
                    value = rs.getObject(1, Float.class);
                } else if (t == Double.class || t == double.class) {
                    value = rs.getObject(1, Double.class);
                } else if (t == Date.class) {
                    value = rs.getTimestamp(1);
                    if (value != null)
                        value = TypeMapSystem
                                .sqlTimestampTojavaDate((Timestamp) value);
                } else if (t == java.sql.Date.class) {
                    value = rs.getDate(1);
                } else if (t == java.sql.Timestamp.class) {
                    value = rs.getTimestamp(1);
                } else if (t == java.sql.Time.class) {
                    value = rs.getTime(1);
                } else if (t == LocalDate.class) {
                    value = rs.getDate(1);
                    if (value != null)
                        value = ((java.sql.Date) value).toLocalDate();
                } else if (t == LocalDateTime.class) {
                    value = rs.getTimestamp(1);
                    if (value != null)
                        value = ((Timestamp) value).toLocalDateTime();
                } else if (t == LocalTime.class) {
                    value = rs.getTime(1);
                    if (value != null)
                        value = ((Time) value).toLocalTime();
                } else if (t == Byte.class || t == byte.class) {
                    value = rs.getObject(1, Byte.class);
                } else if (t == char.class || t == Character.class) {
                    value = rs.getString(1);
                    if (value != null && value.toString().length() > 0) {
                        value = value.toString().charAt(0);
                    } else {
                        char c = '\0';
                        value = c;
                    }
                } else if (t == java.math.BigDecimal.class) {
                    value = rs.getBigDecimal(1);
                } else if (t == java.math.BigInteger.class) {
                    value = rs.getObject(1, Long.class);
                    if (value != null)
                        value = new java.math.BigInteger(Long.toString((Long) value));
                } else {
                    value = rs.getObject(1, t);
                }
            } else {
                long start = System.currentTimeMillis();
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
                value = rs.getObject(labelName);
                if (value == null) {
                    return null;
                } else {
                    if (NumberUtils.isNumber(value)) {
                        value = NumberUtils.convertNumberToTargetClass((Number) value, t);
                        return value;
                    }
                }

                if (t == String.class) {
                    value = rs.getString(labelName);
                } else if (t == Integer.class || t == int.class) {
                    value = rs.getObject(labelName, Integer.class);
                } else if (t == boolean.class || t == Boolean.class) {
                    value = rs.getObject(labelName, Boolean.class);
                } else if (t == Long.class || t == long.class) {
                    value = rs.getObject(labelName, Long.class);
                } else if (t == Short.class || t == short.class) {
                    value = rs.getObject(labelName, Short.class);
                } else if (t == Float.class || t == float.class) {
                    value = rs.getObject(labelName, Float.class);
                } else if (t == Double.class || t == double.class) {
                    value = rs.getObject(labelName, Double.class);
                } else if (t == Date.class) {
                    value = rs.getTimestamp(labelName);
                    if (value != null)
                        value = TypeMapSystem
                                .sqlTimestampTojavaDate((Timestamp) value);
                } else if (t == java.sql.Date.class) {
                    value = rs.getDate(labelName);
                } else if (t == java.sql.Timestamp.class) {
                    value = rs.getTimestamp(labelName);
                } else if (t == java.sql.Time.class) {
                    value = rs.getTime(labelName);
                } else if (t == LocalDate.class) {
                    value = rs.getDate(labelName);
                    if (value != null)
                        value = ((java.sql.Date) value).toLocalDate();
                } else if (t == LocalDateTime.class) {
                    value = rs.getTimestamp(labelName);
                    if (value != null)
                        value = ((Timestamp) value).toLocalDateTime();
                } else if (t == LocalTime.class) {
                    value = rs.getTime(labelName);
                    if (value != null)
                        value = ((Time) value).toLocalTime();
                } else if (t == Byte.class || t == byte.class) {
                    value = rs.getObject(labelName, Byte.class);
                } else if (t == char.class || t == Character.class) {
                    value = rs.getString(labelName);
                    if (value != null && value.toString().length() > 0) {
                        value = value.toString().charAt(0);
                    } else {
                        char c = '\0';
                        value = c;
                    }
                } else if (t == java.math.BigDecimal.class) {
                    value = rs.getBigDecimal(labelName);
                } else if (t == java.math.BigInteger.class) {
                    value = rs.getObject(labelName, Long.class);
                    if (value != null)
                        value = new java.math.BigInteger(Long.toString((Long) value));
                } else {
                    value = rs.getObject(labelName, t);
                }
            }

        } catch (Exception ex) {
            throw new DbException(ex);
        }
        return value;
    }


    public static void getValueFromCallableStatement(DBMS dbms, CallableStatement rs,
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

                value = rs.getObject(key);
                if (value == null) {
                    ///
                } else if (NumberUtils.isNumber(value)) {
                    value = NumberUtils.convertNumberToTargetClass((Number) value, t);
                } else {
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
                        if (value != null)
                            value = TypeMapSystem
                                    .sqlTimestampTojavaDate((Timestamp) value);
                    } else if (t == java.sql.Date.class) {
                        value = rs.getDate(key);
                    } else if (t == Timestamp.class) {
                        value = rs.getTimestamp(key);
                    } else if (t == Time.class) {
                        value = rs.getTime(key);
                    } else if (t == LocalDate.class) {
                        value = rs.getDate(key);
                        if (value != null)
                            value = ((java.sql.Date) value).toLocalDate();
                    } else if (t == LocalDateTime.class) {
                        value = rs.getTimestamp(key);
                        if (value != null)
                            value = ((Timestamp) value).toLocalDateTime();
                    } else if (t == LocalTime.class) {
                        value = rs.getTime(key);
                        if (value != null) {
                            value = ((Time) value).toLocalTime();
                        }
                    } else if (t == Byte.class || t == byte.class) {
                        value = rs.getByte(key);
                    } else if (t == char.class || t == Character.class) {
                        value = rs.getString(key);
                        if (value != null) {
                            if (value.toString().length() > 0) {
                                value = value.toString().charAt(0);
                            } else {
                                value = '\0';
                            }
                        }
                    } else if (t == java.math.BigDecimal.class) {
                        value = rs.getBigDecimal(key);
                    } else if (t == java.math.BigInteger.class) {
                        value = rs.getLong(key);
                        if (value != null) {
                            value = new java.math.BigInteger(Long.toString((Long) value));
                        }
                    } else if (t == DataBaseSet.class || t == ResultSet.class) {
                        value = rs.getObject(key);
                        ResultSet rss = null;
                        CachedRowSet crs = null;
                        try {
                            rss = (ResultSet) value;
                            crs = new CachedRowSetImpl();
                            crs.populate(rss);
                        } finally {
                            if (rss != null) {
                                rss.close();
                            }
                        }
                        if (t == ResultSet.class) value = crs;
                        else if (t == DataBaseSet.class) {
                            Object temp = new DataBaseSet(crs);
                            value = temp;
                        }
                    } else {
                        value = rs.getObject(key);

                    }
                }
                if (returnKeyValues != null)
                    returnKeyValues.put(key, value);
            }
        } catch (Exception ex) {
            throw new DbException(ex);
        }

    }

    public static String generateSelectSql(String dbpoolName,
                                           Object selectObject,
                                           Class reflectClass,
                                           String[] whereProperteis,
                                           Map<Integer, Object> returnvParameters,
                                           QueryOptions options,
                                           DBMS dataBaseType) throws Exception {

        String sql = "";
        String className = reflectClass.getSimpleName();
        String select = "select *";
        QueryHint queryHint = null;
        if (options != null) {
            queryHint = options.getQueryHint();

        }
        if (queryHint != null) {
            if (StringUtils.hasText(queryHint.select())) {
                select = "select " + queryHint.select().trim();
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
            if (queryHint != null) {
                if (StringUtils.hasText(queryHint.groupBy())) {
                    sql = sql + " group by " + queryHint.groupBy().trim();
                    if (StringUtils.hasText(queryHint.having())) {
                        sql = sql + " having " + queryHint.having().trim();
                    }
                }
                if (StringUtils.hasText(queryHint.orderBy())) {
                    sql = sql + " order by  " + queryHint.orderBy().trim();
                }

                if (queryHint.limit() != null) {
                    sql = dataBaseType.topN(sql, queryHint.limit());
                } else if (options != null && options.isLimitOne()) {
                    sql = dataBaseType.topN(sql, 1);
                }
            }
            return sql;
        } catch (Exception e) {
            throw new DbException(e);
        }

    }

    public static String generateSelectSqlBySelectObject(String dbpoolName, Object selectObject, Class reflectClass,
                                                         Map<Integer, Object> returnvParameters, QueryOptions options,
                                                         DBMS dataBaseType)
            throws Exception {

        String sql = "";
        String className = reflectClass.getSimpleName();
        String select = "select *";
        QueryHint queryHint = null;
        if (options != null) {
            queryHint = options.getQueryHint();

        }
        if (queryHint != null) {
            if (StringUtils.hasText(queryHint.select())) {
                select = "select " + queryHint.select().trim();
            }
        }
        sql = select + " from " +
                dbEscapeLef(dataBaseType) + getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType) + " ";
        try {
            int index = 1;
            String where = " where ";
            Map<String, TResult2<Method, Object>> map = PropertyUtil.describeForTypes(selectObject, reflectClass);
            Set<?> set = map.keySet();

            Iterator<?> inames = set.iterator();

            while (inames.hasNext()) {
                String name = (String) inames.next();

                TResult2<Method, Object> tr2 = map.get(name);
                Class<?> t = tr2.getFirstValue().getReturnType();
                Object value = null;

                // name为javabean属性名
                if (TypeMapSystem.checkedSimpleType(t)) {// 简单类型

                    value = tr2.getSecondValue();
                    if (value != null) {
                        where = where + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, name) + dbEscapeRight(dataBaseType) + "=? and ";
                        returnvParameters.put(index++, value);
                    } else {
                        continue;
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
            if (queryHint != null) {
                if (StringUtils.hasText(queryHint.groupBy())) {
                    sql = sql + " group by " + queryHint.groupBy().trim();
                    if (StringUtils.hasText(queryHint.having())) {
                        sql = sql + " having " + queryHint.having().trim();
                    }
                }
                if (StringUtils.hasText(queryHint.orderBy())) {
                    sql = sql + " order by  " + queryHint.orderBy().trim();
                }

                if (queryHint.limit() != null) {
                    sql = dataBaseType.topN(sql, queryHint.limit());
                } else if (options != null && options.isLimitOne()) {
                    sql = dataBaseType.topN(sql, 1);
                }
            }
            return sql.trim();
        } catch (Exception e) {
            throw new DbException(e);
        }

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


    public static String generateDeleteSqlByObject(String dbpoolName, Object deleteObject,
                                                   Class reflectClass,
                                                   Map<Integer, Object> returnvParameters,
                                                   UpdateOptions options, DBMS dataBaseType) throws Exception {

        if(dataBaseType.isClickHouseFamily()){
            throw new DbException(dataBaseType+"不支持对象删除操作，因为这些生成标准的update语句ClickHouse不支持，" +
                    "请在md文件里或在\"sql:\"前缀后填写ClickHouse特定的删除语句，语法如：" +
                    "ALTER TABLE [db.]table [ON CLUSTER cluster] DELETE WHERE filter_expr");
        }
        List<String> whereProperteis = new ArrayList<>();

        Map<String, TResult2<Method, Object>> map = PropertyUtil.describeForTypes(deleteObject, reflectClass);
        Set<?> set = map.keySet();

        Iterator<?> inames = set.iterator();
        int i = 0;
        while (inames.hasNext()) {
            String name = (String) inames.next();
            TResult2<Method, Object> tr2 = map.get(name);
            Class<?> t = tr2.getFirstValue().getReturnType();
            Object value = null;

            // name为javabean属性名
            if (TypeMapSystem.checkedSimpleType(t)) {// 简单类型
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
        proName = DBColum2JavaKyewordTool.toDbColumName(proName);
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

        if(dataBaseType.isClickHouseFamily()){
            throw new DbException(dataBaseType+"不支持对象删除操作，因为这些生成标准的update语句ClickHouse不支持，" +
                    "请在md文件里或在\"sql:\"前缀后填写ClickHouse特定的删除语句，语法如：" +
                    "ALTER TABLE [db.]table [ON CLUSTER cluster] DELETE WHERE filter_expr");
        }
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
     * 根据对象生成更新的SQL语句
     *
     * @param dbpoolName        数据库连接池
     * @param properties        需更新的属性，如果为空，表明更新所有主键属性以外的属性
     * @param updateObject      需更新的对象
     * @param reflectClass      如果不为null，则使用它生成表名，用于javaBean存在继承时指定哪个父类用于生成表名
     * @param whereProperteis   updateObject对象里为主键属性名，复合主键以","隔开，主键属性用于唯一标示一个对象，通过它生成where语句
     * @param returnvParameters 生成的参数，每个参数对应于生成语句的一个"?"字符
     * @param options           更新选项
     * @param ignoreNull        是否忽略为空的属性，即如果属性为空，则不生成对应属性的sql
     * @param dataBaseType      DBMS对象
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
            throw new DbException("whereProperteis为空");
        }
        if(dataBaseType.isClickHouseFamily()){
            throw new DbException(dataBaseType+"不支持对象更新操作，因为这些生成标准的update语句ClickHouse不支持，" +
                    "请在md文件里或在\"sql:\"前缀后填写ClickHouse特定的更新语句，语法如：" +
                    "ALTER TABLE [db.]table [ON CLUSTER cluster] UPDATE column1 = expr1 [, ...] WHERE filter_expr");
        }
        String sql = "";
        String className = reflectClass.getSimpleName();
        sql = "update " + dbEscapeLef(dataBaseType) + getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType) + " ";
        String colPart = "set ";
        String[] keys = whereProperteis;
        try {
            Map<String, TResult2<Method, Object>> map = PropertyUtil.describeForTypes(updateObject, reflectClass);
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

                if (ArrayUtils.containsIgnoreCase(keys, name)) {
                    continue;
                }

                Class<?> t = map.get(name).getFirstValue().getReturnType();

                // name为javabean属性名
                if (TypeMapSystem.checkedSimpleType(t)) {// 简单类型
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
                where = where + dbEscapeLef(dataBaseType)+ getColumName(dbpoolName, keys[n]) +dbEscapeRight(dataBaseType) + "=?";
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
     *
     * @param dbpoolName        数据库连接池名称
     * @param properties        哪些属性需要更新
     * @param insertObject      需插入的对象
     * @param reflectClass      真实插入对象的class，用于在javaBean存在继承时，使用哪个继承层次的类名作为表名
     * @param returnvParameters 生成的参数，每个参数对应于生成语句的相应位置的"?"字符
     * @param options           更新选项
     * @param ignoreNull        是否忽略properties属性里空值的属性更新到数据库
     * @param dataBaseType      DBMS对象
     * @return 回根据对象生成的插入SQL语句
     * @throws Exception 异常
     */
    public static String generateInsertSql(String dbpoolName, String[] properties,
                                           Object insertObject, Class reflectClass, Map<Integer, Object> returnvParameters,
                                           UpdateOptions options, boolean ignoreNull, DBMS dataBaseType) throws Exception {
        String sql = "";

        String className = reflectClass.getSimpleName();

        String prependSql = "";
        sql = "insert into " + dbEscapeLef(dataBaseType) + getTableName(dbpoolName, className) + dbEscapeRight(dataBaseType);
        String colPart = "(";
        String values = "values(";

        try {
            Map<String, TResult2<Method, Object>> map = PropertyUtil.describeForTypes(insertObject, reflectClass);
            Set<?> set = map.keySet();
            Iterator<?> i = set.iterator();
            int index = 1;

            while (i.hasNext()) {

                String name = (String) i.next();

                if (properties != null
                        && !ArrayUtils.containsIgnoreCase(properties, name)) {
                    continue;
                }

                TResult2<Method, Object> tr2 = map.get(name);
                Class<?> t = tr2.getFirstValue().getReturnType();

                // name为javabean属性名
                if (TypeMapSystem.checkedSimpleType(t)) {// 简单类型

                    Object colValue = tr2.getSecondValue();
                    AkaColumn akaColumn = tr2.getFirstValue().getAnnotation(AkaColumn.class);

                    if (options != null) {
                        GenerateID generateID = (GenerateID) options.getGenerateID();
                        if (generateID != null) {
                            if (generateID.getIdName() != null &&
                                    generateID.getIdName().equals(name)) {
                                colValue = generateID.getIdValue();
                            }
                        } else {
                            if (akaColumn != null && akaColumn.isAutoincrement()) {
                                generateID = new GenerateID();
                                generateID.setIdName(name);
                                options.setGenerateID(generateID);
                            }
                        }

                    }

                    if (colValue == null) {
                        if (ignoreNull) {
                            continue;
                        } else {
                            if (akaColumn != null) {
                                if (akaColumn.isAutoincrement()) {
                                    continue;
                                }
                                if (!akaColumn.isNullable()) {
                                    throw new DbException(getTableName(dbpoolName, className) + "表里的列" + getColumName(dbpoolName, name) + "不能插入空值！");
                                }
                            }

                            colPart = colPart + dbEscapeLef(dataBaseType) + getColumName(dbpoolName, name) + dbEscapeRight(dataBaseType);
                            values = values + "null";
                            colPart = colPart + ",";
                            values = values + ",";
                            continue;
                        }

                    } else {
                        if (akaColumn != null && akaColumn.isAutoincrement()) {
                            if (dataBaseType.isSQLServerFamily()) {
                                prependSql = "SET IDENTITY_INSERT " + getTableName(dbpoolName, className) + " ON;";
                            }
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
            if (!prependSql.isEmpty()) {
                sql = prependSql + sql;
            }
            return sql;
        } catch (Exception e) {
            throw e;
        }

    }

    public static String registForStoredProc(Map<Integer, Object> vParameters,
                                             CallableStatement preStmt,DBMS dbms) throws SQLException {
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
                preStmt.registerOutParameter(key, TypeMapSystem.javaType2sql((Class) val,dbms));
                paramStr = paramStr + "[" + key + ":" + val + "]";

            }
        }
        return paramStr;

    }

    /**
     * 用于设置执行存储过程PreparedStatement的参数
     *
     * @param vParameters 参数
     * @param preStmt     PreparedStatement对象
     * @return 返回调试的字符串
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
     *
     * @param sql         带?的sql语句
     * @param vParameters 参数
     * @param dbms        数据方言
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
                } else if (obj instanceof java.sql.Timestamp) {
                    preStmt.setTimestamp(j, (Timestamp) obj);
                    paramStr = paramStr + "[" + j + ":" + obj.toString() + "]";
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
                }  else {
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
     * 把rs的一行转换成一个javabean
     *
     * @param dbpoolName 数据库连接池
     * @param clazz      ResultSet的每行数据映射到类型为clazz的对象
     * @param rs         需要抽取数据的ResultSet
     * @param <T>        泛型
     * @return 返回行数据映射的对象
     */
    public static <T> T getBeanFromResultSet(String dbpoolName, Class<T> clazz, ResultSet rs) {

        try {

            T bean = clazz.getDeclaredConstructor().newInstance();

            Map<String, TResult2<Method, Object>> map = PropertyUtil.describeForTypes(bean, bean.getClass());
            Set<?> set = map.keySet();
            Iterator<?> i = set.iterator();

            while (i.hasNext()) {

                String name = (String) i.next();
                Class<?> t = map.get(name).getFirstValue().getReturnType();
                Object value = null;
                // name为javabean属性名
                if (TypeMapSystem.checkedSimpleType(t)) {// 简单类型

                    try {
                        value = SqlUtils.getValueFromResult(dbpoolName, clazz, t, "", name, rs);
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
            throw new DbException(e);
        }

    }

    public static String encodeSQLStr(String sqlStr) {
        if (sqlStr == null) {
            return null;
        }
        return StringUtils.replace(sqlStr, "'", "''");
    }


    public static DataBase.SQLType decideSqlType(String sqltxt) throws DbException {
        sqltxt = StringUtils.trim(sqltxt);
        //删除/* */
        int start=sqltxt.indexOf("*/");
        String newSqltxt=sqltxt;
        if(start>2) {
            newSqltxt = sqltxt.substring(sqltxt.indexOf("*/")+2);
            newSqltxt=newSqltxt.trim();
        }
        if (StringUtils.startsWithIgnoreCase(newSqltxt, "select")) {
            return DataBase.SQLType.SELECT;
        } else if (StringUtils.startsWithIgnoreCase(newSqltxt, "insert")) {
            return DataBase.SQLType.INSERT;
        } else if (StringUtils.startsWithIgnoreCase(newSqltxt, "update")
                || StringUtils.startsWithIgnoreCase(newSqltxt, "create")
                || StringUtils.startsWithIgnoreCase(newSqltxt, "drop")
                || StringUtils.startsWithIgnoreCase(newSqltxt, "alter")
                || StringUtils.startsWithIgnoreCase(newSqltxt, "truncate")
        ) {
            return DataBase.SQLType.UPDATE;
        } else if (StringUtils.startsWithIgnoreCase(newSqltxt, "delete")) {
            return DataBase.SQLType.DELETE;
        } else if (StringUtils.startsWithIgnoreCase(newSqltxt, "{")
                && StringUtils.endsWithIgnoreCase(newSqltxt, "}")
                && StringUtils.indexOf(newSqltxt, "[{=]\\s*call\\s+", true) >= 0) {
            return DataBase.SQLType.STORE_DPROCEDURE;

        } else {
            return DataBase.SQLType.OTHER;
        }
    }
}
