package com.github.ulwx.aka.dbutils.database;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 存放一到多关联关系的类
 *
 */
public class QueryMapNestOne2Many extends MapNest {

	private Class nestedClassType; //子关联类的Class
	// 子关联类主键属性
	private String[] nestedBeanPropertyKeys = new String[0];

	// 存放主类的唯一键(复合键,以英文逗号分隔)与子关联对象，子关联对象存入LinkedHashMap，里面存放子关联对象唯一键值与子关联对象的映射
	private Map<String, LinkedHashMap<String, Object>> result = new HashMap<>();

	public QueryMapNestOne2Many() {

	}

	public String[] getNestedBeanPropertyKeys() {
		return nestedBeanPropertyKeys;
	}

	/**
	 * 设置关联属性
	 * 
	 * @param nestedBeanClassType
	 *            对应子关联类的Class
	 * @param toPropertyName
	 *            映射到主类的属性名称
	 * @param nestedBeanPropertyKeys
	 *            子关联类的主键属性，如果为复合主键，以","隔开
	 * @param sqlPrefix
	 *            子关联类对应sql语句的前缀
	 * @param toBeanPros
	 *            子关联类所映射的属性名，如果为null，子关联类的所有属性全部映射
	 */
	public void set(Class nestedBeanClassType, String toPropertyName,
			String[] nestedBeanPropertyKeys, String sqlPrefix, String[] toBeanPros) {

		this.toPros = toBeanPros;
		this.nestedClassType = nestedBeanClassType;
		this.prefix = sqlPrefix;
		this.nestedBeanPropertyKeys = nestedBeanPropertyKeys;
		this.toPropertyName = toPropertyName;
		this.mapRelation = ONE_TO_MANY;
	}

	/**
	 * 设置关联属性
	 * 
	 * @param nestedBeanClassType
	 *            对应子关联类的Class
	 * @param toPropertyName
	 *            对应子关联类属性名
	 * @param nestedBeanPropertyKeys
	 *            子关联类的主键属性，如果为复合主键，以","隔开
	 * @param sqlPrefix
	 *            子关联类对应sql语句的前缀
	 * @param toBeanPros
	 *            子关联类所映射的属性名，如果为null，子关联类的所有属性全部映射
	 */
	public QueryMapNestOne2Many(Class nestedBeanClassType,
			String toPropertyName, String[] nestedBeanPropertyKeys, String sqlPrefix,
			String[] toBeanPros) {
		// QueryMapNest(String[] toPros, String nestedBeanName, String prefix)
		this.toPros = toBeanPros;
		this.nestedClassType = nestedBeanClassType;
		this.prefix = sqlPrefix;
		this.toPropertyName = toPropertyName;
		this.nestedBeanPropertyKeys = nestedBeanPropertyKeys;
		this.mapRelation = ONE_TO_MANY;

	}

	public Map<String, LinkedHashMap<String, Object>> getResult() {
		return result;
	}

	public Class getNestedClassType() {
		return nestedClassType;
	}


	public static void main(String[] args) {

	}

}
