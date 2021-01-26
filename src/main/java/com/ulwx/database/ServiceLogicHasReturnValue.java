package com.ulwx.database;

@FunctionalInterface
public  interface ServiceLogicHasReturnValue<R,T>{
	public R call(T argObj) throws Exception;
}
