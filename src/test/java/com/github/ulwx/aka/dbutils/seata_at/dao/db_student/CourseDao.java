package com.github.ulwx.aka.dbutils.seata_at.dao.db_student;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.MDataBase;
import com.github.ulwx.aka.dbutils.seata_at.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.CTime;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试aka-dbutils的基本用法
 */
public class CourseDao {

    public static String DbPoolXML = "seata_at/dbpool.xml";
    public static String DbPoolName = DbPoolXML + "#db_student";
    public static String DbPoolName_product = DbPoolXML + "#db_student_product";


    public void testUpdate() {
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);
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
                "update `course`  set `creatime`='2021-03-15 22:31:40' where `name`='add' and `id`=null");
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of(Course::getName, Course::getId));
        Assert.equal(sql.toString(),
                "update `course`  set `creatime`='2021-03-15 22:31:40' where `name`='add' and `id`=null");
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of("name", "id"), true);
        Assert.equal(sql.toString(),
                "update `course`  set `class_hours`=null,`creatime`='2021-03-15 22:31:40',`teacher_id`=null where `name`='add' and `id`=null");
        ret = MDbUtils.updateBy(DbPoolName, course, MD.of("name"), MD.of(Course::getCreatime));
        Assert.equal(sql.toString(),
                "update `course`  set `creatime`='2021-03-15 22:31:40' where `name`='add'");

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
                "update `course`  set `creatime`='2021-03-15 22:31:40' where `name`='add';update `course`  set `creatime`='2021-03-15 22:31:40' where `name`='add1'");

    }


    public void testInsertWithMd() {

        StringBuffer sql = new StringBuffer();
        DbContext.setDebugSQLListener(sqltxt -> {
            sql.setLength(0);
            sql.append(sqltxt);
        });
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);

        Map<String, Object> args = new HashMap<>();
        args.put("name", "course_md");
        args.put("classHours", 123);
        args.put("creatime", localDateTime);
        MDbUtils.insert(DbPoolName, MD.md(), args);
        Assert.equal(sql.toString(),
                "INSERT INTO `course` ( `name`, `class_hours`, `creatime` ) VALUES ( 'course_md', 123, '2021-03-15 22:31:40' )");

        Course course1 = new Course();
        course1.setName("course_md01");
        course1.setClassHours(231);
        course1.setCreatime(localDateTime);
        MDbUtils.insert(DbPoolName, MD.md(), MD.map(course1));
        Assert.equal(sql.toString(),
                "INSERT INTO `course` ( `name`, `class_hours`, `creatime` ) VALUES ( 'course_md01', 231, '2021-03-15 22:31:40' )");

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
        Assert.equal(sql2.toString(), "INSERT INTO `course` ( `name`, `class_hours`, `creatime` ) VALUES ( 'course_md01', 231, '2021-03-15 22:31:40' );INSERT INTO `course` ( `name`, `class_hours`, `creatime` ) VALUES ( 'course_md02', 232, '2021-03-15 22:31:40' )");
        DbContext.removeDebugSQLListener();

    }


    public Course testInsertInTrans(MDataBase mdb) {
        LocalDateTime localDateTime = LocalDateTime.parse("2021-03-15 22:31:40", CTime.DTF_YMD_HH_MM_SS);
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



    public void testUpdateInManager(String name) {
        Course course = new Course();
        course.setId(1);
        course.setName(name);
        int ret = MDbUtils.updateBy(DbPoolName, course, MD.of("id"));

    }
}
