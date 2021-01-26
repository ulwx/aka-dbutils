package com.ulwx.database.sql;

import com.ulwx.database.DataBaseKeyMap;
import com.ulwx.database.DataBaseSet;
import com.ulwx.tool.support.PropertyUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanUtils {

	public static Object setOne2One(String db,Object nestedObj, String[] toPros,
			String prefix, DataBaseSet rs) throws Exception {

		if (toPros != null) {// 部分映射

			for (int j = 0; toPros != null && j < toPros.length; j++) {

				String toPro = toPros[j];

				Class type = PropertyUtil.getPropertyType(nestedObj, toPro);
				Object value = SqlUtils.getValueFromResult(db,type,
						prefix ,toPro, rs.getResultSet(), DataBaseKeyMap.getMap());

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

				Object value = SqlUtils.getValueFromResult( db,type, prefix ,name,
						rs.getResultSet(),DataBaseKeyMap.getMap());

				PropertyUtil.setProperty(nestedObj, name, value);

			}
			
		}

		return nestedObj;

	}

	public static String getKeyValue(Object bean, String keys)  {
		String[] nestKeys = keys.split(";|,");

		String nestKeyValueStr = "";
		for (int q = 0; q < nestKeys.length; q++) {
			Object obj = PropertyUtil.getProperty(bean, nestKeys[q].trim());
			if (obj == null)
				return null;
			nestKeyValueStr = nestKeyValueStr + obj;
			if (q == nestKeys.length - 1) {
				break;
			}
			nestKeyValueStr = nestKeyValueStr + ",";
		}
		return nestKeyValueStr;
	}

	public static String getKeyValue(String db,Object bean, String sqlPrefix, String key,
			DataBaseSet rs)  {

		String nestkeyStr = "";
		String[] keys = key.split(";|,");

		for (int n = 0; n < keys.length; n++) {
			String nkey = keys[n].trim();
			Class keyClass = PropertyUtil.getPropertyType(bean, nkey);

			Object keyValue = SqlUtils.getValueFromResult(db,keyClass, sqlPrefix
					, nkey, rs.getResultSet(),DataBaseKeyMap.getMap());
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
	 * 
	 * @param bean
	 * @param keyNames
	 * @param compareToValues 以英文分号隔开
	 * @return
	 */
	public static boolean equals(Object bean, String[] keyNames, String compareToValues) {
		boolean b = false;
		try {

			String toValues = "";
			for (int i = 0; i < keyNames.length; i++) {
				Object value = PropertyUtil.getProperty(bean,
						keyNames[i]);

				if(value==null)
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String s = "ssss;;sss";
		System.out.println(s.split(";").length);
		System.out.println(s.split(";")[0]);
		System.out.println(s.split(";")[1]);
		System.out.println(s.split(";")[2]);
	}

}
