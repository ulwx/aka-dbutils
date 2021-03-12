package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.DataBase.MainSlaveModeConnectMode;
import com.github.ulwx.aka.dbutils.database.spring.DBTransInfo;

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
	public static String  main_slave_mode_connectmode="main_slave_mode_connectmode";
	public static String  key_debug_log="key_debug_log";

	public static Class<?>[] getReflectClass() {
		return (Class<?>[]) localDbContext.get().contextMap.get(key_reflectclazz);
	}

	/**
	 * 同一线程的维度里是否允许打印debug日志，如果设置为true，则允许打印debug日志，否则不打印debug日志，注意：需要考虑到
	 * 日志框架（如log4j）设置日志级别设置，如果设置为为error以上级别，则也是不会打印日志。
	 * @param permit
	 */
	public static void permitDebugLog(boolean permit){
		localDbContext.get().contextMap.put(key_debug_log, permit);
	}

	/**
	 * 返回是否允许打印debug日志标志
	 * @return
	 */
	public static boolean permitDebugLog(){
		Boolean ret=(Boolean)localDbContext.get().contextMap.get(key_debug_log);
		if(ret==null || ret){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 设置生成SQL语句时使用javaBean的对象（具有父类或接口）的哪个继承层次类的类名转换成表名
	 * @param clzz  指定哪个继承层级的类，利用指定类的类名转换成表名
	 */
	public static void setReflectClass(Class<?>... clzz) {
		localDbContext.get().contextMap.put(key_reflectclazz, clzz);
	}

	public static void clearReflectClass() {
		localDbContext.get().contextMap.remove(key_reflectclazz);
	}
	public static void setDbTransInfo(DBTransInfo dbt){
		localDbContext.get().contextMap.put(key_db_trans_info, dbt);
	}
	public static MainSlaveModeConnectMode getMainSlaveModeConnectMode(){
		MainSlaveModeConnectMode mainSlaveModeConnectMode =
				(MainSlaveModeConnectMode) localDbContext.get().contextMap.get(main_slave_mode_connectmode);
		MainSlaveModeConnectMode useMainSlaveModeConnectMode=null;
		if(mainSlaveModeConnectMode==null){
			useMainSlaveModeConnectMode=MainSlaveModeConnectMode.Connect_MainServer;
		}else{
			useMainSlaveModeConnectMode=mainSlaveModeConnectMode;
		}
		return useMainSlaveModeConnectMode;
	}
	public static void setMainSlaveModeConnectMode(MainSlaveModeConnectMode mainSlaveModeConnectMode){
		localDbContext.get().contextMap.put(main_slave_mode_connectmode, mainSlaveModeConnectMode);
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
	public static TransactionDataBaseTrace getTransactionStart(Map<String, DataBaseDecorator> context){
		if(context.get(MDbTransactionManager._transaction_start)!=null) {
			return ((TransactionDataBaseTrace)context.get(MDbTransactionManager._transaction_start));
		}else {//说明是顶级

		}
		return null;
	}
	public static Integer getTransactionLevel(Map<String, DataBaseDecorator> context){
		TransactionDataBaseTrace transactionDataBaseTrace = getTransactionStart(context);
		if(transactionDataBaseTrace!=null){
			return transactionDataBaseTrace.getInfo().getLevel();
		}
		return null;
	}
	public static TransactionDataBaseTrace findNestStartInTransactionContextStack() {
		Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();

		for (int i = stack.size() - 1; i >= 0; i--) {
			Map<String, DataBaseDecorator> tempContext = stack.get(i);
			TransactionDataBaseTrace transactionStart=(TransactionDataBaseTrace)tempContext.get(MDbTransactionManager._transaction_start);
			if(transactionStart.getInfo().getNestedLevel()==0){//
				return transactionStart;
			}

		}
		return null;

	}

	public static DataBaseDecorator findDataBaseInTransactionContextStack(String dbPoolName) {
		Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();

		for (int i = stack.size() - 1; i >= 0; i--) {
			Map<String, DataBaseDecorator> tempContext = stack.get(i);
			DataBaseDecorator db = tempContext.get(dbPoolName);
			TransactionDataBaseTrace transactionStart=(TransactionDataBaseTrace)tempContext.get(MDbTransactionManager._transaction_start);
			if(transactionStart.getInfo().getLevel()>0){//
				if (db != null) {
					return db;
				} else {
					//继续向上查找
				}
			}else{  //截止查找
				if(db!=null){ //到达顶级事务上下文
					return db;
				}
				break;
			}
			
		}
		return null;

	}

}
