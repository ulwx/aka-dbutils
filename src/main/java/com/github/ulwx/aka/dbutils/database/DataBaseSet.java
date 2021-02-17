package com.github.ulwx.aka.dbutils.database;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;

public class DataBaseSet {

	private ResultSet rs = null;

	public int size() throws DbException {
		if (rs instanceof CachedRowSet) {
			return ((CachedRowSet) rs).size();
		} else {
			throw new DbException("not supported!");
		}
	}

	public RowSet getRowSet() {
		if (rs instanceof RowSet)
			return (RowSet) rs;
		return null;
	}

	public ResultSet getResultSet() {
		return rs;
	}

	public DataBaseSet(ResultSet rs) {
		this.rs = rs;
	}

	public String getString(int columnIndex) throws DbException {
		String s = null;
		try {
			s = rs.getString(columnIndex);
			if (s == null) {
				return "";
			}
			s = s.trim();
		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return s;
	}

	public String getString(String columnName) throws DbException {

		String s = null;
		try {
			s = rs.getString(columnName);
			if (s == null) {
				return "";
			}
			s = s.trim();
		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return s;
	}

	public boolean next() throws DbException {
		if (rs == null)
			return false;

		try {
			return rs.next();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DbException(e);
		}
	}
	public java.util.Date getDateTime(String columnName) throws DbException {
		Timestamp b;
		try {
			b = rs.getTimestamp(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public int getInt(String columnName) throws DbException {
		int s;
		try {
			s = rs.getInt(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return s;

	}

	public Date getDate(String columnName) throws DbException {
		Date s;
		try {
			s = rs.getDate(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return s;

	}

	public long getLong(int columnIndex) throws DbException {
		long b;
		try {
			b = rs.getLong(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public long getLong(String columnName) throws DbException {
		long s;
		try {
			s = rs.getLong(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return s;

	}

	public void close() throws DbException {
		try {
			rs.close();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return;
	}

	public boolean getBoolean(int columnIndex) throws DbException {
		boolean b;
		try {
			b = rs.getBoolean(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public byte getByte(int columnIndex) throws DbException {
		byte b;
		try {
			b = rs.getByte(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public short getShort(int columnIndex) throws DbException {
		short b;
		try {
			b = rs.getShort(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public int getInt(int columnIndex) throws DbException {
		int b;
		try {
			b = rs.getInt(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public float getFloat(int columnIndex) throws DbException {
		float b;
		try {
			b = rs.getFloat(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public double getDouble(int columnIndex) throws DbException {
		double b;
		try {
			b = rs.getDouble(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public byte[] getBytes(int columnIndex) throws DbException {
		byte[] b;
		try {
			b = rs.getBytes(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Time getTime(int columnIndex) throws DbException {
		Time b;
		try {
			b = rs.getTime(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Timestamp getTimestamp(int columnIndex) throws DbException {
		Timestamp b;
		try {
			b = rs.getTimestamp(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.io.InputStream getAsciiStream(int columnIndex)
			throws DbException {
		java.io.InputStream b;
		try {
			b = rs.getAsciiStream(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.io.InputStream getBinaryStream(int columnIndex)
			throws DbException {
		java.io.InputStream b;
		try {
			b = rs.getBinaryStream(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean getBoolean(String columnName) throws DbException {
		boolean b;
		try {
			b = rs.getBoolean(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public byte getByte(String columnName) throws DbException {
		byte b;
		try {
			b = rs.getByte(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public short getShort(String columnName) throws DbException {
		short b;
		try {
			b = rs.getShort(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public float getFloat(String columnName) throws DbException {
		float b;
		try {
			b = rs.getFloat(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public double getDouble(String columnName) throws DbException {
		double b;
		try {
			b = rs.getDouble(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public BigDecimal getBigDecimal(String columnName, int scale)
			throws DbException {
		BigDecimal b;
		try {
			b = rs.getBigDecimal(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public byte[] getBytes(String columnName) throws DbException {
		byte[] b;
		try {
			b = rs.getBytes(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Time getTime(String columnName) throws DbException {
		Time b;
		try {
			b = rs.getTime(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Timestamp getTimestamp(String columnName)
			throws DbException {
		Timestamp b;
		try {
			b = rs.getTimestamp(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.io.InputStream getAsciiStream(String columnName)
			throws DbException {
		java.io.InputStream b;
		try {
			b = rs.getAsciiStream(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.io.InputStream getBinaryStream(String columnName)
			throws DbException {
		java.io.InputStream b;
		try {
			b = rs.getBinaryStream(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public ResultSetMetaData getMetaData() throws DbException {
		ResultSetMetaData b;
		try {
			b = rs.getMetaData();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Object getObject(int columnIndex) throws DbException {
		Object b;
		try {
			b = rs.getObject(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Object getObject(String columnName) throws DbException {
		Object b;
		try {
			b = rs.getObject(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.io.Reader getCharacterStream(int columnIndex)
			throws DbException {
		java.io.Reader b;
		try {
			b = rs.getCharacterStream(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.io.Reader getCharacterStream(String columnName)
			throws DbException {
		java.io.Reader b;
		try {
			b = rs.getCharacterStream(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public BigDecimal getBigDecimal(int columnIndex) throws DbException {
		BigDecimal b;
		try {
			b = rs.getBigDecimal(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public BigDecimal getBigDecimal(String columnName) throws DbException {
		BigDecimal b;
		try {
			b = rs.getBigDecimal(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean isBeforeFirst() throws DbException {
		boolean b;
		try {
			b = rs.isBeforeFirst();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean isAfterLast() throws DbException {
		boolean b;
		try {
			b = rs.isAfterLast();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean isFirst() throws DbException {
		boolean b;
		try {
			b = rs.isFirst();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean isLast() throws DbException {
		boolean b;
		try {
			b = rs.isLast();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public void beforeFirst() throws DbException {

		try {
			rs.beforeFirst();

		} catch (Exception e) {

			throw new DbException(e);
		}
		return;
	}

	public void afterLast() throws DbException {

		try {
			rs.afterLast();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return;
	}

	public boolean first() throws DbException {
		boolean b;
		try {
			b = rs.first();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean last() throws DbException {
		boolean b;
		try {
			b = rs.last();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public int getRow() throws DbException {
		int b;
		try {
			b = rs.getRow();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean absolute(int row) throws DbException {
		boolean b;
		try {
			b = rs.absolute(row);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean relative(int rows) throws DbException {
		boolean b;
		try {
			b = rs.relative(rows);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public boolean previous() throws DbException {
		boolean b;
		try {
			b = rs.previous();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public void setFetchDirection(int direction) throws DbException {

		try {
			rs.setFetchDirection(direction);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}

	}

	public int getFetchDirection() throws DbException {
		int b;
		try {
			b = rs.getFetchDirection();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public void setFetchSize(int rows) throws DbException {

		try {
			rs.setFetchSize(rows);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}

	}

	public int getFetchSize() throws DbException {
		int b;
		try {
			b = rs.getFetchSize();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public int getType() throws DbException {
		int b;
		try {
			b = rs.getType();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public int getConcurrency() throws DbException {
		int b;
		try {
			b = rs.getConcurrency();

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Object getObject(int i, java.util.Map<String, Class<?>> map)
			throws DbException {
		Object b;
		try {
			b = rs.getObject(i, map);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Ref getRef(int i) throws DbException {
		Ref b;
		try {
			b = rs.getRef(i);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Blob getBlob(int i) throws DbException {
		Blob b;
		try {
			b = rs.getBlob(i);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Clob getClob(int i) throws DbException {
		Clob b;
		try {
			b = rs.getClob(i);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Array getArray(int i) throws DbException {
		Array b;
		try {
			b = rs.getArray(i);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Object getObject(String colName, java.util.Map<String, Class<?>> map)
			throws DbException {
		Object b;
		try {
			b = rs.getObject(colName, map);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Ref getRef(String colName) throws DbException {
		Ref b;
		try {
			b = rs.getRef(colName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Blob getBlob(String colName) throws DbException {
		Blob b;
		try {
			b = rs.getBlob(colName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Clob getClob(String colName) throws DbException {
		Clob b;
		try {
			b = rs.getClob(colName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Array getArray(String colName) throws DbException {
		Array b;
		try {
			b = rs.getArray(colName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Date getDate(int columnIndex, Calendar cal)
			throws DbException {
		Date b;
		try {
			b = rs.getDate(columnIndex, cal);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Date getDate(String columnName, Calendar cal)
			throws DbException {
		Date b;
		try {
			b = rs.getDate(columnName, cal);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Time getTime(int columnIndex, Calendar cal)
			throws DbException {
		Time b;
		try {
			b = rs.getTime(columnIndex, cal);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Time getTime(String columnName, Calendar cal)
			throws DbException {
		Time b;
		try {
			b = rs.getTime(columnName, cal);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws DbException {
		Timestamp b;
		try {
			b = rs.getTimestamp(columnIndex, cal);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws DbException {
		Timestamp b;
		try {
			b = rs.getTimestamp(columnName, cal);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.net.URL getURL(int columnIndex) throws DbException {
		java.net.URL b;
		try {
			b = rs.getURL(columnIndex);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

	public java.net.URL getURL(String columnName) throws DbException {
		java.net.URL b;
		try {
			b = rs.getURL(columnName);

		} catch (Exception e) {
			// TODO: handle exception
			throw new DbException(e);
		}
		return b;
	}

}
