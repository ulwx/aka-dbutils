package com.github.ulwx.aka.dbutils.tool.support.type;

public class TShort implements TType {

    private Short value;

    @Override
    public Class wrappedClass() {
        return Short.class;
    }

    public TShort() {

    }

    public TShort(Short val) {
        this.value = val;
    }

    @Override
    public Short getValue() {
        return value;
    }

    public void setValue(Short value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }
}
