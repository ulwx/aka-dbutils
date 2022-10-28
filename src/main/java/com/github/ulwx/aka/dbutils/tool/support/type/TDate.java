package com.github.ulwx.aka.dbutils.tool.support.type;

import java.util.Date;

public class TDate implements TType {
    private Date value;

    public TDate() {

    }

    public TDate(Date value) {
        this.value = value;
    }

    @Override
    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class wrappedClass() {
        return Date.class;
    }
}
