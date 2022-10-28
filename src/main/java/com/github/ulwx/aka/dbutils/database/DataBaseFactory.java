package com.github.ulwx.aka.dbutils.database;


import com.github.ulwx.aka.dbutils.tool.support.PropertyUtil;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import com.github.ulwx.aka.dbutils.tool.support.type.TString;

import java.lang.reflect.Method;
import java.util.Map;

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

    /**
     * @param dbPoolName   对应于dbpool.xml里的元素dbpool的name属性值。
     * <blockquote><code>
     * dbPoolName参数的格式如下：
     * 格式为：[配置xml文件的路径文件名称]#[连接池名称]
     * <ul>
     * <li>如：mydbpool.xml#sysdb，则在所有root类路径（包含jar）下查找mydbpool.xml文件并指向sysdb连接池。等效于classpath*:/mydbpool.xml#sysdb。</li>
     * <li>mysql/mydbpool.xml#sysdb，则在/mysql类路径下查找mydbpool.xml文件并指向sysdb连接池。等效于classpath*:/mysql/mydbpool.xml#sysdb。。</li>
     * <li>如：file:/D:/config/mydbpool.xml#sysdb，则查找file:/D:/config/mydbpool.xml文件并指向sysdb连接池。</li>
     * <li>如：classpath*:/mydbpool.xml#sysdb 或 classpath:/mydbpool.xml#sysdb，若为"classpath*:"前缀则表明在所有root类路径（含jar）下查找。</li>
     *</ul>
     *  </code></blockquote>
     * @return
     * @throws DbException
     */
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


    public static void main(String[] args) throws Exception{
        // TODO Auto-generated method stub
        Map<String, TResult2<Method, Object>> ret= PropertyUtil.describeForTypes(new TString(), TString.class);
        int i=1;
    }
}


