package com.github.ulwx.aka.dbutils.database;

import java.lang.reflect.Method;

public class DBInterceptorInfo {
    private DataBase dataBase;
    private String debugSql;
    private Method interceptedMethod;
    private Object result;
    private Exception exception;
    private boolean success;
    private long exeTimeMil;//执行的毫秒数
    private long startTime;

    public long getExeTimeMil() {
        return exeTimeMil;
    }

    public void setExeTimeMil(long exeTimeMil) {
        this.exeTimeMil = exeTimeMil;
    }

    public DBInterceptorInfo(){
        this.startTime=System.currentTimeMillis();
    }
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
        this.exeTimeMil=System.currentTimeMillis()-this.startTime;
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
