package com.github.ulwx.aka.dbutils.tool.support.type;

public class TInteger implements TType {

    private Integer value ;

    @Override
    public Class getWrappedClass() {
        return Integer.class;
    }
    public TInteger() {

    }

    public TInteger(Integer val) {
        this.value = val;
    }
    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

}
