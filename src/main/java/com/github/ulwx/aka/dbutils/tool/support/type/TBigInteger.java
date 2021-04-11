package com.github.ulwx.aka.dbutils.tool.support.type;

import java.math.BigInteger;

public class TBigInteger implements TType{
    private BigInteger value;

    public TBigInteger() {

    }
    public TBigInteger(BigInteger value) {
        this.value = value;
    }
    @Override
    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public String toString() {
        return value + "";
    }

    @Override
    public Class getWrappedClass() {
        return BigInteger.class;
    }
}
