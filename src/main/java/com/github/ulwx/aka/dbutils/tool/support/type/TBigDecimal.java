package com.github.ulwx.aka.dbutils.tool.support.type;

import java.math.BigDecimal;

public class TBigDecimal implements TType {
    private BigDecimal value;

    public TBigDecimal() {

    }

    public TBigDecimal(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String toString() {
        return value.toString();
    }

    @Override
    public Class getWrappedClass() {
        return BigDecimal.class;
    }
}
