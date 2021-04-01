package com.github.ulwx.aka.dbutils.database;

public class MapNest {
    public static int ONE_TO_ONE = 1;
    public static int ONE_TO_MANY = 2;

    protected String[] toChildPros = null;// 指定关联子对象里哪些属性需要赋值，如果指定为空，则表明所有属性都需要赋值
    protected String toPropertyName = null;//指定父对象里的哪个是关联属性，此属性具有对象关联映射（一对一，一对多）
    protected String prefix = null;
    protected int mapRelation;

    public String[] getToChildPros() {
        return toChildPros;
    }

    public String getToPropertyName() {
        return toPropertyName;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getMapRelation() {
        return mapRelation;
    }


}
