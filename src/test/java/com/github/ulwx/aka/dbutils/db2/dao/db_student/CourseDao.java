package com.github.ulwx.aka.dbutils.db2.dao.db_student;

import com.github.ulwx.aka.dbutils.database.*;
import com.github.ulwx.aka.dbutils.db2.Utils;
import com.github.ulwx.aka.dbutils.db2.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.db2.domain.db.db_teacher.Teacher;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.PageBean;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.CTime;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * 测试aka-dbutils的基本用法
 */
public class CourseDao {

    public static String DbPoolXML = "db2/dbpool.xml";
    public static String DbPoolName = DbPoolXML + "#db_student";
    public static String DbPoolName_product = DbPoolXML + "#db_student_product";

    @Before
    public void setup() {
        Utils.importDbStudent();
    }

    @Test
    public void testProfile() {
        System.setProperty(MD.AKA_PROFILE_NAME, "product");
        Course course = new Course();
        course.setName("course1");
        course.setClassHours(11);
        //MD.ofPool()方法会根据系统变量（优先级比环境变量高）和环境变量，来决定调用的真实文件
        //例如db2/dbpool.xml#db_student_product，因为系统变量设置的为"product"，
        //真实为查找db2/dbpool-product.xml#db_student_product
        List<Course> list = MDbUtils.queryListBy(MD.ofPool(DbPoolName_product), course);
        Assert.notEmpty(list);
        Course compareTo = new Course();
        compareTo.setId(1);
        compareTo.setName("course1");
        compareTo.setClassHours(11);
        compareTo.setTeacherId(1);
        compareTo.setCreatime(LocalDateTime.of(2021, 03, 15, 22, 31, 48));
        Assert.equal(list, Arrays.asList(compareTo));
        System.setProperty(MD.AKA_PROFILE_NAME, "");
    }

    @Test
    public void testQueryListBy() {
        Course course = new Course();
        course.setName("course1");
        course.setClassHours(11);
        List<Course> list = MDbUtils.queryListBy(DbPoolName, course);

        Assert.notEmpty(list);
        Course compareTo = new Course();
        compareTo.setId(1);
        compareTo.setName("course1");
        compareTo.setClassHours(11);
        compareTo.setTeacherId(1);
        compareTo.setCreatime(LocalDateTime.of(2021, 03, 15, 22, 31, 48));
        Assert.equal(list, Arrays.asList(compareTo));

        course = new Course();
        course.setTeacherId(1);
        list = MDbUtils.queryListBy(DbPoolName, course);

        Assert.equal(list.toString(),"[{\"id\":1,\"name\":\"course1\",\"classHours\":11,\"teacherId\":1,\"creatime\":\"2021-03-15 22:31:48\"}, {\"id\":5,\"name\":\"course5\",\"classHours\":11,\"teacherId\":1,\"creatime\":\"2021-03-15 22:31:48\"}, {\"id\":6,\"name\":\"course6\",\"classHours\":12,\"teacherId\":1,\"creatime\":\"2021-03-15 22:31:48\"}, {\"id\":11,\"name\":\"course11\",\"classHours\":17,\"teacherId\":1,\"creatime\":\"2021-03-15 22:31:48\"}, {\"id\":15,\"name\":\"course15\",\"classHours\":21,\"teacherId\":1,\"creatime\":\"2021-03-15 22:31:48\"}, {\"id\":18,\"name\":\"course18\",\"classHours\":24,\"teacherId\":1,\"creatime\":\"2021-03-15 22:31:48\"}]");

    }

