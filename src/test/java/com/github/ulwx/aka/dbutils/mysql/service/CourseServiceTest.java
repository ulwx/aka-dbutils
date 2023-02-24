package com.github.ulwx.aka.dbutils.mysql.service;

import com.github.ulwx.aka.dbutils.database.*;
import com.github.ulwx.aka.dbutils.database.transaction.AkaPropagationType;
import com.github.ulwx.aka.dbutils.database.transaction.MDbTransactionManager;

import com.github.ulwx.aka.dbutils.database.transaction.TransactionTemplate;
import com.github.ulwx.aka.dbutils.mysql.Utils;
import com.github.ulwx.aka.dbutils.mysql.dao.db_student.CourseDao;
import com.github.ulwx.aka.dbutils.mysql.dao.db_teacher.TeacherDao;
import com.github.ulwx.aka.dbutils.mysql.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.mysql.domain.db.db_teacher.Teacher;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.CTime;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试aka-dbutils通过MDataBase实例实现数据库事务及通过MDbTransactionManager实现事务
 */
public class CourseServiceTest {
    private CourseDao courseDao=new CourseDao();
    private TeacherDao teacherDao=new TeacherDao();
    public static String DbPoolName_Student = "mysql/dbpool.xml#db_student";
    public static String DbPoolName_Teacher= "mysql/dbpool.xml#db_teacher";
    @Before
    public void setup(){
        Utils.importDbStudent();
        Utils.inportDbTeacher();
    }
    /**
     * 测试通过MDataBase实现事务
     */
    @Test
    public void testTransaction(){
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
        Course queryCourse=new Course();
        queryCourse.setId(course.getId());
        queryCourse= MDbUtils.queryOneBy(DbPoolName_Student,queryCourse);
        Assert.equal(queryCourse.getName(), "addxyz");

    }

    /**
     * 测试事务的SavePoint功能
     */
    @Test
    public void testTransactionWithSavePoint1(){
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
        queryCourse= MDbUtils.queryOneBy(DbPoolName_Student,queryCourse);
        Assert.equal(queryCourse.getCreatime().format(CTime.DTF_YMD_HH_MM_SS),
                "2021-03-18 22:31:40");


    }

