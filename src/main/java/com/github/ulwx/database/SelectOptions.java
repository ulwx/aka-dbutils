package com.github.ulwx.database;

public  class SelectOptions {
	private String select="";
	private String orderBy="" ;
	private String groupBy="";
	private String limit="";
	private String having="";

	/**
	 *   设置select部分有哪些字段需要查询，如果不指定则默认为select * 。
	 *   例如指定为："sum(count_data) as countData, ab_cd as abCd"，as后面的别名对应javaBean里的属性，
	 *   既countData、abCd都为javaBean的属性，as前面的为数据库里的字段。
	 *   或者"countData,abCd"
	 * @return
	 */
	public SelectOptions select(String selectStr) {
		this.select=selectStr;
		return this;
	}
	/**
	 * 设置order by的属性名，以英文逗号分隔。例如指定"abCd asc,mnXy desc"，则abCd,mnXy都为javaBean的属性名称
	 * @return
	 */
	public SelectOptions orderBy(String orderByStr) {

		this.orderBy=orderByStr;
		return this;
	}
	/**
	 * 设置group by的属性名，例如 "abCd asc,mnXy desc"，这里abCd,mnXy对应javaBean的属性名
	 * @param groupByStr
	 * @return
	 */
	public SelectOptions groupBy(String groupByStr) {
		this.groupBy=groupByStr;
		return this;
	}
	/**
	 * 设置having部分，例如 "sum(mnXy)>10"，mnXy为javaBean属性
	 * @param havingStr
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

	public String getOrderBy() {
		return orderBy;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public String getLimit() {
		return limit;
	}

	public String getSelect() {
		return select;
	}

	public String getHaving() {
		return having;
	}
}
