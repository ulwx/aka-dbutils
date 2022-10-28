package com.github.ulwx.aka.dbutils.sqlserver.dao.db_student;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2Many;
import com.github.ulwx.aka.dbutils.database.QueryMapNestOne2One;
import com.github.ulwx.aka.dbutils.sqlserver.Utils;
import com.github.ulwx.aka.dbutils.sqlserver.domain.cus.MyStudent;
import com.github.ulwx.aka.dbutils.sqlserver.domain.cus.One2ManyStudent;
import com.github.ulwx.aka.dbutils.sqlserver.domain.cus.One2OneStudent;
import com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_student.Student;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.PageBean;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.CTime;
import com.github.ulwx.aka.dbutils.tool.support.type.TInteger;
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
public class StudentDao {
    public static String DbPoolName = "sqlserver/dbpool.xml#db_student";

    @Before
    public void setup() {
        Utils.importDbStudent();
    }

    @Test
    public void testQueryListOne2One() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", new String[]{"student1", "student2", "student3"});
        QueryMapNestOne2One queryMapNestOne2One = new QueryMapNestOne2One();
        queryMapNestOne2One.set(null, "course", "c@");
        One2OneMapNestOptions one2OneMapNestOptions = MD.ofOne2One(
                "stu@"
                , queryMapNestOne2One
        );
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        List<One2OneStudent> list = MDbUtils.queryListOne2One(DbPoolName, One2OneStudent.class,
                MD.md(), args, one2OneMapNestOptions);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select stu.id as stu@id, stu.name as stu@name, stu.age as stu@age, stu.birth_day as stu@birth_day, c.id  as c@id, c.name as c@name, c.class_hours as c@class_hours, c.teacher_id as c@teacher_id, c.creatime as c@creatime from student stu,student_course sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student2','student3') order by stu.id");

