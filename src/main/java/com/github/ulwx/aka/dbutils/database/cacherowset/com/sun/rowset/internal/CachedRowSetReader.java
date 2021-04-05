

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.CachedRowSetImpl;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.JdbcRowSetResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetReader;
import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.sql.*;


public class CachedRowSetReader implements RowSetReader, Serializable {


    private int writerCalls = 0;

    private boolean userCon = false;

    private int startPosition;

    private JdbcRowSetResourceBundle resBundle;

    public CachedRowSetReader() {
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public void readData(RowSetInternal caller) throws SQLException {
        Connection con = null;
        try {
            CachedRowSet crs = (CachedRowSet) caller;

            // Get rid of the current contents of the rowset.



            if (crs.getPageSize() == 0 && crs.size() > 0) {
                // When page size is not set,
                // crs.size() will show the total no of rows.
                crs.close();
            }

            writerCalls = 0;

            // Get a connection.  This reader assumes that the necessary
            // properties have been set on the caller to let it supply a
            // connection.
            userCon = false;

            con = this.connect(caller);

            // Check our assumptions.
            if (con == null || crs.getCommand() == null)
                throw new SQLException(resBundle.handleGetObject("crsreader.connecterr").toString());

            try {
                con.setTransactionIsolation(crs.getTransactionIsolation());
            } catch (Exception ex) {
                ;
            }
            // Use JDBC to read the data.
            PreparedStatement pstmt = con.prepareStatement(crs.getCommand());
            // Pass any input parameters to JDBC.

            decodeParams(caller.getParams(), pstmt);
            try {
                pstmt.setMaxRows(crs.getMaxRows());
                pstmt.setMaxFieldSize(crs.getMaxFieldSize());
                pstmt.setEscapeProcessing(crs.getEscapeProcessing());
                pstmt.setQueryTimeout(crs.getQueryTimeout());
            } catch (Exception ex) {

                throw new SQLException(ex.getMessage());
            }

            if (crs.getCommand().toLowerCase().indexOf("select") != -1) {
                // can be (crs.getCommand()).indexOf("select")) == 0
                // because we will be getting resultset when
                // it may be the case that some false select query with
                // select coming in between instead of first.

                // if ((crs.getCommand()).indexOf("?")) does not return -1
                // implies a Prepared Statement like query exists.

                ResultSet rs = pstmt.executeQuery();
                if (crs.getPageSize() == 0) {
                    crs.populate(rs);
                } else {

                    pstmt = con.prepareStatement(crs.getCommand(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    decodeParams(caller.getParams(), pstmt);
                    try {
                        pstmt.setMaxRows(crs.getMaxRows());
                        pstmt.setMaxFieldSize(crs.getMaxFieldSize());
                        pstmt.setEscapeProcessing(crs.getEscapeProcessing());
                        pstmt.setQueryTimeout(crs.getQueryTimeout());
                    } catch (Exception ex) {

                        throw new SQLException(ex.getMessage());
                    }
                    rs = pstmt.executeQuery();
                    crs.populate(rs, startPosition);
                }
                rs.close();
            } else {
                pstmt.executeUpdate();
            }

            // Get the data.
            pstmt.close();
            try {
                con.commit();
            } catch (SQLException ex) {
                ;
            }
            // only close connections we created...
            if (getCloseConnection() == true)
                con.close();
        } catch (SQLException ex) {
            // Throw an exception if reading fails for any reason.
            throw ex;
        } finally {
            try {
                // only close connections we created...
                if (con != null && getCloseConnection() == true) {
                    try {
                        if (!con.getAutoCommit()) {
                            con.rollback();
                        }
                    } catch (Exception dummy) {

                    }
                    con.close();
                    con = null;
                }
            } catch (SQLException e) {
                // will get exception if something already went wrong, but don't
                // override that exception with this one
            }
        }
    }


    public boolean reset() throws SQLException {
        writerCalls++;
        return writerCalls == 1;
    }


    public Connection connect(RowSetInternal caller) throws SQLException {

        // Get a JDBC connection.
        if (caller.getConnection() != null) {
            // A connection was passed to execute(), so use it.
            // As we are using a connection the user gave us we
            // won't close it.
            userCon = true;
            return caller.getConnection();
        } else if (((RowSet) caller).getDataSourceName() != null) {
            // Connect using JNDI.
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup
                        (((RowSet) caller).getDataSourceName());

                // Check for username, password,
                // if it exists try getting a Connection handle through them
                // else try without these
                // else throw SQLException

                if (((RowSet) caller).getUsername() != null) {
                    return ds.getConnection(((RowSet) caller).getUsername(),
                            ((RowSet) caller).getPassword());
                } else {
                    return ds.getConnection();
                }
            } catch (NamingException ex) {
                SQLException sqlEx = new SQLException(resBundle.handleGetObject("crsreader.connect").toString());
                sqlEx.initCause(ex);
                throw sqlEx;
            }
        } else if (((RowSet) caller).getUrl() != null) {
            // Connect using the driver manager.
            return DriverManager.getConnection(((RowSet) caller).getUrl(),
                    ((RowSet) caller).getUsername(),
                    ((RowSet) caller).getPassword());
        } else {
            return null;
        }
    }


    @SuppressWarnings("deprecation")
    private void decodeParams(Object[] params,
                              PreparedStatement pstmt) throws SQLException {
        // There is a corresponding decodeParams in JdbcRowSetImpl
        // which does the same as this method. This is a design flaw.
        // Update the JdbcRowSetImpl.decodeParams when you update
        // this method.

        // Adding the same comments to JdbcRowSetImpl.decodeParams.

        int arraySize;
        Object[] param = null;

        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Object[]) {
                param = (Object[]) params[i];

                if (param.length == 2) {
                    if (param[0] == null) {
                        pstmt.setNull(i + 1, ((Integer) param[1]).intValue());
                        continue;
                    }

                    if (param[0] instanceof Date ||
                            param[0] instanceof Time ||
                            param[0] instanceof Timestamp) {
                        System.err.println(resBundle.handleGetObject("crsreader.datedetected").toString());
                        if (param[1] instanceof java.util.Calendar) {
                            System.err.println(resBundle.handleGetObject("crsreader.caldetected").toString());
                            pstmt.setDate(i + 1, (Date) param[0],
                                    (java.util.Calendar) param[1]);
                            continue;
                        } else {
                            throw new SQLException(resBundle.handleGetObject("crsreader.paramtype").toString());
                        }
                    }

                    if (param[0] instanceof Reader) {
                        pstmt.setCharacterStream(i + 1, (Reader) param[0],
                                ((Integer) param[1]).intValue());
                        continue;
                    }


                    if (param[1] instanceof Integer) {
                        pstmt.setObject(i + 1, param[0], ((Integer) param[1]).intValue());
                        continue;
                    }

                } else if (param.length == 3) {

                    if (param[0] == null) {
                        pstmt.setNull(i + 1, ((Integer) param[1]).intValue(),
                                (String) param[2]);
                        continue;
                    }

                    if (param[0] instanceof InputStream) {
                        switch (((Integer) param[2]).intValue()) {
                            case CachedRowSetImpl.UNICODE_STREAM_PARAM:
                                pstmt.setUnicodeStream(i + 1,
                                        (InputStream) param[0],
                                        ((Integer) param[1]).intValue());
                                break;
                            case CachedRowSetImpl.BINARY_STREAM_PARAM:
                                pstmt.setBinaryStream(i + 1,
                                        (InputStream) param[0],
                                        ((Integer) param[1]).intValue());
                                break;
                            case CachedRowSetImpl.ASCII_STREAM_PARAM:
                                pstmt.setAsciiStream(i + 1,
                                        (InputStream) param[0],
                                        ((Integer) param[1]).intValue());
                                break;
                            default:
                                throw new SQLException(resBundle.handleGetObject("crsreader.paramtype").toString());
                        }
                    }


                    if (param[1] instanceof Integer && param[2] instanceof Integer) {
                        pstmt.setObject(i + 1, param[0], ((Integer) param[1]).intValue(),
                                ((Integer) param[2]).intValue());
                        continue;
                    }

                    throw new SQLException(resBundle.handleGetObject("crsreader.paramtype").toString());

                } else {
                    // common case - this catches all SQL92 types
                    pstmt.setObject(i + 1, params[i]);
                    continue;
                }
            } else {
                // Try to get all the params to be set here
                pstmt.setObject(i + 1, params[i]);

            }
        }
    }


    protected boolean getCloseConnection() {
        if (userCon == true)
            return false;

        return true;
    }


    public void setStartPosition(int pos) {
        startPosition = pos;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Default state initialization happens here
        ois.defaultReadObject();
        // Initialization of  Res Bundle happens here .
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    static final long serialVersionUID = 5049738185801363801L;
}
