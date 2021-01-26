package com.github.ulwx.database;

import java.sql.SQLException;

public class DbException extends RuntimeException {


	public DbException() {
		
		super();
	}

	public DbException(Throwable cause) {
		super(cause);
		//causes.add(cause);
	}

	public DbException(String msg,Throwable cause) {
		super(msg,cause);
		//causes.add(cause);
	}
	public DbException(String msg) {
		super(msg);
	}
	
	public SQLException getSQLException(){
		StackTraceElement[]  ste=this.getStackTrace();
		Throwable t=this;
		while(t!=null){
			 t= t.getCause();
			if( t instanceof SQLException){
				SQLException se=(SQLException)t;
				return se;
			}
		}
		return null;
	}
}