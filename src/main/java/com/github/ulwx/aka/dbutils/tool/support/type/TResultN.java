package com.github.ulwx.aka.dbutils.tool.support.type;

import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

public class TResultN  {

    private Object[] values;

    public TResultN(Object... objects) {
        this.values=objects;
    }

    public Object getValueByIndex(int index) {
        return values[index];
    }

    public void setValueByIndex(int index, Object value) {
        values[index] = value;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public String toString() {
        return ObjectUtils.toString(values);
    }
}
