

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;





public class XmlErrorHandler extends DefaultHandler {
    public int errorCounter = 0;

    public void error(SAXParseException e) throws SAXException {
        errorCounter++;

    }

    public void fatalError(SAXParseException e) throws SAXException {
        errorCounter++;

    }

    public void warning(SAXParseException exception) throws SAXException {

    }
}
