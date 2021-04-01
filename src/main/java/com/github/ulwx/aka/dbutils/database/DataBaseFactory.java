package com.github.ulwx.aka.dbutils.database;


import com.github.ulwx.aka.dbutils.tool.support.StringUtils;

public class DataBaseFactory {

    public static String DefaultDbpoolName = "default";


    public static DataBase getDataBase() throws DbException {
        DataBase db = new TransactionDataBase();
        try {
            db.connectDb(DefaultDbpoolName);
        } catch (DbException e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("can't connect database!", e);
        }
        return db;
    }

    public static DataBase getDataBase(String dbPoolName) throws DbException {
        DataBase db = new TransactionDataBase();
        try {
            if (StringUtils.isEmpty(dbPoolName)) {
                return getDataBase();
            }
            db.connectDb(dbPoolName);
        } catch (DbException e) {
            if (e instanceof DbException) throw (DbException) e;
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


