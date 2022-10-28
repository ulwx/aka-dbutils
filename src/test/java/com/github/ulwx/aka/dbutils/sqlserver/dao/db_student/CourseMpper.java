package com.github.ulwx.aka.dbutils.sqlserver.dao.db_student;

import com.github.ulwx.aka.dbutils.database.AkaMapper;
import com.github.ulwx.aka.dbutils.database.DataBaseSet;
import com.github.ulwx.aka.dbutils.database.MDMethods.InsertOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions;
import com.github.ulwx.aka.dbutils.database.MDataBase;
import com.github.ulwx.aka.dbutils.database.annotation.AkaParam;
import com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_student.Course;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public abstract class CourseMpper extends AkaMapper {

    public abstract DataBaseSet getRSet(Map<String, Object> ars);

    public abstract DataBaseSet getRSetPage(Map<String, Object> ars, PageOptions pageOptions);

    public abstract String getOneString(String name);

    public abstract Integer getOneInteger(String name);

    public abstract BigInteger getOneBigInteger(String name);

    public abstract List<BigInteger> getOneBigIntegerList(String name);

    public abstract LocalDateTime getOneLocalDateTime();

    public abstract Timestamp getOneTimestamp();

    public abstract List<Timestamp> getOneTimestampList();

    public abstract Integer getOneIntegerReturnNull(String name);

    public abstract Course getOneCourse(Integer id, @AkaParam("name") String name, Map<String, Object> ars, Course cs);
    public abstract List<Course> getCoursesByIds(Integer[] ids, String name, Map<String, Object> ars, Course cs);

    public abstract List<Course> getCouseList(String name);

    public abstract List<Course> getCouseListPage(String name, PageOptions pageOptions);

    public abstract void addCourse(Course course);

    public abstract int addCourseReturnKey(Course course, InsertOptions insertOptions);

    public abstract int updateCourse(Course course);

    public abstract void dropCourse();

    public void updateMyCourse() {
        Course course = new Course();
        course.setName("course1");
        course.setClassHours(11);
        MDataBase mDataBase = this.getMdDataBase();
        //默认每次执行完数据库操作后关闭数据库连接
        List<Course> list = mDataBase.queryListBy(course);
        course.setId(1);
        //默认每次执行完数据库操作后关闭数据库连接
        this.updateCourse(course);
    }

    public void updateMyCourseIntrans() {
        Course course = new Course();
        course.setName("course1");
        course.setClassHours(11);
        MDataBase mDataBase = this.getMdDataBase();
        try {
            mDataBase.setAutoCommit(false);//设置启动事务标志
            List<Course> list = mDataBase.queryListBy(course);
            course.setId(1);
            this.updateCourse(course);
            mDataBase.commit();
        } catch (Exception e) {
            mDataBase.rollback();
            ;
        } finally {
            mDataBase.close();
        }
    }

}
