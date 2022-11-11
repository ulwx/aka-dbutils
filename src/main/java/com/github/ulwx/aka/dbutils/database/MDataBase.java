package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.tool.PageBean;

import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MDataBase extends DBObjectOperation, AutoCloseable {
    DataBase getDataBase();

    /**
     * 执行某个文件夹下的某个脚本文件。
     * 整个脚本的执行在一个事务里，如果执行过程中出错根据continueIfError参数决定是否抛出异常并回滚。可以指定脚本在执行过程中如果出现警告是否抛出异常并回滚，
     * @param dirPath      :sql  脚本所在的目录，例如 D:/mysql/a.sql ; /user/mysql/a.sql
     * @param sqlFileName  ：sql脚本的文件名，例如 db.sql
     * @param throwWarning 脚本执行时如果出现warning，是否退出并回滚
     * @param continueIfError  执行过程中出现错误是否继续执行。true:如果出现错误会继续后面语句的执行； false:如果出现错误，则抛出异常并全部回滚。
     * @param delimiters   脚本执行时判断脚本里某条执行语句结束的标志，例如 ";" 。注意：执行语句结尾处的delimiters之后后面必须为换行符
     * @param encoding     脚本文件的encoding
     * @return 执行成功的结果 ，否则抛出异常
     * @throws DbException
     */
    public String exeScriptInDir(String dirPath, String sqlFileName,
                                 boolean throwWarning, boolean continueIfError,String delimiters, String encoding) throws DbException;

    /**
     * 执行sql脚本，packageFullName指定SQL脚本所在的包（全路径），如com.xx.yy，sqlFileName为脚本文件的名称，脚本文件里存放的是SQL脚本，
     * 整个脚本的执行在一个事务里，如果执行过程中出错根据continueIfError参数决定是否抛出异常并回滚。可以指定脚本在执行过程中如果出现警告是否抛出异常并回滚，
     * 脚本是按每个语句依次执行，脚本里每个语句的分界是根据英文分号和换行共同判定，即 ";\n" 或 ";\r\n"。
     *
     * @param packageFullName :sql脚本所在的包，例如com.xx.yy
     * @param sqlFileName     为脚本文件的名称，脚本文件里存放的是SQL脚本
     * @param throwWarning    脚本执行时如果出现warning时是否抛出异常并回滚
     * @param continueIfError  执行过程中出现错误是否继续执行。true:如果出现错误会继续后面语句的执行； false:如果出现错误，则抛出异常并全部回滚。
     * @param delimiters      脚本执行时判断脚本里某条执行语句结束的标志，例如 ";" 。注意：执行语句结尾处的delimiters之后后面必须为换行符(\n或\r\n)
     * @param encoding        脚本文件的encoding
     * @return 返回执行脚本的结果
     * @throws DbException
     */
    String exeScript(String packageFullName, String sqlFileName,
                     boolean throwWarning,boolean continueIfError, String delimiters, String encoding) throws DbException;

    /**
     * 执行md方法地址指定的脚本，并且可以传入参数,脚本里执行时按每个SQL语句执行，执行的时候利用的是jdbc的PrepareStatement，能有效防止注入式攻击
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param throwWarning     脚本执行时如果出现warning时是否抛出异常并回滚
     * @param delimiters       脚本执行时判断脚本里某条执行语句结束的标志，例如 ";" 。注意：执行语句结尾处的delimiters之后后面必须为换行符
     * @param args             传入md方法的参数
     * @param delimiters       指定每个SQL语句的分界，例如";"
     * @return 返回脚本执行的结果
     * @throws DbException
     */
    String exeScript(String mdFullMethodName, boolean throwWarning, String delimiters, Map<String, Object> args) throws DbException;

    /**
     * 根据mdFullMethodName指定的md方法地址所在的SQL从数据库查询记录，aka-dbutils会在内部封装SQL从而形成分页查询的SQL，
     * 最终返回当前页的离线结果集。
     *
     * @param mdFullMethodName         分如下两种情况：<ul>
     *                                 <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                                 <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                                 </ul>
     * @param args                     md方法里用到的参数
     * @param page                     当前页码（从1开始）
     * @param perPage                  每页多少行
     * @param pageBean                 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSqlMdFullMethodName 可以指定四种类型的参数，<br>
     *                                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                                 md方法地址：表示计算总数的SQL的md方法地址<br>
     *                                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @return 返回离线结果集
     * @throws DbException
     */
    DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                  PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    /**
     * 根据mdFullMethodName指定的md方法地址所在的SQL从数据库查询记录，返回离线结果集
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             md方法里用到的参数
     * @return 返回离线结果集
     * @throws DbException
     */
    DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 根据md方法指定的SQL语句来查询记录，并通过rowMapper映射到指定类型的对象，本方法为分页查询，参数page为页码（从1开始），
     * 参数perPage为每页多少行记录，aka-dbutils会根据这些信息生成分页的select语句，例如，如果当前数据库类型为mysql，请求第2页（page=2），
     * 每页10条（perPage=10），如果mdFullMethodName地址指定的SQL为<blockquote><pre>
     * select * from course  where name='course_page' ，</pre></blockquote>
     * 则生成的分页查询语句如下：
     * <blockquote><pre>
     *     select * from course  where name='course_page' limit 20,10
     *     (由于 mysql的limit后面的索引是从0开始，所以这里的20表示从第21条开始往后的10条记录，即第二页数据)
     * </pre></blockquote>
     * 如果countSqlMdFullMethodName=null，那么aka-dbutils在生成上面的分页select语句之前，会首先自动生成查询总数的select语句，
     * 如果当前数据库类型为mysql，生成的语句如下：
     * <blockquote><pre>
     *      select count(1) from (select * from course  where name='course_page') t
     *  </pre></blockquote>
     *
     * @param mdFullMethodName         分如下两种情况：<ul>
     *                                 <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                                 <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                                 </ul>
     * @param args                     md方法所用到的参数
     * @param page                     当前请求页码
     * @param perPage                  每页多少行
     * @param pageBean                 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param rowMapper                自定义映射接口，可通过此接口，开发者可以自定义结果集到对象的映射
     * @param countSqlMdFullMethodName 可以指定四种类型的参数，<br>
     *                                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                                 md方法地址：表示计算总数的SQL的md方法地址<br>
     *                                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @param <T>
     * @return 返回一个List对象，包含通过rowMapper映射的对象。
     * @throws DbException
     */
    <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean,
                          RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException;

    /**
     * 根据md方法指定的SQL查询记录，每行记录映射到一个Map对象，并返回包含Map对象的List。本方法为分页查询，参数page为页码（从1开始），
     * 参数perPage为每页多少行记录，aka-dbutils会根据这些信息生成分页的select语句，例如，如果当前数据库类型为mysql，请求第2页（page=2），
     * 每页10条（perPage=10），如果mdFullMethodName地址指定的SQL为
     * <blockquote><pre>
     * select * from course  where name='course_page' ，</pre></blockquote>
     * 则生成的分页查询语句如下：
     * <blockquote><pre>
     *     select * from course  where name='course_page' limit 20,10
     *     (由于 mysql的limit后面的索引是从0开始，所以这里的20表示从第21条开始往后的10条记录，即第二页数据)
     * </pre></blockquote>
     * 如果countSqlMdFullMethodName=null，那么aka-dbutils在生成上面的分页select语句之前，会首先自动生成查询总数的select语句，
     * 如果当前数据库类型为mysql，生成的语句如下：
     * <blockquote><pre>
     *      select count(1) from (select * from course  where name='course_page') t</pre></blockquote>
     *
     * @param mdFullMethodName         分如下两种情况：<ul>
     *                                 <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                                 <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                                 </ul>
     * @param args                     md方法所用到的参数
     * @param page                     当前请求页码
     * @param perPage                  每页多少行
     * @param pageBean                 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSqlMdFullMethodName 可以指定四种类型的参数，<br>
     *                                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                                 md方法地址：表示计算总数的SQL的md方法地址<br>
     *                                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @return 返回一个含有map的List，每个map对应一行记录，key为表字段名称，value为字段的值
     * @throws DbException
     */
    List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                       PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    /**
     * 根据md方法指定的SQL语句查询记录，每行记录映射到指定类型的对象。
     *
     * @param clazz            记录映射到对象的类型
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             md方法用到的参数
     * @param <T>
     * @return 返回一个List对象，包含行记录映射的对象。
     * @throws DbException
     */
    <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 根据mdFullMethodName指定的SQL查询记录，每条记录映射到一个对象，并返回一个对象。注意，如果指定的SQL语句查询多条记录，aka-dbutils会
     * 内部依然会映射到一个列表，并从列表里取出第一个对象，所以，如果调用此方法，建议指定的SQL里要含有限制取第一个对象，如对mysql，SQL后面加limit 1。
     *
     * @param clazz            映射的对象的类型
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             md方法所用到的参数
     * @param <T>
     * @return 返回一个对象
     * @throws DbException
     */
    <T> T queryOne(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 一对一关联分页查询。是针对一个对象"一对一关联"另一对象，通过在对象的类里定义一个关联属性，包含关联属性的类为主类，
     * 关联属性的类型为子关联类。主类和子关联类分别都对应到数据库表，例如student表，其每个学生只学习一门课程（对应course表一条记录），
     * 那么student表一行学生信息记录就一对一关联course表的一门课程记录，从类的角度来说就是主类Student和子关联类Course具有一对一关联，
     * 并且在Student类里定义了一个名为"course"（名称随意）的关联属性，其类型为Course，它是子关联类型。数据库表可以设计成如下：<blockquote><pre>
     * student表：
     * id,student_name
     *
     * course表：
     * id,course_name
     *
     * student_course表（里面的记录是一条student记录id对应一条course记录id，即一对一关联）：
     * id, student_id,course_id
     *
     * javaBean为：
     * public class Student{
     *     private Integer id;
     *     private String studentName;
     *     private Course course;   ①-1
     *     ......
     * }
     *
     * public class StudentCourse{
     *     private Integer id,
     *     private Integer studentId;
     *     private Integer courseId;
     *     ......
     * }
     *
     * public class Course{
     *     private Integer id,
     *     private String courseName;
     *     ......
     * }
     *
     * md文件里对应的SQL语句如下：
     * testQueryListOne2One
     * ====
     *  select
     *   stu.*,    ③-1
     *   c.*       ②-1
     *  from student stu,student_course sc,course c
     *  where stu.id=sc.student_id and  c.id=sc.course_id
     *  and c.student_name like #{name%} order by c.id
     *
     * //CourseDao .java
     *  public class CourseDao {
     *     ......
     *     public  void testQueryListOne2One(){
     *         Map&lt;String,Object&gt;args = new HashMap&lt;&gt;();
     *         args.put("name","student");
     *         QueryMapNestOne2One queryMapNestOne2One = new QueryMapNestOne2One();
     *         queryMapNestOne2One.set(null,  //子关联类哪些属性被映射，null表明全部映射
     *                              "course",  ①  //指定主类里的关联属性
     *                                "c.");   ② //限定哪些列映射到子关联类里
     *
     *         One2OneMapNestOptions one2OneMapNestOptions=MD.ofOne2One(
     *                 "stu."  ③
     *                 ,queryMapNestOne2One
     *         );
     *         List&lt;One2OneStudent&gt; list=MDbUtils.queryListOne2One(DbPoolName, One2OneStudent.class,
     *                  MD.md(), args, one2OneMapNestOptions);
     *         System.out.println("list="+ ObjectUtils.toPrettyJsonString(list));
     *
     *     }
     *
     *    public static void main(String[] args) throws Exception{
     *         CourseDao dao=new CourseDao();
     *         dao.testQueryListOne2One();
     *     }
     * }
     * </pre></blockquote>
     * ③处的"stu."指定了一个前缀，它限定了SQL语句里哪些列字段（③-1处）要映射到主类（包含关联属性的类）里对应的属性。
     * queryMapNestOne2One对象被设置了"course"（①处），它指定了主类One2OneStudent类里的关联属性（即对应于①-1处的course属性），
     * 在②处设置了"c."，这限定了SQL语句里哪些列字段（②-1处）要映射到子关联子里的对应的属性（即Course类里的属性）。
     *
     * @param clazz                 映射到的对象所属类型
     * @param mdFullMethodName      分如下两种情况：<ul>
     *                              <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                              <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                              </ul>
     * @param args                  md方法里用到的参数
     * @param one2OneMapNestOptions 关联子对象的映射配置对象。
     * @param <T>
     * @return
     * @throws DbException
     */
    <T> List<T> queryListOne2One(Class<T> clazz, String mdFullMethodName, Map<String, Object> args,
                                 One2OneMapNestOptions one2OneMapNestOptions) throws DbException;

    /**
     * 根据md方法指定的SQL语句查询记录，每行记录映射到指定类型的对象。本方法为分页查询，参数page为页码（从1开始），
     * 参数perPage为每页多少行记录，aka-dbutils会根据这些信息生成分页的select语句，例如，如果当前数据库类型为mysql，
     * 请求第2页（page=2），每页10条（perPage=10），如果mdFullMethodName地址指定的SQL为<blockquote><pre>
     * select * from course  where name='course_page' ，</pre></blockquote>
     * 则生成的分页查询语句如下：
     * <blockquote><pre>
     *     select * from course  where name='course_page' limit 20,10
     *     (由于 mysql的limit后面的索引是从0开始，所以这里的20表示从第21条开始往后的10条记录，即第二页数据)
     * </pre></blockquote>
     * 如果countSqlMdFullMethodName=null，那么aka-dbutils在生成上面的分页select语句之前，会首先自动生成查询总数的select语句，
     * 如果当前数据库类型为mysql，生成的语句如下：
     * <blockquote><pre>
     *      select count(1) from (select * from course  where name='course_page') t
     *  </pre></blockquote>
     *
     * @param clazz                    记录映射到对象的类型
     * @param mdFullMethodName         分如下两种情况：<ul>
     *                                 <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                                 <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                                 </ul>
     * @param args                     md方法用到的参数
     * @param page                     当前请求页码
     * @param perPage                  每页多少行
     * @param pageBean                 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSqlMdFullMethodName 可以指定四种类型的参数，<br>
     *                                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                                 md方法地址：表示计算总数的SQL的md方法地址<br>
     *                                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @param <T>
     * @return 返回一个List对象，包含行记录映射的对象。
     * @throws DbException
     */
    <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page,
                          int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException;

    /**
     * 一对一关联分页查询。是针对一个对象"一对一关联"另一对象，通过在对象的类里定义一个关联属性，包含关联属性的类为主类，
     * 主类里关联属性类型为子类型。主类和子类型分别都对应到数据库表，例如student表，其每个学生只学习一门课程（对应course表一条记录），
     * 那么student表一行学生信息记录就一对一关联course表的一门课程记录，从类的角度来说就是主类Student和子类型Course具有一对一关联，
     * 并且在Student类里定义了一个名为"course"的关联属性，其类型为Course，它是子类型。
     * student表：
     * id,student_name
     *
     * course表：
     * id,course_name
     *
     * student_course表（里面的记录是一条student记录id对应一条course记录id，即一对一关联）：
     * id, student_id,course_id
     *
     * javaBean为：
     * public class Student{
     *     private Integer id;
     *     private String studentName;
     *     private Course course;   ①-1
     *     ......
     * }
     *
     * public class StudentCourse{
     *     private Integer id,
     *     private Integer studentId;
     *     private Integer courseId;
     *     ......
     * }
     *
     * public class Course{
     *     private Integer id,
     *     private String courseName;
     *     ......
     * }
     *
     * md文件里对应的SQL语句如下：
     * testQueryListOne2OnePage
     * ====
     *  select
     *   stu.id as `stu.id`, ③-1
     *   stu.name as `stu.name`,
     *   stu.age as `stu.age`,
     *   stu.birth_day as `stu.birth_day`,
     *   c.id as `c.id`,  ②-1
     *   c.name as `c.name`,
     *   c.class_hours as `c.class_hours`,
     *   c.teacher_id as `c.teacher_id`,
     *   c.creatime as `c.creatime`
     *  from student stu,student_course sc,course c
     *  where stu.id=sc.student_id and  c.id=sc.course_id
     *  and c.student_name like #{name%} order by c.id
     *
     * //CourseDao .java
     *  public class CourseDao {
     *     ......
     *     public  void testQueryListOne2OnePage(){
     *         Map&lt;String,Object&gt;args = new HashMap&lt;&gt;();
     *         args.put("name","student");
     *         QueryMapNestOne2One queryMapNestOne2One = new QueryMapNestOne2One();
     *         queryMapNestOne2One.set(null,  //子类型哪些属性被映射，null表明全部映射
     *                              "course",  ①  //指定主类型的关联属性
     *                                "c.");   ② //限定哪些列映射到子类型里
     *
     *         One2OneMapNestOptions one2OneMapNestOptions=MD.ofOne2One(
     *                 "stu."  ③
     *                 ,queryMapNestOne2One
     *         );
     *          PageBean pageBean = new PageBean();
     *          List<One2OneStudent> list = MDbUtils.queryListOne2One(DbPoolName, One2OneStudent.class,
     *          MD.md(), args,one2OneMapNestOptions, 2,3, pageBean, null);
     *     }
     *
     *    public static void main(String[] args) throws Exception{
     *         CourseDao dao=new CourseDao();
     *         dao.testQueryListOne2OnePage();
     *     }
     * }
     * </pre></blockquote><ul>
     * <li>①处设置了"course"（①处），它指定了主类One2OneStudent类里的关联属性（即对应于①-1处的course属性），</li>
     * <li>②处设置了"c."，这限定了SQL语句里哪些列字段（②-1处）要映射到子关联子里的对应的属性（即Course类里的属性）。</li>
     * <li>③处的"stu."指定了一个前缀，它限定了SQL语句里哪些列字段（如③-1处）要映射到主类里对应的属性中。</li>
     *</ul>
     *
     * @param clazz                    映射到的对象所属类型
     * @param mdFullMethodName         分如下两种情况：<ul>
     *                                 <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                                 <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                                 </ul>
     * @param args                     md方法里用到的参数
     * @param one2OneMapNestOptions    关联子对象的映射配置对象。
     * @param page                     当前请求页码
     * @param perPage                  每页多少行
     * @param pageBean                 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSqlMdFullMethodName 可以指定四种类型的参数，<br>
     *                                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                                 md方法地址：表示计算总数的SQL的md方法地址<br>
     *                                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @param <T>
     * @return
     * @throws DbException
     */
    <T> List<T> queryListOne2One(Class<T> clazz, String mdFullMethodName,
                                 Map<String, Object> args, One2OneMapNestOptions one2OneMapNestOptions,
                                 int page, int perPage, PageBean pageBean,
                                 String countSqlMdFullMethodName) throws DbException;

    /**
     * 一对多关联查询。是针对一个对象"一对多关联"另一对象，通过在对象的类（主类）里定义一个关联属性，关联属性的类型为子关联类。
     * 主类和子关联类分别都对应到数据库表，例如student表，其每个学生只学习多门课程（对应course表里多条记录），
     * 那么student表一行学生信息记录就一对多关联course表的多门课程记录，从类的角度来说就是主类Student和子关联类Course具有一对多关联，
     * 并且在Student类里定义了一个名为"courseList"的关联属性，其类型为List&lt;Course&gt;，Course为子关联类型。
     * 数据库表可以设计成如下：<blockquote><pre>
     * student表：
     * id,student_name
     *
     * course表：
     * id,course_name
     *
     * student_course表（里面的记录是一条student记录id对应多条条course记录id，即一对多关联）：
     * id, student_id,course_id
     *
     * javaBean为：
     * public class Student{
     *     private Integer id;
     *     private String studentName;
     *     private List&lt;Course&gt; courseList;   ①-1
     *     ......
     * }
     *
     * public class StudentCourse{
     *     private Integer id,
     *     private Integer studentId;
     *     private Integer courseId;
     *     ......
     * }
     *
     * public class Course{
     *     private Integer id,
     *     private String courseName;
     *     ......
     * }
     *
     * md文件里对应的SQL语句如下：
     * testQueryListOne2Many
     * ====
     *  select
     *   stu.*,    ④-1
     *   c.*       ③-1
     *  from student stu,student_course sc,course c
     *  where stu.id=sc.student_id and  c.id=sc.course_id
     *  &#x40;if( $$:name ){
     *      and c.name in #{name}
     *  &#x40;}
     *  order by c.id
     *
     *  public class CourseDao {
     *     ......
     *    public  void testQueryListOne2Many(){
     *         Map&lt;String,Object&gt;args = new HashMap&lt;&gt;();
     *         args.put("name",new String[]{"student1","student2","student3"});
     *         QueryMapNestOne2Many queryMapNestOne2Many = new QueryMapNestOne2Many();
     *         queryMapNestOne2Many.set(Course.class, //子关联类
     *                 "courseList",  ①    //对应主类里的关联属性名称
     *                 new String[]{"id"}, ②  //指定子关联类的唯一键属性（可能是多个属性共同组成唯一键），对应子关联类Course里id属性
     *                 "c.",    ③   //限定sql里哪些列映射到子关联类
     *                 null);   //指定子关联类里哪些属性被映射，如果为null表明所有属性被映射
     *         One2ManyMapNestOptions one2ManyMapNestOptions=MD.ofOne2Many(
     *                  "stu."   ④     //限定sql里哪些列被映射到主类里
     *                 , new String[]{"id"},  ⑤  //指定主类的主键属性，对应主类Student里的id属性
     *                 queryMapNestOne2Many);
     *         List&lt;One2ManyStudent&gt; list=MDbUtils.queryListOne2Many(DbPoolName,One2ManyStudent.class,
     *                 MD.md(),
     *                 args,
     *                 one2ManyMapNestOptions);
     *         System.out.println("list="+ ObjectUtils.toPrettyJsonString(list));
     *
     *     }
     *    public static void main(String[] args) throws Exception{
     *         CourseDao dao=new CourseDao();
     *         dao.testQueryListOne2Many();
     *     }
     * }
     *
     * </pre></blockquote>
     * ④处通过指定"stu."限定SQL语句里哪些列字段（③-1处）映射到主类里对应的属性。
     * queryMapNestOne2Many对象设置了关联属性"courseList"（①处），对应到主类One2ManyStudent里的关联属性名courseList（①-1处），
     * 在③处设置了"c."，这限定了SQL语句里哪些字段（③-1处）映射到子关联类（Course）里的属性。⑤处指定了主类里哪些属性组成唯一键来确定一个对象
     * （即这些属性对应的表字段合起来是是唯一键），aka-dbutils利用这些属性对查询的记录进行分组，分组内的记录再通过子关联类主键属性（②处）去
     * 掉重复生成的关联子对象，从而构成最终的关联子对象列表。
     *
     * @param clazz                  映射到的对象所属类型
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args                   md方法里用到的参数
     * @param one2ManyMapNestOptions 指定一对多关联子对象的映射信息，为数组类型，可以指定多个一对多关联映射
     * @param <T>
     * @return
     * @throws DbException
     */
    <T> List<T> queryListOne2Many(Class<T> clazz, String mdFullMethodName,
                                  Map<String, Object> args,
                                  One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException;

    /**
     * 根据md方法指定的SQL语句来查询记录，并通过rowMapper映射到指定类型的对象。
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             md方法所用到的参数，如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile
     * @param rowMapper        自定义映射接口，可通过此接口，开发者可以自定义结果集到对象的映射
     * @param <T>
     * @return 返回一个List对象，包含通过rowMapper映射的对象。
     * @throws DbException
     */
    <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException;

    /**
     * 根据md方法指定的SQL查询记录，每行记录映射到一个Map对象，并返回包含Map对象的List。
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>。
     * @param args             md方法所用到的参数
     * @return 返回一个含有map的List，每个map对应一行记录，key为表字段名称，value为字段的值
     * @throws DbException
     */
    List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 执行mdFullMethodName指定的SQL执行删除操作
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             md方法里用到的参数
     * @return 返回删除操作删除的条数
     * @throws DbException
     */
    int del(String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 执行存储过程，可传入参数，得到输出参数和返回的离线结果集，
     * 传入参数的用法如下：
     * <pre>
     * //parms参数可以按如下形式添加
     * parms.put("country","U.S.A");//默认为in类型
     * parms.put("province:in","New York");
     * parms.put("count:in",new Integer(3));
     * parms.put("oSumCnt:out",int.class); //①
     * //parms.put("oSumCnt:out",3); //和①处的效果一样，但用的是Integer类型
     * parms.put("oData:out",java.util.date.class);
     * parms.put("ioQuantity:inout",new Long(44));//使用的是Long类型
     *
     * 如果参数是out类型（key里含有:out），表明参数只为输出参数，即可以给value里指定一个类型（①处），也可以指定一个具体的值，如果指定值，此值并不会传入参考过程/函数，aka-dbutils可以根据值获取其类型作为输出类型。out类型的参数表明是存储过程/函数的输出，在存储过程/函数执行后，可以通过outPramsValues根据参数名称可以获取输出值。
     * 如果参数是inout类型（key里含有:inout)，表明参数既是输入也是输出参数，必须指定具体值传入到存储过程/函数，存储过程执行完成后，可以通过outPramsValues根据参数名称获取输出值。
     * 如果参数是in类型（key里包含:in)，表明参数只是输入参数，指定的值会传入存储过程/函数。
     *
     * outPramsValues存放输出参数的返回值，与parms(输入参数)里的out和inout类型的参数对应
     * 上面的例子产生的输出参数如下：
     * {
     *   oSumCnt:45556,
     *   oData:"2015-09-23 12:34:56"
     *   ioQuantity:34456
     * }
     * 传入参数的名称格式为 ：**参数名称:[in|out|inout]**，其中in，out，inout对应存储过程或存储函数里的参数类型。
     *
     * returnDataBaseSets只针对存储过程有效，当存储过程里执行了select查询语句时，可以通过returnDataBaseSets返回select查询语句的离线结果集。
     * </pre>
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param parms              传入的参数
     * @param outPramsValues     输出参数
     * @param returnDataBaseSets 返回的离线离线结果集
     * @throws DbException
     */
    void callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
                       List<DataBaseSet> returnDataBaseSets) throws DbException;

    /**
     * 根据mdFullMethodName指定的SQL执行插入操作，args为动态生成SQL语句的参数
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>。
     * @param args             为动态生成SQL语句的参数
     * @return 返回执行插入操作后插入记录的条数
     * @throws DbException
     */
    int insert(String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 根据mdFullMethodName指定的SQL执行插入操作，args为动态生成SQL语句的参数，返回插入单条记录的自增主键id
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>。
     * @param args             为动态生成SQL语句的参数
     * @return 返回插入单条记录的自增主键id
     * @throws DbException
     */
    long insertReturnKey(String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 根据多个md方法地址指定的SQL执行插入数据库操作，返回每个md方法执行后插入的记录条数，每个md方法对应一个Map&lt;String, Object&gt;对象，用于传递参数。
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args              mdFullMethodNames数组里每个md方法地址所定义的SQL，可以被提供一个Map&lt;String, Object&gt;对象，用于传递参数。
     * @return 每个md方法地址指定的SQL执行插入操作返回的记录条数存放于int数组相应位置
     * @throws DbException
     */
    int[] insert(String[] mdFullMethodName, Map<String, Object>[] args) throws DbException;

    /**
     * 根据多个md方法地址指定的SQL执行插入数据库操作，返回每个md方法执行后插入的记录条数
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @return 每个md方法地址指定的SQL执行插入操作返回的记录条数存放于int数组相应位置
     * @throws DbException
     */
    int[] insert(ArrayList<String> mdFullMethodName) throws DbException;

    /**
     * 执行插入操作，一个md方法地址对应多个参数，同一个md方法会根据不同的参数多次执行，每次执行返回插入的记录数
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             参数数组，会根据数组里的每个参数执行一次md方法，从而生成不同的insert语句
     * @return 每此执行插入操作返回的记录条数存放于int数组相应位置
     * @throws DbException
     */
    int[] insert(String mdFullMethodName, List<Map<String, Object>> args) throws DbException;

    /**
     * 更新操作，一个md方法地址对应多个Map&lt;String, Object&gt;参数对象，从而同一个md方法可以执行多次，每次对应不同的Map&lt;String, Object&gt;对象（用于传达SQL使用的参数）。
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             多个Map&lt;String, Object&gt;对象，对应于每次执行的md方法
     * @return 返回每次执行md方法的SQL语句返回的条数
     * @throws DbException
     */
    int[] update(String mdFullMethodName, List<Map<String, Object>> args) throws DbException;

    /**
     * 更新操作，根据提供的md方法地址数组执行批量更新操作,数组里的每个md方法的SQL参数数组（vParametersArray）相应参数Map。
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args              每个md方法的SQL可以使用一个Map&lt;String, Object&gt;对象来传递参数
     * @return 返回每次执行md方法的SQL语句返回的条数
     * @throws DbException
     */
    int[] update(String[] mdFullMethodName, Map<String, Object>[] args) throws DbException;

    /**
     * 更新操作，根据提供的多个md方法地址指定的SQL执行批量更新操作
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @return 返回每个md方法地址指定的SQL所更新的记录条数
     * @throws DbException
     */
    int[] update(ArrayList<String> mdFullMethodName) throws DbException;

    /**
     * 更新操作，根据md方法地址定义的SQL执行更新，可提供参数给SQL使用
     *
     * @param mdFullMethodName 分如下两种情况：<ul>
     *                         <li>md方法地址：如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile。</li>
     *                         <li><xmp>sql:<SQL语句></xmp>：如"sql:select * from stu where name=#{stuName}"。</li>
     *                         </ul>
     * @param args             md方法用到的参数
     * @return md方法地址指定的SQL执行返回的条数
     * @throws DbException
     */
    int update(String mdFullMethodName, Map<String, Object> args) throws DbException;

    /**
     * 根据interfaceType指定的接口生成动态代理。interfaceType接口里的方法映射到对应的md方法，
     * 接口名称与md文件名称相同（不包含.md后缀）
     *
     * @param interfaceType 指定接口，生成代理对象
     * @param <T>
     * @return 返回根据interfaceType接口生成的动态代理对象
     */
    <T> T getMapper(Class<T> interfaceType) throws DbException;

    /**
     * 返回当前连接，如果force=true，若当前没有连接，则新生成一个连接；force=false，若连接
     * 不存在或已经关闭则会返回null。
     *
     * @param force
     * @return
     */
    Connection getConnection(boolean force);

    /**
     * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBaseMd里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过close()方法关闭数据库连接
     *
     * @throws DbException
     */
    void setAutoCommit(boolean b) throws DbException;

    /**
     * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    boolean getAutoCommit() throws DbException;

    /**
     * 用于事务性操作的回滚，如果事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    void rollback() throws DbException;

    /**
     * 得到保存点信息；
     *
     * @return
     */
    Map<String, Savepoint> getSavepoint();

    /**
     * 设置事务保存点，rollbackToSavepoint()方法会回滚到某个保存点，用于事务的局部回滚
     *
     * @param savepointName 保存点
     * @throws DbException
     */
    void setSavepoint(String savepointName) throws DbException;

    /**
     * 释放并删除指定名称的savepoint
     *
     * @param savepointName
     * @throws DbException
     */
    void releaseSavepoint(String savepointName) throws DbException;

    /**
     * 用于事务回滚到保存点。
     *
     * @param savepointName 回滚到的保存点
     * @throws DbException 异常
     */
    void rollbackToSavepoint(String savepointName) throws DbException;

    /**
     * 判断资源和底层数据库连接是否关闭
     *
     * @return
     * @throws DbException
     */
    boolean isColsed() throws DbException;

    /**
     * 事务性操作的事务的提交，当 {@link #setAutoCommit(boolean)}设为false，
     * 会用到此方法，一般对于事务性操作会用到，如果 事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    void commit() throws DbException;

    /**
     * 关闭数据库连接，释放底层占用资源
     */
    void close();

    DBMS getDataBaseType();
}
