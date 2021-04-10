package com.github.ulwx.aka.dbutils.tool.support.type;

public class TBoolean implements TType{

    private Boolean value;

    public TBoolean() {

    }
    public TBoolean(Boolean value) {
        this.value = value;
    }
    @Override
    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class getWrappedClass() {
        return Boolean.class;
    }
}
