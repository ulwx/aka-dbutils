package com.github.ulwx.database;

import com.github.ulwx.database.dbpool.DBPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDbManager {



	private static Logger log = LoggerFactory.getLogger(DataBase.class);

	/**
	 * 用于从连接池构造DataBase对象，连接池配置文件在src目录下的dbpool.xml里
	 * 
	 * @return
	 * @throws DbException
	 */
	public static MDataBase getDataBase() throws DbException {
		return getDataBase(DataBase.MainSlaveModeConnectMode.Try_Connect_MainServer);

	}

	@SuppressWarnings("resource")
	public static MDataBase getDataBase(DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode) throws DbException {
		DataBase db = new TransactionDataBase();
		db.connectDb("tt1", mainSlaveModeConnectMode);
		return new MDataBase(db);

	}

	/**
	 * 
	 * @param dbPoolName 连接池的名字
	 * @return
	 * @throws DbException
	 */
	public static MDataBase getDataBase(String dbPoolName) throws DbException {
		return getDataBase(dbPoolName, DataBase.MainSlaveModeConnectMode.Try_Connect_MainServer);

	}

	@SuppressWarnings("resource")
	public static MDataBase getDataBase(String dbPoolName, DataBase.MainSlaveModeConnectMode mainSlaveModeConnectMode)
			throws DbException {
		DataBase db = new TransactionDataBase();
		db.connectDb(dbPoolName, mainSlaveModeConnectMode);
		return new MDataBase(db);

	}

	/**
	 * @param args @throws
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println("start..................");
			DBPoolFactory.init();
			System.out.println("init..................");

			// System.out.println("sleep out..................");

			String sql = "select 12.345 as a ,123 as b";
//			DataBaseSet rs=DbUtils.doCachedQuery(null,sql, null);
//			while(rs.next()){
//				System.out.println(rs.getDateTime("UpdateTime"));
//			}
//			
//			 rs=DbUtils.doCachedQuery(null,sql, null);
//			while(rs.next()){
//				System.out.println(rs.getDateTime("UpdateTime"));
//			}
//			 rs=DbUtils.doCachedQuery(null,sql, null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
