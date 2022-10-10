

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.JdbcRowSetResourceBundle;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.SQLException;
import java.text.MessageFormat;


public class WebRowSetXmlReader implements XmlReader, Serializable {


    private JdbcRowSetResourceBundle resBundle;

    public WebRowSetXmlReader() {
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public void readXML(WebRowSet caller, Reader reader) throws SQLException {
        try {
            // Crimson Parser(as in J2SE 1.4.1 is NOT able to handle
            // Reader(s)(FileReader).
            //
            // But getting the file as a Stream works fine. So we are going to take
            // the reader but send it as a InputStream to the parser. Note that this
            // functionality needs to work against any parser
            // Crimson(J2SE 1.4.x) / Xerces(J2SE 1.5.x).
            InputSource is = new InputSource(reader);
            DefaultHandler dh = new XmlErrorHandler();
            XmlReaderContentHandler hndr = new XmlReaderContentHandler((RowSet) caller);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();

            parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

            XMLReader reader1 = parser.getXMLReader();
            reader1.setEntityResolver(new XmlResolver());
            reader1.setContentHandler(hndr);

            reader1.setErrorHandler(dh);

            reader1.parse(is);

        } catch (SAXParseException err) {
            System.out.println(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), new Object[]{err.getMessage(), err.getLineNumber(), err.getSystemId()}));
            err.printStackTrace();
            throw new SQLException(err.getMessage());

        } catch (SAXException e) {
            Exception x = e;
            if (e.getException() != null)
                x = e.getException();
            x.printStackTrace();
            throw new SQLException(x.getMessage());

        }

        // Will be here if trying to write beyond the RowSet limits

        catch (ArrayIndexOutOfBoundsException aie) {
            throw new SQLException(resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
        } catch (Throwable e) {
            throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.readxml").toString(), e.getMessage()));
        }

    }


    public void readXML(WebRowSet caller, InputStream iStream) throws SQLException {
        try {
            InputSource is = new InputSource(iStream);
            DefaultHandler dh = new XmlErrorHandler();

            XmlReaderContentHandler hndr = new XmlReaderContentHandler((RowSet) caller);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);

            SAXParser parser = factory.newSAXParser();

            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");

            XMLReader reader1 = parser.getXMLReader();
            reader1.setEntityResolver(new XmlResolver());
            reader1.setContentHandler(hndr);

            reader1.setErrorHandler(dh);

            reader1.parse(is);

        } catch (SAXParseException err) {
            System.out.println(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), new Object[]{err.getLineNumber(), err.getSystemId()}));
            System.out.println("   " + err.getMessage());
            err.printStackTrace();
            throw new SQLException(err.getMessage());

        } catch (SAXException e) {
            Exception x = e;
            if (e.getException() != null)
                x = e.getException();
            x.printStackTrace();
            throw new SQLException(x.getMessage());

        }

        // Will be here if trying to write beyond the RowSet limits

        catch (ArrayIndexOutOfBoundsException aie) {
            throw new SQLException(resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
        } catch (Throwable e) {
            throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.readxml").toString(), e.getMessage()));
        }
    }


    public void readData(RowSetInternal caller) {
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

    static final long serialVersionUID = -9127058392819008014L;
}
