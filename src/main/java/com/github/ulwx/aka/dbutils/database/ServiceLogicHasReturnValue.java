package com.github.ulwx.aka.dbutils.database;

@FunctionalInterface
public  interface ServiceLogicHasReturnValue<R,T>{
	public R call(T argObj) throws Exception;
}
