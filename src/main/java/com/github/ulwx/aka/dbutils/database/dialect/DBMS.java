package com.github.ulwx.aka.dbutils.database.dialect;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.dialect.page.DialectPageSqlTemplate;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.CTime;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public enum DBMS {

    DamengDialect,// equal to InformixDialect
    GBaseDialect,// equal to Oracle8iDialect
    // Below dialects found on Internet
    AccessDialect, //
    CobolDialect, //
    DbfDialect, //
    ExcelDialect, //
    ParadoxDialect, //
    SQLiteDialect, //
    TextDialect, //
    XMLDialect, //
    // Below dialects imported from Hibernate
    Cache71Dialect,//
    CUBRIDDialect,//
    DataDirectOracle9Dialect,//
    DB2390Dialect,//
    DB2390V8Dialect,//
    DB2400Dialect,//
    DB297Dialect,//
    DB2Dialect,//
    DerbyDialect,
    DerbyTenFiveDialect,//
    DerbyTenSevenDialect,//
    DerbyTenSixDialect,//
    FirebirdDialect,//
    FrontBaseDialect,//
    H2Dialect,//
    HANAColumnStoreDialect,//
    HANARowStoreDialect,//
    HSQLDialect,//
    Informix10Dialect,//
    InformixDialect,//
    Ingres10Dialect,//
    Ingres9Dialect,//
    IngresDialect,//
    InterbaseDialect,//
    JDataStoreDialect,//
    MariaDB102Dialect,//
    MariaDB103Dialect,//
    MariaDB10Dialect,//
    MariaDB53Dialect,//
    MariaDBDialect,//
    MckoiDialect,//
    MimerSQLDialect,//
    MySQL55Dialect,//
    MySQL57Dialect,//
    MySQL57InnoDBDialect,//
    MySQL5Dialect,//
    MySQL5InnoDBDialect,//
    MySQL8Dialect,//
    MySQLDialect,//
    MySQLInnoDBDialect,//
    MySQLMyISAMDialect,//
    Oracle10gDialect,//
    Oracle12cDialect,//
    Oracle8iDialect,//
    Oracle9iDialect,//
    PointbaseDialect,//
    PostgresPlusDialect,//
    PostgreSQL81Dialect,//
    PostgreSQL82Dialect,//
    PostgreSQL91Dialect,//
    PostgreSQL92Dialect,//
    PostgreSQL93Dialect,//
    PostgreSQL94Dialect,//
    PostgreSQL95Dialect,//
    PostgreSQL9Dialect,//
    PostgreSQLDialect,//
    ProgressDialect,//
    RDMSOS2200Dialect,//
    SAPDBDialect,//
    SQLServer2005Dialect,//
    SQLServer2008Dialect,//
    SQLServer2012Dialect,//
    SQLServerDialect,//
    Sybase11Dialect,//
    SybaseAnywhereDialect,//
    SybaseASE157Dialect,//
    SybaseASE15Dialect,//
    SybaseDialect,//
    Teradata14Dialect,//
    TeradataDialect,//
    TimesTenDialect,//
    UnsupportDialect;


    private static final String SKIP_ROWS = "$SKIP_ROWS";
    private static final String PAGESIZE = "$PAGESIZE";
    private static final String TOTAL_ROWS = "$TOTAL_ROWS";
    private static final String DISTINCT_TAG = "($DISTINCT)";
    private String sqlTemplate = null;
    private String topLimitTemplate = null;
    private DBType dbType = null;
    private String checkSql = "select 1";

    static {
        for (DBMS d : DBMS.values()) {
            d.sqlTemplate = DialectPageSqlTemplate.initializePaginSQLTemplate(d);
            d.topLimitTemplate = DialectPageSqlTemplate.initializeTopLimitSqlTemplate(d);
            d.decideDBType();
        }
    }

    public DBType getDbType() {
        return dbType;
    }

    public String getCheckSql() {
        return this.checkSql;
    }

    private void decideDBType() {
        if (isMySqlFamily()) {
            dbType = DBType.MYSQL;
        } else if (isInfomixFamily()) {
            dbType = DBType.INFOMIX;
        } else if (isOracleFamily()) {
            dbType = DBType.ORACLE;
            this.checkSql = "select 1 from dual";
        } else if (isSQLServerFamily()) {
            dbType = DBType.MS_SQL_SERVER;
        } else if (isH2Family()) {
            dbType = DBType.H2;
        } else if (isPostgresFamily()) {
            dbType = DBType.POSTGRE;
        } else if (isSybaseFamily()) {
            dbType = DBType.SYBASE;
        } else if (isDB2Family()) {
            dbType = DBType.DB2;
            this.checkSql = "select 1 from sysibm.sysdummy1";
        } else if (isDerbyFamily()) {
            dbType = DBType.DERBY;
            this.checkSql = "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        } else if (isSQLiteFamily()) {
            dbType = DBType.SQLITE;
        } else if (isHSQLFamily()) {
            dbType = DBType.HSQL;
            this.checkSql = "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        } else {
            dbType = DBType.OTHER;
        }
    }

    public String encodeForSQL(String str) {
        return SqlUtils.encodeSQLStr(str);
    }

    public String javaObjToSqlValue(Object obj) {
        String ret = null;
        if (obj == null) {
            return "" + obj + "";
        } else if (obj instanceof Boolean) {
            return "'" + obj.toString() + "'";
        } else if (obj instanceof Character) {
            return "'" + obj.toString() + "'";
        } else if (obj instanceof String) {
            return "'" + encodeForSQL(obj.toString()) + "'";
        } else if (obj instanceof Integer) {
            return obj.toString();
        } else if (obj instanceof Long) {
            return obj.toString();
        } else if (obj instanceof java.math.BigDecimal) {
            return obj.toString();
        } else if (obj instanceof java.math.BigInteger) {
            return obj.toString();
        } else if (obj instanceof Float) {
            return obj.toString();
        } else if (obj instanceof Double) {
            return obj.toString();
        } else if (obj instanceof java.sql.Date) {
            return javaDateToSqlValue(new Date(
                    ((java.sql.Date) obj).getTime()));
        } else if (obj instanceof Timestamp) {
            LocalDateTime localDateTime = ((Timestamp) obj).toLocalDateTime();
            return javaDateToSqlValue(localDateTime);
        } else if (obj instanceof Time) {
            LocalTime localTime = ((Time) obj).toLocalTime();
            return javaDateToSqlValue(localTime);
        } else if (obj instanceof Date) {
            return javaDateToSqlValue(obj);
        } else if (obj instanceof LocalDate) {
            return javaDateToSqlValue(obj);
        } else if (obj instanceof LocalDateTime) {
            return javaDateToSqlValue(obj);
        } else if (obj instanceof LocalTime) {
            return javaDateToSqlValue(obj);
        } else if (obj instanceof Class) {
            return "?:" + ((Class) obj).getName();
        } else {
            //防止注入式攻击
            return "'" + encodeForSQL(obj.toString()) + "'";
        }
    }


    private String javaDateToSqlValue(Object dateObj) {
        switch (this.dbType) {
            case MS_SQL_SERVER:
                if (dateObj instanceof Date) {
                    String str = "'" + CTime.formatWholeDate((Date) dateObj) + "'";
                    return "CONVERT(datetime," + str + ",20)";
                } else if (dateObj instanceof LocalDate) {
                    String str = "'" + CTime.formatLocalDate((LocalDate) dateObj) + "'";
                    return "CONVERT(date," + str + ",23)";
                } else if (dateObj instanceof LocalDateTime) {
                    String str = "'" + ((LocalDateTime) dateObj).format(CTime.DTF_YMD_HH_MM_SS) + "'";
                    return "CONVERT(datetime," + str + ",20)";
                } else if (dateObj instanceof LocalTime) {
                    String str = "'" + ((LocalTime) dateObj).format(CTime.DTF_HH_MM_SS) + "'";
                    return "CONVERT(time," + str + ",24)";
                } else {
                }
                break;
            case ORACLE:
            case HSQL:
            case INFOMIX:
            case POSTGRE:
                if (dateObj instanceof Date) {
                    String str = "'" + CTime.formatWholeDate((Date) dateObj) + "'";
                    return "to_date(" + str + ",'yyyy-mm-dd hh24:mi:ss')";
                } else if (dateObj instanceof LocalDate) {
                    String str = "'" + CTime.formatLocalDate((LocalDate) dateObj) + "'";
                    return "to_date(" + str + ",'yyyy-mm-dd')";
                } else if (dateObj instanceof LocalDateTime) {
                    String str = "'" + ((LocalDateTime) dateObj).format(CTime.DTF_YMD_HH_MM_SS) + "'";
                    return "to_date(" + str + ",'yyyy-mm-dd hh24:mi:ss')";
                } else if (dateObj instanceof LocalTime) {
                    String str = "'" + ((LocalTime) dateObj).format(CTime.DTF_HH_MM_SS) + "'";
                    return "to_date(" + str + ",'hh24:mi:ss')";
                } else {
                }
                break;
            case H2:
                if (dateObj instanceof Date) {
                    String str = "'" + CTime.formatWholeDate((Date) dateObj) + "'";
                    return "parsedatetime(" + str + ",'dd-MM-yyyy hh:mm:ss')";
                } else if (dateObj instanceof LocalDate) {
                    String str = "'" + CTime.formatLocalDate((LocalDate) dateObj) + "'";
                    return "parsedatetime(" + str + ",'dd-MM-yyyy')";
                } else if (dateObj instanceof LocalDateTime) {
                    String str = "'" + ((LocalDateTime) dateObj).format(CTime.DTF_YMD_HH_MM_SS) + "'";
                    return "parsedatetime(" + str + ",'dd-MM-yyyy hh:mm:ss')";
                } else if (dateObj instanceof LocalTime) {
                    String str = "'" + ((LocalTime) dateObj).format(CTime.DTF_HH_MM_SS) + "'";
                    return "parsedatetime(" + str + ",'hh:mm:ss')";
                } else {
                }
                break;
            case SQLITE:
            case DERBY:
                if (dateObj instanceof Date) {
                    String str = "'" + CTime.formatWholeDate((Date) dateObj) + "'";
                    return "datetime(" + str + ")";
                } else if (dateObj instanceof LocalDate) {
                    String str = "'" + CTime.formatLocalDate((LocalDate) dateObj) + "'";
                    return "date(" + str + ")";
                } else if (dateObj instanceof LocalDateTime) {
                    String str = "'" + ((LocalDateTime) dateObj).format(CTime.DTF_YMD_HH_MM_SS) + "'";
                    if (this.dbType == DBType.DERBY) {
                        return "timestamp(" + str + ")";
                    } else {
                        return "datetime(" + str + ")";
                    }
                } else if (dateObj instanceof LocalTime) {
                    String str = "'" + ((LocalTime) dateObj).format(CTime.DTF_HH_MM_SS) + "'";
                    return "time(" + str + ")";
                } else {
                }
                break;
            case SYBASE:
            case MYSQL:
            case DB2:
            case OTHER:
            default:
                if (dateObj instanceof Date) {
                    return "'" + CTime.formatWholeDate((Date) dateObj) + "'";
                } else if (dateObj instanceof LocalDate) {
                    return "'" + CTime.formatLocalDate((LocalDate) dateObj) + "'";
                } else if (dateObj instanceof LocalDateTime) {
                    return "'" + ((LocalDateTime) dateObj).format(CTime.DTF_YMD_HH_MM_SS) + "'";
                } else if (dateObj instanceof LocalTime) {
                    return "'" + ((LocalTime) dateObj).format(CTime.DTF_HH_MM_SS) + "'";
                } else {

                }
                break;
        }
        throw new DbException(dateObj + "[" + dateObj.getClass().getName() +
                "]不为日期类型！");
    }

    /**
     * @return true if is MySql family
     */
    public boolean isMySqlFamily() {
        return this.toString().startsWith("MySQL");
    }

    /**
     * @return true if is Infomix family
     */
    public boolean isInfomixFamily() {
        return this.toString().startsWith("Infomix");
    }

    /**
     * @return true if is Oracle family
     */
    public boolean isOracleFamily() {
        return this.toString().startsWith("Oracle");
    }

    /**
     * @return true if is SQL Server family
     */
    public boolean isSQLServerFamily() {
        return this.toString().startsWith("SQLServer");
    }

    /**
     * @return true if is H2 family
     */
    public boolean isH2Family() {
        return this.toString().startsWith("H2");
    }

    /**
     * @return true if is Postgres family
     */
    public boolean isPostgresFamily() {
        return this.toString().startsWith("Postgres");
    }

    /**
     * @return true if is Sybase family
     */
    public boolean isSybaseFamily() {
        return this.toString().startsWith("Sybase");
    }

    /**
     * @return true if is DB2 family
     */
    public boolean isDB2Family() {
        return this.toString().startsWith("DB2");
    }

    /**
     * @return true if is Derby family
     */
    public boolean isDerbyFamily() {
        return this.toString().startsWith("Derby");
    }

    public boolean isSQLiteFamily() {
        return this.toString().startsWith("SQLite");
    }

    public boolean isHSQLFamily() {
        return this.toString().startsWith("HSQL");
    }

    public String pageSQL(String sql, int pageNumber, int pageSize) {// NOSONAR
        String result = null;
        Assert.hasText(sql, "sql string can not be empty");
        String trimedSql = sql.trim();
        Assert.hasText(trimedSql, "sql string can not be empty");

        if (!StringUtils.startsWithIgnoreCase(trimedSql, "select "))
            throw new DbException(trimedSql + ",SQL should start with \"select \".");
        String body = trimedSql.substring(7).trim();
        Assert.hasText(body, "SQL body can not be empty");

        int skipRows = (pageNumber - 1) * pageSize;
        int totalRows = pageNumber * pageSize;
        String useTemplate;
        if (skipRows == 0) {
            useTemplate = topLimitTemplate;
            if (SQLServer2012Dialect.equals(this) && !StringUtils.containsIgnoreCase(trimedSql, "order by "))
                useTemplate = SQLServer2005Dialect.topLimitTemplate;
        } else {
            useTemplate = sqlTemplate;
            if (SQLServer2012Dialect.equals(this) && !StringUtils.containsIgnoreCase(trimedSql, "order by "))
                useTemplate = SQLServer2005Dialect.sqlTemplate;
        }

        if (StringUtils.isEmpty(useTemplate)) {
            return null;//表示不支持分页
        }

        if (useTemplate.contains(DISTINCT_TAG)) {
            // if distinct template use non-distinct sql, delete distinct tag
            if (!StringUtils.startsWithIgnoreCase(body, "distinct "))
                useTemplate = StringUtils.replace(useTemplate, DISTINCT_TAG, "");
            else {
                // if distinct template use distinct sql, use it
                useTemplate = StringUtils.replace(useTemplate, DISTINCT_TAG, "distinct");
                body = body.substring(9);
            }
        }

        // if have $XXX tag, replaced by real values
        // StringUtils.rep
        result = StringUtils.replaceIgnoreCase(useTemplate, SKIP_ROWS, String.valueOf(skipRows));
        result = StringUtils.replaceIgnoreCase(result, PAGESIZE, String.valueOf(pageSize));
        result = StringUtils.replaceIgnoreCase(result, TOTAL_ROWS, String.valueOf(totalRows));

        // now insert the customer's real full SQL here
        result = StringUtils.replace(result, "$SQL", trimedSql);

        // or only insert the body without "select "
        result = StringUtils.replace(result, "$BODY", body);
        return result;
    }

    public String tableCommentSql(String dbName){
        String sql="";
        if (this.isMySqlFamily()) {
            sql = "SELECT TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES "
                    + "WHERE TABLE_NAME=? AND TABLE_SCHEMA='"+dbName+"'";

        }else if (this.isSQLServerFamily()) {
            sql="select c.name,cast(isnull(f.[value], '') as nvarchar(100)) as TABLE_COMMENT " +
                    "from sys.objects c left join sys.extended_properties f on f.major_id=c.object_id " +
                    "and f.minor_id=0 and f.class=1 where c.type='u' and c.name=?";

        }else{

        }
        return sql;
    }

    public String colCommentSql(){
        String sql="";
        if (this.isMySqlFamily()) {

        }else if (this.isSQLServerFamily()) {
            sql="SELECT " +
                    "A.name AS TABLE_NAME," +
                    "B.name AS COLUMN_NAME," +
                    "CONVERT(nvarchar,  C.value) AS COLUMN_DESCRIPTION" +
                    "　FROM sys.tables A" +
                    "　INNER JOIN sys.columns B ON B.object_id = A.object_id" +
                    "　LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id" +
                    "　WHERE A.name = ?";

        }else{

        }
        return sql;
    }

    public static void main(String[] args) {

    }

}
