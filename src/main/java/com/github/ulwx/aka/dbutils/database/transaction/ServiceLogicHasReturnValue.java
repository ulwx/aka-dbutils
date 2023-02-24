package com.github.ulwx.aka.dbutils.database.transaction;

@FunctionalInterface
public interface ServiceLogicHasReturnValue<R> {
    public R call() throws Exception;
}
