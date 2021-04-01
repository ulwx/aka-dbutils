package com.github.ulwx.aka.dbutils.database;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 存放一到多关联关系的类
 */
public class QueryMapNestOne2Many extends MapNest {

    private Class nestedClassType; //子关联类的Class
    // 子关联类主键属性
    private String[] nestedBeanPropertyKeys = new String[0];

    // 存放主类的唯一键(复合键,以英文逗号分隔)与子关联对象，子关联对象存入LinkedHashMap，里面存放子关联对象唯一键值与子关联对象的映射
    private Map<String, LinkedHashMap<String, Object>> result = new LinkedHashMap<>();

    public QueryMapNestOne2Many() {

    }

    public String[] getNestedBeanPropertyKeys() {
        return nestedBeanPropertyKeys;
    }

    /**
     * 设置关联属性
     *
     * @param nestedBeanClassType    对应子关联类的Class类型
     * @param toPropertyName         映射到主类的哪个属性名称，即关联属性。属性类型必须为List<X>类型，X为子关联类型，List里存放关联对象。
     * @param nestedBeanPropertyKeys 子关联类里哪些属性构成主键属性（可以是多个属性构成复合主键），如果为复合主键，以","隔开。
     *                               子关联属性其列表里关联对象的去重
     * @param sqlPrefix              指定一个sql语句的前缀，SQL语句select里这个前缀下的列会映射到子关联对象
     * @param toChildPros            指定子关联类里的哪些属性需要被映射。如果为null，子关联类的所有属性尝试被映射
     */
    public void set(Class nestedBeanClassType, String toPropertyName,
                    String[] nestedBeanPropertyKeys, String sqlPrefix, String[] toChildPros) {

        this.toChildPros = toChildPros;
        this.nestedClassType = nestedBeanClassType;
        this.prefix = sqlPrefix;
        this.nestedBeanPropertyKeys = nestedBeanPropertyKeys;
        this.toPropertyName = toPropertyName;
        this.mapRelation = ONE_TO_MANY;
    }

    /**
     * 设置关联属性
     *
     * @param nestedBeanClassType    对应子关联类的Class类型
     * @param toPropertyName         映射到主类的哪个属性名称，即关联属性。属性类型必须为List<X>类型，X为子关联类型，List里存放关联对象。
     * @param nestedBeanPropertyKeys 子关联类里哪些属性构成主键属性（可以是多个属性构成复合主键），如果为复合主键，以","隔开。
     *                               子关联属性其列表里关联对象的去重
     * @param sqlPrefix              指定一个sql语句的前缀，SQL语句select里这个前缀下的列会映射到子关联对象
     * @param toChildPros            指定子关联类里的哪些属性需要被映射。如果为null，子关联类的所有属性尝试被映射
     */
    public QueryMapNestOne2Many(Class nestedBeanClassType,
                                String toPropertyName, String[] nestedBeanPropertyKeys, String sqlPrefix,
                                String[] toChildPros) {
        // QueryMapNest(String[] toPros, String nestedBeanName, String prefix)
        this.toChildPros = toChildPros;
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
