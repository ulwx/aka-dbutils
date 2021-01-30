package com.github.ulwx.database;

public class MapNest {
	public static int ONE_TO_ONE = 1;
	public static int ONE_TO_MANY = 2;

	protected String[] toPros = null;// 对应于javabean的属性名
	protected String toPropertyName = null;
	protected String prefix = null;
	protected int mapRelation ;
	
	public String[] getToPros() {
		return toPros; 
	}
	public String getToPropertyName() {
		return toPropertyName;
	}
	public String getPrefix() {
		return prefix;
	}

	public  int getMapRelation() {
		return mapRelation;
	}
	
	
}
