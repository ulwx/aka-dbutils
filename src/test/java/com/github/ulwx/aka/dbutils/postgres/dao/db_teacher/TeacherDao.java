package com.github.ulwx.aka.dbutils.postgres.dao.db_teacher;

import com.github.ulwx.aka.dbutils.database.DBInterceptor;
import com.github.ulwx.aka.dbutils.database.DataBase;
import com.github.ulwx.aka.dbutils.database.DataBase.MainSlaveModeConnectMode;
import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.transaction.TransactionTemplate;
import com.github.ulwx.aka.dbutils.postgres.Utils;
import com.github.ulwx.aka.dbutils.postgres.domain.db.db_teacher.Teacher;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * 测试aka-dbutils的数据库主从的支持
 */
public class TeacherDao {
    public static String DbPoolName = "postgres/dbpool.xml#db_teacher";

    @Before
    public void setup() {
        Utils.inportDbTeacher();
    }

    public void testUpdateInManager(String name) {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setName(name);
        int ret = MDbUtils.updateBy(DbPoolName, teacher, MD.of("id"));

    }

    @Test
    public void testInsert() {
        Teacher teacher = new Teacher();
        teacher.setName("new teacher");
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        //默认为MainSlaveModeConnectMode.Connect_MainServer方式
        MDbUtils.insertBy(DbPoolName, teacher);//更新方法会在主库上执行
        Assert.equal(sql.toString(), "insert into \"teacher\" (\"name\") values('new teacher')");


        DbContext.setMainSlaveModeConnectMode(MainSlaveModeConnectMode.Connect_SlaveServer);
        try {
            //Connect_SlaveServer模式下，会限制更新方法不能在从库上执行
            MDbUtils.insertBy(DbPoolName, teacher);

        } catch (Exception e) {
            Assert.state(e instanceof DbException && e.getMessage().equals("从库只能执行select语句！"));
        }
        sql.setLength(0);
        DbContext.setMainSlaveModeConnectMode(MainSlaveModeConnectMode.Connect_Auto);
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                Assert.state(dataBase.getMainSlaveModeConnectMode() == MainSlaveModeConnectMode.Connect_Auto);
                Assert.state(dataBase.connectedToMaster());

            }
        });
        MDbUtils.insertBy(DbPoolName, teacher);//更新方法会在主库上执行
        DbContext.removeDebugSQLListener();
        DbContext.removeDBInterceptor();
        DbContext.removeMainSlaveModeConnectMode();

        Assert.equal(sql.toString(), "insert into \"teacher\" (\"name\") values('new teacher')");
    }

    @Test
    public void testUpdate() {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setName("xyz");
        Map<String, Object> args = MD.map(teacher);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        DbContext.setMainSlaveModeConnectMode(MainSlaveModeConnectMode.Connect_Auto);
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception, String debugSql,long exeTime) {
                Assert.state(dataBase.getMainSlaveModeConnectMode() == MainSlaveModeConnectMode.Connect_Auto);
                Assert.state(dataBase.connectedToMaster());

            }

            @Override
            public boolean beforeDbOperationExeute(DataBase dataBase, boolean inBatch, String debugSql) {
                return true;
            }
        });
        MDbUtils.updateBy(DbPoolName, teacher, MD.of(teacher::getId));//更新方法会在主库上执行
        DbContext.removeDebugSQLListener();
        DbContext.removeDBInterceptor();
        DbContext.removeMainSlaveModeConnectMode();
        Assert.equal(sql.toString(), "update \"teacher\"  set \"name\"='xyz' where \"id\"=1");


    }

    @Test
    public void testSelect() {
        Map<String, Object> args = new HashMap<>();
        args.put("lname", "abc");
        //如果当前线程没有设置主从连接模式，默认主从模式默认为Connect_MainServer，下面操作连的是主库
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                Assert.state(dataBase.getMainSlaveModeConnectMode() == MainSlaveModeConnectMode.Connect_MainServer);
                Assert.state(dataBase.connectedToMaster());

            }
        });
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });

        List<Teacher> list = MDbUtils.queryList(DbPoolName, Teacher.class,
                MD.md(), args);
        Assert.equal(sql.toString(), "SELECT id, name FROM teacher where name like 'abc%'");
        //通过DbContext设置Connect_Auto主从连接模式，即自动识别是否连接主库还是从库，自动识别规则是
        // 如果SQL语句为更新语句或者SQL语句在事务里，则会在主库上执行；否则在从库上执行。
        DbContext.setMainSlaveModeConnectMode(MainSlaveModeConnectMode.Connect_Auto);
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                Assert.state(dataBase.getMainSlaveModeConnectMode() == MainSlaveModeConnectMode.Connect_Auto);
                Assert.state(!dataBase.connectedToMaster());

            }
        });
        list = MDbUtils.queryList(DbPoolName, Teacher.class,
                MD.md(), args);
        //总是连接主库操作
        DbContext.setMainSlaveModeConnectMode(MainSlaveModeConnectMode.Connect_MainServer);
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception, String debugSql,long exeTime) {
                Assert.state(dataBase.getMainSlaveModeConnectMode() == MainSlaveModeConnectMode.Connect_MainServer);
                Assert.state(dataBase.connectedToMaster());

            }
        });
        list = MDbUtils.queryList(DbPoolName, Teacher.class,
                MD.md(), args);


        //总是连接从库操作
        DbContext.setMainSlaveModeConnectMode(MainSlaveModeConnectMode.Connect_SlaveServer);
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                Assert.state(dataBase.getMainSlaveModeConnectMode() == MainSlaveModeConnectMode.Connect_SlaveServer);
                Assert.state(!dataBase.connectedToMaster());

            }
        });
        list = MDbUtils.queryList(DbPoolName, Teacher.class,
                MD.md(), args);

        DbContext.removeDebugSQLListener();
        DbContext.removeDBInterceptor();
        DbContext.removeMainSlaveModeConnectMode();


    }

    @Test
    public void testTransaction() {
        DbContext.setMainSlaveModeConnectMode(MainSlaveModeConnectMode.Connect_Auto);
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                Assert.state(dataBase.getMainSlaveModeConnectMode() == MainSlaveModeConnectMode.Connect_Auto);
                Assert.state(dataBase.connectedToMaster());
            }
        });
        TransactionTemplate.execute(() -> {

            testSelectIntrans(); //由于在事务里，查询操作会在主库执行
            testUpdateInTrans(); //由于在事务里，更新操作会在主库执行
        });
        DbContext.removeDBInterceptor();

    }

    public void testUpdateInTrans() {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setName("xyz");
        Map<String, Object> args = MD.map(teacher);
        MDbUtils.updateBy(DbPoolName, teacher, MD.of(teacher::getId));//更新方法会在主库上执行

    }

    public void testSelectIntrans() {
        Map<String, Object> args = new HashMap<>();
        args.put("lname", "abc");
        List<Teacher> list = MDbUtils.queryList(DbPoolName, Teacher.class,
                MD.md(), args);
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
        DbContext.removeDBInterceptor();
        DbContext.removeMainSlaveModeConnectMode();
    }


}
