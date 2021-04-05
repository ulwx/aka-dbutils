

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.BitSet;


public class Row extends BaseRow implements Serializable, Cloneable {

    static final long serialVersionUID = 5047859032611314762L;


    private Object[] currentVals;


    private BitSet colsChanged;


    private boolean deleted;


    private boolean updated;


    private boolean inserted;


    private int numCols;


    public Row(int numCols) {
        origVals = new Object[numCols];
        currentVals = new Object[numCols];
        colsChanged = new BitSet(numCols);
        this.numCols = numCols;
    }


    public Row(int numCols, Object[] vals) {
        origVals = new Object[numCols];
        System.arraycopy(vals, 0, origVals, 0, numCols);
        currentVals = new Object[numCols];
        colsChanged = new BitSet(numCols);
        this.numCols = numCols;
    }


    public void initColumnObject(int idx, Object val) {
        origVals[idx - 1] = val;
    }


    public void setColumnObject(int idx, Object val) {
        currentVals[idx - 1] = val;
        setColUpdated(idx - 1);
    }


    public Object getColumnObject(int columnIndex) throws SQLException {
        if (getColUpdated(columnIndex - 1)) {
            return (currentVals[columnIndex - 1]); // maps to array!!
        } else {
            return (origVals[columnIndex - 1]); // maps to array!!
        }
    }

    public boolean getColUpdated(int idx) {
        return colsChanged.get(idx);
    }


    public void setDeleted() { // %%% was public
        deleted = true;
    }



    public boolean getDeleted() {
        return (deleted);
    }


    public void clearDeleted() {
        deleted = false;
    }


    public void setInserted() {
        inserted = true;
    }



    public boolean getInserted() {
        return (inserted);
    }


    public void clearInserted() { // %%% was public
        inserted = false;
    }


    public boolean getUpdated() {
        return (updated);
    }


    public void setUpdated() {
        // only mark something as updated if one or
        // more of the columns has been changed.
        for (int i = 0; i < numCols; i++) {
            if (getColUpdated(i) == true) {
                updated = true;
                return;
            }
        }
    }


    private void setColUpdated(int idx) {
        colsChanged.set(idx);
    }


    public void clearUpdated() {
        updated = false;
        for (int i = 0; i < numCols; i++) {
            currentVals[i] = null;
            colsChanged.clear(i);
        }
    }


    public void moveCurrentToOrig() {
        for (int i = 0; i < numCols; i++) {
            if (getColUpdated(i) == true) {
                origVals[i] = currentVals[i];
                currentVals[i] = null;
                colsChanged.clear(i);
            }
        }
        updated = false;
    }


    public BaseRow getCurrentRow() {
        return null;
    }
}
