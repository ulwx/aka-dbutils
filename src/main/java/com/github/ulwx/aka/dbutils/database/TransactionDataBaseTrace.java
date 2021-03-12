package com.github.ulwx.aka.dbutils.database;

public class TransactionDataBaseTrace extends DataBaseDecorator {

	private Info info=new Info();
	static class Info{
		private StackTraceElement stackTraceElement;
		private int level=0;
		private boolean needRollBack=false;//是否整个事务回滚
		private boolean nestedStart=false;//是否是嵌套事务开始，如果是，则nestedLevel=0
		private String  nestedStartSavepointName=null;
		private  int nestedLevel=-1;//-1表示没有嵌套事务，如果有嵌套事务，则nestedLevel>=0
		public boolean isNeedRollBack() {
			return needRollBack;
		}
		public int getNestedLevel() {
			return nestedLevel;
		}
		public void setNestedLevel(int nestedLevel) {
			this.nestedLevel = nestedLevel;
		}
		public boolean isNestedStart() {
			return nestedStart;
		}
		public void setNestedStart(boolean nestedStart) {
			this.nestedStart = nestedStart;
		}
		public void setNeedRollBack(boolean needRollBack) {
			this.needRollBack = needRollBack;
		}

		public String getNestedStartSavepointName() {
			return nestedStartSavepointName;
		}

		public void setNestedStartSavepointName(String nestedStartSavepointName) {
			this.nestedStartSavepointName = nestedStartSavepointName;
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
	}

	public Info getInfo() {
		return info;
	}

	public TransactionDataBaseTrace(StackTraceElement stackTraceElement, int level) {
		super(null);
		this.info.stackTraceElement=stackTraceElement;
		this.info.level=level;
	}
	public StackTraceElement getStackTraceElement() {
		return info.stackTraceElement;
	}
	public void setStackTraceElement(StackTraceElement stackTraceElement) {
		this.info.stackTraceElement = stackTraceElement;
	}

	@Override
	public void connectDb(String dbPoolName) throws DbException {
		throw new UnsupportedOperationException();
	}
	@Override
	public void setAutoCommit(boolean b) throws DbException {
		throw new UnsupportedOperationException();
	}
	@Override
	public void rollback() throws DbException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSavepoint(String savepointName) throws DbException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void releaseSavepoint(String savepointName) throws DbException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rollbackToSavepoint(String savepointName) throws DbException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void commit() throws DbException {
		throw new UnsupportedOperationException();
	}


	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}
}
