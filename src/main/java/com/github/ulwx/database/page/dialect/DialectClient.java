/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:DialectClient.java  2011-11-18 下午2:54 poplar.yfyang ]
 */
package com.github.ulwx.database.page.dialect;

import com.github.ulwx.database.page.dialect.db.*;
import com.github.ulwx.database.page.model.DBMS;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<DBMS, Dialect> DBMS_DIALECT = new ConcurrentHashMap<DBMS, Dialect>();
 
    /**
     * 根据数据库名称获取数据库分页查询的方言实现。
     *
     * @param dbms 数据库名称
     * @return 数据库分页方言实现
     */
    public static Dialect getDbmsDialect(DBMS dbms) {
        if (DBMS_DIALECT.containsKey(dbms)) {
            return DBMS_DIALECT.get(dbms);
        }
        Dialect dialect = createDbmsDialect(dbms);
        DBMS_DIALECT.put(dbms, dialect);
        return dialect;
    }



    /**
     * 创建数据库方言
     *
     * @param dbms 数据库
     * @return 数据库
     */
    private static Dialect createDbmsDialect(DBMS dbms) {
        switch (dbms) {
            case MYSQL:
                return new MySQLDialect();
            case ORACLE:
                return new OracleDialect();
            case DB2:
                return new DB2Dialect();
            case POSTGRE:
                return new PostgreSQLDialect();
            case SQL_SERVER:
                return new SQLServerDialect();
            case SQL_SERVER_2005:
                return new SQLServer2005Dialect();
            case SYBASE:
                return new SybaseDialect();
            case H2:
                return new H2Dialect();
            case HSQL:
                return new HSQLDialect();
            default:
                throw new UnsupportedOperationException("数据库方言不能为空");
        }
    }


}
