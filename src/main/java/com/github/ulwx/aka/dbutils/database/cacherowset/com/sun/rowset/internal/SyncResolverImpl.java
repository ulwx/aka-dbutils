

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.CachedRowSetImpl;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.JdbcRowSetResourceBundle;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;


public class SyncResolverImpl extends CachedRowSetImpl implements SyncResolver {

    private CachedRowSetImpl crsRes;


    private CachedRowSetImpl crsSync;


    private ArrayList<?> stats;


    private CachedRowSetWriter crw;


    private int rowStatus;


    private int sz;


    private transient Connection con;


    private CachedRowSet row;

    private JdbcRowSetResourceBundle resBundle;


    public SyncResolverImpl() throws SQLException {
        try {
            crsSync = new CachedRowSetImpl();
            crsRes = new CachedRowSetImpl();
            crw = new CachedRowSetWriter();
            row = new CachedRowSetImpl();
            rowStatus = 1;
            try {
                resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }

        } catch (SQLException sqle) {
        }
    }


    public int getStatus() {
        return ((Integer) stats.get(rowStatus - 1)).intValue();
    }


    public Object getConflictValue(int index) throws SQLException {
        try {
            return crsRes.getObject(index);
        } catch (SQLException sqle) {
            throw new SQLException(sqle.getMessage());
        }
    }


    public Object getConflictValue(String columnName) throws SQLException {
        try {
            return crsRes.getObject(columnName);
        } catch (SQLException sqle) {
            throw new SQLException(sqle.getMessage());
        }
    }


    public void setResolvedValue(int index, Object obj) throws SQLException {
        // modify method to throw SQLException in spec


        try {
            // check whether the index is in range
            if (index <= 0 || index > crsSync.getMetaData().getColumnCount()) {
                throw new SQLException(resBundle.handleGetObject("syncrsimpl.indexval").toString() + index);
            }
            // check whether index col is in conflict
            if (crsRes.getObject(index) == null) {
                throw new SQLException(resBundle.handleGetObject("syncrsimpl.noconflict").toString());
            }
        } catch (SQLException sqle) {
            // modify method to throw for SQLException
            throw new SQLException(sqle.getMessage());
        }
        try {
            boolean bool = true;


            if (((crsSync.getObject(index)).toString()).equals(obj.toString()) ||
                    ((crsRes.getObject(index)).toString()).equals(obj.toString())) {


                crsRes.updateNull(index);
                crsRes.updateRow();


                if (row.size() != 1) {
                    row = buildCachedRow();
                }

                row.updateObject(index, obj);
                row.updateRow();

                for (int j = 1; j < crsRes.getMetaData().getColumnCount(); j++) {
                    if (crsRes.getObject(j) != null) {
                        bool = false;
                        break;
                        // break out of loop and wait for other cols
                        // in same row to get resolved
                    } //end if

                } //end for

                if (bool) {

                    try {


                        writeData(row);

                        //crw.writeData( (RowSetInternal)crsRow);
                        //System.out.printlnt.println("12");

                    } catch (SyncProviderException spe) {

                        throw new SQLException(resBundle.handleGetObject("syncrsimpl.syncnotpos").toString());
                    }
                } //end if(bool)

            } else {
                throw new SQLException(resBundle.handleGetObject("syncrsimpl.valtores").toString());
            } //end if (crs.getObject ...) block


        } catch (SQLException sqle) {
            throw new SQLException(sqle.getMessage());
        }
    }


    private void writeData(CachedRowSet row) throws SQLException {
        crw.updateResolvedConflictToDB(row, crw.getReader().connect((RowSetInternal) crsSync));
    }


