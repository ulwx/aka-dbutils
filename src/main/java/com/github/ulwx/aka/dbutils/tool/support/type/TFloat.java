package com.github.ulwx.aka.dbutils.tool.support.type;

public class TFloat {
    private Float value = 0f;


    public TFloat() {

    }

    public TFloat(Float val) {
        this.value = val;
    }

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
