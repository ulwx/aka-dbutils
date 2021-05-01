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

    /**
     *
     * @param dbPoolName 对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                   如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略
     * @return
     * @throws DbException
     */
    @SuppressWarnings("resource")
    public static MDataBase getDataBase(String dbPoolName)
            throws DbException {
        if (StringUtils.isEmpty(dbPoolName)) {
            return MDbManager.getDataBase();
        }
        DataBase db = new TransactionDataBase();
        db.connectDb(dbPoolName);
        return new MDataBaseImpl(db);

    }

    public static void main(String[] args) {

    }
}
