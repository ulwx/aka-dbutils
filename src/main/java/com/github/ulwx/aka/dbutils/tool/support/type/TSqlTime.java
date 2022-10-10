package com.github.ulwx.aka.dbutils.tool.support.type;

import java.sql.Time;

public class TSqlTime implements TType {
    private Time value;

    public TSqlTime() {

    }

    public TSqlTime(Time value) {
        this.value = value;
    }

    @Override
    public Time getValue() {
        return value;
    }

    public void setValue(Time value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class getWrappedClass() {
        return Time.class;
    }
}
