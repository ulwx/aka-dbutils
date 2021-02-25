package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDbManager {

	private static Logger log = LoggerFactory.getLogger(DataBase.class);


	@SuppressWarnings("resource")
	public static MDataBase getDataBase() throws DbException {
		DataBase db = new TransactionDataBase();
		db.connectDb(DataBaseFactory.DefaultDbpoolName);
		return new MDataBaseImpl(db);
	}

	@SuppressWarnings("resource")
	public static MDataBase getDataBase(String dbPoolName)
			throws DbException {
		if (StringUtils.isEmpty(dbPoolName)) {
			return  MDbManager.getDataBase();
		}
		DataBase db = new TransactionDataBase();
		db.connectDb(dbPoolName);
		return new MDataBaseImpl(db);

	}

	public static void main(String[] args) {

	}
}
