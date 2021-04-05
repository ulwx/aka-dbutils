

package com.github.ulwx.aka.dbutils.database.cacherowset.com.sun.rowset;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;



public class JdbcRowSetResourceBundle implements Serializable {


    private static String fileName;


    private transient PropertyResourceBundle propResBundle;


    private static volatile JdbcRowSetResourceBundle jpResBundle;


    private static final String PROPERTIES = "properties";


    private static final String UNDERSCORE = "_";


    private static final String DOT = ".";


    private static final String SLASH = "/";


    private static final String PATH = JdbcRowSetResourceBundle.class.getPackage().getName() + "/RowSetResourceBundle";


    private JdbcRowSetResourceBundle() throws IOException {
        // Try to load the resource bundle according
        // to the locale. Else if no bundle found according
        // to the locale load the default.

        // In default case the default locale resource bundle
        // should always be loaded else it
        // will be difficult to throw appropriate
        // exception string messages.
        Locale locale = Locale.getDefault();

        // Load appropriate bundle according to locale
        propResBundle = (PropertyResourceBundle) ResourceBundle.getBundle(PATH,
                locale, Thread.currentThread().getContextClassLoader());

    }


    public static JdbcRowSetResourceBundle getJdbcRowSetResourceBundle()
            throws IOException {

        if (jpResBundle == null) {
            synchronized (JdbcRowSetResourceBundle.class) {
                if (jpResBundle == null) {
                    jpResBundle = new JdbcRowSetResourceBundle();
                } //end if
            } //end synchronized block
        } //end if
        return jpResBundle;
    }


    @SuppressWarnings("rawtypes")
    public Enumeration getKeys() {
        return propResBundle.getKeys();
    }



    public Object handleGetObject(String key) {
        return propResBundle.handleGetObject(key);
    }

    static final long serialVersionUID = 436199386225359954L;
}
