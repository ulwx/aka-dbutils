package com.github.ulwx.aka.dbutils.database;

public class TransactionDataBaseTrace extends DataBaseDecorator {

	private StackTraceElement stackTraceElement;
	private int level=0;
	public TransactionDataBaseTrace(StackTraceElement stackTraceElement,int level) {
		super(null);
		this.stackTraceElement=stackTraceElement;
		this.level=level;
	}
	public StackTraceElement getStackTraceElement() {
		return stackTraceElement;
	}
	public void setStackTraceElement(StackTraceElement stackTraceElement) {
		this.stackTraceElement = stackTraceElement;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isSameMethodCall(StackTraceElement src) {
		if(src.getClassName().equals(stackTraceElement.getClassName()) 
				&& src.getMethodName().equals(stackTraceElement.getMethodName())){
			return true;
		}
		return false;
	}


	@Override
	public void connectDb(String dbPoolName) throws DbException {

	}
	@Override
	public void setAutoCommit(boolean b) throws DbException {

	}
	@Override
	public void rollback() throws DbException {

	}
	@Override
	public void commit() throws DbException {
	}


	@Override
	public void close() {
	}
}
