/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:DialectClient.java  2011-11-18 下午2:54 poplar.yfyang ]
 */
package com.github.ulwx.aka.dbutils.database.dialect;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * <p>
 * 数据库分页方言获取类.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午2:54
 * @since JDK 1.5
 */
public class DialectClient implements Serializable {
    private static final long serialVersionUID = 8107330250767760951L;

    public static DBMS decideDialect(Connection conn) throws Exception {
        DatabaseMetaData meta = conn.getMetaData();
        String driverName = meta.getDriverName();
        String databaseName = meta.getDatabaseProductName();
        int majorVersion = meta.getDatabaseMajorVersion();
        int minorVersion = meta.getDatabaseMinorVersion();
        return decideDialect(driverName, databaseName, majorVersion, minorVersion);

    }

    public static DBMS decideDialect(String driverName, String databaseName,
                                     Integer majorVersion, int minorVersion) {
        if ("CUBRID".equalsIgnoreCase(databaseName))
            return DBMS.CUBRIDDialect;
        else if("ClickHouse".equalsIgnoreCase(databaseName)){
            return DBMS.ClickHouseDialect;
        }
        else if ("HSQL Database Engine".equals(databaseName))
            return DBMS.HSQLDialect;
        else if ("H2".equals(databaseName))
            return DBMS.H2Dialect;
        if ("MySQL".equals(databaseName)) {
            if (majorVersion < 5)
                return DBMS.MySQLDialect;
            else if (majorVersion == 5) {
                if (minorVersion < 5)
                    return DBMS.MySQL5Dialect;
                else if (minorVersion < 7)
                    return DBMS.MySQL55Dialect;
                else
                    return DBMS.MySQL57Dialect;
            }
            return DBMS.MySQL57Dialect;
        }
        if (driverName != null && driverName.startsWith("MariaDB")) {
            if (majorVersion == 10) {
                if (minorVersion >= 3)
                    return DBMS.MariaDB103Dialect;
                else if (minorVersion == 2)
                    return DBMS.MariaDB102Dialect;
                else if (minorVersion >= 0)
                    return DBMS.MariaDB10Dialect;
                return DBMS.MariaDB53Dialect;
            } else if (majorVersion > 5 || (majorVersion == 5 && minorVersion >= 3)) {
                return DBMS.MariaDB53Dialect;
            }
            return DBMS.MariaDBDialect;
        }
        if ("PostgreSQL".equals(databaseName)) {
            if (majorVersion == 9) {
                if (minorVersion >= 4) {
                    return DBMS.PostgreSQL94Dialect;
                } else if (minorVersion >= 2) {
                    return DBMS.PostgreSQL92Dialect;
                }
                return DBMS.PostgreSQL9Dialect;
            }else if (majorVersion == 8 && minorVersion >= 2) {
                return DBMS.PostgreSQL82Dialect;
            }else if(majorVersion>=10){
                return DBMS.PostgreSQL10Dialect;
            }
            return DBMS.PostgreSQL81Dialect;
        }
        if ("EnterpriseDB".equals(databaseName))
            return DBMS.PostgresPlusDialect;
        if ("Apache Derby".equals(databaseName)) {
            if (majorVersion > 10 || (majorVersion == 10 && minorVersion >= 7))
                return DBMS.DerbyTenSevenDialect;
            else if (majorVersion == 10 && minorVersion == 6)
                return DBMS.DerbyTenSixDialect;
            else if (majorVersion == 10 && minorVersion == 5)
                return DBMS.DerbyTenFiveDialect;
            else
                return DBMS.DerbyDialect;
        }
        if ("ingres".equalsIgnoreCase(databaseName)) {
            switch (majorVersion) {
                case 9:
                    if (minorVersion > 2)
                        return DBMS.Ingres9Dialect;
                    else
                        return DBMS.IngresDialect;
                case 10:
                    return DBMS.Ingres10Dialect;
                default:
            }
            return DBMS.IngresDialect;
        }
        if (databaseName.startsWith("Microsoft SQL Server")) {
            switch (majorVersion) {
                case 8:
                    return DBMS.SQLServerDialect;
                case 9:
                    return DBMS.SQLServer2005Dialect;
                case 10:
                    return DBMS.SQLServer2008Dialect;
                case 11:
                case 12:
                case 13:
                    return DBMS.SQLServer2012Dialect;
                default:
                    if (majorVersion < 8)
                        return DBMS.SQLServerDialect;
                    else
                        return DBMS.SQLServer2012Dialect;
            }
        }
        if ("Sybase SQL Server".equals(databaseName) || "Adaptive Server Enterprise".equals(databaseName))
            return DBMS.SybaseASE15Dialect;
        if (databaseName.startsWith("Adaptive Server Anywhere"))
            return DBMS.SybaseAnywhereDialect;
        if ("Informix Dynamic Server".equals(databaseName))
            return DBMS.InformixDialect;
        if ("DB2 UDB for AS/400".equals(databaseName))
            return DBMS.DB2400Dialect;
        if (databaseName.startsWith("DB2/"))
            return DBMS.DB2Dialect;
        if ("Oracle".equals(databaseName)) {
            switch (majorVersion) {
                case 12:
                    return DBMS.Oracle12cDialect;
                case 11:
                case 10:
                    return DBMS.Oracle10gDialect;
                case 9:
                    return DBMS.Oracle9iDialect;
                case 8:
                    return DBMS.Oracle8iDialect;
                default:
            }
            return DBMS.Oracle12cDialect;
        }
        if ("HDB".equals(databaseName))
            return DBMS.HANAColumnStoreDialect;
        if (databaseName.startsWith("Firebird"))
            return DBMS.FirebirdDialect;
        if (StringUtils.containsIgnoreCase(databaseName, "sqlite"))
            return DBMS.SQLiteDialect;
        return null;
    }


}
