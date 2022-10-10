

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.providers;

import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.JdbcRowSetResourceBundle;

import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.*;
import java.io.IOException;
import java.sql.SQLException;


public final class RIXMLProvider extends SyncProvider {


    private String providerID = RIXMLProvider.class.getName();


    private String vendorName = "Oracle Corporation";


    private String versionNumber = "1.0";

    private JdbcRowSetResourceBundle resBundle;

    private XmlReader xmlReader;
    private XmlWriter xmlWriter;


    public RIXMLProvider() {
        providerID = this.getClass().getName();
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public String getProviderID() {
        return providerID;
    }

    // additional methods that sit on top of reader/writer methods back to
    // original datasource. Allow XML state to be written out and in


    public void setXmlReader(XmlReader reader) throws SQLException {
        xmlReader = reader;
    }


    public void setXmlWriter(XmlWriter writer) throws SQLException {
        xmlWriter = writer;
    }


    public XmlReader getXmlReader() throws SQLException {
        return xmlReader;
    }


    public XmlWriter getXmlWriter() throws SQLException {
        return xmlWriter;
    }


    public int getProviderGrade() {
        return SyncProvider.GRADE_NONE;
    }


    public int supportsUpdatableView() {
        return SyncProvider.NONUPDATABLE_VIEW_SYNC;
    }


    public int getDataSourceLock() throws SyncProviderException {
        return SyncProvider.DATASOURCE_NO_LOCK;
    }


    public void setDataSourceLock(int lock) throws SyncProviderException {
        throw new UnsupportedOperationException(resBundle.handleGetObject("rixml.unsupp").toString());
    }


    public RowSetWriter getRowSetWriter() {
        return null;
    }


    public RowSetReader getRowSetReader() {
        return null;
    }


    public String getVersion() {
        return this.versionNumber;
    }


    public String getVendor() {
        return this.vendorName;
    }
}
