package com.github.ulwx.aka.dbutils.tool.support.type;

import java.time.LocalDate;

public class TLocalDate implements TType {
    private LocalDate value;

    public TLocalDate() {

    }

    public TLocalDate(LocalDate value) {
        this.value = value;
    }

    @Override
    public LocalDate getValue() {
        return value;
    }

    public void setValue(LocalDate value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class wrappedClass() {
        return LocalDate.class;
    }
}
