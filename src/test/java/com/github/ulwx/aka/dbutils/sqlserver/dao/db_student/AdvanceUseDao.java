package com.github.ulwx.aka.dbutils.sqlserver.dao.db_student;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.sqlserver.Utils;
import com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_student.Student;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.type.TInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AdvanceUseDao {
    public static String DbPoolName = "sqlserver/dbpool.xml#db_student";

    @Before
    public void setup() {
        Utils.importDbStudent();
    }

    /**
     * 测试更新逻辑
     */
    @Test
    public void testUpdate() {
        Student student = new Student();
        student.setId(1);
        student.setAge(18);
        student.setName("add");
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        int ret = MDbUtils.update(DbPoolName, MD.md(), MD.map(student));
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "update student set name='add', age=18 where id=1");
        Assert.equal(ret, 1);

    }

    /**
     * 测试变量替换(即${XXX}形式)逻辑。
     */
    @Test
    public void testVarSubstitution() {
        Student student = new Student();
        student.setId(1);
        student.setAge(18);
        student.setName("add'a");//${XXX}变量替换时，XXX里如果存在',则会替换成''
        Map<String, Object> map = MD.map(student);
        map.put("ids", new int[]{1, 2, 3});
        map.put("lname", "stu'b");
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });

        int ret = MDbUtils.update(DbPoolName, MD.md(), map);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "update student set name='add''a', age=18 where id=1 or id in(1,2,3) or name like '%stu''b'");
        Assert.equal(ret, 3);

    }

    /**
     * 测试md文件的高级语法逻辑
     */
    @Test
    public void allMdSyntax() {
        Map<String, Object> map = new HashMap<>();
        map.put("ids", new int[]{1, 2, 3});
        map.put("lname", "add'b");
        map.put("lnameList", Arrays.asList("abc", "efg", "bed"));
        map.put("birthDay", LocalDate.of(1980, 11, 2));
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        TInteger ret = MDbUtils.queryOne(DbPoolName, TInteger.class, MD.md(), map);
        Assert.equal(sql.toString(), "SELECT COUNT(1) AS `value` FROM student WHERE (1=1) AND `name` = 'add''b' AND `name` = 'add''b' AND `name` in('abc','efg','bed') AND `name` in( 'abc','efg','bed' ) AND `name` like '%add''b%' AND `name` like 'add''b%' AND `name` like 'add''b%' AND `name`=3  and id in (1,2,3 )  and id in (1,2,3 )");

    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }

    public static void main(String[] args) {
        AdvanceUseDao advanceUseDao = new AdvanceUseDao();
        advanceUseDao.setup();
        advanceUseDao.testUpdate();
        advanceUseDao.after();
    }

}
