

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset;

import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;
import java.io.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Hashtable;


public class FilteredRowSetImpl extends WebRowSetImpl implements Serializable, Cloneable, FilteredRowSet {

    private Predicate p;

    private boolean onInsertRow = false;


    public FilteredRowSetImpl() throws SQLException {
        super();
    }


    @SuppressWarnings("rawtypes")
    public FilteredRowSetImpl(Hashtable env) throws SQLException {
        super(env);
    }


    public void setFilter(Predicate p) throws SQLException {
        this.p = p;
    }


    public Predicate getFilter() {
        return this.p;
    }


    protected boolean internalNext() throws SQLException {
        // CachedRowSetImpl.next() internally calls
        // this(crs).internalNext() NOTE: this holds crs object
        // So when frs.next() is called,
        // internally this(frs).internalNext() will be called
        // which will be nothing but this method.
        // because this holds frs object

        // keep on doing super.internalNext()
        // rather than doing it once.


        // p.evaluate will help us in changing the cursor
        // and checking the next value by returning true or false.
        // to fit the filter

        // So while() loop will have a "random combination" of
        // true and false returned depending upon the records
        // are in or out of filter.
        // We need to traverse from present cursorPos till end,
        // whether true or false and check each row for "filter"
        // "till we get a "true"


        boolean bool = false;

        for (int rows = this.getRow(); rows <= this.size(); rows++) {
            bool = super.internalNext();

            if (!bool || p == null) {
                return bool;
            }
            if (p.evaluate(this)) {
                break;
            }

        }

        return bool;
    }


    protected boolean internalPrevious() throws SQLException {
        boolean bool = false;
        // with previous move backwards,
        // i.e. from any record towards first record

        for (int rows = this.getRow(); rows > 0; rows--) {

            bool = super.internalPrevious();

            if (p == null) {
                return bool;
            }

            if (p.evaluate(this)) {
                break;
            }

        }

        return bool;
    }


    protected boolean internalFirst() throws SQLException {

        // from first till present cursor position(go forward),
        // find the actual first which matches the filter.

        boolean bool = super.internalFirst();

        if (p == null) {
            return bool;
        }

        while (bool) {

            if (p.evaluate(this)) {
                break;
            }
            bool = super.internalNext();
        }
        return bool;
    }


    protected boolean internalLast() throws SQLException {
        // from last to the present cursor position(go backward),
        // find the actual last which matches the filter.

        boolean bool = super.internalLast();

        if (p == null) {
            return bool;
        }

        while (bool) {

            if (p.evaluate(this)) {
                break;
            }

            bool = super.internalPrevious();

        }
        return bool;

    } // end internalLast()


    public boolean relative(int rows) throws SQLException {

        boolean retval;
        boolean bool = false;
        boolean boolval = false;

        if (getType() == ResultSet.TYPE_FORWARD_ONLY) {
            throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.relative").toString());
        }

        if (rows > 0) {

            int i = 0;
            while (i < (rows)) {

                if (isAfterLast()) {
                    return false;
                }
                bool = internalNext();
                i++;
            }

            retval = bool;
        } else {
            int j = rows;
            while ((j) < 0) {

                if (isBeforeFirst()) {
                    return false;
                }
                boolval = internalPrevious();
                j++;
            }
            retval = boolval;
        }
        if (rows != 0)
            notifyCursorMoved();
        return retval;
    }


    public boolean absolute(int rows) throws SQLException {

        boolean retval;
        boolean bool = false;

        if (rows == 0 || getType() == ResultSet.TYPE_FORWARD_ONLY) {
            throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.absolute").toString());
        }

        if (rows > 0) {
            bool = internalFirst();

            int i = 0;
            while (i < (rows - 1)) {
                if (isAfterLast()) {
                    return false;
                }
                bool = internalNext();
                i++;
            }
            retval = bool;
        } else {
            bool = internalLast();

            int j = rows;
            while ((j + 1) < 0) {
                if (isBeforeFirst()) {
                    return false;
                }
                bool = internalPrevious();
                j++;
            }
            retval = bool;
        }
        notifyCursorMoved();
        return retval;
    }


    public void moveToInsertRow() throws SQLException {

        onInsertRow = true;
        super.moveToInsertRow();
    }


