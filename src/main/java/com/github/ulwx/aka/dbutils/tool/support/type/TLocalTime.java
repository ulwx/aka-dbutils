package com.github.ulwx.aka.dbutils.tool.support.type;

import java.time.LocalTime;

public class TLocalTime implements TType {
    private LocalTime value;

    public TLocalTime() {

    }

    public TLocalTime(LocalTime value) {
        this.value = value;
    }

    @Override
    public LocalTime getValue() {
        return value;
    }

    public void setValue(LocalTime value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class wrappedClass() {
        return LocalTime.class;
    }
}
