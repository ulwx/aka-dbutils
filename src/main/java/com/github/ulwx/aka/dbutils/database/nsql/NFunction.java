package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;

public class NFunction {

	public static boolean isNotEmpty(Object obj){
		if(obj==null) return false;
		if(obj instanceof String){
			if(obj.equals("")){
				return false;
			}
			
		}else if(obj.getClass().isArray()){//如果是数组
			if(Array.getLength(obj)==0){
				return false;
			}
		}else if(obj instanceof Collection) {
			if(((Collection<?>)obj).size()==0) {
				return false;
			}
		}
		return true;
		
	}
	
	public static boolean isNull(Object obj) {
		if(obj==null) return true;
		return false;
	}
	
	public static String trim(String str,String trimStr) {
		return StringUtils.trimString(str, trimStr);
	}
	public static String ltrim(String str,String trimStr) {
		return StringUtils.trimLeadingString(str, trimStr);
	}
	
	public static String rtrim(String str,String trimStr) {
		return StringUtils.trimTailString(str, trimStr);
	}
	

}
