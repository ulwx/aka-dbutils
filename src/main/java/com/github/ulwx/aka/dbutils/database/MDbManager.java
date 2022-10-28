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
     *根据dbpool.xml里设置的连接池名称来获取MDataBase实例对象。
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
     *
     * @throws DbException 异常
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
