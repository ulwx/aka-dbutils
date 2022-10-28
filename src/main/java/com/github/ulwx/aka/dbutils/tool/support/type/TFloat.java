package com.github.ulwx.aka.dbutils.tool.support.type;

public class TFloat implements TType {
    private Float value;

    @Override
    public Class wrappedClass() {
        return Float.class;
    }

    public TFloat() {

    }

    public TFloat(Float val) {
        this.value = val;
    }

    @Override
    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }
}
