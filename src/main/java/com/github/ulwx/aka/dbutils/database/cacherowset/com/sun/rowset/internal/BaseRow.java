

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;


public abstract class BaseRow implements Serializable, Cloneable {


    private static final long serialVersionUID = 4152013523511412238L;

    protected Object[] origVals;


    public Object[] getOrigRow() {
        Object[] origRow = this.origVals;
        return (origRow == null) ? null : Arrays.copyOf(origRow, origRow.length);
    }


    public abstract Object getColumnObject(int idx) throws SQLException;


    public abstract void setColumnObject(int idx, Object obj) throws SQLException;
}