    @Test
    public void testQueryListForPage() {
        PageBean pageBean = new PageBean();
        Course course = new Course();
        course.setTeacherId(1);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        QueryHint hint=new QueryHint();
        hint.orderBy("\"id\" asc");
        DbContext.setQueryHint(hint);
        List<Course> list =
                MDbUtils.queryListBy(DbPoolName, course, 2, 4, pageBean); // ①
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select count(1) from (select * from \"course\"  where \"teacher_id\"=1) t;select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( select * from \"course\"  where \"teacher_id\"=1   order by  \"id\" asc fetch first 8 rows only ) as inner2_ ) as inner1_ where rownumber_ > 4 order by rownumber_");
        Course compareTo = null;
        List compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(15);
        compareTo.setName("course15");
        compareTo.setClassHours(21);
        compareTo.setTeacherId(1);
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48",
                CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(18);
        compareTo.setName("course18");
        compareTo.setClassHours(24);
        compareTo.setTeacherId(1);
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48",
                CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);
        Assert.equal(list, compareToList);
        QueryHint queryHint=MD.ofQueryHint().select("\"class_hours\" as classHours , \"id\"").
                orderBy("\"id\" desc");
        DbContext.setQueryHint(queryHint);
        list = MDbUtils.queryListBy(DbPoolName, course, 2, 3, pageBean);

