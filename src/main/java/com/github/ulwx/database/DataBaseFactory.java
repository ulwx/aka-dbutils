package com.github.ulwx.database;



import com.github.ulwx.database.DataBase.MainSlaveModeConnectMode;

public class DataBaseFactory {

	public static String DefaultDbpoolName="default";
	/**
	 * 用于从连接池构造DataBase对象，连接池配置文件为classpath下的dbpool.xml文件
	 * @return
	 * @throws DbException
	 */
	public static DataBase getDataBase() throws DbException {
		return getDataBase(MainSlaveModeConnectMode.Try_Connect_MainServer);

	}
	
	public static DataBase getDataBase(MainSlaveModeConnectMode mainSlaveModeConnectMode) throws DbException {
		DataBase db = new TransactionDataBase();
		try {
			db.connectDb(DefaultDbpoolName,mainSlaveModeConnectMode);
		} catch (DbException e) {
			throw new DbException("can't connect database!", e);
		}
		return db;

	}
	/**
	 * 
	 * @param dbPoolName 连接池的名字
	 * @return
	 * @throws DbException
	 */
	public static DataBase getDataBase(String dbPoolName) throws DbException {
		return getDataBase(dbPoolName,MainSlaveModeConnectMode.Try_Connect_MainServer);

	}
	
	public static DataBase getDataBase(String dbPoolName,MainSlaveModeConnectMode mainSlaveModeConnectMode ) throws DbException {
		DataBase db = new TransactionDataBase();
		try {
			db.connectDb(dbPoolName,mainSlaveModeConnectMode);
		} catch (DbException e) {
			throw new DbException("can't connect database!", e);
		}
		return db;

	}
	/**
	 * @param args
	 * @throws
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	}
}


