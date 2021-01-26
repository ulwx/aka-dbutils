package com.ulwx.database;



public interface RowMapper<T> {
	public T mapRow(DataBaseSet rs) throws Exception;  
}
