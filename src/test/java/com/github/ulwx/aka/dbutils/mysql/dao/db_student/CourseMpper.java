package com.github.ulwx.aka.dbutils.mysql.dao.db_student;

import com.github.ulwx.aka.dbutils.database.AkaMapper;
import com.github.ulwx.aka.dbutils.database.DataBaseSet;
import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.MDMethods.InsertOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions;
import com.github.ulwx.aka.dbutils.database.MDataBase;
import com.github.ulwx.aka.dbutils.mysql.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.tool.support.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public abstract class CourseMpper extends AkaMapper {

    public abstract  DataBaseSet getRSet(Map<String,Object> ars);
    public abstract DataBaseSet getRSetPage(Map<String,Object> ars, PageOptions pageOptions);

    /**
     * 方法声明的所有相同名称的参数（包含Map<String,Objec>和javaBean涵盖的参数），后面的会覆盖前面的
     * @param id  类型为Integer，为简单类型，简单类型可以直接用于md文件里的参数
     * @param name  类型为Integer，为简单类型，简单类型可以直接用于md文件里的参数
     * @param ars 类型为Map<String,Objec>类型，则可以作为md文件里参数容器，可以设置多个参数
     * @param cs  类型为自定义javaBean类型，则可以作为md文件里参数的容器，javaBean所有属性（含null值的属性）都会作为参数
     * @return
     */
    public abstract Course getOneCourse(Integer id, String name, Map<String,Object> ars, Course cs);
    public abstract  List<Course> getCoursesByIds(Integer[] ids, String name, Map<String,Object> ars,Course cs);
    public abstract List<Course> getCouseList(String name);
    public abstract List<Course> getCouseListPage(String name,PageOptions pageOptions);
    public abstract void addCourse(Course course);
    public abstract int addCourseReturnKey(Course course, InsertOptions insertOptions);
    public abstract int updateCourse(Course course);
    public abstract  void dropCourse();

    public void updateMyCourse(){
        Course course=new Course();
        course.setName("course1");
        course.setClassHours(11);
        MDataBase mDataBase=this.getMdDataBase();
        //默认每次执行完数据库操作后关闭数据库连接
        List<Course> list = mDataBase.queryListBy(course);
        course.setId(1);
        //默认每次执行完数据库操作后关闭数据库连接
        this.updateCourse(course);
    }
    public void updateMyCourseIntrans(){
        Course course=new Course();
        course.setName("course1");
        course.setClassHours(11);
        MDataBase mDataBase=this.getMdDataBase();
        try {
            mDataBase.setAutoCommit(false);//设置启动事务标志
            List<Course> list = mDataBase.queryListBy(course);
            course.setId(1);
            this.updateCourse(course);
            mDataBase.commit();
        }catch (Exception e){
            mDataBase.rollback();;
        }finally{
            mDataBase.close();
        }
    }

}
