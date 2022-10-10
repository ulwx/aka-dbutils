package com.github.ulwx.aka.dbutils.tool.support.type;

public class TString implements TType {

    private String value;

    @Override
    public Class getWrappedClass() {
        return String.class;
    }

    public TString() {

    }

    public TString(String str) {
        this.value = str;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }
}
