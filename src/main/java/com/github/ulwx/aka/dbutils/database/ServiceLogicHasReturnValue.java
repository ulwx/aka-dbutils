package com.github.ulwx.aka.dbutils.database;

@FunctionalInterface
public interface ServiceLogicHasReturnValue<R> {
    public R call() throws Exception;
}