    private CachedRowSet buildCachedRow() throws SQLException {
        int iColCount;
        CachedRowSetImpl crsRow = new CachedRowSetImpl();

        RowSetMetaDataImpl rsmd = new RowSetMetaDataImpl();
        RowSetMetaDataImpl rsmdWrite = (RowSetMetaDataImpl) crsSync.getMetaData();
        RowSetMetaDataImpl rsmdRow = new RowSetMetaDataImpl();

        iColCount = rsmdWrite.getColumnCount();
        rsmdRow.setColumnCount(iColCount);

        for (int i = 1; i <= iColCount; i++) {
            rsmdRow.setColumnType(i, rsmdWrite.getColumnType(i));
            rsmdRow.setColumnName(i, rsmdWrite.getColumnName(i));
            rsmdRow.setNullable(i, ResultSetMetaData.columnNullableUnknown);

            try {
                rsmdRow.setCatalogName(i, rsmdWrite.getCatalogName(i));
                rsmdRow.setSchemaName(i, rsmdWrite.getSchemaName(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } //end for

        crsRow.setMetaData(rsmdRow);

        crsRow.moveToInsertRow();

        for (int col = 1; col <= crsSync.getMetaData().getColumnCount(); col++) {
            crsRow.updateObject(col, crsSync.getObject(col));
        }

        crsRow.insertRow();
        crsRow.moveToCurrentRow();

        crsRow.absolute(1);
        crsRow.setOriginalRow();

        try {
            crsRow.setUrl(crsSync.getUrl());
        } catch (SQLException sqle) {

        }

        try {
            crsRow.setDataSourceName(crsSync.getCommand());
        } catch (SQLException sqle) {

        }

        try {
            if (crsSync.getTableName() != null) {
                crsRow.setTableName(crsSync.getTableName());
            }
        } catch (SQLException sqle) {

        }

        try {
            if (crsSync.getCommand() != null)
                crsRow.setCommand(crsSync.getCommand());
        } catch (SQLException sqle) {

        }

        try {
            crsRow.setKeyColumns(crsSync.getKeyColumns());
        } catch (SQLException sqle) {

        }
        return crsRow;
    }


    public void setResolvedValue(String columnName, Object obj) throws SQLException {
        // modify method to throw SQLException in spec
        // %%% Missing implementation!
    }


    void setCachedRowSet(CachedRowSet crs) {
        crsSync = (CachedRowSetImpl) crs;
    }


    void setCachedRowSetResolver(CachedRowSet crs) {
        try {
            crsRes = (CachedRowSetImpl) crs;
            crsRes.afterLast();
            sz = crsRes.size();
        } catch (SQLException sqle) {
            // do nothing
        }
    }


    @SuppressWarnings("rawtypes")
    void setStatus(ArrayList status) {
        stats = status;
    }


    void setCachedRowSetWriter(CachedRowSetWriter CRWriter) {
        crw = CRWriter;
    }


    public boolean nextConflict() throws SQLException {

        boolean bool = false;

        crsSync.setShowDeleted(true);
        while (crsSync.next()) {
            crsRes.previous();
            rowStatus++;  //sz--;

            if ((rowStatus - 1) >= stats.size()) {
                bool = false;
                break;
            }

            if (((Integer) stats.get(rowStatus - 1)).intValue() == SyncResolver.NO_ROW_CONFLICT) {
                // do nothing
                // bool remains as false
                ;
            } else {
                bool = true;
                break;
            } //end if

        } //end while

        crsSync.setShowDeleted(false);
        return bool;
    } // end next() method


    public boolean previousConflict() throws SQLException {
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    // Properties
    //-----------------------------------------------------------------------


    public void setCommand(String cmd) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void populate(ResultSet data) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void execute(Connection conn) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void acceptChanges() throws SyncProviderException {
        throw new UnsupportedOperationException();
    }

    public void acceptChanges(Connection con) throws SyncProviderException {
        throw new UnsupportedOperationException();
    }


    public void restoreOriginal() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void release() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void undoDelete() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void undoInsert() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void undoUpdate() throws SQLException {
        throw new UnsupportedOperationException();

    }


    public RowSet createShared() throws SQLException {
        throw new UnsupportedOperationException();
    }


    protected Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException();
    }


    public CachedRowSet createCopy() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public CachedRowSet createCopySchema() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public CachedRowSet createCopyNoConstraints() throws SQLException {
        throw new UnsupportedOperationException();
    }


    @SuppressWarnings("rawtypes")
    public Collection toCollection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("rawtypes")
    public Collection toCollection(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }


    @SuppressWarnings("rawtypes")
    public Collection toCollection(String column) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public SyncProvider getSyncProvider() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setSyncProvider(String providerStr) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void execute() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean next() throws SQLException {
        throw new UnsupportedOperationException();
    }


    protected boolean internalNext() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void close() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException();
    }


    protected BaseRow getCurrentRow() {
        throw new UnsupportedOperationException();
    }


    protected void removeCurrentRow() {
        throw new UnsupportedOperationException();
    }


    public String getString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte getByte(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public short getShort(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getInt(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public long getLong(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public float getFloat(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public double getDouble(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.sql.Date getDate(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    @Deprecated
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();

    }


    public String getString(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean getBoolean(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte getByte(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public short getShort(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getInt(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public long getLong(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public float getFloat(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public double getDouble(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    @Deprecated
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte[] getBytes(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.sql.Date getDate(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.io.InputStream getAsciiStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();

    }


    @Deprecated
    public java.io.InputStream getUnicodeStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.io.InputStream getBinaryStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public SQLWarning getWarnings() {
        throw new UnsupportedOperationException();
    }


    public void clearWarnings() {
        throw new UnsupportedOperationException();
    }


    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int findColumn(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.io.Reader getCharacterStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int size() {
        throw new UnsupportedOperationException();
    }


    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }


    protected boolean internalFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }


    protected boolean internalLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getRow() throws SQLException {
        return crsSync.getRow();
    }


    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }


    protected boolean internalPrevious() throws SQLException {
        throw new UnsupportedOperationException();
    }


    //---------------------------------------------------------------------
    // Updates
    //---------------------------------------------------------------------


    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean columnUpdated(int idx) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean columnUpdated(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException();

    }


    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateNull(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBoolean(String columnName, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateByte(String columnName, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateShort(String columnName, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateInt(String columnName, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateLong(String columnName, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateFloat(String columnName, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDouble(String columnName, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateString(String columnName, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBytes(String columnName, byte x[]) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDate(String columnName, java.sql.Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTime(String columnName, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateAsciiStream(String columnName,
                                  java.io.InputStream x,
                                  int length) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBinaryStream(String columnName, java.io.InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateCharacterStream(String columnName,
                                      java.io.Reader reader,
                                      int length) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(String columnName, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(int columnIndex,
                            Map<String, Class<?>> map)
            throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(String columnName,
                            Map<String, Class<?>> map)
            throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Ref getRef(String colName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Blob getBlob(String colName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Clob getClob(String colName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Array getArray(String colName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(String columnName, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setMetaData(RowSetMetaData md) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public ResultSet getOriginal() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public ResultSet getOriginalRow() throws SQLException {
        throw new UnsupportedOperationException();

    }


    public void setOriginalRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setOriginal() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getTableName() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setTableName(String tabName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int[] getKeyColumns() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setKeyColumns(int[] keys) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRef(int columnIndex, Ref ref) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRef(String columnName, Ref ref) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateClob(int columnIndex, Clob c) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateClob(String columnName, Clob c) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBlob(int columnIndex, Blob b) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBlob(String columnName, Blob b) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateArray(int columnIndex, Array a) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateArray(String columnName, Array a) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.net.URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public java.net.URL getURL(String columnName) throws SQLException {
        throw new UnsupportedOperationException();

    }


    public RowSetWarning getRowSetWarnings() {
        throw new UnsupportedOperationException();
    }


    public void commit() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void rollback() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void rollback(Savepoint s) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void unsetMatchColumn(int[] columnIdxes) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void unsetMatchColumn(String[] columnIdxes) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String[] getMatchColumnNames() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int[] getMatchColumnIndexes() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setMatchColumn(int[] columnIdxes) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setMatchColumn(String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setMatchColumn(int columnIdx) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setMatchColumn(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void unsetMatchColumn(int columnIdx) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void unsetMatchColumn(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void rowSetPopulated(RowSetEvent event, int numRows) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void populate(ResultSet data, int start) throws SQLException {
        throw new UnsupportedOperationException();

    }


    public boolean nextPage() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setPageSize(int size) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getPageSize() {
        throw new UnsupportedOperationException();
    }


    public boolean previousPage() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateNCharacterStream(int columnIndex,
                                       java.io.Reader x,
                                       int length)
            throws SQLException {
        throw new UnsupportedOperationException("Operation not yet supported");
    }


    public void updateNCharacterStream(String columnName,
                                       java.io.Reader x,
                                       int length)
            throws SQLException {
        throw new UnsupportedOperationException("Operation not yet supported");
    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Default state initialization happens here
        ois.defaultReadObject();
        // Initialization of transient Res Bundle happens here .
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    static final long serialVersionUID = -3345004441725080251L;
} //end class
