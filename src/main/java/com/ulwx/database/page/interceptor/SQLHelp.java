package com.ulwx.database.page.interceptor;

import com.ulwx.database.page.dialect.Dialect;
import com.ulwx.database.page.dialect.DialectClient;
import com.ulwx.database.page.model.DBMS;
import com.ulwx.tool.PageBean;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 上午11:30
 * @since JDK 1.5
 */
public class SQLHelp {




    /**
     * 根据数据库方言，生成特定的分页sql
     *
     * @param sql  Mapper中的Sql语句
     * @param page 分页对象
     * @param dbms 方言类型
     * @return 分页SQL
     */
    public static String generatePageSql(String sql, PageBean page, DBMS dbms) {
        Dialect dialect = DialectClient.getDbmsDialect(dbms);
        if (dialect.supportsLimit()) {
            int pageSize = page.getPerPage();
            int start=page.getStart();
            return dialect.getLimitString(sql, start, pageSize);
        } else {
            return sql;
        }
    }
}
