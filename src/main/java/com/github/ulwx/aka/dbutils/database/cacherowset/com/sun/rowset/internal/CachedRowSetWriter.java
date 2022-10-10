

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.CachedRowSetImpl;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.JdbcRowSetResourceBundle;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.ReflectUtil;

import javax.sql.RowSetInternal;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.serial.*;
import javax.sql.rowset.spi.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;


public class CachedRowSetWriter implements TransactionalWriter, Serializable {


    private transient Connection con;


    private String selectCmd;


    private String updateCmd;


    private String updateWhere;


    private String deleteCmd;


    private String deleteWhere;


    private String insertCmd;


    private int[] keyCols;


    private Object[] params;


    private CachedRowSetReader reader;


    private ResultSetMetaData callerMd;


    private int callerColumnCount;


    private CachedRowSetImpl crsResolve;


    private ArrayList<Integer> status;


    private int iChangedValsInDbAndCRS;


    private int iChangedValsinDbOnly;

    private JdbcRowSetResourceBundle resBundle;

    public CachedRowSetWriter() {
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public boolean writeData(RowSetInternal caller) throws SQLException {
        long conflicts = 0;
        boolean showDel = false;
        PreparedStatement pstmtIns = null;
        iChangedValsInDbAndCRS = 0;
        iChangedValsinDbOnly = 0;

        // We assume caller is a CachedRowSet
        CachedRowSetImpl crs = (CachedRowSetImpl) caller;
        // crsResolve = new CachedRowSetImpl();
        this.crsResolve = new CachedRowSetImpl();
        ;

        // The reader is registered with the writer at design time.
        // This is not required, in general.  The reader has logic
        // to get a JDBC connection, so call it.

        con = reader.connect(caller);


        if (con == null) {
            throw new SQLException(resBundle.handleGetObject("crswriter.connect").toString());
        }


        initSQLStatements(crs);
        int iColCount;

        RowSetMetaDataImpl rsmdWrite = (RowSetMetaDataImpl) crs.getMetaData();
        RowSetMetaDataImpl rsmdResolv = new RowSetMetaDataImpl();

        iColCount = rsmdWrite.getColumnCount();
        int sz = crs.size() + 1;
        status = new ArrayList<>(sz);

        status.add(0, null);
        rsmdResolv.setColumnCount(iColCount);

        for (int i = 1; i <= iColCount; i++) {
            rsmdResolv.setColumnType(i, rsmdWrite.getColumnType(i));
            rsmdResolv.setColumnName(i, rsmdWrite.getColumnName(i));
            rsmdResolv.setNullable(i, ResultSetMetaData.columnNullableUnknown);
        }
        this.crsResolve.setMetaData(rsmdResolv);

        // moved outside the insert inner loop
        //pstmtIns = con.prepareStatement(insertCmd);

        if (callerColumnCount < 1) {
            // No data, so return success.
            if (reader.getCloseConnection() == true)
                con.close();
            return true;
        }
        // We need to see rows marked for deletion.
        showDel = crs.getShowDeleted();
        crs.setShowDeleted(true);

        // Look at all the rows.
        crs.beforeFirst();

        int rows = 1;
        while (crs.next()) {
            if (crs.rowDeleted()) {
                // The row has been deleted.
                if (deleteOriginalRow(crs, this.crsResolve)) {
                    status.add(rows, SyncResolver.DELETE_ROW_CONFLICT);
                    conflicts++;
                } else {
                    // delete happened without any occurrence of conflicts
                    // so update status accordingly
                    status.add(rows, SyncResolver.NO_ROW_CONFLICT);
                }

            } else if (crs.rowInserted()) {
                // The row has been inserted.

                pstmtIns = con.prepareStatement(insertCmd);
                if (insertNewRow(crs, pstmtIns, this.crsResolve)) {
                    status.add(rows, SyncResolver.INSERT_ROW_CONFLICT);
                    conflicts++;
                } else {
                    // insert happened without any occurrence of conflicts
                    // so update status accordingly
                    status.add(rows, SyncResolver.NO_ROW_CONFLICT);
                }
            } else if (crs.rowUpdated()) {
                // The row has been updated.
                if (updateOriginalRow(crs)) {
                    status.add(rows, SyncResolver.UPDATE_ROW_CONFLICT);
                    conflicts++;
                } else {
                    // update happened without any occurrence of conflicts
                    // so update status accordingly
                    status.add(rows, SyncResolver.NO_ROW_CONFLICT);
                }

            } else {

                int icolCount = crs.getMetaData().getColumnCount();
                status.add(rows, SyncResolver.NO_ROW_CONFLICT);

                this.crsResolve.moveToInsertRow();
                for (int cols = 0; cols < iColCount; cols++) {
                    this.crsResolve.updateNull(cols + 1);
                } //end for

                this.crsResolve.insertRow();
                this.crsResolve.moveToCurrentRow();

            } //end if
            rows++;
        } //end while

        // close the insert statement
        if (pstmtIns != null)
            pstmtIns.close();
        // reset
        crs.setShowDeleted(showDel);

        crs.beforeFirst();
        this.crsResolve.beforeFirst();

        if (conflicts != 0) {
            SyncProviderException spe = new SyncProviderException(conflicts + " " +
                    resBundle.handleGetObject("crswriter.conflictsno").toString());
            //SyncResolver syncRes = spe.getSyncResolver();

            SyncResolverImpl syncResImpl = (SyncResolverImpl) spe.getSyncResolver();

            syncResImpl.setCachedRowSet(crs);
            syncResImpl.setCachedRowSetResolver(this.crsResolve);

            syncResImpl.setStatus(status);
            syncResImpl.setCachedRowSetWriter(this);

            throw spe;
        } else {
            return true;
        }


    } //end writeData


    private boolean updateOriginalRow(CachedRowSet crs)
            throws SQLException {
        PreparedStatement pstmt;
        int i = 0;
        int idx = 0;

        // Select the row from the database.
        ResultSet origVals = crs.getOriginalRow();
        origVals.next();

        try {
            updateWhere = buildWhereClause(updateWhere, origVals);


            String tempselectCmd = selectCmd.toLowerCase();

            int idxWhere = tempselectCmd.indexOf("where");

            if (idxWhere != -1) {
                String tempSelect = selectCmd.substring(0, idxWhere);
                selectCmd = tempSelect;
            }

            pstmt = con.prepareStatement(selectCmd + updateWhere,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            for (i = 0; i < keyCols.length; i++) {
                if (params[i] != null) {
                    pstmt.setObject(++idx, params[i]);
                } else {
                    continue;
                }
            }

            try {
                pstmt.setMaxRows(crs.getMaxRows());
                pstmt.setMaxFieldSize(crs.getMaxFieldSize());
                pstmt.setEscapeProcessing(crs.getEscapeProcessing());
                pstmt.setQueryTimeout(crs.getQueryTimeout());
            } catch (Exception ex) {
                // Older driver don't support these operations.
            }

            ResultSet rs = null;
            rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            if (rs.next()) {
                if (rs.next()) {


                    return true;
                }

                // don't close the rs
                // we require the record in rs to be used.
                // rs.close();
                // pstmt.close();
                rs.first();

                // how many fields need to be updated
                int colsNotChanged = 0;
                Vector<Integer> cols = new Vector<>();
                String updateExec = updateCmd;
                Object orig;
                Object curr;
                Object rsval;
                boolean boolNull = true;
                Object objVal = null;

                // There's only one row and the cursor
                // needs to be on that row.

                boolean first = true;
                boolean flag = true;

                this.crsResolve.moveToInsertRow();

                for (i = 1; i <= callerColumnCount; i++) {
                    orig = origVals.getObject(i);
                    curr = crs.getObject(i);
                    rsval = rs.getObject(i);

                    Map<String, Class<?>> map = (crs.getTypeMap() == null) ? con.getTypeMap() : crs.getTypeMap();
                    if (rsval instanceof Struct) {

                        Struct s = (Struct) rsval;

                        // look up the class in the map
                        Class<?> c = null;
                        c = map.get(s.getSQLTypeName());
                        if (c != null) {
                            // create new instance of the class
                            SQLData obj = null;
                            try {
                                obj = (SQLData) ReflectUtil.newInstance(c);
                            } catch (Exception ex) {
                                throw new SQLException("Unable to Instantiate: ", ex);
                            }
                            // get the attributes from the struct
                            Object attribs[] = s.getAttributes(map);
                            // create the SQLInput "stream"
                            SQLInputImpl sqlInput = new SQLInputImpl(attribs, map);
                            // read the values...
                            obj.readSQL(sqlInput, s.getSQLTypeName());
                            rsval = obj;
                        }
                    } else if (rsval instanceof SQLData) {
                        rsval = new SerialStruct((SQLData) rsval, map);
                    } else if (rsval instanceof Blob) {
                        rsval = new SerialBlob((Blob) rsval);
                    } else if (rsval instanceof Clob) {
                        rsval = new SerialClob((Clob) rsval);
                    } else if (rsval instanceof Array) {
                        rsval = new SerialArray((Array) rsval, map);
                    }

                    // reset boolNull if it had been set
                    boolNull = true;


                    if (rsval == null && orig != null) {
                        // value in db has changed
                        // don't proceed with synchronization
                        // get the value in db and pass it to the resolver.

                        iChangedValsinDbOnly++;
                        // Set the boolNull to false,
                        // in order to set the actual value;
                        boolNull = false;
                        objVal = rsval;
                    } else if (rsval != null && (!rsval.equals(orig))) {
                        // value in db has changed
                        // don't proceed with synchronization
                        // get the value in db and pass it to the resolver.

                        iChangedValsinDbOnly++;
                        // Set the boolNull to false,
                        // in order to set the actual value;
                        boolNull = false;
                        objVal = rsval;
                    } else if ((orig == null || curr == null)) {


                        if (first == false || flag == false) {
                            updateExec += ", ";
                        }
                        updateExec += crs.getMetaData().getColumnName(i);
                        cols.add(i);
                        updateExec += " = ? ";
                        first = false;


                    } else if (orig.equals(curr)) {
                        colsNotChanged++;
                        //nothing to update in this case since values are equal


                    } else if (orig.equals(curr) == false) {
                        // When values from db and values in CachedRowSet are not equal,
                        // if db value is same as before updation for each col in
                        // the row before fetching into CachedRowSet,
                        // only then we go ahead with updation, else we
                        // throw SyncProviderException.

                        // if value has changed in db after fetching from db
                        // for some cols of the row and at the same time, some other cols
                        // have changed in CachedRowSet, no synchronization happens

                        // Synchronization happens only when data when fetching is
                        // same or at most has changed in cachedrowset

                        // check orig value with what is there in crs for a column
                        // before updation in crs.

                        if (crs.columnUpdated(i)) {
                            if (rsval.equals(orig)) {
                                // At this point we are sure that
                                // the value updated in crs was from
                                // what is in db now and has not changed
                                if (flag == false || first == false) {
                                    updateExec += ", ";
                                }
                                updateExec += crs.getMetaData().getColumnName(i);
                                cols.add(i);
                                updateExec += " = ? ";
                                flag = false;
                            } else {
                                // Here the value has changed in the db after
                                // data was fetched
                                // Plus store this row from CachedRowSet and keep it
                                // in a new CachedRowSet
                                boolNull = false;
                                objVal = rsval;
                                iChangedValsInDbAndCRS++;
                            }
                        }
                    }

                    if (!boolNull) {
                        this.crsResolve.updateObject(i, objVal);
                    } else {
                        this.crsResolve.updateNull(i);
                    }
                } //end for

                rs.close();
                pstmt.close();

                this.crsResolve.insertRow();
                this.crsResolve.moveToCurrentRow();


                if ((first == false && cols.size() == 0) ||
                        colsNotChanged == callerColumnCount) {
                    return false;
                }

                if (iChangedValsInDbAndCRS != 0 || iChangedValsinDbOnly != 0) {
                    return true;
                }


                updateExec += updateWhere;

                pstmt = con.prepareStatement(updateExec);

                // Comments needed here
                for (i = 0; i < cols.size(); i++) {
                    Object obj = crs.getObject(cols.get(i));
                    if (obj != null)
                        pstmt.setObject(i + 1, obj);
                    else
                        pstmt.setNull(i + 1, crs.getMetaData().getColumnType(i + 1));
                }
                idx = i;

                // Comments needed here
                for (i = 0; i < keyCols.length; i++) {
                    if (params[i] != null) {
                        pstmt.setObject(++idx, params[i]);
                    } else {
                        continue;
                    }
                }

                i = pstmt.executeUpdate();


                return false;

            } else {

                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // if executeUpdate fails it will come here,
            // update crsResolve with null rows
            this.crsResolve.moveToInsertRow();

            for (i = 1; i <= callerColumnCount; i++) {
                this.crsResolve.updateNull(i);
            }

            this.crsResolve.insertRow();
            this.crsResolve.moveToCurrentRow();

            return true;
        }
    }


    private boolean insertNewRow(CachedRowSet crs,
                                 PreparedStatement pstmt, CachedRowSetImpl crsRes) throws SQLException {

        boolean returnVal = false;

        try (PreparedStatement pstmtSel = con.prepareStatement(selectCmd,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = pstmtSel.executeQuery();
             ResultSet rs2 = con.getMetaData().getPrimaryKeys(null, null,
                     crs.getTableName())
        ) {

            ResultSetMetaData rsmd = crs.getMetaData();
            int icolCount = rsmd.getColumnCount();
            String[] primaryKeys = new String[icolCount];
            int k = 0;
            while (rs2.next()) {
                primaryKeys[k] = rs2.getString("COLUMN_NAME");
                k++;
            }

            if (rs.next()) {
                for (String pkName : primaryKeys) {
                    if (!isPKNameValid(pkName, rsmd)) {


                        continue;
                    }

                    Object crsPK = crs.getObject(pkName);
                    if (crsPK == null) {

                        break;
                    }

                    String rsPK = rs.getObject(pkName).toString();
                    if (crsPK.toString().equals(rsPK)) {
                        returnVal = true;
                        this.crsResolve.moveToInsertRow();
                        for (int i = 1; i <= icolCount; i++) {
                            String colname = (rs.getMetaData()).getColumnName(i);
                            if (colname.equals(pkName))
                                this.crsResolve.updateObject(i, rsPK);
                            else
                                this.crsResolve.updateNull(i);
                        }
                        this.crsResolve.insertRow();
                        this.crsResolve.moveToCurrentRow();
                    }
                }
            }

            if (returnVal) {
                return returnVal;
            }

            try {
                for (int i = 1; i <= icolCount; i++) {
                    Object obj = crs.getObject(i);
                    if (obj != null) {
                        pstmt.setObject(i, obj);
                    } else {
                        pstmt.setNull(i, crs.getMetaData().getColumnType(i));
                    }
                }

                pstmt.executeUpdate();
                return false;

            } catch (SQLException ex) {

                this.crsResolve.moveToInsertRow();

                for (int i = 1; i <= icolCount; i++) {
                    this.crsResolve.updateNull(i);
                }

                this.crsResolve.insertRow();
                this.crsResolve.moveToCurrentRow();

                return true;
            }
        }
    }


    private boolean deleteOriginalRow(CachedRowSet crs, CachedRowSetImpl crsRes) throws SQLException {
        PreparedStatement pstmt;
        int i;
        int idx = 0;
        String strSelect;
        // Select the row from the database.
        ResultSet origVals = crs.getOriginalRow();
        origVals.next();

        deleteWhere = buildWhereClause(deleteWhere, origVals);
        pstmt = con.prepareStatement(selectCmd + deleteWhere,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        for (i = 0; i < keyCols.length; i++) {
            if (params[i] != null) {
                pstmt.setObject(++idx, params[i]);
            } else {
                continue;
            }
        }

        try {
            pstmt.setMaxRows(crs.getMaxRows());
            pstmt.setMaxFieldSize(crs.getMaxFieldSize());
            pstmt.setEscapeProcessing(crs.getEscapeProcessing());
            pstmt.setQueryTimeout(crs.getQueryTimeout());
        } catch (Exception ex) {

            ;
        }

        ResultSet rs = pstmt.executeQuery();

        if (rs.next() == true) {
            if (rs.next()) {
                // more than one row
                return true;
            }
            rs.first();

            // Now check all the values in rs to be same in
            // db also before actually going ahead with deleting
            boolean boolChanged = false;

            crsRes.moveToInsertRow();

            for (i = 1; i <= crs.getMetaData().getColumnCount(); i++) {

                Object original = origVals.getObject(i);
                Object changed = rs.getObject(i);

                if (original != null && changed != null) {
                    if (!(original.toString()).equals(changed.toString())) {
                        boolChanged = true;
                        crsRes.updateObject(i, origVals.getObject(i));
                    }
                } else {
                    crsRes.updateNull(i);
                }
            }

            crsRes.insertRow();
            crsRes.moveToCurrentRow();

            if (boolChanged) {
                // do not delete as values in db have changed
                // deletion will not happen for this row from db
                // exit now returning true. i.e. conflict
                return true;
            } else {
                // delete the row.
                // Go ahead with deleting,
                // don't do anything here
            }

            String cmd = deleteCmd + deleteWhere;
            pstmt = con.prepareStatement(cmd);

            idx = 0;
            for (i = 0; i < keyCols.length; i++) {
                if (params[i] != null) {
                    pstmt.setObject(++idx, params[i]);
                } else {
                    continue;
                }
            }

            if (pstmt.executeUpdate() != 1) {
                return true;
            }
            pstmt.close();
        } else {
            // didn't find the row
            return true;
        }

        // no conflict
        return false;
    }


    public void setReader(CachedRowSetReader reader) throws SQLException {
        this.reader = reader;
    }


    public CachedRowSetReader getReader() throws SQLException {
        return reader;
    }


    private void initSQLStatements(CachedRowSet caller) throws SQLException {

        int i;

        callerMd = caller.getMetaData();
        callerColumnCount = callerMd.getColumnCount();
        if (callerColumnCount < 1)
            // No data, so return.
            return;


        String table = caller.getTableName();
        if (table == null) {

            table = callerMd.getTableName(1);
            if (table == null || table.length() == 0) {
                throw new SQLException(resBundle.handleGetObject("crswriter.tname").toString());
            }
        }
        String catalog = callerMd.getCatalogName(1);
        String schema = callerMd.getSchemaName(1);
        DatabaseMetaData dbmd = con.getMetaData();


        // Project List
        selectCmd = "SELECT ";
        for (i = 1; i <= callerColumnCount; i++) {
            selectCmd += callerMd.getColumnName(i);
            if (i < callerMd.getColumnCount())
                selectCmd += ", ";
            else
                selectCmd += " ";
        }

        // FROM clause.
        selectCmd += "FROM " + buildTableName(dbmd, catalog, schema, table);


        updateCmd = "UPDATE " + buildTableName(dbmd, catalog, schema, table);


        String tempupdCmd = updateCmd.toLowerCase();

        int idxupWhere = tempupdCmd.indexOf("where");

        if (idxupWhere != -1) {
            updateCmd = updateCmd.substring(0, idxupWhere);
        }
        updateCmd += "SET ";


        insertCmd = "INSERT INTO " + buildTableName(dbmd, catalog, schema, table);
        // Column list
        insertCmd += "(";
        for (i = 1; i <= callerColumnCount; i++) {
            insertCmd += callerMd.getColumnName(i);
            if (i < callerMd.getColumnCount())
                insertCmd += ", ";
            else
                insertCmd += ") VALUES (";
        }
        for (i = 1; i <= callerColumnCount; i++) {
            insertCmd += "?";
            if (i < callerColumnCount)
                insertCmd += ", ";
            else
                insertCmd += ")";
        }


        deleteCmd = "DELETE FROM " + buildTableName(dbmd, catalog, schema, table);


        buildKeyDesc(caller);
    }


    private String buildTableName(DatabaseMetaData dbmd,
                                  String catalog, String schema, String table) throws SQLException {

        // trim all the leading and trailing whitespaces,
        // white spaces can never be catalog, schema or a table name.

        String cmd = "";

        catalog = catalog.trim();
        schema = schema.trim();
        table = table.trim();

        if (dbmd.isCatalogAtStart() == true) {
            if (catalog != null && catalog.length() > 0) {
                cmd += catalog + dbmd.getCatalogSeparator();
            }
            if (schema != null && schema.length() > 0) {
                cmd += schema + ".";
            }
            cmd += table;
        } else {
            if (schema != null && schema.length() > 0) {
                cmd += schema + ".";
            }
            cmd += table;
            if (catalog != null && catalog.length() > 0) {
                cmd += dbmd.getCatalogSeparator() + catalog;
            }
        }
        cmd += " ";
        return cmd;
    }


    private void buildKeyDesc(CachedRowSet crs) throws SQLException {

        keyCols = crs.getKeyColumns();
        ResultSetMetaData resultsetmd = crs.getMetaData();
        if (keyCols == null || keyCols.length == 0) {
            ArrayList<Integer> listKeys = new ArrayList<Integer>();

            for (int i = 0; i < callerColumnCount; i++) {
                if (resultsetmd.getColumnType(i + 1) != Types.CLOB &&
                        resultsetmd.getColumnType(i + 1) != Types.STRUCT &&
                        resultsetmd.getColumnType(i + 1) != Types.SQLXML &&
                        resultsetmd.getColumnType(i + 1) != Types.BLOB &&
                        resultsetmd.getColumnType(i + 1) != Types.ARRAY &&
                        resultsetmd.getColumnType(i + 1) != Types.OTHER)
                    listKeys.add(i + 1);
            }
            keyCols = new int[listKeys.size()];
            for (int i = 0; i < listKeys.size(); i++)
                keyCols[i] = listKeys.get(i);
        }
        params = new Object[keyCols.length];
    }

    private String buildWhereClause(String whereClause,
                                    ResultSet rs) throws SQLException {
        whereClause = "WHERE ";

        for (int i = 0; i < keyCols.length; i++) {
            if (i > 0) {
                whereClause += "AND ";
            }
            whereClause += callerMd.getColumnName(keyCols[i]);
            params[i] = rs.getObject(keyCols[i]);
            if (rs.wasNull() == true) {
                whereClause += " IS NULL ";
            } else {
                whereClause += " = ? ";
            }
        }
        return whereClause;
    }

    void updateResolvedConflictToDB(CachedRowSet crs, Connection con) throws SQLException {
        //String updateExe = ;
        PreparedStatement pStmt;
        String strWhere = "WHERE ";
        String strExec = " ";
        String strUpdate = "UPDATE ";
        int icolCount = crs.getMetaData().getColumnCount();
        int keyColumns[] = crs.getKeyColumns();
        Object param[];
        String strSet = "";

        strWhere = buildWhereClause(strWhere, crs);

        if (keyColumns == null || keyColumns.length == 0) {
            keyColumns = new int[icolCount];
            for (int i = 0; i < keyColumns.length; ) {
                keyColumns[i] = ++i;
            }
        }
        param = new Object[keyColumns.length];

        strUpdate = "UPDATE " + buildTableName(con.getMetaData(),
                crs.getMetaData().getCatalogName(1),
                crs.getMetaData().getSchemaName(1),
                crs.getTableName());

        // changed or updated values will become part of
        // set clause here
        strUpdate += "SET ";

        boolean first = true;

        for (int i = 1; i <= icolCount; i++) {
            if (crs.columnUpdated(i)) {
                if (first == false) {
                    strSet += ", ";
                }
                strSet += crs.getMetaData().getColumnName(i);
                strSet += " = ? ";
                first = false;
            } //end if
        } //end for

        // keycols will become part of where clause
        strUpdate += strSet;
        strWhere = "WHERE ";

        for (int i = 0; i < keyColumns.length; i++) {
            if (i > 0) {
                strWhere += "AND ";
            }
            strWhere += crs.getMetaData().getColumnName(keyColumns[i]);
            param[i] = crs.getObject(keyColumns[i]);
            if (crs.wasNull() == true) {
                strWhere += " IS NULL ";
            } else {
                strWhere += " = ? ";
            }
        }
        strUpdate += strWhere;

        pStmt = con.prepareStatement(strUpdate);

        int idx = 0;
        for (int i = 0; i < icolCount; i++) {
            if (crs.columnUpdated(i + 1)) {
                Object obj = crs.getObject(i + 1);
                if (obj != null) {
                    pStmt.setObject(++idx, obj);
                } else {
                    pStmt.setNull(i + 1, crs.getMetaData().getColumnType(i + 1));
                } //end if ..else
            } //end if crs.column...
        } //end for

        // Set the key cols for after WHERE =? clause
        for (int i = 0; i < keyColumns.length; i++) {
            if (param[i] != null) {
                pStmt.setObject(++idx, param[i]);
            }
        }

        int id = pStmt.executeUpdate();
    }


    public void commit() throws SQLException {
        con.commit();
        if (reader.getCloseConnection() == true) {
            con.close();
        }
    }

    public void commit(CachedRowSetImpl crs, boolean updateRowset) throws SQLException {
        con.commit();
        if (updateRowset) {
            if (crs.getCommand() != null)
                crs.execute(con);
        }

        if (reader.getCloseConnection() == true) {
            con.close();
        }
    }


    public void rollback() throws SQLException {
        con.rollback();
        if (reader.getCloseConnection() == true) {
            con.close();
        }
    }


    public void rollback(Savepoint s) throws SQLException {
        con.rollback(s);
        if (reader.getCloseConnection() == true) {
            con.close();
        }
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

    static final long serialVersionUID = -8506030970299413976L;


    private boolean isPKNameValid(String pk, ResultSetMetaData rsmd) throws SQLException {
        boolean isValid = false;
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            String colName = rsmd.getColumnClassName(i);
            if (colName.equalsIgnoreCase(pk)) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }
}
