package com.github.ulwx.aka.dbutils.database.utils;


import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.dbpool.DBPoolFactory;
import com.github.ulwx.aka.dbutils.database.spring.DBTransInfo;
import com.github.ulwx.aka.dbutils.database.spring.MDataBaseFactory;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

public class DbConst {
    private static Logger log = Logger.getLogger(DbConst.class);

    public static String TableNameRule = "table-name-rule";
    public static String TableColumRule = "table-colum-rule";

    public static class TableNameRules {
        public static String underline_to_camel = "underline_to_camel";
        public static String normal = "normal";
        public static String first_letter_upcase = "first_letter_upcase";
    }

    public static class TableColumRules {
        public static String underline_to_camel = "underline_to_camel";
        public static String normal = "normal";
    }

    public static String getValue(String poolname, String propertyName) {
        if (StringUtils.hasText(poolname)) {
            String[] strs = DBPoolFactory.parseRefDbPoolName(poolname);
            try {
                Map<String, Map<String, String>> maps = DBPoolFactory.getInstance(strs[0]).getReadConfig().getProperties();
                return StringUtils.trim(maps.get(strs[1]).get(propertyName));
            } catch (Exception e) {
                if (e instanceof DbException) throw (DbException) e;
                throw new DbException(e);
            }
        } else {
            return null;
        }
    }

    public static String getTableColumRule() {

        return getTableColumRule(null);
    }

    public static String getTableColumRule(String dbpoolName) {
        DBTransInfo dbTransInfo = DbContext.getDbTransInfo();
        if (dbTransInfo != null) {
            MDataBaseFactory mDataBaseFactory = dbTransInfo.getmDataBaseFactory();
            String tableColumRule = mDataBaseFactory.getTableColumRule();
            if (StringUtils.hasText(tableColumRule)) {
                return tableColumRule;
            } else {
                return TableColumRules.underline_to_camel;
            }
        }
        String str = StringUtils.trim(getValue(dbpoolName, TableColumRule));
        if (str.isEmpty()) {
            return TableColumRules.underline_to_camel;
        }
        return str;
    }

    public static String getTableNameRule() {

        return getTableNameRule(null);
    }

    public static String getTableNameRule(String dbpoolName) {
        DBTransInfo dbTransInfo = DbContext.getDbTransInfo();
        if (dbTransInfo != null) {
            MDataBaseFactory mDataBaseFactory = dbTransInfo.getmDataBaseFactory();
            String tableNameRule = mDataBaseFactory.getTableNameRule();
            if (StringUtils.hasText(tableNameRule)) {
                return tableNameRule;
            } else {
                return TableNameRules.underline_to_camel;
            }
        }
        String str = StringUtils.trim(getValue(dbpoolName, TableNameRule));
        if (str.isEmpty()) {
            return TableNameRules.underline_to_camel;
        }
        return str;
    }

    public static void main(String[] args) throws Exception {
        //System.out.println(CommonFrameProperty.getPoolName(CommonFrameProperty.Area));

    }
}
