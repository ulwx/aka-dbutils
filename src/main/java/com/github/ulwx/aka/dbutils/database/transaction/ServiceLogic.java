package com.github.ulwx.aka.dbutils.database.transaction;

@FunctionalInterface
public interface ServiceLogic {
    public void call() throws Exception;
}
