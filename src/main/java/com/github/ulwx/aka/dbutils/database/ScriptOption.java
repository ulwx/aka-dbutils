package com.github.ulwx.aka.dbutils.database;


public class ScriptOption {
    private String source;
    private boolean fromMDMethod = true;

    public boolean isFromMDMethod() {
        return fromMDMethod;
    }

    public void setFromMDMethod(boolean fromMDMethod) {
        this.fromMDMethod = fromMDMethod;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
