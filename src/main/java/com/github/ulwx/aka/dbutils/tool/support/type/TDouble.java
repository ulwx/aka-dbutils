package com.github.ulwx.aka.dbutils.tool.support.type;

public class TDouble implements TType{

    private Double value ;

    @Override
    public Class getWrappedClass() {
        return Double.class;
    }
    public TDouble() {

    }

    public TDouble(Double val) {
        this.value = val;
    }
    @Override
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }
}
