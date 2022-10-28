package com.github.ulwx.aka.dbutils.database.sql;

import com.github.ulwx.aka.dbutils.database.DataBaseSet;
import com.github.ulwx.aka.dbutils.tool.support.PropertyUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanUtils {

    public static Object setOne2One(String db, Object nestedObj, String[] toPros,
                                    String prefix, DataBaseSet rs) throws Exception {

        if (toPros != null) {// 部分映射

            for (int j = 0; toPros != null && j < toPros.length; j++) {

                String toPro = toPros[j];

                Class type = PropertyUtil.getPropertyType(nestedObj, toPro);
                Object value = SqlUtils.getValueFromResult(db, nestedObj.getClass(), type,
                        prefix, toPro, rs.getResultSet());

                PropertyUtil.setProperty(nestedObj, toPro, value);
            }
        } else { // 全部映射
            Map map = PropertyUtil.describe(nestedObj);
            Set set = map.keySet();
            Iterator i = set.iterator();
            while (i.hasNext()) {
                String name = (String) i.next();
                //
                Class type = PropertyUtil.getPropertyType(nestedObj, name);

                Object value = SqlUtils.getValueFromResult(db, nestedObj.getClass(), type, prefix, name,
                        rs.getResultSet());

                PropertyUtil.setProperty(nestedObj, name, value);

            }

        }

        return nestedObj;

    }

    public static String getKeyValue(Object bean, String[] keys) {

        String nestKeyValueStr = "";
        for (int q = 0; q < keys.length; q++) {
            Object obj = PropertyUtil.getProperty(bean, keys[q].trim());
            if (obj == null)
                return null;
            nestKeyValueStr = nestKeyValueStr + obj;
            if (q == keys.length - 1) {
                break;
            }
            nestKeyValueStr = nestKeyValueStr + ",";
        }
        return nestKeyValueStr;
    }

    public static String getKeyValue(String db, Object bean, String sqlPrefix, String[] keys,
                                     DataBaseSet rs) {

        String nestkeyStr = "";
        for (int n = 0; n < keys.length; n++) {
            String nkey = keys[n].trim();
            Class keyClass = PropertyUtil.getPropertyType(bean, nkey);

            Object keyValue = SqlUtils.getValueFromResult(db, bean.getClass(), keyClass, sqlPrefix
                    , nkey, rs.getResultSet());
            if (keyValue == null) {
                return null;
            }
            if (n == keys.length - 1) {
                nestkeyStr = nestkeyStr + keyValue;
            } else {
                nestkeyStr = nestkeyStr + keyValue + ",";
            }

        }
        return nestkeyStr;

    }

    /**
     * 指定javaBean的属性
     *
     * @param bean            javaBean对象
     * @param keyNames        属性名称
     * @param compareToValues 以英文分号隔开
     * @return 是否相等
     */
    public static boolean equals(Object bean, String[] keyNames, String compareToValues) {
        boolean b = false;
        try {

            String toValues = "";
            for (int i = 0; i < keyNames.length; i++) {
                Object value = PropertyUtil.getProperty(bean,
                        keyNames[i]);

                if (value == null)
                    return false;
                if (i == keyNames.length - 1) {
                    toValues = toValues + value;
                } else {
                    toValues = toValues + value + ",";
                }
            }

            if (compareToValues.equals(toValues)) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;

    }


}