    public void updateInt(int columnIndex, int x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(Integer.valueOf(x), columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateInt(columnIndex, x);
    }


    public void updateInt(String columnName, int x) throws SQLException {

        this.updateInt(findColumn(columnName), x);
    }


    public void updateBoolean(int columnIndex, boolean x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(Boolean.valueOf(x), columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateBoolean(columnIndex, x);
    }


    public void updateBoolean(String columnName, boolean x) throws SQLException {

        this.updateBoolean(findColumn(columnName), x);
    }


    public void updateByte(int columnIndex, byte x) throws SQLException {
        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(Byte.valueOf(x), columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateByte(columnIndex, x);
    }


    public void updateByte(String columnName, byte x) throws SQLException {

        this.updateByte(findColumn(columnName), x);
    }


    public void updateShort(int columnIndex, short x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(Short.valueOf(x), columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateShort(columnIndex, x);
    }


    public void updateShort(String columnName, short x) throws SQLException {

        this.updateShort(findColumn(columnName), x);
    }


    public void updateLong(int columnIndex, long x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(Long.valueOf(x), columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateLong(columnIndex, x);
    }


    public void updateLong(String columnName, long x) throws SQLException {

        this.updateLong(findColumn(columnName), x);
    }


    public void updateFloat(int columnIndex, float x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(Float.valueOf(x), columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateFloat(columnIndex, x);
    }


    public void updateFloat(String columnName, float x) throws SQLException {

        this.updateFloat(findColumn(columnName), x);
    }


    public void updateDouble(int columnIndex, double x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(Double.valueOf(x), columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateDouble(columnIndex, x);
    }


    public void updateDouble(String columnName, double x) throws SQLException {

        this.updateDouble(findColumn(columnName), x);
    }


    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateBigDecimal(columnIndex, x);
    }


    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {

        this.updateBigDecimal(findColumn(columnName), x);
    }


    public void updateString(int columnIndex, String x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateString(columnIndex, x);
    }


    public void updateString(String columnName, String x) throws SQLException {

        this.updateString(findColumn(columnName), x);
    }


    public void updateBytes(int columnIndex, byte[] x) throws SQLException {

        boolean bool;
        String val = "";

        Byte[] obj_arr = new Byte[x.length];

        for (int i = 0; i < x.length; i++) {
            obj_arr[i] = Byte.valueOf(x[i]);
            val = val.concat(obj_arr[i].toString());
        }


        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(val, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateBytes(columnIndex, x);
    }


    public void updateBytes(String columnName, byte[] x) throws SQLException {

        this.updateBytes(findColumn(columnName), x);
    }


    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateDate(columnIndex, x);
    }


    public void updateDate(String columnName, java.sql.Date x) throws SQLException {

        this.updateDate(findColumn(columnName), x);
    }


    public void updateTime(int columnIndex, Time x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateTime(columnIndex, x);
    }


    public void updateTime(String columnName, Time x) throws SQLException {

        this.updateTime(findColumn(columnName), x);
    }


    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateTimestamp(columnIndex, x);
    }


    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {

        this.updateTimestamp(findColumn(columnName), x);
    }


    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateAsciiStream(columnIndex, x, length);
    }


    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {

        this.updateAsciiStream(findColumn(columnName), x, length);
    }


    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateCharacterStream(columnIndex, x, length);
    }


    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        this.updateCharacterStream(findColumn(columnName), reader, length);
    }


    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateBinaryStream(columnIndex, x, length);
    }


    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {

        this.updateBinaryStream(findColumn(columnName), x, length);
    }


    public void updateObject(int columnIndex, Object x) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateObject(columnIndex, x);
    }


    public void updateObject(String columnName, Object x) throws SQLException {

        this.updateObject(findColumn(columnName), x);
    }


    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {

        boolean bool;

        if (onInsertRow) {
            if (p != null) {
                bool = p.evaluate(x, columnIndex);

                if (!bool) {
                    throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
                }
            }
        }

        super.updateObject(columnIndex, x, scale);
    }


    public void updateObject(String columnName, Object x, int scale) throws SQLException {

        this.updateObject(findColumn(columnName), x, scale);
    }


    public void insertRow() throws SQLException {

        onInsertRow = false;
        super.insertRow();
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

    static final long serialVersionUID = 6178454588413509360L;
} // end FilteredRowSetImpl class
