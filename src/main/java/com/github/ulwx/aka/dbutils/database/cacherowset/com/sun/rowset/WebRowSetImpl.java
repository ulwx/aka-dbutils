

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset;

import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal.WebRowSetXmlReader;
import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal.WebRowSetXmlWriter;

import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncProvider;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;


public class WebRowSetImpl extends CachedRowSetImpl implements WebRowSet {


    private WebRowSetXmlReader xmlReader;


    private WebRowSetXmlWriter xmlWriter;


    private int curPosBfrWrite;

    private SyncProvider provider;


    public WebRowSetImpl() throws SQLException {
        super();

        // %%%
        // Needs to use to SPI  XmlReader,XmlWriters
        //
        xmlReader = new WebRowSetXmlReader();
        xmlWriter = new WebRowSetXmlWriter();
    }


    @SuppressWarnings("rawtypes")
    public WebRowSetImpl(Hashtable env) throws SQLException {

        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        if (env == null) {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.nullhash").toString());
        }

        String providerName =
                (String) env.get(SyncFactory.ROWSET_SYNC_PROVIDER);

        // set the Reader, this maybe overridden latter
        provider = SyncFactory.getInstance(providerName);

        // xmlReader = provider.getRowSetReader();
        // xmlWriter = provider.getRowSetWriter();
    }


    public void writeXml(ResultSet rs, Writer writer)
            throws SQLException {
        // WebRowSetImpl wrs = new WebRowSetImpl();
        this.populate(rs);

        // Store the cursor position before writing
        curPosBfrWrite = this.getRow();

        this.writeXml(writer);
    }


    public void writeXml(Writer writer) throws SQLException {
        // %%%
        // This will change to a XmlReader, which over-rides the default
        // Xml that is used when a WRS is instantiated.
        // WebRowSetXmlWriter xmlWriter = getXmlWriter();
        if (xmlWriter != null) {

            // Store the cursor position before writing
            curPosBfrWrite = this.getRow();

            xmlWriter.writeXML(this, writer);
        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
        }
    }


    public void readXml(Reader reader) throws SQLException {
        // %%%
        // This will change to a XmlReader, which over-rides the default
        // Xml that is used when a WRS is instantiated.
        //WebRowSetXmlReader xmlReader = getXmlReader();
        try {
            if (reader != null) {
                xmlReader.readXML(this, reader);

                // Position is before the first row
                // The cursor position is to be stored while serializng
                // and deserializing the WebRowSet Object.
                if (curPosBfrWrite == 0) {
                    this.beforeFirst();
                }

                // Return the position back to place prior to callin writeXml
                else {
                    this.absolute(curPosBfrWrite);
                }

            } else {
                throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
            }
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    // Stream based methods


    public void readXml(InputStream iStream) throws SQLException, IOException {
        if (iStream != null) {
            xmlReader.readXML(this, iStream);

            // Position is before the first row
            // The cursor position is to be stored while serializng
            // and deserializing the WebRowSet Object.
            if (curPosBfrWrite == 0) {
                this.beforeFirst();
            }

            // Return the position back to place prior to callin writeXml
            else {
                this.absolute(curPosBfrWrite);
            }

        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
        }
    }


    public void writeXml(OutputStream oStream) throws SQLException, IOException {
        if (xmlWriter != null) {

            // Store the cursor position before writing
            curPosBfrWrite = this.getRow();

            xmlWriter.writeXML(this, oStream);
        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
        }

    }


    public void writeXml(ResultSet rs, OutputStream oStream) throws SQLException, IOException {
        this.populate(rs);

        // Store the cursor position before writing
        curPosBfrWrite = this.getRow();

        this.writeXml(oStream);
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

    static final long serialVersionUID = -8771775154092422943L;
}
