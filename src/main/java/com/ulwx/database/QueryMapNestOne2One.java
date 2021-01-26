package com.ulwx.database;

/**
 * 
 *存放一到一关联关系的类
 * 
 */
public class QueryMapNestOne2One extends MapNest {

	public QueryMapNestOne2One() {
	}

	/**
	 * 
	 * @param toBeanPros
	 *            存放子关联类需要映射的属性名，如果为空，表明全映射
	 * @param nestedBeanName
	 *             对子关联类属性名
	 * @param sqlPrefix
	 *            sql前缀
	 */
	public QueryMapNestOne2One(String[] toBeanPros, String toPropertyName,
			String sqlPrefix) {
		this.toPros = toBeanPros;
		this.toPropertyName = toPropertyName;
		this.prefix = sqlPrefix;
		this.mapRelation = ONE_TO_ONE;
	}

	/**
	 * 设置一到一关系
	 * 
	 * @param toBeanPros
	 *            存放子关联类需要映射的属性名，如果为空，表明全映射
	 * @param nestedBeanName
	 *             对应子关联类属性名
	 * @param sqlPrefix
	 *            sql前缀
	 */
	public void set(String[] toBeanPros, String toPropertyName, String sqlPrefix) {
		this.toPros = toBeanPros;
		this.toPropertyName = toPropertyName;
		this.prefix = sqlPrefix;
		this.mapRelation = ONE_TO_ONE;
	}

}
