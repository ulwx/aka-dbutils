package com.github.ulwx.aka.dbutils.tool.support.type;

import java.sql.Timestamp;

public class TSqlDateTime  implements TType{
    private Timestamp value;

    public TSqlDateTime() {

    }
    public TSqlDateTime(Timestamp value) {
        this.value = value;
    }
    @Override
    public Timestamp getValue() {
        return value;
    }

    public void setValue(Timestamp value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class getWrappedClass() {
        return Timestamp.class;
    }
}