    /**
     * 测试事务的SavePoint功能
     */
    @Test
    public void testTransactionWithSavePoint2(){
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
            mdb.rollbackToSavepoint("abc");
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
        queryCourse= MDbUtils.queryOneBy(DbPoolName_Student,queryCourse);
        Assert.equal(queryCourse.getCreatime().format(CTime.DTF_YMD_HH_MM_SS),
                "2021-03-15 22:31:40");
    }
    @Test
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
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"update...2");
    }


    public void executeThrowException(AkaPropagationType propagation){ //抛出异常
        TransactionTemplate.execute(propagation,()->{
            courseDao.testUpdateInManager("update.exception");
            int i=1/0;
        });
    }

    @Test
    public void testTransactionManagerOuterHavingException(){
        TResult<DataBase> tDataBase=new TResult<>();
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                tDataBase.setValue(dataBase);
            }
        });
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
            saveException=e;
        }
        Assert.state(saveException!=null);
        DbContext.removeDBInterceptor();
        DataBase dataBase = tDataBase.getValue();
        Assert.state(dataBase.isColsed());
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"course1");
    }
    @Test
    public void testTransactionManagerNestedHavingException(){
        TResult<DataBase> tDataBase=new TResult<>();
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                         Object result, Exception exception, String debugSql,long exeTime) {
                tDataBase.setValue(dataBase);
            }
        });
        Exception saveException=null;
        try {
            TransactionTemplate.execute(() -> { //① 默认事务传播级别为PROPAGATION.REQUIRED
                Exception throwException=null;
                try {
                    this.executeThrowException(AkaPropagationType.NESTED);//抛出的异常虽然被捕获，但仍然会导致②的回滚
                } catch (Exception e) {
                    throwException=e;
                }
                Assert.state(throwException!=null);

                this.testTransactionManagerInner(); //②

            });
        }catch (Exception e){
            saveException=e;
        }
        Assert.state(saveException==null);
        DbContext.removeDBInterceptor();
        DataBase dataBase = tDataBase.getValue();
        Assert.state(dataBase.isColsed());
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"update...1");
    }

    @Test
    public void testTransactionManagerReqNewHavingException(){
        TResult<DataBase> tDataBase=new TResult<>();
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                tDataBase.setValue(dataBase);
            }
        });
        Exception saveException=null;
        try {
            TransactionTemplate.execute(() -> { //① 默认事务传播级别为PROPAGATION.REQUIRED
                Exception throwException=null;
                try {
                    this.executeThrowException(AkaPropagationType.REQUIRES_NEW);//抛出的异常虽然被捕获，但仍然会导致②的回滚
                } catch (Exception e) {
                    throwException=e;
                }
                Assert.state(throwException!=null);

                this.testTransactionManagerInner(); //②

            });
        }catch (Exception e){
            saveException=e;
        }
        Assert.state(saveException==null);
        DbContext.removeDBInterceptor();
        DataBase dataBase = tDataBase.getValue();
        Assert.state(dataBase.isColsed());
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"update...1");
    }
    @Test
    public void testTransactionManagerForDiffDB(){

        TResult<Map<String,DataBase>> tDataBases=new TResult<>(new HashMap<>());

        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                tDataBases.getValue().put(dataBase.getDbPoolName(),dataBase);
            }
        });
        TransactionTemplate.execute(()->{
            courseDao.testUpdateInManager("abcd1");
            teacherDao.testUpdateInManager("abcd2");
        });
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"abcd1");

        Teacher teacher=new Teacher();
        teacher.setId(1);
        teacher=MDbUtils.queryOneBy(DbPoolName_Teacher, teacher);
        Assert.equal(teacher.getName(),"abcd2");
        Assert.state(tDataBases.getValue().get(DbPoolName_Student).isColsed());
        Assert.state(tDataBases.getValue().get(DbPoolName_Teacher).isColsed());
    }
    public void testTransactionManagerForDiffDBInnner(){

        TransactionTemplate.execute(()->{
            courseDao.testUpdateInManager("abcd1");
            teacherDao.testUpdateInManager("abcd2");
        });
    }
    @Test
    public void testTransactionManagerForDiffDBOuter(){
        TResult<Map<String,DataBase>> tDataBases=new TResult<>(new HashMap<>());
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                tDataBases.getValue().put(dataBase.getDbPoolName(),dataBase);
            }
        });
        TransactionTemplate.execute(()->{
            testTransactionManagerForDiffDBInnner();
            courseDao.testUpdateInManager("abcdx");
            teacherDao.testUpdateInManager("abcdy");
        });
        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"abcdx");

        Teacher teacher=new Teacher();
        teacher.setId(1);
        teacher=MDbUtils.queryOneBy(DbPoolName_Teacher, teacher);
        Assert.equal(teacher.getName(),"abcdy");
        Assert.state(tDataBases.getValue().get(DbPoolName_Student).isColsed());
        Assert.state(tDataBases.getValue().get(DbPoolName_Teacher).isColsed());
    }
    @Test
    public void testRollBackTransactionManagerForDiffDB(){

        TResult<Map<String,DataBase>> tDataBases=new TResult<>(new HashMap<>());
        DbContext.setDBInterceptor(new DBInterceptor() {
            @Override
            public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                              Object result, Exception exception,
                                              String debugSql,long exeTime) {
                tDataBases.getValue().put(dataBase.getDbPoolName(),dataBase);
            }
        });

        TransactionTemplate.execute(()->{ //②
            try {
                testTransactionManagerException(); // 嵌套事务
            }catch (Exception e){ //嵌套事务抛出的异常被捕获不会引起外层事务的回滚，即不会引起②处整个方法事务的回滚，即③，④处不会被回滚
            }
            courseDao.testUpdateInManager("abcdx"); //③
            teacherDao.testUpdateInManager("abcdy"); //④

        });

        Course course=new Course();
        course.setId(1);
        course=MDbUtils.queryOneBy(DbPoolName_Student, course);
        Assert.equal(course.getName(),"abcdx");

        Teacher teacher=new Teacher();
        teacher.setId(1);
        teacher=MDbUtils.queryOneBy(DbPoolName_Teacher, teacher);
        Assert.equal(teacher.getName(),"abcdy");
        Assert.state(tDataBases.getValue().get(DbPoolName_Student).isColsed());
        Assert.state(tDataBases.getValue().get(DbPoolName_Teacher).isColsed());

    }
    public void testTransactionManagerException(){
        TransactionTemplate.execute(AkaPropagationType.NESTED,()->{ //①
            courseDao.testUpdateInManager("87654321");
            testTransactionManagerExceptionMore();//会导致嵌套事务回滚，即①的执行全部回滚
        });
    }
    public void testTransactionManagerExceptionMore(){
        TransactionTemplate.execute(AkaPropagationType.REQUIRED,()->{
            teacherDao.testUpdateInManager("12345678");
             int f= 1/0;
        });
    }

}