        Assert.equal(list.toString(), "[{\"id\":6,\"name\":null,\"classHours\":12,\"teacherId\":null,\"creatime\":null}, {\"id\":5,\"name\":null,\"classHours\":11,\"teacherId\":null,\"creatime\":null}, {\"id\":1,\"name\":null,\"classHours\":11,\"teacherId\":null,\"creatime\":null}]");

    }

    @Test
    public void testQueryListForWhere() {
        Course course = new Course();
        course.setName("course1");
        course.setClassHours(11);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.append(sqltxt);
        });
        List<Course> list = MDbUtils.queryListBy(DbPoolName,
                course, MD.of(course::getName, course::getCreatime));
        DbContext.removeDebugSQLListener();

        Assert.equal(sql.toString(), "select * from \"course\"  where \"name\"='course1' and \"creatime\"=null");
        Assert.isTrue(list.isEmpty());

    }

    @Test
    public void testQueryForResultSet() {
        List<Course> list = new ArrayList<>();
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course1");
        DataBaseSet rs = MDbUtils.queryForResultSet(DbPoolName, MD.md(), args);
        while (rs.next()) {
            String name = rs.getString("name");
            Integer classHours = rs.getInt("class_hours");
            String creatime = rs.getString("creatime");
            Course course = new Course();
            course.setName(name);
            //course.setCreatime(creatime);
            course.setClassHours(classHours);
            course.setId(rs.getInt("id"));
            list.add(course);
        }
        Course compareTo = null;
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(1);
        compareTo.setClassHours(11);
        compareTo.setName("course1");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(10);
        compareTo.setClassHours(16);
        compareTo.setName("course10");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(11);
        compareTo.setClassHours(17);
        compareTo.setName("course11");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(12);
        compareTo.setClassHours(18);
        compareTo.setName("course12");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(13);
        compareTo.setClassHours(19);
        compareTo.setName("course13");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(14);
        compareTo.setClassHours(20);
        compareTo.setName("course14");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(15);
        compareTo.setClassHours(21);
        compareTo.setName("course15");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(16);
        compareTo.setClassHours(22);
        compareTo.setName("course16");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(17);
        compareTo.setClassHours(23);
        compareTo.setName("course17");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(18);
        compareTo.setClassHours(24);
        compareTo.setName("course18");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(19);
        compareTo.setClassHours(25);
        compareTo.setName("course19");
        compareToList.add(compareTo);

        Assert.equal(list, compareToList);

    }

    @Test
    public void testQueryForResultSetPage() {
        List<Course> list = new ArrayList<>();
        PageBean pageBean = new PageBean();
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        args.put("classHours", new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19});
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.append(sqltxt);
        });
        DataBaseSet rs = MDbUtils.queryForResultSet(DbPoolName, MD.md(), args, 2,
                5, pageBean, "-1");
        // DataBaseSet rs=MDbUtils.queryForResultSet(DbPoolName, MD.md(), args, 2,5, pageBean, "");
        DbContext.removeDebugSQLListener();
        while (rs.next()) {
            String name = rs.getString("name");
            Integer classHours = rs.getInt("class_hours");
            Course course = new Course();
            course.setName(name);
            course.setClassHours(classHours);
            course.setId(rs.getInt("id"));
            list.add(course);
        }

        Assert.equal(sql.toString(), "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( select * from \"course\" where 1=1 and \"name\" like 'course%' and \"class_hours\" in(10,11,12,13,14,15,16,17,18,19) order by \"id\" fetch first 10 rows only ) as inner2_ ) as inner1_ where rownumber_ > 5 order by rownumber_");

        Course compareTo = null;
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(6);
        compareTo.setClassHours(12);
        compareTo.setName("course6");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(7);
        compareTo.setClassHours(13);
        compareTo.setName("course7");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(8);
        compareTo.setClassHours(14);
        compareTo.setName("course8");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(9);
        compareTo.setClassHours(15);
        compareTo.setName("course9");
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(10);
        compareTo.setClassHours(16);
        compareTo.setName("course10");
        compareToList.add(compareTo);

        Assert.equal(list, compareToList);

    }

    @Test
    public void testQueryList() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        args.put("classHours", new Integer[]{10, 11});
        List<Course> list = MDbUtils.queryList(DbPoolName, Course.class, MD.md(), args);
        for (int i = 0; i < list.size(); i++) {
            Course course = list.get(i);
        }

        Course compareTo = null;
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(1);
        compareTo.setClassHours(11);
        compareTo.setName("course1");
        compareTo.setTeacherId(1);
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(4);
        compareTo.setClassHours(10);
        compareTo.setName("course4");
        compareTo.setTeacherId(4);
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(5);
        compareTo.setClassHours(11);
        compareTo.setName("course5");
        compareTo.setTeacherId(1);
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);
        Assert.equal(list, compareToList);
    }

    @Test
    public void testQueryListMdPage() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        PageBean pageBean = new PageBean();
        List<Course> list = MDbUtils.queryList(DbPoolName, Course.class, MD.md(), args, 2, 3, pageBean, null);

        Assert.equal(list.size(), 3);

    }

    @Test
    public void testQueryListWithRowMapper() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        args.put("ids", new Integer[]{10, 11, 12});
        List<Course> list = MDbUtils.queryList(DbPoolName, MD.md(), args, (rs) -> {
            Course course = new Course();
            course.setId(rs.getInt("id"));
            course.setName(rs.getString("name"));
            course.setClassHours(rs.getInt("class_hours"));
            course.setCreatime(CTime.sqlTimestampToLocalDateTime(rs.getTimestamp("creatime")));
            return course;
        });
        for (int i = 0; i < list.size(); i++) {
            Course course = list.get(i);
        }
        Course compareTo = null;
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(10);
        compareTo.setClassHours(16);
        compareTo.setName("course10");
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(11);
        compareTo.setClassHours(17);
        compareTo.setName("course11");
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new Course();
        compareTo.setId(12);
        compareTo.setClassHours(18);
        compareTo.setName("course12");
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        Assert.equal(list, compareToList);

    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }
    @Test
    public void testStoredProc() {
        Map<String, Object> args = new HashMap<>();
        args.put("name:in", "course1");
        args.put("count:in", 222);//存入的是类型
        Map<String, Object> out = new HashMap<>();
        List<DataBaseSet> list = new ArrayList<>();
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        MDbUtils.callStoredPro(DbPoolName, MD.md(), args, out, list);
        Assert.equal(sql.toString(),
                "{call \"query_course_proc\"('course1',222)}");
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                DataBaseSet dataBaseSet = list.get(i);
                dataBaseSet.next();
                Assert.equal(dataBaseSet.getString("name_"), "course1");
                Assert.equal(dataBaseSet.getInt("class_hours_"), 11);
                Assert.equal(dataBaseSet.getLocalDateTime("creatime_").format(CTime.DTF_YMD_HH_MM_SS)
                        , "2021-03-15 22:31:48");
            }
        }

    }

    @Test
    public void testStoredFunc() throws Exception {
        // Map<String, Object> args = new HashMap<>();
        // args.put("name:in", "course1");
        // args.put("count:out", Integer.class);
        // Map<String, Object> outMap = new HashMap<>();
        // StringBuffer sql = new StringBuffer();
        // DbContext.setDebugSQLListener(sqltxt -> {
        //     sql.setLength(0);
        //     sql.append(sqltxt);
        // });
        // MDbUtils.callStoredPro(DbPoolName, MD.md(), args, outMap, null);
        // Assert.equal(sql.toString(),
        //         "{?:int= call query_course_cnt_func('course1')}");
        // System.out.println("out=" + ObjectUtils.toString(outMap));
        // Map<String, Object> outComparedMap = new HashMap<>();
        // outComparedMap.put("count", 1);
        // Assert.equal(outMap, outComparedMap);

    }
    @Test
    public void testQueryOne() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        args.put("classHours", new Integer[]{10, 11});
        Course course = MDbUtils.queryOne(DbPoolName, Course.class, MD.md(), args);

        Course compareTo = null;
        List<Course> compareToList = new ArrayList();
        compareTo = new Course();
        compareTo.setId(1);
        compareTo.setClassHours(11);
        compareTo.setName("course1");
        compareTo.setTeacherId(1);
        compareTo.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        Assert.equal(course, compareTo);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                Teacher teacher = MDbUtils.queryOneBy(DbPoolXML + "#xyz", new Teacher());
                Assert.notNull(teacher);
            });
        }
        try {
            executorService.shutdown();
            executorService.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void testQueryMap() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        args.put("classHours", new Integer[]{10, 11});
        List<Map<String, Object>> mapList = MDbUtils.queryMap(DbPoolName, MD.md(), args);

        Map<String, Object> compareTo = null;
        List<Map<String, Object>> compareToList = new ArrayList();
        compareTo = new HashMap<>();
        compareTo.put("id", 1);
        compareTo.put("class_hours", 11);
        compareTo.put("name", "course1");
        compareTo.put("teacher_id", 1);
        compareTo.put("creatime", LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new HashMap<>();
        compareTo.put("id", 4);
        compareTo.put("class_hours", 10);
        compareTo.put("name", "course4");
        compareTo.put("teacher_id", 4);
        compareTo.put("creatime", LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new HashMap<>();
        compareTo.put("id", 5);
        compareTo.put("class_hours", 11);
        compareTo.put("name", "course5");
        compareTo.put("teacher_id", 1);
        compareTo.put("creatime", LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        Assert.equal(mapList, compareToList);
    }

    @Test
    public void testQueryMapPage() {
        PageBean pageBean = new PageBean();
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course");
        args.put("ids", MD.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        List<Map<String, Object>> list = MDbUtils.queryMap(DbPoolName, MD.md(), args, 2, 3,
                pageBean,
                "-1"); //-1表示总记录行未知，aka-dbutils不会生成计算总数的SQL

        Map<String, Object> compareTo = null;
        List<Map<String, Object>> compareToList = new ArrayList();
        compareTo = new HashMap<>();
        compareTo.put("id", 4);
        compareTo.put("class_hours", 10);
        compareTo.put("name", "course4");
        compareTo.put("teacher_id", 4);
        compareTo.put("creatime", LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new HashMap<>();
        compareTo.put("id", 5);
        compareTo.put("class_hours", 11);
        compareTo.put("name", "course5");
        compareTo.put("teacher_id", 1);
        compareTo.put("creatime", LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        compareTo = new HashMap<>();
        compareTo.put("id", 6);
        compareTo.put("class_hours", 12);
        compareTo.put("name", "course6");
        compareTo.put("teacher_id", 1);
        compareTo.put("creatime", LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compareToList.add(compareTo);

        Assert.equal(list.size() ,compareToList.size());

        PageBean comparedPageBean = new PageBean();
        comparedPageBean.setStart(3);
        comparedPageBean.setEnd(6);
        comparedPageBean.setPerPage(3);
        comparedPageBean.setTotal(-1);
        comparedPageBean.setPage(2);
        comparedPageBean.setMaxPage(-1);
        Assert.equal(pageBean, comparedPageBean);

    }

    @Test
    public void testAdd() {
        Course course = new Course();
        course.setName("add");
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS));
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        int ret = MDbUtils.insertBy(DbPoolName, course);
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"creatime\",\"name\") values('2021-03-15 22:31:40','add')");
        ret = MDbUtils.insertBy(DbPoolName, course, true);
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"class_hours\",\"creatime\",\"name\",\"teacher_id\") values(null,'2021-03-15 22:31:40','add',null)");
        ret = MDbUtils.insertBy(DbPoolName, course, MD.of("name", "id"));
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"name\") values('add')");
        ret = MDbUtils.insertBy(DbPoolName, course, MD.of(Course::getName, Course::getId));
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"name\") values('add')");
        ret = MDbUtils.insertBy(DbPoolName, course, MD.of(Course::getName, Course::getId), false);
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"name\") values('add')");
        DbContext.removeDebugSQLListener();
        Assert.equal(ret, 1);

    }

    @Test
    public void testAddAndReturnKey() {
        Course course = new Course();
        course.setName("add");
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS));
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        int key = (int) MDbUtils.insertReturnKeyBy(DbPoolName, course);
        Assert.equal(course.getId(),key);
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"creatime\",\"name\") values('2021-03-15 22:31:40','add')");
        DbContext.removeDebugSQLListener();

    }

    @Test
    public void testAddManyObjs() {
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);
        Course course1 = new Course();
        course1.setName("add1");
        course1.setCreatime(localDateTime);
        Course course2 = new Course();
        course2.setName("add2");
        course2.setCreatime(localDateTime);
        Course course3 = new Course();
        course3.setName("add3");
        course3.setCreatime(localDateTime);
        Course[] courses = new Course[]{course1, course2, course3};
        int[] rets = null;
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        rets = MDbUtils.insertBy(DbPoolName, courses);
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"creatime\",\"name\") values('2021-03-15 22:31:40','add1');insert into \"course\" (\"creatime\",\"name\") values('2021-03-15 22:31:40','add2');insert into \"course\" (\"creatime\",\"name\") values('2021-03-15 22:31:40','add3')");
        sql.setLength(0);
        rets = MDbUtils.insertBy(DbPoolName, courses, true);
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"class_hours\",\"creatime\",\"name\",\"teacher_id\") values(null,'2021-03-15 22:31:40','add1',null);insert into \"course\" (\"class_hours\",\"creatime\",\"name\",\"teacher_id\") values(null,'2021-03-15 22:31:40','add2',null);insert into \"course\" (\"class_hours\",\"creatime\",\"name\",\"teacher_id\") values(null,'2021-03-15 22:31:40','add3',null)");
        sql.setLength(0);
        rets = MDbUtils.insertBy(DbPoolName, courses, MD.of("name", "id"));
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"name\") values('add1');insert into \"course\" (\"name\") values('add2');insert into \"course\" (\"name\") values('add3')");
        sql.setLength(0);
        rets = MDbUtils.insertBy(DbPoolName, courses, MD.of(Course::getName, Course::getId));
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"name\") values('add1');insert into \"course\" (\"name\") values('add2');insert into \"course\" (\"name\") values('add3')");
        sql.setLength(0);
        rets = MDbUtils.insertBy(DbPoolName, courses, MD.of(Course::getName, Course::getId), false);
        Assert.equal(sql.toString(),
                "insert into \"course\" (\"name\") values('add1');insert into \"course\" (\"name\") values('add2');insert into \"course\" (\"name\") values('add3')");
        DbContext.removeDebugSQLListener();

    }

    @Test
    public void testUpdate() {
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);
        Course course = new Course();
        course.setName("add");
        course.setCreatime(localDateTime);
        int ret = 0;
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of("name", "id"));
        Assert.equal(sql.toString(),
                "update \"course\"  set \"creatime\"='2021-03-15 22:31:40' where \"name\"='add' and \"id\"=null");
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of(Course::getName, Course::getId));
        Assert.equal(sql.toString(),
                "update \"course\"  set \"creatime\"='2021-03-15 22:31:40' where \"name\"='add' and \"id\"=null");
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of("name", "id"), true);
        Assert.equal(sql.toString(),
                "update \"course\"  set \"class_hours\"=null,\"creatime\"='2021-03-15 22:31:40',\"teacher_id\"=null where \"name\"='add' and \"id\"=null");
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of("name"), MD.of(Course::getCreatime));
        Assert.equal(sql.toString(),
                "update \"course\"  set \"creatime\"='2021-03-15 22:31:40' where \"name\"='add'");

        DbContext.removeDebugSQLListener();

        Course newCourse = new Course();
        newCourse.setName("add1");
        newCourse.setCreatime(localDateTime);
        Course[] courses = new Course[]{course, newCourse};
        int[] rets = null;
        StringBuffer sql2 = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql2.length() > 0) {
                sql2.append(";");
            }
            sql2.append(sqltxt);
        });
        rets = MDbUtils.updateBy(DbPoolName, courses, MD.of("name"));
        Assert.equal(sql2.toString(),
                "update \"course\"  set \"creatime\"='2021-03-15 22:31:40' where \"name\"='add';update \"course\"  set \"creatime\"='2021-03-15 22:31:40' where \"name\"='add1'");

    }

    @Test
    public void testUpdateCourse() {
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);
        Course course = new Course();
        course.setName("add");
        course.setId(1);
        course.setCreatime(localDateTime);
        int ret = 0;
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of("name", "id"));
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "update \"course\"  set \"creatime\"='2021-03-15 22:31:40' where \"name\"='add' and \"id\"=1");

    }

    @Test
    public void testDelAll() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        MDbUtils.del(DbPoolName, MD.md(), null);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(), "delete from \"course\"");

    }

    @Test
    public void testDelete() {
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);

        Course course = new Course();
        course.setName("add");
        int ret = 0;
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        ret = MDbUtils.delBy(DbPoolName, course, MD.of("name", "id"));
        Assert.equal(sql.toString(), "delete from \"course\" where \"name\"='add' and \"id\"=null");
        ret = MDbUtils.delBy(DbPoolName, course, MD.of(Course::getName, Course::getId));
        Assert.equal(sql.toString(), "delete from \"course\" where \"name\"='add' and \"id\"=null");
        DbContext.removeDebugSQLListener();

        Course newCourse = new Course();
        newCourse.setName("add1");
        newCourse.setCreatime(localDateTime);
        Course[] courses = new Course[]{course, newCourse};
        int[] rets = null;
        StringBuffer sql2 = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql2.length() > 0) {
                sql2.append(";");
            }
            sql2.append(sqltxt);
        });
        rets = MDbUtils.delManyBy(DbPoolName, courses, MD.of("name"));
        DbContext.removeDebugSQLListener();
        Assert.equal(sql2.toString(), "delete from \"course\" where \"name\"='add';delete from \"course\" where \"name\"='add1'");

    }

    @Test
    public void testInsertWithMd() {

        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);

        Map<String, Object> args = new HashMap<>();
        args.put("name", "course_md");
        args.put("classHours", 123);
        args.put("creatime", localDateTime);
        MDbUtils.insert(DbPoolName, MD.md(), args);
        Assert.equal(sql.toString(),
                "INSERT INTO \"course\" ( \"name\", \"class_hours\", \"creatime\" ) VALUES ( 'course_md', 123, '2021-03-15 22:31:40' )");

        Course course1 = new Course();
        course1.setName("course_md01");
        course1.setClassHours(231);
        course1.setCreatime(localDateTime);
        MDbUtils.insert(DbPoolName, MD.md(), MD.map(course1));
        Assert.equal(sql.toString(),
                "INSERT INTO \"course\" ( \"name\", \"class_hours\", \"creatime\" ) VALUES ( 'course_md01', 231, '2021-03-15 22:31:40' )");

        Course course2 = new Course();
        course2.setName("course_md02");
        course2.setClassHours(232);
        course2.setCreatime(localDateTime);
        StringBuffer sql2 = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql2.length() > 0) {
                sql2.append(";");
            }
            sql2.append(sqltxt);
        });
        MDbUtils.insert(DbPoolName, MD.md(), MD.mapList(course1, course2));
        Assert.equal(sql2.toString(), "INSERT INTO \"course\" ( \"name\", \"class_hours\", \"creatime\" ) VALUES ( 'course_md01', 231, '2021-03-15 22:31:40' );INSERT INTO \"course\" ( \"name\", \"class_hours\", \"creatime\" ) VALUES ( 'course_md02', 232, '2021-03-15 22:31:40' )");
        DbContext.removeDebugSQLListener();

    }

    @Test
    public void testUpdateWithMd() {
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);

        Map<String, Object> args = new HashMap<>();
        args.put("name", "course_md");
        args.put("classHours", 123);
        args.put("creatime", localDateTime);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        MDbUtils.update(DbPoolName, MD.md(), args);
        Assert.equal(sql.toString(), "UPDATE \"course\" SET \"class_hours\" = 123, \"creatime\" = '2021-03-15 22:31:40' WHERE \"name\" = 'course_md'");

        Course course1 = new Course();
        course1.setName("course_md01");
        course1.setClassHours(231);
        course1.setCreatime(localDateTime);
        MDbUtils.update(DbPoolName, MD.md(), MD.map(course1));
        Assert.equal(sql.toString(), "UPDATE \"course\" SET \"class_hours\" = 231, \"creatime\" = '2021-03-15 22:31:40' WHERE \"name\" = 'course_md01'");

        Course course2 = new Course();
        course2.setName("course_md02");
        course2.setClassHours(232);
        course2.setCreatime(localDateTime);
        StringBuffer sql2 = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql2.length() > 0) {
                sql2.append(";");
            }
            sql2.append(sqltxt);
        });
        MDbUtils.update(DbPoolName, MD.md(), MD.mapList(course1, course2));
        Assert.equal(sql2.toString(),
                "UPDATE \"course\" SET \"class_hours\" = 231, \"creatime\" = '2021-03-15 22:31:40' WHERE \"name\" = 'course_md01';UPDATE \"course\" SET \"class_hours\" = 232, \"creatime\" = '2021-03-15 22:31:40' WHERE \"name\" = 'course_md02'");
        DbContext.removeDebugSQLListener();

    }

    @Test
    public void testDeleteWithMd() {

        Map<String, Object> args = new HashMap<>();
        args.put("name", "course_md");
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        MDbUtils.del(DbPoolName, MD.md(), args);
        Assert.equal(sql.toString(),
                "DELETE FROM \"course\" WHERE \"name\" = 'course_md'");
        DbContext.removeDebugSQLListener();

    }
    @Test
    public void testInsertWithMdReturnKey() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);

        Map<String, Object> args = new HashMap<>();
        args.put("name", "course_md");
        args.put("classHours", 123);
        args.put("creatime", localDateTime);
        long ret=MDbUtils.insertReturnKey(DbPoolName, MD.md(), args);
        Assert.equal(ret,22);
        Assert.equal(sql.toString(),
                "INSERT INTO \"course\" ( \"name\", \"class_hours\", \"creatime\" ) VALUES ( 'course_md', 123, '2021-03-15 22:31:40' )");

    }

    @Test
    public void testInsertWithSQL() {
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);

        Map<String, Object> args = new HashMap<>();
        args.put("name", "course_md");
        args.put("classHours", 123);
        args.put("creatime", localDateTime);
        String sqlContent="INSERT INTO \"course\" (" +
                "\"name\"," +
                "\"class_hours\"," +
                "\"creatime\"" +
                ")" +
                "VALUES" +
                "(" +
                "#{name}," +
                "#{classHours}," +
                "#{creatime}" +
                ")";
        long ret=MDbUtils.insertReturnKey(DbPoolName, "sql:"+sqlContent, args);
        Assert.equal(ret,22);
        Assert.equal(sql.toString(),
                "INSERT INTO \"course\" (\"name\",\"class_hours\",\"creatime\")VALUES('course_md',123,'2021-03-15 22:31:40')");

    }
    @Test
    public void testExeSqlScript() throws Exception {
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        Map<String, Object> args = new HashMap<>();
        args.put("name", "course_md");
        args.put("classHours", 123);
        args.put("creatime", localDateTime);
        String str = MDbUtils.exeScript(DbPoolName, MD.md(),false, ";", args);
        Assert.equal(sql.toString(),
                "INSERT INTO \"course\" ( \"name\", \"class_hours\", \"creatime\" ) VALUES ( 'course_md', 123, '2021-03-15 22:31:40' );select * from \"course\";select * from \"course\" where \"name\"='course_md';UPDATE \"course\" SET \"class_hours\" = 123, \"creatime\" = '2021-03-15 22:31:40' WHERE \"name\" = 'course_md';DELETE FROM \"course\" WHERE \"name\" = 'course_md'");

        Assert.hasText(str);
        System.out.println(str);
        sql.setLength(0);
        str = MDbUtils.exeScript(DbPoolName, CourseDao.class.getPackage().getName(),
                "testscript.sql", false,null);
        System.out.println("str="+str);
        Assert.hasText(str);
        DbContext.removeDebugSQLListener();

    }


    public Course testInsertInTrans(MDataBase mdb) {
        LocalDateTime  localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);
        Course course = new Course();
        course.setName("addxyz");
        course.setCreatime(localDateTime);
        long key = mdb.insertReturnKeyBy(course);
        course.setId((int) key);
        return course;
    }

    public void testUpdateInTrans(MDataBase mdb, Course course) {
        mdb.updateBy(course, MD.of("id"));
    }

    /**
     * 测试aka-dbutils支持事务功能
     */
    @Test
    public void testTransaction() {
        MDataBase mdb = null;
        try {
            mdb = MDbManager.getDataBase(DbPoolName);
            mdb.setAutoCommit(false);
            Course course = testInsertInTrans(mdb);
            course.setName("addxyz1234");
            testUpdateInTrans(mdb, course);
            mdb.commit();
        } catch (Exception e) {
            mdb.rollback();
            throw new DbException(e);
        } finally {
            if (mdb != null) {
                mdb.close();
            }
        }
        Course course = new Course();
        course.setName("addxyz1234");
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS));
        Course returnCourse = MDbUtils.queryOneBy(DbPoolName, course);
        Assert.notNull(returnCourse);

    }

    /**
     * 测试aka-dbutils支持事务功能
     */
    @Test
    public void testTransactionForRollBack() {
        MDataBase mdb = null;
        try {
            mdb = MDbManager.getDataBase(DbPoolName);
            mdb.setAutoCommit(false);
            Course course = testInsertInTrans(mdb);
            testUpdateInTrans(mdb, course);
            int i = 1 / 0;//使之抛出异常，使其回滚
            mdb.commit();
        } catch (Exception e) {
            mdb.rollback();
        } finally {
            if (mdb != null) {
                mdb.close();
            }
        }
        Course course = new Course();
        course.setName("addxyz1234");
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS));
        Course returnCourse = MDbUtils.queryOneBy(DbPoolName, course);
        Assert.isNull(returnCourse);

    }


    /**
     * 测试aka-dbutils支持事务功能
     */
    @Test
    public void testTransactionForRollBackSavePont() {
        MDataBase mdb = null;
        try {
            mdb = MDbManager.getDataBase(DbPoolName);
            mdb.setAutoCommit(false);
            Course course = testInsertInTrans(mdb);
            mdb.setSavepoint("abc");
            course.setName("uwx");
            testUpdateInTrans(mdb, course);
            mdb.rollbackToSavepoint("abc");
            mdb.commit();
        } catch (Exception e) {
            mdb.rollback();
        } finally {
            if (mdb != null) {
                mdb.close();
            }
        }
        Course course = new Course();
        course.setName("addxyz");
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS));
        Course returnCourse = MDbUtils.queryOneBy(DbPoolName, course);
        Assert.notNull(returnCourse);
        Assert.state(returnCourse.getName().equals("addxyz"));

    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }

    public void testUpdateInManager(String name) {
        Course course = new Course();
        course.setId(1);
        course.setName(name);
        int ret = MDbUtils.updateBy(DbPoolName, course, MD.of("id"));

    }
}
