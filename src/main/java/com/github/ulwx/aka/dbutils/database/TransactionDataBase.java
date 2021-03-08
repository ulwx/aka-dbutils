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
			if (context != null) {// 如果在事务上下文中,则新建的数据库实例放入当前上下文中
				DataBaseDecorator contextDb =
						DbContext.findDataBaseInTransactionContextStack(dbPoolName);
				if (contextDb != null) {
					this.db=contextDb.db;
					log.debug("["+dbPoolName+"]db from context stack!");
					context.put(dbPoolName, this);
					int level=DbContext.getTransactionLevel(context);
					log.debug("context："
							+ ObjectUtils.toJsonString(context.keySet())+":level="+level);
					return;
				} else {
					this.db.connectDb(dbPoolName);
					this.db.setAutoCommit(false);
					log.debug(dbPoolName+": a new db is created and put into context stack!");
				}
				context.put(dbPoolName, this);
				int level=DbContext.getTransactionLevel(context);
				log.debug("context："
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
