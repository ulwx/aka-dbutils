

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.JdbcRowSetResourceBundle;

import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlWriter;
import java.io.*;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Stack;


public class WebRowSetXmlWriter implements XmlWriter, Serializable {


    private transient Writer writer;


    private Stack<String> stack;

    private JdbcRowSetResourceBundle resBundle;

    public WebRowSetXmlWriter() {

        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public void writeXML(WebRowSet caller, Writer wrt)
            throws SQLException {

        // create a new stack for tag checking.
        stack = new Stack<>();
        writer = wrt;
        writeRowSet(caller);
    }


    public void writeXML(WebRowSet caller, OutputStream oStream)
            throws SQLException {

        // create a new stack for tag checking.
        stack = new Stack<>();
        writer = new OutputStreamWriter(oStream);
        writeRowSet(caller);
    }


    private void writeRowSet(WebRowSet caller) throws SQLException {

        try {

            startHeader();

            writeProperties(caller);
            writeMetaData(caller);
            writeData(caller);

            endHeader();

        } catch (IOException ex) {
            throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.ioex").toString(), ex.getMessage()));
        }
    }

    private void startHeader() throws IOException {

        setTag("webRowSet");
        writer.write("<?xml version=\"1.0\"?>\n");
        writer.write("<webRowSet xmlns=\"http://java.sun.com/xml/ns/jdbc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        writer.write("xsi:schemaLocation=\"http://java.sun.com/xml/ns/jdbc http://java.sun.com/xml/ns/jdbc/webrowset.xsd\">\n");
    }

    private void endHeader() throws IOException {
        endTag("webRowSet");
    }


    private void writeProperties(WebRowSet caller) throws IOException {

        beginSection("properties");

        try {
            propString("command", processSpecialCharacters(caller.getCommand()));
            propInteger("concurrency", caller.getConcurrency());
            propString("datasource", caller.getDataSourceName());
            propBoolean("escape-processing",
                    caller.getEscapeProcessing());

            try {
                propInteger("fetch-direction", caller.getFetchDirection());
            } catch (SQLException sqle) {
                // it may be the case that fetch direction has not been set
                // fetchDir  == 0
                // in that case it will throw a SQLException.
                // To avoid that catch it here
            }

            propInteger("fetch-size", caller.getFetchSize());
            propInteger("isolation-level",
                    caller.getTransactionIsolation());

            beginSection("key-columns");

            int[] kc = caller.getKeyColumns();
            for (int i = 0; kc != null && i < kc.length; i++)
                propInteger("column", kc[i]);

            endSection("key-columns");

            //Changed to beginSection and endSection for maps for proper indentation
            beginSection("map");
            Map<String, Class<?>> typeMap = caller.getTypeMap();
            if (typeMap != null) {
                for (Map.Entry<String, Class<?>> mm : typeMap.entrySet()) {
                    propString("type", mm.getKey());
                    propString("class", mm.getValue().getName());
                }
            }
            endSection("map");

            propInteger("max-field-size", caller.getMaxFieldSize());
            propInteger("max-rows", caller.getMaxRows());
            propInteger("query-timeout", caller.getQueryTimeout());
            propBoolean("read-only", caller.isReadOnly());

            int itype = caller.getType();
            String strType = "";

            if (itype == 1003) {
                strType = "ResultSet.TYPE_FORWARD_ONLY";
            } else if (itype == 1004) {
                strType = "ResultSet.TYPE_SCROLL_INSENSITIVE";
            } else if (itype == 1005) {
                strType = "ResultSet.TYPE_SCROLL_SENSITIVE";
            }

            propString("rowset-type", strType);

            propBoolean("show-deleted", caller.getShowDeleted());
            propString("table-name", caller.getTableName());
            propString("url", caller.getUrl());

            beginSection("sync-provider");
            // Remove the string after "@xxxx"
            // before writing it to the xml file.
            String strProviderInstance = (caller.getSyncProvider()).toString();
            String strProvider = strProviderInstance.substring(0, (caller.getSyncProvider()).toString().indexOf("@"));

            propString("sync-provider-name", strProvider);
            propString("sync-provider-vendor", "Oracle Corporation");
            propString("sync-provider-version", "1.0");
            propInteger("sync-provider-grade", caller.getSyncProvider().getProviderGrade());
            propInteger("data-source-lock", caller.getSyncProvider().getDataSourceLock());

            endSection("sync-provider");

        } catch (SQLException ex) {
            throw new IOException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), ex.getMessage()));
        }

        endSection("properties");
    }


    private void writeMetaData(WebRowSet caller) throws IOException {
        int columnCount;

        beginSection("metadata");

        try {

            ResultSetMetaData rsmd = caller.getMetaData();
            columnCount = rsmd.getColumnCount();
            propInteger("column-count", columnCount);

            for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
                beginSection("column-definition");

                propInteger("column-index", colIndex);
                propBoolean("auto-increment", rsmd.isAutoIncrement(colIndex));
                propBoolean("case-sensitive", rsmd.isCaseSensitive(colIndex));
                propBoolean("currency", rsmd.isCurrency(colIndex));
                propInteger("nullable", rsmd.isNullable(colIndex));
                propBoolean("signed", rsmd.isSigned(colIndex));
                propBoolean("searchable", rsmd.isSearchable(colIndex));
                propInteger("column-display-size", rsmd.getColumnDisplaySize(colIndex));
                propString("column-label", rsmd.getColumnLabel(colIndex));
                propString("column-name", rsmd.getColumnName(colIndex));
                propString("schema-name", rsmd.getSchemaName(colIndex));
                propInteger("column-precision", rsmd.getPrecision(colIndex));
                propInteger("column-scale", rsmd.getScale(colIndex));
                propString("table-name", rsmd.getTableName(colIndex));
                propString("catalog-name", rsmd.getCatalogName(colIndex));
                propInteger("column-type", rsmd.getColumnType(colIndex));
                propString("column-type-name", rsmd.getColumnTypeName(colIndex));

                endSection("column-definition");
            }
        } catch (SQLException ex) {
            throw new IOException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), ex.getMessage()));
        }

        endSection("metadata");
    }


    private void writeData(WebRowSet caller) throws IOException {
        ResultSet rs;

        try {
            ResultSetMetaData rsmd = caller.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int i;

            beginSection("data");

            caller.beforeFirst();
            caller.setShowDeleted(true);
            while (caller.next()) {
                if (caller.rowDeleted() && caller.rowInserted()) {
                    beginSection("modifyRow");
                } else if (caller.rowDeleted()) {
                    beginSection("deleteRow");
                } else if (caller.rowInserted()) {
                    beginSection("insertRow");
                } else {
                    beginSection("currentRow");
                }

                for (i = 1; i <= columnCount; i++) {
                    if (caller.columnUpdated(i)) {
                        rs = caller.getOriginalRow();
                        rs.next();
                        beginTag("columnValue");
                        writeValue(i, (RowSet) rs);
                        endTag("columnValue");
                        beginTag("updateRow");
                        writeValue(i, caller);
                        endTag("updateRow");
                    } else {
                        beginTag("columnValue");
                        writeValue(i, caller);
                        endTag("columnValue");
                    }
                }

                endSection(); // this is unchecked
            }
            endSection("data");
        } catch (SQLException ex) {
            throw new IOException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), ex.getMessage()));
        }
    }

    private void writeValue(int idx, RowSet caller) throws IOException {
        try {
            int type = caller.getMetaData().getColumnType(idx);

            switch (type) {
                case Types.BIT:
                case Types.BOOLEAN:
                    boolean b = caller.getBoolean(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeBoolean(b);
                    break;
                case Types.TINYINT:
                case Types.SMALLINT:
                    short s = caller.getShort(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeShort(s);
                    break;
                case Types.INTEGER:
                    int i = caller.getInt(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeInteger(i);
                    break;
                case Types.BIGINT:
                    long l = caller.getLong(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeLong(l);
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    float f = caller.getFloat(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeFloat(f);
                    break;
                case Types.DOUBLE:
                    double d = caller.getDouble(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeDouble(d);
                    break;
                case Types.NUMERIC:
                case Types.DECIMAL:
                    writeBigDecimal(caller.getBigDecimal(idx));
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    break;
                case Types.DATE:
                    Date date = caller.getDate(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeLong(date.getTime());
                    break;
                case Types.TIME:
                    Time time = caller.getTime(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeLong(time.getTime());
                    break;
                case Types.TIMESTAMP:
                    Timestamp ts = caller.getTimestamp(idx);
                    if (caller.wasNull())
                        writeNull();
                    else
                        writeLong(ts.getTime());
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    writeStringData(caller.getString(idx));
                    break;
                default:
                    System.out.println(resBundle.handleGetObject("wsrxmlwriter.notproper").toString());
                    //Need to take care of BLOB, CLOB, Array, Ref here
            }
        } catch (SQLException ex) {
            throw new IOException(resBundle.handleGetObject("wrsxmlwriter.failedwrite").toString() + ex.getMessage());
        }
    }


    private void beginSection(String tag) throws IOException {
        // store the current tag
        setTag(tag);

        writeIndent(stack.size());

        // write it out
        writer.write("<" + tag + ">\n");
    }


    private void endSection(String tag) throws IOException {
        writeIndent(stack.size());

        String beginTag = getTag();

        if (beginTag.indexOf("webRowSet") != -1) {
            beginTag = "webRowSet";
        }

        if (tag.equals(beginTag)) {
            // get the current tag and write it out
            writer.write("</" + beginTag + ">\n");
        } else {
            ;
        }
        writer.flush();
    }

    private void endSection() throws IOException {
        writeIndent(stack.size());

        // get the current tag and write it out
        String beginTag = getTag();
        writer.write("</" + beginTag + ">\n");

        writer.flush();
    }

    private void beginTag(String tag) throws IOException {
        // store the current tag
        setTag(tag);

        writeIndent(stack.size());

        // write tag out
        writer.write("<" + tag + ">");
    }

    private void endTag(String tag) throws IOException {
        String beginTag = getTag();
        if (tag.equals(beginTag)) {
            // get the current tag and write it out
            writer.write("</" + beginTag + ">\n");
        } else {
            ;
        }
        writer.flush();
    }

    private void emptyTag(String tag) throws IOException {
        // write an emptyTag
        writer.write("<" + tag + "/>");
    }

    private void setTag(String tag) {
        // add the tag to stack
        stack.push(tag);
    }

    private String getTag() {
        return stack.pop();
    }

    private void writeNull() throws IOException {
        emptyTag("null");
    }

    private void writeStringData(String s) throws IOException {
        if (s == null) {
            writeNull();
        } else if (s.equals("")) {
            writeEmptyString();
        } else {

            s = processSpecialCharacters(s);

            writer.write(s);
        }
    }

    private void writeString(String s) throws IOException {
        if (s != null) {
            writer.write(s);
        } else {
            writeNull();
        }
    }


    private void writeShort(short s) throws IOException {
        writer.write(Short.toString(s));
    }

    private void writeLong(long l) throws IOException {
        writer.write(Long.toString(l));
    }

    private void writeInteger(int i) throws IOException {
        writer.write(Integer.toString(i));
    }

    private void writeBoolean(boolean b) throws IOException {
        writer.write(Boolean.valueOf(b).toString());
    }

    private void writeFloat(float f) throws IOException {
        writer.write(Float.toString(f));
    }

    private void writeDouble(double d) throws IOException {
        writer.write(Double.toString(d));
    }

    private void writeBigDecimal(java.math.BigDecimal bd) throws IOException {
        if (bd != null)
            writer.write(bd.toString());
        else
            emptyTag("null");
    }

    private void writeIndent(int tabs) throws IOException {
        // indent...
        for (int i = 1; i < tabs; i++) {
            writer.write("  ");
        }
    }

    private void propString(String tag, String s) throws IOException {
        beginTag(tag);
        writeString(s);
        endTag(tag);
    }

    private void propInteger(String tag, int i) throws IOException {
        beginTag(tag);
        writeInteger(i);
        endTag(tag);
    }

    private void propBoolean(String tag, boolean b) throws IOException {
        beginTag(tag);
        writeBoolean(b);
        endTag(tag);
    }

    private void writeEmptyString() throws IOException {
        emptyTag("emptyString");
    }


    public boolean writeData(RowSetInternal caller) {
        return false;
    }


    private String processSpecialCharacters(String s) {

        if (s == null) {
            return null;
        }
        char[] charStr = s.toCharArray();
        String specialStr = "";

        for (int i = 0; i < charStr.length; i++) {
            if (charStr[i] == '&') {
                specialStr = specialStr.concat("&amp;");
            } else if (charStr[i] == '<') {
                specialStr = specialStr.concat("&lt;");
            } else if (charStr[i] == '>') {
                specialStr = specialStr.concat("&gt;");
            } else if (charStr[i] == '\'') {
                specialStr = specialStr.concat("&apos;");
            } else if (charStr[i] == '\"') {
                specialStr = specialStr.concat("&quot;");
            } else {
                specialStr = specialStr.concat(String.valueOf(charStr[i]));
            }
        }

        s = specialStr;
        return s;
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

    static final long serialVersionUID = 7163134986189677641L;
}
