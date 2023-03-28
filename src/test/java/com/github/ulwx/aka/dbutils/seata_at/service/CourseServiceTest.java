package com.github.ulwx.aka.dbutils.seata_at.service;

import com.github.ulwx.aka.dbutils.database.*;
import com.github.ulwx.aka.dbutils.database.transaction.AkaPropagationType;
import com.github.ulwx.aka.dbutils.database.transaction.GlobalTransactionTemplate;
import com.github.ulwx.aka.dbutils.database.transaction.TransactionTemplate;
import com.github.ulwx.aka.dbutils.seata_at.Utils;
import com.github.ulwx.aka.dbutils.seata_at.dao.db_student.CourseDao;
import com.github.ulwx.aka.dbutils.seata_at.dao.db_teacher.TeacherDao;
import com.github.ulwx.aka.dbutils.seata_at.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.CTime;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 测试aka-dbutils通过MDataBase实例实现数据库事务及通过MDbTransactionManager实现事务
 */
public class CourseServiceTest {
    private CourseDao courseDao=new CourseDao();
    private TeacherDao teacherDao=new TeacherDao();
    public static String DbPoolName_Student = "seata_at/dbpool.xml#db_student";
    public static String DbPoolName_Teacher= "seata_at/dbpool.xml#db_teacher";
    public static String getDbPoolFileName="seata_at/dbpool.xml";
    @Before
    public void setup(){
        Utils.importDbStudent();
        Utils.inportDbTeacher();
    }
    @Test
    public void testGlobalTransaction(){
        Course course= GlobalTransactionTemplate.execute(getDbPoolFileName,()->{
            return testTransaction();
        },AkaPropagationType.REQUIRED);

        Course queryCourse=new Course();
        queryCourse.setId(course.getId());
        queryCourse= MDbUtils.queryOneBy(DbPoolName_Student,queryCourse);
        Assert.equal(queryCourse.getName(), "addxyz");

    }

    public Course testTransaction(){
        MDataBase mdb = null;
        Course course=null;
        try {
            mdb = MDbManager.getDataBase(DbPoolName_Student);
            mdb.setAutoCommit(false);//开启事务标志
            course=courseDao.testInsertInTrans(mdb);
            courseDao.testUpdateInTrans(mdb,course);
            mdb.commit();
        } catch (Exception e){
            mdb.rollback();
            throw new DbException(e);
        }finally {
            if (mdb != null) {
                mdb.close();
            }
        }
        return  course;

    }


    @Test
    public void testGlobalTransactionWithSavePoint1(){
        Course queryCourse= GlobalTransactionTemplate.execute(getDbPoolFileName,()->{
            return testTransactionWithSavePoint();
        },AkaPropagationType.REQUIRED);

        queryCourse= MDbUtils.queryOneBy(DbPoolName_Student,queryCourse);
        Assert.equal(queryCourse.getCreatime().format(CTime.DTF_YMD_HH_MM_SS),
                "2021-03-18 22:31:40");

    }
    /**
     * 测试事务的SavePoint功能
     */
    //@Test
    public Course testTransactionWithSavePoint(){
        MDataBase mdb = null;
        Course course=null;
        try {
            mdb = MDbManager.getDataBase(DbPoolName_Student);
            mdb.setAutoCommit(false);//开启事务标志
            course=courseDao.testInsertInTrans(mdb);
            mdb.setSavepoint("abc");
            LocalDateTime localDateTime = LocalDateTime.parse("2021-03-18 22:31:40", CTime.DTF_YMD_HH_MM_SS);
            course.setCreatime(localDateTime);
            courseDao.testUpdateInTrans(mdb,course);
            mdb.setSavepoint("a123");
            localDateTime = LocalDateTime.parse("2021-03-21 22:31:40", CTime.DTF_YMD_HH_MM_SS);
            course.setCreatime(localDateTime);
            courseDao.testUpdateInTrans(mdb,course);
            mdb.rollbackToSavepoint("a123");
            mdb.commit();
        } catch (Exception e){
            mdb.rollback();
            throw new DbException(e);
        }finally {
            if (mdb != null) {
                mdb.close();
            }
        }
        Course queryCourse=new Course();
        queryCourse.setId(course.getId());

        return queryCourse;

    }

    @Test
    public void testGlobalTransactionManager(){
       GlobalTransactionTemplate.execute(getDbPoolFileName,()->{
            testTransactionManager();
            return null;
        },AkaPropagationType.REQUIRED);

       int i=2;
    }
    //@Test
    public void testTransactionManager(){
        TResult<DataBase> tDataBase=new TResult<>();
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                tDataBase.setValue(dataBase);
            }
        });
        TransactionTemplate.execute(()->{
            courseDao.testInsertWithMd();
            courseDao.testUpdate();
        });
        DbContext.removeDBInterceptor();
        DataBase dataBase = tDataBase.getValue();
        Assert.state(dataBase.isColsed());
    }


    public void testTransactionManagerInner(){

        TransactionTemplate.execute(()->{
            courseDao.testUpdateInManager("update...1");
        });
    }

    @Test
    public void testGlobalTransactionManagerOuter(){
        GlobalTransactionTemplate.execute(getDbPoolFileName,()->{
            testTransactionManagerOuter();
            return null;
        },AkaPropagationType.REQUIRED);

        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"update...2");
    }


    public void testTransactionManagerOuter(){
        TResult<DataBase> tDataBase=new TResult<>();
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase,
                                              Method interceptedMethod,
                                              Object result,
                                              Exception exception,
                                              String debugSql,long exeTime) {
                tDataBase.setValue(dataBase);
            }
        });
        TransactionTemplate.execute(()->{ //默认事务传播级别为PROPAGATION.REQUIRED
            this.testTransactionManagerInner();
            courseDao.testUpdateInManager("update...2");
        });
        DbContext.removeDBInterceptor();
        DataBase dataBase = tDataBase.getValue();
        Assert.state(dataBase.isColsed());


    }


    public void executeThrowException(AkaPropagationType propagation){ //抛出异常
        TransactionTemplate.execute(propagation,()->{
            courseDao.testUpdateInManager("update.exception");
            int i=1/0;
        });
    }
    @Test
    public void testGlobalTransactionException(){
        try {
            GlobalTransactionTemplate.execute(getDbPoolFileName, () -> {

                courseDao.testUpdateInManager("update.exception");
                int i=1/0;
                return i;
            }, AkaPropagationType.REQUIRED);
        }catch (Throwable e){
            e.printStackTrace();
        }
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"course1");
    }
    @Test
    public void testGlobalTransactionManagerOuterHavingException(){
        try {
            GlobalTransactionTemplate.execute(getDbPoolFileName, () -> {

                testTransactionManagerOuterHavingException();
                return null;
            }, AkaPropagationType.REQUIRED);
        }catch (Throwable e){
            e.printStackTrace();
        }
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"course1");
    }

    public void testTransactionManagerOuterHavingException(){

        Exception saveException=null;
        try {
            TransactionTemplate.execute(() -> { //① 默认事务传播级别为PROPAGATION.REQUIRED
                Exception throwException=null;
                try {
                    this.executeThrowException(AkaPropagationType.REQUIRED);//抛出的异常虽然被捕获，但仍然会导致②的回滚
                } catch (Exception e) {
                    throwException=e;
                }

                Assert.state(throwException!=null);

                this.testTransactionManagerInner(); //②

            });
        }catch (Exception e){
            throw  e;
        }finally {
            DbContext.removeDBInterceptor();
        }


    }




}
