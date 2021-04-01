package com.github.ulwx.aka.dbutils.database;

import java.lang.reflect.Method;

public class DBInterceptorInfo {
    private DataBase dataBase;
    private String debugSql;
    private Method interceptedMethod;
    private Object result;
    private Exception exception;
    private boolean success;

    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public String getDebugSql() {
        return debugSql;
    }

    public void setDebugSql(String debugSql) {
        this.debugSql = debugSql;
    }

    public Method getInterceptedMethod() {
        return interceptedMethod;
    }

    public void setInterceptedMethod(Method interceptedMethod) {
        this.interceptedMethod = interceptedMethod;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
