package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Stack;


public class TransactionDataBase extends DataBaseDecorator {
	private static Logger log = LoggerFactory.getLogger(TransactionDataBase.class);
	public TransactionDataBase() {
		super(new DataBaseImpl());
	}
	public TransactionDataBase(DataBase db) {
		super(db);
	}

	@Override
	public void connectDb(String dbPoolName) throws DbException {

		Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();
		if (stack != null) {
			Map<String, DataBaseDecorator> context = DbContext.getTransactionContextStackTopContext(stack);
			if (context != null) {// 如果存在在事务上下文,则新建的数据库实例放入当前上下文中
				DataBaseDecorator contextDb =
						DbContext.findDataBaseInTransactionContextStack(dbPoolName);
				if (contextDb != null) {
					this.db=contextDb.db;//使用老连接
					log.debug("fetch a ["+dbPoolName+"]db from context stack!");
				} else {
					this.db.connectDb(dbPoolName);//获取新连接，会延迟获取
					this.db.setAutoCommit(false);
					log.debug("create a ["+dbPoolName+"]db and put it into context stack!");
				}
				TransactionDataBaseTrace start=DbContext.getTransactionStart(context);
				if(start.getInfo().getNestedLevel()>=0){//说明有嵌套事务
					//查找nestStart
					TransactionDataBaseTrace nestStart =
							DbContext.findNestStartInTransactionContextStack();
					this.db.setSavepoint(nestStart.getInfo().getNestedStartSavepointName());
				}
				context.put(dbPoolName, this);
				int level=DbContext.getTransactionLevel(context);
				log.debug("current context："
						+ ObjectUtils.toJsonString(context.keySet())+":level="+level);
				return;
			}
		}
		db.connectDb(dbPoolName);
	}
	@Override
	public void close() {
		DataBase findDb = findInCurTransactionContext(this.getDbPoolName());
		if (findDb != null) {// 拦截
			return;
		}
		db.close();
	}
	@Override
	public void rollback() throws DbException {
		DataBase findDb = findInCurTransactionContext(this.getDbPoolName());
		if (findDb != null) {// 拦截
			return;
		}

		db.rollback();
	}

	@Override
	public void rollbackToSavepoint(String savepointName) throws DbException {
		DataBase findDb = findInCurTransactionContext(this.getDbPoolName());
		if (findDb != null) {// 拦截
			return;
		}
		db.rollbackToSavepoint(savepointName);
	}
	@Override
	public void setSavepoint(String savepointName) throws DbException{
		DataBase findDb = findInCurTransactionContext(this.getDbPoolName());
		if (findDb != null) {// 拦截
			return;
		}
		db.setSavepoint(savepointName);
	}

	@Override
	public void releaseSavepoint(String savepointName) throws DbException{
		DataBase findDb = findInCurTransactionContext(this.getDbPoolName());
		if (findDb != null) {// 拦截
			return;
		}
		db.releaseSavepoint(savepointName);
	}
	@Override
	public void commit() throws DbException {
		DataBase findDb = findInCurTransactionContext(this.getDbPoolName());
		if (findDb != null) {// 拦截
			return;
		}
		db.commit();
	}

	@Override
	public void setAutoCommit(boolean b) throws DbException {
		DataBase findDb = findInCurTransactionContext(this.getDbPoolName());
		if (findDb != null) {// 拦截
			return;
		}
		db.setAutoCommit(b);
	}
	private  Map<String, DataBaseDecorator> getCurTransactionContext() {
		Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();
		if (stack != null) {
			Map<String, DataBaseDecorator> context = DbContext.getTransactionContextStackTopContext(stack);
			if (context != null) {
				return context;
			}
		}
		return null;
	}

	private DataBaseDecorator findInCurTransactionContext(String dbPoolName) {
		Map<String, DataBaseDecorator> context = getCurTransactionContext();
		if (context != null) {
			DataBaseDecorator contextDb = context.get(dbPoolName);
			if (contextDb != null) {// 找到
				return contextDb;
			} else {
				throw new DbException("当前事务上下文无法找到数据库实例！");
			}
		}

		return null;
	}


}
