package com.github.ulwx.aka.dbutils.database.nsql;

public class NSQLException extends Exception {


	public NSQLException() {
		
		super();
	}

	public NSQLException(Throwable cause) {
		super(cause);
		//causes.add(cause);
	}

	public NSQLException(String msg,Throwable cause) {
		super(msg,cause);
		//causes.add(cause);
	}
	public NSQLException(String msg) {
		super(msg);
	}
	
	
}