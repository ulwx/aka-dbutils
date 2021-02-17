package com.github.ulwx.aka.dbutils.database.dialect;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.dialect.page.DialectPageSqlTemplate;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;

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
    private DBType dbType=null;
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

    private void decideDBType(){
        if(isMySqlFamily()){
            dbType=DBType.MYSQL;
        }else if(isInfomixFamily()){
            dbType=DBType.INFOMIX;
        }else if(isOracleFamily()){
            dbType=DBType.ORACLE;
        }else if(isSQLServerFamily()){
            dbType=DBType.MS_SQL_SERVER;
        }else if(isH2Family()){
            dbType=DBType.H2;
        }else if(isPostgresFamily()){
            dbType=DBType.POSTGRE;
        }else if(isSybaseFamily()){
            dbType=DBType.SYBASE;
        }else if(isDB2Family()){
            dbType=DBType.DB2;
        }else if(isDerbyFamily()){
            dbType=DBType.DERBY;
        }else if (isSQLiteFamily()){
            dbType=DBType.SQLITE;
        }else if(isHSQLFamily()) {
            dbType=DBType.HSQL;
        }else{
            dbType=DBType.OTHER;
        }
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
    public boolean isSQLServerFamily(){
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
    public String pageSQL(String sql,int pageNumber, int pageSize) {// NOSONAR
        String result = null;
        Assert.hasText(sql, "sql string can not be empty");
        String trimedSql = sql.trim();
        Assert.hasText(trimedSql, "sql string can not be empty");

        if (!StringUtils.startsWithIgnoreCase(trimedSql, "select "))
            throw new DbException(trimedSql+",SQL should start with \"select \".");
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

    public static void main(String[] args) {

    }

}
