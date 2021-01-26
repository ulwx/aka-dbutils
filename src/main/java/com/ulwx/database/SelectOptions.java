package com.ulwx.database;

import com.ulwx.database.sql.SqlUtils;

public  class SelectOptions {

	private String orderBy="" ;
	private String groupBy="";
	private String limit="";
	private String select="";
	private String having="";

	public static  String md(Class<?> daoClass, String method) {
		String prefix = daoClass.getName();
		return prefix + ".md:" + method;
	}

	public static  String md() {
		StackTraceElement[] StackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement callMethodInfo = StackTraceElements[2];
		String className=callMethodInfo.getClassName();
		String methodName=callMethodInfo.getMethodName();
		
		
	  return className + ".md:" + methodName;
	}
	/**
	 *   设置select部分有哪些查询属性，例如：  "sum(count_data) as countData, ab_cd as abCd"，countData，abCd为javaBean的属性，
	 *   或者"countData,abCd"
	 * @param properties  以英文逗号分隔
	 * @return
	 */
	public SelectOptions select(String selectStr) {
		this.select=selectStr;
		return this;
	}
	/**
	 * 设置order by部分的排序属性，例如 "abCd asc,mnXy desc"，abCd,mnXy为javaBean的属性
	 * @param properties
	 * @param asc
	 * @return
	 */
	public SelectOptions orderBy(String orderByStr) {

		this.orderBy=orderByStr;
		return this;
	}
	/**
	 * 设置group by部分的排序属性，例如 "abCd asc,mnXy desc"，abCd,mnXy为javaBean的属性
	 * @param properties
	 * @param asc
	 * @return
	 */
	public SelectOptions groupBy(String groupByStr) {
		this.groupBy=groupByStr;
		return this;
	}
	/**
	 * 设置having部分的排序属性，例如 "sum(mn_xy)>10"，不支持javaBean属性
	 * @param properties
	 * @param asc
	 * @return
	 */
	public SelectOptions having(String havingStr) {
		this.having=havingStr;
		return this;
	}
	public SelectOptions limit(int n) {
		this.limit=n+"";
		return this;
	}

	public String selectPartString(String dbpoolName,String dataBaseType) {
		return select;
	}


	public  String tailPartString(String dbpoolName,String dataBaseType) {
		String orderStr="";
		if(!this.orderBy.isEmpty()) {
			String[] strs=orderBy.split(",");
			
			for(int i=0; i<strs.length; i++) {
				String s=strs[i].trim();
				String[] temps=s.split(" +");
				
				String part=SqlUtils.dbEscapeLefChar.get(dataBaseType)
						+SqlUtils.getColumName(dbpoolName,temps[0])+SqlUtils.dbEscapeRightChar.get(dataBaseType);
				if(temps.length>=2) {
					part=part+" "+temps[1];
				}
				if(i==0) {
					orderStr=orderStr+""+part;
				}else {
					orderStr=orderStr+","+part;
				}
			}
		}
		String groupByStr="";
		if(!this.groupBy.isEmpty()) {
			String[] strs=groupBy.split(",");
			
			for(int i=0; i<strs.length; i++) {
				String s=strs[i].trim();
				String part=SqlUtils.dbEscapeLefChar.get(dataBaseType)
						+SqlUtils.getColumName(dbpoolName,s)+SqlUtils.dbEscapeRightChar.get(dataBaseType);
				if(i==0) {
					groupByStr=groupByStr+""+part;
				}else {
					groupByStr=groupByStr+","+part;
				}
			}
		}
		String ret="";


		if(!groupByStr.isEmpty()) {
			ret=ret+" group by "+groupByStr;
		}
		if(!this.having.isEmpty()) {
			ret=ret+" having by "+this.having;
		}
		if(!orderStr.isEmpty()) {
			ret=ret+" order by "+orderStr;
		}
		if(!this.limit.isEmpty()) {
			ret=ret+" limit "+this.limit;
		}
		return ret;
	}
	
	
	
}
