package com.github.ulwx.aka.dbutils.oracle.dao.db_student;

import com.github.ulwx.aka.dbutils.database.DataBaseSet;
import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.MDMethods.InsertOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.InsertOptions.ReturnFlag;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions;
import com.github.ulwx.aka.dbutils.database.nsql.CompilerTask;
import com.github.ulwx.aka.dbutils.database.transaction.TransactionTemplate;
import com.github.ulwx.aka.dbutils.oracle.Utils;
import com.github.ulwx.aka.dbutils.oracle.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.CTime;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 测试Mapper映射器的用法
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CourseMpperTest {
    public static String DbPoolName = "oracle/dbpool.xml#db_student";
    static LocalDateTime createTime =
            LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);

    @Before
    public void setup() {
        Utils.importDbStudent();
    }

    @Test
    public void testGetOne() {
        Map<String, Object> arg = new HashMap<>();
        arg.put("teacherId", "1");
        Course cs = new Course();
        cs.setCreatime(createTime);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.append(sqltxt);
        });
        Course course = MDbUtils.getMapper(DbPoolName, CourseMpper.class).getOneCourse(
                2,
                "course",
                arg,
                //cs里的name属性覆盖了前面name形参声明的参数，所以md文件里的name参数引用的值为null。
                // 同时cs里的teacherId属性覆盖了arg里的teacherId参数的值，所以md文件里tearcherId参数的引用的值也为null
                cs);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "SELECT \"name\", \"class_hours\" FROM \"course\" WHERE 1 = 1");

        String source = CompilerTask.getSource(CourseMpper.class.getName() + "Md");
        System.out.println(source);

    }

    @Test
    public void testGetCourseListByIds() {
        Map<String, Object> arg = new HashMap<>();
        arg.put("teacherId", "1");
        Course cs = new Course();
        cs.setCreatime(createTime);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.append(sqltxt);
        });
        List<Course> courseList = MDbUtils.getMapper(DbPoolName, CourseMpper.class).getCoursesByIds(
                new Integer[]{1, 3},
                "course",//①
                arg, //
                cs);//cs里的name属性代表的参数覆盖了①处代表的参数，由于cs的name属性值为null，所以md文件里对name参数引用的值为null
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "select \"id\",\"name\" ,\"class_hours\" as classHours,\"creatime\" from \"course\" where 1=1 and \"id\" in (1,3) order by \"id\" asc");
        Course compareTo = null;
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(1);
        compareTo.setClassHours(11);
        compareTo.setName("course1");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(3);
        compareTo.setClassHours(13);
        compareTo.setName("course3");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);
        Assert.equal(courseList, compareToList);
    }

    @Test
    public void testGetList() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.append(sqltxt);
        });
        List<Course> courseList = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getCouseList("course");
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "select * from \"course\" where 1=1 and \"name\" like 'course%'");
        Assert.isTrue(courseList.size() == 21);
    }

    @Test
    public void testGetOneSimpleType() {
        String ret = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneString("abc");
        Assert.equal(ret, "abc");
        Integer ret2 = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneInteger("abc");
        Assert.equal(ret2, 1);

        Integer ret3 = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneIntegerReturnNull("abc");
        Assert.isNull(ret3);

        BigInteger ret4 = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneBigInteger("abc");
        Assert.equal(ret4, 123);
        ;

        List<BigInteger> ret5 = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneBigIntegerList("abc");
        List<BigInteger> comparedList = Arrays.asList(new BigInteger("1"), new BigInteger("2"),
                new BigInteger("3"));
        Assert.equal(ret5, comparedList);

        LocalDateTime ret6 = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneLocalDateTime();
        Assert.equal(ret6, LocalDateTime.parse("2014-04-22 15:47:06", CTime.DTF_YMD_HH_MM_SS));

        Timestamp ret7 = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneTimestamp();
        Assert.equal(ret7.toLocalDateTime(), LocalDateTime.parse("2014-04-22 15:47:06", CTime.DTF_YMD_HH_MM_SS));

        List<Timestamp> ret8 = MDbUtils.getMapper(DbPoolName,
                CourseMpper.class).getOneTimestampList();
        Assert.equal(ret8.get(0).toLocalDateTime(), LocalDateTime.parse("2014-04-22 15:47:06", CTime.DTF_YMD_HH_MM_SS));
        Assert.equal(ret8.get(1).toLocalDateTime(), LocalDateTime.parse("2015-04-22 15:47:06", CTime.DTF_YMD_HH_MM_SS));
    }

    @Test
    public void testCouseListPage() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        List<Course> courseList = MDbUtils.getMapper(DbPoolName, CourseMpper.class).
                getCouseListPage("course",
                        MD.ofPage(2, 3, null));
        Assert.equal(sql.toString(),
                "select count(1) from (select * from \"course\" where 1=1 and \"name\" like 'course%') t;select * from ( select row_.*, rownum rownum_ from ( select * from \"course\" where 1=1 and \"name\" like 'course%' order by \"id\" ) row_ where rownum <= 6) where rownum_ > 3");

        StringBuffer sql2 = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql2.length() > 0) {
                sql2.append(";");
            }
            sql2.append(sqltxt);
        });

        List<Course> courseList2 = MDbUtils.getMapper(DbPoolName, CourseMpper.class).
                getCouseListPage("course",
                        MD.ofPage(2, 3, MD.md(CourseMpper.class,
                                "getCouseListPageCount"), null));
        Assert.equal(sql2.toString(),
                "select count(1) from \"course\" where 1=1 and \"name\" like 'course%';select * from ( select row_.*, rownum rownum_ from ( select * from \"course\" where 1=1 and \"name\" like 'course%' order by \"id\" ) row_ where rownum <= 6) where rownum_ > 3");

        DbContext.removeDebugSQLListener();
    }

    @Test
    public void testReturnDataBaseSet() {
        Map<String, Object> arg = new HashMap<>();
        arg.put("name", "course2");

        List<Course> courseList = new ArrayList();
        DataBaseSet rs = (DataBaseSet) MDbUtils.getMapper(DbPoolName, CourseMpper.class).getRSet(arg);
        while (rs.next()) {
            String name = rs.getString("name");
            Integer classHours = rs.getInt("class_hours");
            LocalDateTime creatime = rs.getLocalDateTime("creatime");
            Integer id = rs.getInt("id");
            Course course = new Course();
            course.setId(id);
            course.setClassHours(classHours);
            course.setName(name);
            course.setCreatime(creatime);
            courseList.add(course);
        }
        Course compareTo = null;
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(2);
        compareTo.setClassHours(12);
        compareTo.setName("course2");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(20);
        compareTo.setClassHours(26);
        compareTo.setName("course20");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(21);
        compareTo.setClassHours(27);
        compareTo.setName("course21");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);

        Assert.equal(courseList, compareToList);
    }

    @Test
    public void testReturnDataBaseSetAndPage() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        args.put("classHours", new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19});

        PageOptions pageOptions = MD.ofPage(2, 3, null);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        DataBaseSet rs = (DataBaseSet) MDbUtils.
                getMapper(DbPoolName, CourseMpper.class).getRSetPage(args, pageOptions);
        DbContext.removeDebugSQLListener();
        //select * from course where 1=1 and name like 'course%' and class_hours in(10,11,12,13,14,15,16,17,18,19) order by id limit 3, 3
        Assert.equal(sql.toString(),
                "select count(1) from (select * from \"course\" where 1=1 and \"name\" like 'course%' and \"class_hours\" in(10,11,12,13,14,15,16,17,18,19)) t;select * from ( select row_.*, rownum rownum_ from ( select * from \"course\" where 1=1 and \"name\" like 'course%' and \"class_hours\" in(10,11,12,13,14,15,16,17,18,19) order by \"id\" ) row_ where rownum <= 6) where rownum_ > 3");
        List<Course> courseList = new ArrayList();
        while (rs.next()) {
            String name = rs.getString("name");
            Integer classHours = rs.getInt("class_hours");
            LocalDateTime creatime = rs.getLocalDateTime("creatime");
            Integer id = rs.getInt("id");
            Course course = new Course();
            course.setId(id);
            course.setClassHours(classHours);
            course.setName(name);
            course.setCreatime(creatime);
            courseList.add(course);
        }
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);
        Course compareTo = null;
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(4);
        compareTo.setClassHours(10);
        compareTo.setName("course4");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(5);
        compareTo.setClassHours(11);
        compareTo.setName("course5");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(6);
        compareTo.setClassHours(12);
        compareTo.setName("course6");
        compareTo.setCreatime(localDateTime);
        compareToList.add(compareTo);

        Assert.equal(courseList, compareToList);
    }

    @Test
    public void testAddCourse() {
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);
        Course course = new Course();
        course.setName("abcdefg");
        course.setCreatime(localDateTime);
        course.setClassHours(45);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        //DbContext.setGenerateIDForInsert("id","SEQ_COURSE_ID",true);
        InsertOptions insertOptions=new InsertOptions();
        insertOptions.setIdGeneratorParmeter(MD.ofIDGenParmeter("@AKA_GEN_ID","SEQ_COURSE_ID"));
        MDbUtils.getMapper(DbPoolName, CourseMpper.class).addCourse(course,insertOptions);

        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "select \"SEQ_COURSE_ID\".nextval from dual;INSERT INTO \"course\" ( \"id\", \"name\", \"class_hours\", \"creatime\" ) VALUES ( 22, 'abcdefg', 45, to_date('2021-03-15 22:31:48','yyyy-mm-dd hh24:mi:ss') )");
        Course queryCourse = new Course();
        queryCourse.setName("abcdefg");
        queryCourse.setClassHours(45);
        queryCourse.setCreatime(localDateTime);
        List<Course> list = MDbUtils.queryListBy(DbPoolName, queryCourse);
        Assert.isTrue(list.size() == 1);

    }

    @Test
    public void testAddCourseAndReturnKey() {
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);
        Course course = new Course();
        course.setName("abcdefg");
        course.setCreatime(localDateTime);
        course.setClassHours(45);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        InsertOptions insertOptions=new InsertOptions();
        insertOptions.setIdGeneratorParmeter(MD.ofIDGenParmeter("@AKA_GEN_ID","SEQ_COURSE_ID"));
        insertOptions.setReturnFlag(ReturnFlag.AutoID);
        int key = MDbUtils.getMapper(DbPoolName, CourseMpper.class).addCourseReturnKey(course
                , insertOptions);
        Assert.equal(key,22);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "select \"SEQ_COURSE_ID\".nextval from dual;INSERT INTO \"course\" ( \"id\", \"name\", \"class_hours\", \"creatime\" ) VALUES ( 22, 'abcdefg', 45, to_date('2021-03-15 22:31:48','yyyy-mm-dd hh24:mi:ss') )");
        Course queryCourse = new Course();
        queryCourse.setName("abcdefg");
        queryCourse.setClassHours(45);
        queryCourse.setCreatime(localDateTime);
        List<Course> list = MDbUtils.queryListBy(DbPoolName, queryCourse);
        Assert.isTrue(list.size() == 1);
    }

    @Test
    public void testUpdateCourse() {
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);
        Course course = new Course();
        course.setName("abcdefg");
        course.setCreatime(localDateTime);
        course.setClassHours(45);
        course.setId(3);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        CourseMpper courseMpper = MDbUtils.getMapper(DbPoolName, CourseMpper.class);
        //courseMpper 返回的mapper对象不是线程安全的
        courseMpper.updateCourse(course);
        Assert.equal(sql.toString(), "UPDATE \"course\" SET \"name\" = 'abcdefg', \"class_hours\" = 45, \"creatime\" = to_date('2021-03-15 22:31:48','yyyy-mm-dd hh24:mi:ss') WHERE \"id\" = 3");
        courseMpper.updateCourse(course);
        Assert.equal(sql.toString(), "UPDATE \"course\" SET \"name\" = 'abcdefg', \"class_hours\" = 45, \"creatime\" = to_date('2021-03-15 22:31:48','yyyy-mm-dd hh24:mi:ss') WHERE \"id\" = 3");
        courseMpper = MDbUtils.getMapper(DbPoolName, CourseMpper.class);
        courseMpper.updateCourse(course);
        Assert.equal(sql.toString(), "UPDATE \"course\" SET \"name\" = 'abcdefg', \"class_hours\" = 45, \"creatime\" = to_date('2021-03-15 22:31:48','yyyy-mm-dd hh24:mi:ss') WHERE \"id\" = 3");

        DbContext.removeDebugSQLListener();
    }

    @Test
    public void testDropCourse() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        MDbUtils.getMapper(DbPoolName, CourseMpper.class).dropCourse();

        Assert.equal(sql.toString(), "drop table \"course\"");

        DbContext.removeDebugSQLListener();
    }

    @Test
    public void testNestTransaction() {
        TransactionTemplate.execute(() -> {
            testAddCourseAndReturnKey();
            testUpdateCourse();
        });
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS);
        Course queryCourse = new Course();
        queryCourse.setName("abcdefg");
        queryCourse.setClassHours(45);
        queryCourse.setCreatime(localDateTime);
        List<Course> list = MDbUtils.queryListBy(DbPoolName, queryCourse);
        Assert.isTrue(list.size() == 2);
    }

    //@Test
    public void testTransaction() {
        TransactionTemplate.execute(() -> {
            testUpdateCourseManual();
            testNestTransaction();
            testUpdateCourseManual();
        });
    }

    @Test
    public void testUpdateCourseManual() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        MDbUtils.getMapper(DbPoolName, CourseMpper.class).updateMyCourse();

        Assert.equal(sql.toString(), "select * from \"course\"  where \"class_hours\"=11 and \"name\"='course1';UPDATE \"course\" SET \"name\" = 'course1', \"class_hours\" = 11, \"creatime\" = null WHERE \"id\" = 1");
        DbContext.removeDebugSQLListener();
    }

    @Test
    public void testUpdateCourseManualIntrans() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        MDbUtils.getMapper(DbPoolName, CourseMpper.class).updateMyCourseIntrans();

        Assert.equal(sql.toString(), "select * from \"course\"  where \"class_hours\"=11 and \"name\"='course1';UPDATE \"course\" SET \"name\" = 'course1', \"class_hours\" = 11, \"creatime\" = null WHERE \"id\" = 1");
        Course queryCourse = new Course();
        queryCourse.setId(1);
        Course course = MDbUtils.queryOneBy(DbPoolName, queryCourse);
        Course comparedCourse = new Course();
        comparedCourse.setId(1);
        comparedCourse.setName("course1");
        comparedCourse.setClassHours(11);
        comparedCourse.setTeacherId(1);
        Assert.equal(course, comparedCourse);
        DbContext.removeDebugSQLListener();
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }
}
