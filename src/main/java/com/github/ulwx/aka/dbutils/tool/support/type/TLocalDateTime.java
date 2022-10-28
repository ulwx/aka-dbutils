package com.github.ulwx.aka.dbutils.tool.support.type;

import java.time.LocalDateTime;

public class TLocalDateTime implements TType {
    private LocalDateTime value;

    public TLocalDateTime() {

    }

    public TLocalDateTime(LocalDateTime value) {
        this.value = value;
    }

    @Override
    public LocalDateTime getValue() {
        return value;
    }

    public void setValue(LocalDateTime value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class wrappedClass() {
        return LocalDateTime.class;
    }
}
