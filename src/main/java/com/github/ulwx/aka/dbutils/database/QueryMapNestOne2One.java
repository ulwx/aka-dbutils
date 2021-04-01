package com.github.ulwx.aka.dbutils.database;

/**
 * 存放一到一关联关系映射信息的类
 */
public class QueryMapNestOne2One extends MapNest {

    public QueryMapNestOne2One() {
    }

    /**
     * @param toChildBeanPros 存放子关联类需要映射的属性名，如果为空表明全映射
     * @param toPropertyName  对子关联类的属性名
     * @param sqlPrefix       sql前缀
     */
    public QueryMapNestOne2One(String[] toChildBeanPros, String toPropertyName,
                               String sqlPrefix) {
        this.toChildPros = toChildBeanPros;
        this.toPropertyName = toPropertyName;
        this.prefix = sqlPrefix;
        this.mapRelation = ONE_TO_ONE;
    }

    /**
     * 设置一到一关系
     *
     * @param toChildBeanPros 存放子关联类需要映射的属性名，如果为空，表明全映射
     * @param toPropertyName  对应子关联类在主类里的属性名
     * @param sqlPrefix       sql前缀，用于限定SQL语句里哪些字段（sqlPrefix限定的）映射到子关联类的属性
     */
    public void set(String[] toChildBeanPros, String toPropertyName, String sqlPrefix) {
        this.toChildPros = toChildBeanPros;
        this.toPropertyName = toPropertyName;
        this.prefix = sqlPrefix;
        this.mapRelation = ONE_TO_ONE;
    }

}
