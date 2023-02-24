package com.github.ulwx.aka.dbutils.mysql_shardingjdbc.dao.db_student;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2Many;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2One;
import com.github.ulwx.aka.dbutils.mysql_shardingjdbc.Utils;
import com.github.ulwx.aka.dbutils.mysql_shardingjdbc.domain.cus.One2ManyStudent;
import com.github.ulwx.aka.dbutils.mysql_shardingjdbc.domain.cus.One2OneStudent;
import com.github.ulwx.aka.dbutils.mysql_shardingjdbc.domain.db.db_student.Course;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentMapperTest {
    public static String DbPoolName = "mysql_shardingjdbc/dbpool.xml#db_student";

    @Before
    public void setup() {
        Utils.importDbStudent();
    }

    @Test
    public void testQueryListOne2One() {

        QueryMapNestOne2One queryMapNestOne2One = new QueryMapNestOne2One();
        queryMapNestOne2One.set(null, "course", "c.");
        One2OneMapNestOptions one2OneMapNestOptions = MD.ofOne2One(
                "stu."
                , queryMapNestOne2One
        );
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        List<One2OneStudent> studentList = MDbUtils.getMapper(DbPoolName, StudentMapper.class).
                getListOne2One(MD.of("student1", "student2", "student3"),
                        one2OneMapNestOptions);

        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select stu.id as `stu.id`, stu.name as `stu.name`, stu.age as `stu.age`, stu.birth_day as `stu.birth_day`, c.id as `c.id`, c.name as `c.name`, c.class_hours as `c.class_hours`, c.teacher_id as `c.teacher_id`, c.creatime as `c.creatime` from student stu,student_course sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student2','student3') order by stu.id");

        One2OneStudent compared = null;
        List<One2OneStudent> comparedList = new ArrayList<>();
        compared = new One2OneStudent();
        compared.setId(1l);
        compared.setName("student1");
        compared.setAge(40);
        compared.setBirthDay(LocalDate.parse("1980-10-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(10l);
        compared.getCourse().setName("course10");
        compared.getCourse().setClassHours(16);
        compared.getCourse().setTeacherId(4l);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(2l);
        compared.setName("student2");
        compared.setAge(39);
        compared.setBirthDay(LocalDate.parse("1981-11-01", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(13l);
        compared.getCourse().setName("course13");
        compared.getCourse().setClassHours(19);
        compared.getCourse().setTeacherId(0l);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(3l);
        compared.setName("student3");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-10-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(14l);
        compared.getCourse().setName("course14");
        compared.getCourse().setClassHours(20);
        compared.getCourse().setTeacherId(2l);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        Assert.equal(studentList, comparedList);


    }

    @Test
    public void testQueryListOne2OnePage() {

        PageBean pageBean = new PageBean();
        Map<String, Object> args = new HashMap<>();
        args.put("names", new String[]{"student1", "student2", "student3"
                , "student4", "student5", "student6", "student7", "student8", "student9"});
        QueryMapNestOne2One queryMapNestOne2One = new QueryMapNestOne2One();
        queryMapNestOne2One.set(null, "course", "c.");
        One2OneMapNestOptions one2OneMapNestOptions = MD.ofOne2One("stu.", queryMapNestOne2One);
        PageOptions pageOptions = MD.ofPage(2, 3,
                MD.md(StudentMapper.class, "getListOne2OnePageCount"),
                null);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        List<One2OneStudent> list = MDbUtils.getMapper(DbPoolName, StudentMapper.class).
                getListOne2OnePage(args,
                        one2OneMapNestOptions,
                        pageOptions);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select count(1) from student stu,student_course sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student2','student3','student4','student5','student6','student7','student8','student9');select stu.id as `stu.id`, stu.name as `stu.name`, stu.age as `stu.age`, stu.birth_day as `stu.birth_day`, c.id as `c.id`, c.name as `c.name`, c.class_hours as `c.class_hours`, c.teacher_id as `c.teacher_id`, c.creatime as `c.creatime` from student stu,student_course sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student2','student3','student4','student5','student6','student7','student8','student9') order by stu.id limit 3, 3");

        One2OneStudent compared = null;
        List<One2OneStudent> comparedList = new ArrayList<>();
        compared = new One2OneStudent();
        compared.setId(4l);
        compared.setName("student4");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-05-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(15l);
        compared.getCourse().setName("course15");
        compared.getCourse().setClassHours(21);
        compared.getCourse().setTeacherId(1l);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(5l);
        compared.setName("student5");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-06-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(12l);
        compared.getCourse().setName("course12");
        compared.getCourse().setClassHours(18);
        compared.getCourse().setTeacherId(0l);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(6l);
        compared.setName("student6");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-07-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(16l);
        compared.getCourse().setName("course16");
        compared.getCourse().setClassHours(22);
        compared.getCourse().setTeacherId(2l);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        Assert.equal(list, comparedList);

    }

    @Test
    public void testQueryListOne2Many() {

        QueryMapNestOne2Many queryMapNestOne2Many = new QueryMapNestOne2Many();
        queryMapNestOne2Many.set(Course.class,
                "courseList",
                new String[]{"id"},
                "c.",
                null);
        One2ManyMapNestOptions one2ManyMapNestOptions = MD.ofOne2Many("stu."
                , new String[]{"id"}, queryMapNestOne2Many);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });

        List<One2ManyStudent> list = MDbUtils.getMapper(DbPoolName, StudentMapper.class).
                getListOne2Many(MD.of("student1", "student4"),
                        one2ManyMapNestOptions);

        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select stu.id as `stu.id`, stu.name as `stu.name`, stu.age as `stu.age`, stu.birth_day as `stu.birth_day`, c.id as `c.id`, c.name as `c.name`, c.class_hours as `c.class_hours`, c.teacher_id as `c.teacher_id`, c.creatime as `c.creatime` from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4') order by stu.id,c.id");

        One2ManyStudent compared = null;
        Course course = null;
        List<One2ManyStudent> comparedList = new ArrayList<>();
        compared = new One2ManyStudent();
        compared.setId(1l);
        compared.setName("student1");
        compared.setAge(40);
        compared.setBirthDay(LocalDate.parse("1980-10-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(10l);
        course.setName("course10");
        course.setClassHours(16);
        course.setTeacherId(4l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        course = new Course();
        course.setId(13l);
        course.setName("course13");
        course.setClassHours(19);
        course.setTeacherId(0l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        comparedList.add(compared);

        compared = new One2ManyStudent();
        compared.setId(4l);
        compared.setName("student4");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-05-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(12l);
        course.setName("course12");
        course.setClassHours(18);
        course.setTeacherId(0l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        course = new Course();
        course.setId(15l);
        course.setName("course15");
        course.setClassHours(21);
        course.setTeacherId(1l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        comparedList.add(compared);
        Assert.equal(list, comparedList);

    }

    @Test
    public void testQueryListOne2ManyPage() {
        Map<String, Object> args = new HashMap<>();
        args.put("names", new String[]{"student1", "student4", "student6",
                "student7", "student9", "student10"});

        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        PageOptions pageOptions = MD.ofPage(2, 3, null,
                null);
        //获取某页的所有id
        List<Integer> ids = MDbUtils.getMapper(DbPoolName, StudentMapper.class).
                getPageIdList(args,
                        pageOptions);

        Assert.equal(sql.toString(),
                "select count(1) from (select stu.id as `value` from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4','student6','student7','student9','student10') group by stu.id) t;" +
                        "select stu.id as `value` from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4','student6','student7','student9','student10') group by stu.id order by stu.id limit 3, 3");
        sql.setLength(0);
        args.put("ids", ids);

        QueryMapNestOne2Many queryMapNestOne2Many = new QueryMapNestOne2Many();
        queryMapNestOne2Many.set(Course.class,
                "courseList",
                new String[]{"id"},
                "c.",
                null);
        One2ManyMapNestOptions one2ManyMapNestOptions = MD.ofOne2Many("stu.", new String[]{"id"}, queryMapNestOne2Many);

        List<One2ManyStudent> list = MDbUtils.getMapper(DbPoolName, StudentMapper.class).
                getListOne2ManyPage(args,
                        one2ManyMapNestOptions);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select stu.id as `stu.id`, stu.name as `stu.name`, stu.age as `stu.age`, stu.birth_day as `stu.birth_day`, c.id as `c.id`, c.name as `c.name`, c.class_hours as `c.class_hours`, c.teacher_id as `c.teacher_id`, c.creatime as `c.creatime` from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4','student6','student7','student9','student10') and stu.id in (7,9,10) order by stu.id,c.id");

        One2ManyStudent compared = null;
        Course course = null;
        List<One2ManyStudent> comparedList = new ArrayList<>();
        compared = new One2ManyStudent();
        compared.setId(7l);
        compared.setName("student7");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-03-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(12l);
        course.setName("course12");
        course.setClassHours(18);
        course.setTeacherId(0l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        course = new Course();
        course.setId(15l);
        course.setName("course15");
        course.setClassHours(21);
        course.setTeacherId(1l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        comparedList.add(compared);

        compared = new One2ManyStudent();
        compared.setId(9l);
        compared.setName("student9");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-06-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(14l);
        course.setName("course14");
        course.setClassHours(20);
        course.setTeacherId(2l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        course = new Course();
        course.setId(16l);
        course.setName("course16");
        course.setClassHours(22);
        course.setTeacherId(2l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        course = new Course();
        course.setId(20l);
        course.setName("course20");
        course.setClassHours(26);
        course.setTeacherId(0l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        comparedList.add(compared);

        compared = new One2ManyStudent();
        compared.setId(10l);
        compared.setName("student10");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-04-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(11l);
        course.setName("course11");
        course.setClassHours(17);
        course.setTeacherId(1l);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        comparedList.add(compared);

        Assert.equal(list, comparedList);
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }
}
