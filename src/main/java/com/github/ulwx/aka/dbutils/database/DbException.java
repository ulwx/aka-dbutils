package com.github.ulwx.aka.dbutils.database;

import java.sql.SQLException;

public class DbException extends RuntimeException {
    public static class CODE {
        public static final int Intercepted = 10000;
    }

    private int code = 0;
    private String msg = "";
    public DbException() {

        super();
    }

    public DbException(Throwable cause) {
        super(cause);
        while(cause .getCause() != null) {
            cause=cause.getCause();
        }
        this.msg = msg+";"+cause.getMessage();
    }

    public DbException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
        while(cause.getCause() != null) {
            cause=cause.getCause();
        }
        this.msg = msg+";"+cause.getMessage();
        //causes.add(cause);
    }

    public DbException(String msg, int code) {
        super(msg);
        this.code = code;

    }

    public DbException(String msg) {
        super(msg);
    }

    public String toString() {
        return this.msg+ ":code[" + code + "]";
    }

    public SQLException getSQLException() {
        StackTraceElement[] ste = this.getStackTrace();
        Throwable t = this;
        while (t != null) {
            t = t.getCause();
            if (t instanceof SQLException) {
                SQLException se = (SQLException) t;
                return se;
            }
        }
        return null;
    }
}