package com.github.ulwx.database;

import com.github.ulwx.database.spring.DBTransInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class DbContext implements Serializable {

	private  Map<String, Object> contextMap = new HashMap<String, Object>();

	private static final long serialVersionUID = -2348853553489025507L;

	private static volatile ThreadLocal<DbContext> localDbContext = new ThreadLocal<DbContext>() {
		@Override
		protected DbContext initialValue() {
			return new DbContext();
		}
	};

	private static String key_reflectclazz = "key_reflectclazz";
	private static String key_transaction_context_stack = "key_transaction_context_stack";
	private static String key_db_trans_info="key_db_trans_info";

	public static Class<?>[] getReflectClass() {
		return (Class<?>[]) localDbContext.get().contextMap.get(key_reflectclazz);
	}

	public static void setReflectClass(Class<?>... clzz) {
		localDbContext.get().contextMap.put(key_reflectclazz, clzz);
	}

	public static void clearReflectClass() {
		localDbContext.get().contextMap.remove(key_reflectclazz);
	}
	public static void setDbTransInfo(DBTransInfo dbt){
		localDbContext.get().contextMap.put(key_db_trans_info, dbt);
	}
	public static DBTransInfo getDbTransInfo(){
		return (DBTransInfo) localDbContext.get().contextMap.get(key_db_trans_info);
	}
	public static void clearDbTransInfo() {
		localDbContext.get().contextMap.remove(key_db_trans_info);
	}
	public static Stack<Map<String, DataBaseDecorator>> getTransactionContextStack() {
		@SuppressWarnings("unchecked")
		Stack<Map<String, DataBaseDecorator>> stack = (Stack<Map<String, DataBaseDecorator>>) localDbContext.get().contextMap
				.get(key_transaction_context_stack);
		if (stack == null) {
			stack = new Stack<Map<String, DataBaseDecorator>>();
			localDbContext.get().contextMap.put(key_transaction_context_stack, stack);
		}
		return stack;
	}

	public static Map<String, DataBaseDecorator> getTransactionContextStackTopContext( Stack<Map<String, DataBaseDecorator>>  stack) {;
		try {
			return stack.peek();
		} catch (Exception e) {
			return null;
		}
	}
	public static int getTransactionLevel(Map<String, DataBaseDecorator> context){
		int level=0;
		if(context.get(MDbTransactionManager._children_transaction)!=null) {
			level=((TransactionDataBaseTrace)context.get(MDbTransactionManager._children_transaction)).getLevel();
		}else {//说明是顶级
			level=0;
		}
		return level;
	}
	public static DataBaseDecorator findDataBaseInTransactionContextStack(String dbPoolName) {
		Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();

		for (int i = stack.size() - 1; i >= 0; i--) {
			Map<String, DataBaseDecorator> tempContext = stack.get(i);
			DataBaseDecorator db = tempContext.get(dbPoolName);
			if (tempContext.get(MDbTransactionManager._children_transaction) != null) {
				if (db != null) {
					return db;
				} else {
					// 继续向上查找
				}

			} else {// 截止点
				if (db != null) {
					return db;
				}
				break;
			}
			
		}
		return null;

	}

}
