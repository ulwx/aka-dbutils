package com.github.ulwx.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDbManager {

	private static Logger log = LoggerFactory.getLogger(DataBase.class);

	public static MDataBase getDataBase() throws DbException {
		return getDataBase(DataBase.MainSlaveModeConnectMode.Connect_MainServer);

	}
	@SuppressWarnings("resource")
	public static MDataBase getDataBase(DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws DbException {
		DataBase db = new TransactionDataBase();
		db.connectDb(DataBaseFactory.DefaultDbpoolName, mainSlaveModeConnectMode);
		return new MDataBaseImpl(db);

	}

	/**
	 * 
	 * @param dbPoolName 连接池的名字
	 * @return
	 * @throws DbException
	 */
	public static MDataBase getDataBase(String dbPoolName) throws DbException {
		return getDataBase(dbPoolName, DataBase.MainSlaveModeConnectMode.Connect_MainServer);

	}

	@SuppressWarnings("resource")
	public static MDataBase getDataBase(String dbPoolName, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode)
			throws DbException {
		DataBase db = new TransactionDataBase();
		db.connectDb(dbPoolName, mainSlaveModeConnectMode);
		return new MDataBaseImpl(db);

	}

	public static void main(String[] args) {

	}
}
