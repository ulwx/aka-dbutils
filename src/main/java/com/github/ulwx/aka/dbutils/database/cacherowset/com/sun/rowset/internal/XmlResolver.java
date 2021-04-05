

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset.internal;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


public class XmlResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId) {
        String schemaName = systemId.substring(systemId.lastIndexOf("/"));

        if (systemId.startsWith("http://java.sun.com/xml/ns/jdbc")) {
            return new InputSource(this.getClass().getResourceAsStream(schemaName));

        } else {
            // use the default behaviour
            return null;
        }


    }
}
