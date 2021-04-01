package com.github.ulwx.aka.dbutils.tool.support.type;

public class TResult2<T1, T2> {
    T1 val1;
    T2 val2;

    public TResult2() {

    }

    public TResult2(T1 val1, T2 val2) {
        // TODO Auto-generated constructor stub
        this.val1 = val1;
        this.val2 = val2;
    }

    public T1 getFirstValue() {
        return this.val1;
    }

    public T2 getSecondValue() {
        return this.val2;
    }

    public void setFirstValue(T1 value) {
        this.val1 = value;
    }

    public void setSecondValue(T2 value) {
        this.val2 = value;
    }

    public String toString() {
        return this.val1.toString() + ":" + this.val2.toString();
    }
}
