

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset;

import javax.sql.rowset.*;
import java.sql.SQLException;


public final class RowSetFactoryImpl implements RowSetFactory {

    public CachedRowSet createCachedRowSet() throws SQLException {
        return new CachedRowSetImpl();
    }

    public FilteredRowSet createFilteredRowSet() throws SQLException {
        return new FilteredRowSetImpl();
    }


    public JdbcRowSet createJdbcRowSet() throws SQLException {
        return new JdbcRowSetImpl();
    }

    public JoinRowSet createJoinRowSet() throws SQLException {
        return new JoinRowSetImpl();
    }

    public WebRowSet createWebRowSet() throws SQLException {
        return new WebRowSetImpl();
    }

}
