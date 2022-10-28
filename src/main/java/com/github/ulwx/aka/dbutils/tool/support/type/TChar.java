package com.github.ulwx.aka.dbutils.tool.support.type;

public class TChar implements TType {

    private Character value;

    @Override
    public Class wrappedClass() {
        return Character.class;
    }

    public TChar() {

    }

    public TChar(Character val) {
        this.value = val;
    }

    @Override
    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }
}