        One2OneStudent compared = null;
        List<One2OneStudent> comparedList = new ArrayList<>();
        compared = new One2OneStudent();
        compared.setId(1);
        compared.setName("student1");
        compared.setAge(40);
        compared.setBirthDay(LocalDate.parse("1980-10-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(10);
        compared.getCourse().setName("course10");
        compared.getCourse().setClassHours(16);
        compared.getCourse().setTeacherId(4);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(2);
        compared.setName("student2");
        compared.setAge(39);
        compared.setBirthDay(LocalDate.parse("1981-11-01", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(13);
        compared.getCourse().setName("course13");
        compared.getCourse().setClassHours(19);
        compared.getCourse().setTeacherId(0);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(3);
        compared.setName("student3");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-10-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(14);
        compared.getCourse().setName("course14");
        compared.getCourse().setClassHours(20);
        compared.getCourse().setTeacherId(2);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        Assert.equal(list, comparedList);

    }

    @Test
    public void testQueryListOne2OnePage() {
        PageBean pageBean = new PageBean();
        Map<String, Object> args = new HashMap<>();
        args.put("name", new String[]{"student1", "student2", "student3"
                , "student4", "student5", "student6", "student7", "student8", "student9"});
        QueryMapNestOne2One queryMapNestOne2One = new QueryMapNestOne2One();
        queryMapNestOne2One.set(null, "course", "c@");
        One2OneMapNestOptions one2OneMapNestOptions = MD.ofOne2One("stu@", queryMapNestOne2One);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        List<One2OneStudent> list = MDbUtils.queryListOne2One(DbPoolName, One2OneStudent.class,
                MD.md(), args,
                one2OneMapNestOptions, 2,
                3, pageBean, MD.md(this.getClass(),
                        "testQueryListOne2OnePageCount"));
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select count(1) from student stu,student_course sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student2','student3','student4','student5','student6','student7','student8','student9');select stu.id as stu@id, stu.name as stu@name, stu.age as stu@age, stu.birth_day as stu@birth_day, c.id  as c@id, c.name as c@name, c.class_hours as c@class_hours, c.teacher_id as c@teacher_id, c.creatime as c@creatime from student stu,student_course sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student2','student3','student4','student5','student6','student7','student8','student9') order by stu.id offset 3 rows fetch next 3 rows only");

        One2OneStudent compared = null;
        List<One2OneStudent> comparedList = new ArrayList<>();
        compared = new One2OneStudent();
        compared.setId(4);
        compared.setName("student4");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-05-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(15);
        compared.getCourse().setName("course15");
        compared.getCourse().setClassHours(21);
        compared.getCourse().setTeacherId(1);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(5);
        compared.setName("student5");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-06-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(12);
        compared.getCourse().setName("course12");
        compared.getCourse().setClassHours(18);
        compared.getCourse().setTeacherId(0);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        compared = new One2OneStudent();
        compared.setId(6);
        compared.setName("student6");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-07-08", CTime.DTF_YMD));
        compared.setCourse(new Course());
        compared.getCourse().setId(16);
        compared.getCourse().setName("course16");
        compared.getCourse().setClassHours(22);
        compared.getCourse().setTeacherId(2);
        compared.getCourse().setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        comparedList.add(compared);

        Assert.equal(list, comparedList);

    }

    @Test
    public void testQueryListOne2Many() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", new String[]{"student1", "student4"});
        QueryMapNestOne2Many queryMapNestOne2Many = new QueryMapNestOne2Many();
        queryMapNestOne2Many.set(Course.class,
                "courseList",
                new String[]{"id"},
                "c@",
                null);
        One2ManyMapNestOptions one2ManyMapNestOptions = MD.ofOne2Many("stu@"
                , new String[]{"id"}, queryMapNestOne2Many);
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        List<One2ManyStudent> list = MDbUtils.queryListOne2Many(DbPoolName, One2ManyStudent.class,
                MD.md(),
                args,
                one2ManyMapNestOptions);
        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select stu.id as stu@id, stu.name as stu@name, stu.age as stu@age, stu.birth_day as stu@birth_day, c.id  as c@id, c.name as c@name, c.class_hours as c@class_hours, c.teacher_id as c@teacher_id, c.creatime as c@creatime from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4') order by stu.id,c.id");

        One2ManyStudent compared = null;
        Course course = null;
        List<One2ManyStudent> comparedList = new ArrayList<>();
        compared = new One2ManyStudent();
        compared.setId(1);
        compared.setName("student1");
        compared.setAge(40);
        compared.setBirthDay(LocalDate.parse("1980-10-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(10);
        course.setName("course10");
        course.setClassHours(16);
        course.setTeacherId(4);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        course = new Course();
        course.setId(13);
        course.setName("course13");
        course.setClassHours(19);
        course.setTeacherId(0);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        comparedList.add(compared);

        compared = new One2ManyStudent();
        compared.setId(4);
        compared.setName("student4");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-05-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(12);
        course.setName("course12");
        course.setClassHours(18);
        course.setTeacherId(0);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        course = new Course();
        course.setId(15);
        course.setName("course15");
        course.setClassHours(21);
        course.setTeacherId(1);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        comparedList.add(compared);
        Assert.equal(list, comparedList);

    }

    public int[] getPageIdList(Map<String, Object> args, int pageNum, int perPage, PageBean pb) {
        List<TInteger> list = MDbUtils.
                queryList(DbPoolName, TInteger.class,
                        MD.md(), args, pageNum, perPage, pb,
                        null);//让aka-dbutils自动为我们生成计算总数的分页SQL
        int[] ret = list.stream().mapToInt(TInteger::getValue).toArray();
        return ret;
    }

    @Test
    public void testQueryListOne2ManyPage() {
        Map<String, Object> args = new HashMap<>();
        args.put("names", new String[]{"student1", "student4", "student6",
                "student7", "student9", "student10"});
        //获取某页的所有id
        int pageNum = 2;
        int perPage = 3;
        PageBean pageBean = new PageBean();
        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        int[] ids = this.getPageIdList(args, pageNum, perPage, pageBean);

        Assert.equal(sql.toString(),
                "select count(1) from (select stu.id as [value] from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4','student6','student7','student9','student10') group by stu.id) t;select stu.id as [value] from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4','student6','student7','student9','student10') group by stu.id order by stu.id offset 3 rows fetch next 3 rows only");
        sql.setLength(0);
        args.put("ids", ids);

        QueryMapNestOne2Many queryMapNestOne2Many = new QueryMapNestOne2Many();
        queryMapNestOne2Many.set(Course.class,
                "courseList",
                new String[]{"id"},
                "c@",
                null);
        One2ManyMapNestOptions one2ManyMapNestOptions = MD.ofOne2Many("stu@", new String[]{"id"}, queryMapNestOne2Many);
        List<One2ManyStudent> list = MDbUtils.queryListOne2Many(DbPoolName, One2ManyStudent.class,
                MD.md(),
                args,
                one2ManyMapNestOptions);

        DbContext.removeDebugSQLListener();
        Assert.equal(sql.toString(),
                "select stu.id as stu@id, stu.name as stu@name, stu.age as stu@age, stu.birth_day as stu@birth_day, c.id  as c@id, c.name as c@name, c.class_hours as c@class_hours, c.teacher_id as c@teacher_id, c.creatime as c@creatime from student stu,student_many_courses sc,course c where stu.id=sc.student_id and c.id=sc.course_id and stu.name in ('student1','student4','student6','student7','student9','student10') and stu.id in (7,9,10) order by stu.id,c.id");

        One2ManyStudent compared = null;
        Course course = null;
        List<One2ManyStudent> comparedList = new ArrayList<>();
        compared = new One2ManyStudent();
        compared.setId(7);
        compared.setName("student7");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-03-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(12);
        course.setName("course12");
        course.setClassHours(18);
        course.setTeacherId(0);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        course = new Course();
        course.setId(15);
        course.setName("course15");
        course.setClassHours(21);
        course.setTeacherId(1);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        comparedList.add(compared);

        compared = new One2ManyStudent();
        compared.setId(9);
        compared.setName("student9");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-06-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(14);
        course.setName("course14");
        course.setClassHours(20);
        course.setTeacherId(2);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        course = new Course();
        course.setId(16);
        course.setName("course16");
        course.setClassHours(22);
        course.setTeacherId(2);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);

        course = new Course();
        course.setId(20);
        course.setName("course20");
        course.setClassHours(26);
        course.setTeacherId(0);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        comparedList.add(compared);

        compared = new One2ManyStudent();
        compared.setId(10);
        compared.setName("student10");
        compared.setAge(38);
        compared.setBirthDay(LocalDate.parse("1982-04-08", CTime.DTF_YMD));
        compared.setCourseList(new ArrayList<>());
        course = new Course();
        course.setId(11);
        course.setName("course11");
        course.setClassHours(17);
        course.setTeacherId(1);
        course.setCreatime(LocalDateTime.parse("2021-03-15 22:31:48", CTime.DTF_YMD_HH_MM_SS));
        compared.getCourseList().add(course);
        comparedList.add(compared);

        Assert.equal(list, comparedList);


    }

    @Test
    public void testJavaBeanInherit() {
        MyStudent myStudent = new MyStudent();
        myStudent.setAddress("abc");
        myStudent.setName("xyz");
        myStudent.setAge(12);
        myStudent.setBirthDay(LocalDate.parse("2021-03-15", CTime.DTF_YMD));

        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            if (sql.length() > 0) {
                sql.append(";");
            }
            sql.append(sqltxt);
        });
        int ret = 0;
        //下面语句如果去掉注释会报错，生成insert into `my_student` (`birth_day`,`address`,`name`,`age`) values('2021-02-14','abc','xyz',12)
        //ret= MDbUtils.insertBy(DbPoolName, myStudent);

        //DbContext.setReflectClass()指定Student，表示生成SQL时使用student表
        DbContext.setReflectClass(Student.class);
        ret = MDbUtils.insertBy(DbPoolName, myStudent);

        Assert.equal(sql.toString(), "insert into [student] ([age],[birth_day],[name]) values(12,CONVERT(date,'2021-03-15',23),'xyz')");
        sql.setLength(0);
        //DbContext.setReflectClass()每次只能使用一次，若下次想使用需再次声明
        DbContext.setReflectClass(Student.class);

        MDbUtils.updateBy(DbPoolName, myStudent, MD.of(MyStudent::getName));

        Assert.equal(sql.toString(), "update [student]  set [age]=12,[birth_day]=CONVERT(date,'2021-03-15',23) where [name]='xyz'");
        sql.setLength(0);
        DbContext.setReflectClass(Student.class);
        MyStudent student = MDbUtils.queryOneBy(DbPoolName, myStudent);
        Assert.equal(sql.toString(), "select * from [student]  where [age]=12 and [birth_day]=CONVERT(date,'2021-03-15',23) and [name]='xyz'");
        sql.setLength(0);
        DbContext.setReflectClass(Student.class);
        MDbUtils.delBy(DbPoolName, myStudent,
                MD.of("name")  //必须使用Student类里的属性，不能使用MyStudent类里的属性，不然会报错
        );
        Assert.equal(sql.toString(), "delete from [student] where [name]='xyz'");
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }
}
