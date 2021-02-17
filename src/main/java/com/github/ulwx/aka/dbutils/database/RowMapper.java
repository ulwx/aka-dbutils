package com.github.ulwx.aka.dbutils.database;

@FunctionalInterface
public interface RowMapper<T> {
	public T mapRow(DataBaseSet rs) throws Exception;  
}
