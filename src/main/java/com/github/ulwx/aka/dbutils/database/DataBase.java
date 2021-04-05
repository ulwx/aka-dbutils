package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.tool.PageBean;

import javax.sql.DataSource;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DataBase extends DBObjectOperation, AutoCloseable {

    public static enum SQLType {
        OTHER, INSERT, UPDATE, DELETE, SELECT, STORE_DPROCEDURE, SCRIPT
    }

    public static enum ConnectType {
        POOL, //从连接池获取连接
        DATASOURCE, //从数据源获取连接
        CONNECTION //从外部传入连接
    }

    public static enum MainSlaveModeConnectMode {
        // 如果是主从模式，并且是非事务性操作,如果是这种模式，则只去获取主库连接
        Connect_MainServer,
        // 如果是主从模式，并且是非事务性操作，如果是这种模式，则只去获取从库连接
        Connect_SlaveServer,
        // 根据语句或是否含有事务来判断自动获取主库连接还是从库连接。如果是执行语句包含在事务里或者是insert，update，delete语句，则获取主库连接;
        // 如果为查询语句，并且不包含在事务里，会在从库里获取连接
        Connect_Auto
    }

    public MainSlaveModeConnectMode getMainSlaveModeConnectMode();

    String getDbPoolName();

    /**
     * 是否主从模式，true为主从模式（dbpool.xml里有主从库配置），false非主从模式（dbpool.xml里没有从库配置）
     *
     * @return
     */
    boolean isMainSlaveMode();

    /**
     * 是否连接到主库，主从模式时（isMainSlaveMode()返回true），true表明连接到主库，false连接到从库。
     * 如果非主从模式isMainSlaveMode()返回false，固定返回true。
     *
     * @return
     */
    Boolean connectedToMaster();

    void setMainSlaveMode(boolean mainSlaveMode);

    boolean getInternalConnectionAutoCommit() throws DbException;

    DBMS getDataBaseType();

    default void connectDb(DataSource dataSource) {
    }

    ConnectType getConnectionType();

    default void connectDb(Connection connection, boolean externalControlConClose) {
    }

    default boolean isExternalControlConClose() {
        return false;
    }

    /**
     * 从dbpool.xml里设置的连接池获得连接
     *
     * @param dbPoolName 对应于dbpool.xml里的元素&lt;dbpool&gt;name属性值
     * @throws DbException 异常
     */
    void connectDb(String dbPoolName) throws DbException;

    /**
     * 根据SQL从数据库查询记录，aka-dbutils会在内部封装SQL从而形成分页查询的SQL，最终返回当前页的结果集。
     *
     * @param sqlQuery    sql语句
     * @param vParameters sql语句里用到的参数
     * @param page        当前页码（从1开始）
     * @param perPage     每页多少行
     * @param pageBean    存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSql    可以指定四种类型的参数，<br>
     *                    null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                    数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                    计算总数的SQL：表示计算总数的SQL<br>
     *                    -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @return 返回结果集
     * @throws DbException
     */
    DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage, PageBean pageBean,
                                  String countSql) throws DbException;

    /**
     * SQL从数据库查询记录，返回结果集
     *
     * @param sqlQuery    sql语句
     * @param vParameters 参数
     * @return 返回结果集
     * @throws DbException
     */
    DataBaseSet queryForResultSet(String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    /**
     * 根据SQL语句来查询记录，并通过rowMapper映射到指定类型的对象，本方法为分页查询，参数page为页码（从1开始），
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
     * @param sqlQuery  sql语句
     * @param args      参数
     * @param page      当前请求页码
     * @param perPage   每页多少行
     * @param pageBean  存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param rowMapper 自定义映射接口，可通过此接口，开发者可以自定义结果集到对象的映射
     * @param countSql  可以指定四种类型的参数，<br>
     *                  null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                  数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                  表示计算总数的SQL：表示计算总数的SQLbr>
     *                  -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @param <T>
     * @return 返回一个List对象，包含通过rowMapper映射的对象。
     * @throws DbException
     */

    <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageBean,
                          RowMapper<T> rowMapper, String countSql) throws DbException;

    /**
     * 根据SQL查询记录，每行记录映射到一个Map对象，并返回包含Map对象的List。本方法为分页查询，参数page为页码（从1开始），
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
     * @param sqlQuery sql语句
     * @param args     参数
     * @param page     当前请求页码
     * @param perPage  每页多少行
     * @param pageBean 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSql 可以指定四种类型的参数，<br>
     *                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                 表示计算总数的SQL：表示计算总数的SQLbr>
     *                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @return 返回一个含有map的List，每个map对应一行记录，key为表字段名称，value为字段的值
     * @throws DbException
     */
    List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
                                       PageBean pageBean, String countSql) throws DbException;
    /**
     * 分页查询，返回的一页结果为Map列表
     * @param sqlQuery sql语句
     * @param args   参数
     * @return
     * @throws DbException
     */
    List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args) throws DbException;

    /**
     * 根据SQL语句查询记录，每行记录映射到指定类型的对象。
     *
     * @param clazz       记录映射到对象的类型
     * @param sqlQuery
     * @param vParameters 参数
     * @param <T>
     * @return 返回一个List对象，包含行记录映射的对象。
     * @throws DbException
     */
    <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

    /**
     * 根据mdFullMethodName指定的SQL查询记录，每条记录映射到一个对象，并返回一个对象。注意，如果指定的SQL语句查询多条记录，aka-dbutils会
     * 内部依然会映射到一个列表，并从列表里取出第一个对象，所以，如果调用此方法，建议指定的SQL里要含有限制取第一个对象，如对mysql，SQL后面加limit 1。
     *
     * @param clazz       映射的对象的类型
     * @param sqlQuery    sql语句
     * @param vParameters 参数
     * @param <T>
     * @return 返回一个对象
     * @throws DbException
     */
    <T> T queryOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;
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
     * SQL语句如下：
     *  select
     *   stu.*,    ③-1
     *   c.*       ②-1
     *  from student stu,student_course sc,course c
     *  where stu.id=sc.student_id and  c.id=sc.course_id
     *  and c.student_name like ? order by c.id
     *
     * //CourseDao .java
     *  public class CourseDao {
     *     ......
     *     public  void testQueryListOne2One(){
     *         DataBase db=DataBaseFactory.getDataBase("courseDb");
     *         String sql="select stu.*,c.* from student stu,student_course sc,course c where stu.id=sc.student_id and  c.id=sc.course_id and c.student_name like ? order by c.id";
     *         Map&lt;Integer,Object&gt;args = new HashMap&lt;&gt;();
     *         args.put(1,"student%");
     *         QueryMapNestOne2One queryMapNestOne2One = new QueryMapNestOne2One();
     *         queryMapNestOne2One.set(null,  //子关联类哪些属性被映射，null表明全部映射
     *                              "course",  ①  //指定主类里的关联属性
     *                                "c.");   ② //限定哪些列映射到子关联类里
     *
     *         One2OneMapNestOptions one2OneMapNestOptions=MD.ofOne2One(
     *                 "stu."  ③
     *                 ,queryMapNestOne2One
     *         );
     *         List&lt;One2OneStudent&gt; list=db.queryListOne2One(One2OneStudent.class,
     *                  sql, args, one2OneMapNestOptions);
     *         System.out.println("list="+ ObjectUtils.toPrettyJsonString(list));
     *
     *     }

     * }
     * </pre></blockquote>
     * ③处的"stu."指定了一个前缀，它限定了SQL语句里哪些列字段（③-1处）要映射到主类（包含关联属性的类）里对应的属性。
     * queryMapNestOne2One对象被设置了"course"（①处），它指定了主类One2OneStudent类里的关联属性（即对应于①-1处的course属性），
     * 在②处设置了"c."，这限定了SQL语句里哪些列字段（②-1处）要映射到子关联子里的对应的属性（即Course类里的属性）。
     *
     * @param clazz                 映射到的对象所属类型
     * @param sqlQuery      sql语句
     * @param vParameters                  参数
     * @param one2OneMapNestOptions 关联子对象的映射配置对象。
     * @param <T>
     * @return
     * @throws DbException
     */
    <T> List<T> queryListOne2One(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                 One2OneMapNestOptions one2OneMapNestOptions) throws DbException;
    /**
     * 根据SQL语句查询记录，每行记录映射到指定类型的对象。本方法为分页查询，参数page为页码（从1开始），
     * 参数perPage为每页多少行记录，aka-dbutils会根据这些信息生成分页的select语句，例如，如果当前数据库类型为mysql，
     * 请求第2页（page=2），每页10条（perPage=10），如果sqlQuery指定的SQL为<blockquote><pre>
     * select * from course  where name='course_page' ，</pre></blockquote>
     * 则生成的分页查询语句如下：
     * <blockquote><pre>
     *     select * from course  where name='course_page' limit 20,10
     *     (由于 mysql的limit后面的索引是从0开始，所以这里的20表示从第21条开始往后的10条记录，即第二页数据)
     * </pre></blockquote>
     * 如果countSql=null，那么aka-dbutils在生成上面的分页select语句之前，会首先自动生成查询总数的select语句，
     * 如果当前数据库类型为mysql，生成的语句如下：
     * <blockquote><pre>
     *      select count(1) from (select * from course  where name='course_page') t
     *  </pre></blockquote>
     *
     * @param clazz                    记录映射到对象的类型
     * @param sqlQuery         sql语句
     * @param vParameters                    参数
     * @param page                     当前请求页码
     * @param perPage                  每页多少行
     * @param pageBean                 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSql 可以指定四种类型的参数，<br>
     *                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                 表示计算总数的SQL：表示计算总数的SQL<br>
     *                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @param <T>
     * @return 返回一个List对象，包含行记录映射的对象。
     * @throws DbException
     */
    <T> List<T> queryList(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
                          PageBean pageBean, String countSql) throws DbException;
    /**
     * 一对一关联分页查询。是针对一个对象"一对一关联"另一对象，通过在对象的类里定义一个关联属性，包含关联属性的类为主类，
     * 关联属性的类型为子关联类。主类和子关联类分别都对应到数据库表，例如student表，其每个学生只学习一门课程（对应course表一条记录），
     * 那么student表一行学生信息记录就一对一关联course表的一门课程记录，从类的角度来说就是主类Student和子关联类Course具有一对一关联，
     * 并且在Student类里定义了一个名为"course"的关联属性，其类型为Course，它是子关联类型。
     *
     * @param clazz                    映射到的对象所属类型
     * @param sqlQuery          sql语句
     * @param vParameters                     参数
     * @param one2OneMapNestOptions    关联子对象的映射配置对象。
     * @param page                     当前请求页码
     * @param perPage                  每页多少行
     * @param pageBean                 存放分页信息，如总记录数，最大页码，这些信息用于前端UI控件展示
     * @param countSql 可以指定四种类型的参数，<br>
     *                 null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br>
     *                 数字：则表明以指定的数字为总数，用于计算分页信息；<br>
     *                 表示计算总数的SQL：表示计算总数的SQL<br>
     *                 -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
     * @param <T>
     * @return
     * @throws DbException
     */
    <T> List<T> queryListOne2One(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters,
                                 One2OneMapNestOptions one2OneMapNestOptions,
                                 int page, int perPage, PageBean pageBean, String countSql)
            throws DbException;
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
     * 对应的SQL语句如下：
     *  select
     *   stu.*,    ④-1
     *   c.*       ③-1
     *  from student stu,student_course sc,course c
     *  where stu.id=sc.student_id and  c.id=sc.course_id
     *  and c.name in ? order by c.id
     *
     *  public class CourseDao {
     *     ......
     *    public  void testQueryListOne2Many(){
     *         DataBase db=DataBaseFactory.getDataBase("courseDb");
     *         String sql="select stu.*,c.* from student stu,student_course sc,course c where stu.id=sc.student_id and  c.id=sc.course_id and c.name in ? order by c.id";
     *         Map&lt;Integer,Object&gt;args = new HashMap&lt;&gt;();
     *         args.put(1,new String[]{"student1","student2","student3"});
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
     *         List&lt;One2ManyStudent&gt; list=db.queryListOne2Many(One2ManyStudent.class,
     *                 sql,
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
     * @param sqlQuery       sql语句
     * @param vParameters                  参数
     * @param one2ManyMapNestOptions 指定一对多关联子对象的映射信息，为数组类型，可以指定多个一对多关联映射
     * @param <T>
     * @return
     * @throws DbException
     */
    <T> List<T> queryListOne2Many(Class<T> clazz, String sqlQuery,
                                  Map<Integer, Object> vParameters, One2ManyMapNestOptions one2ManyMapNestOptions) throws DbException;
    /**
     * 根据指定的SQL语句来查询记录，并通过rowMapper映射到指定类型的对象。
     *
     * @param sqlQuery sql语句
     * @param args             参数
     * @param rowMapper        自定义映射接口，可通过此接口，开发者可以自定义结果集到对象的映射
     * @param <T>
     * @return 返回一个List对象，包含通过rowMapper映射的对象。
     * @throws DbException
     */
    <T> List<T> queryList(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException;
    /**
     * 执行指定的SQL执行删除操作
     * @param sqltext  sql语句
     * @param vParameters    参数
     * @return 返回删除操作删除的条数
     * @throws DbException
     */
    int del(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    int update(String sqltext, Map<Integer, Object> vParameters) throws DbException;
    /**
     * 调用存储过程，用法如下：
     * <pre>
     * //parms参数可以按如下形式添加
     * parms.put("1","中");//默认为in类型
     * parms.put("2:in","国");
     * parms.put("3:in",new Integer(3));
     * parms.put("4:out",int.class);
     * parms.put("5:out",java.util.data.class);
     * parms.put("6:inout",new Long(44));
     *
     * //outPramsValues存放输出参数的返回值，与parms(输入参数)里的out和inout类型对应，
     * //上面的例子产生的输出参数如下：
     * {
     *   4:45556,
     *   5:"2015-09-23 12:34:56"
     *   6:34456
     * }</pre>
     *
     * @param sqltext            sql语句
     * @param parms              用法举例如下：
     * @param outPramsValues     存放输出参数的返回值，与parms(输入参数)里的out和inout类型对应
     * @param returnDataBaseSets 需返回值的结果集
     * @throws DbException
     */
    void callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
                       List<DataBaseSet> returnDataBaseSets) throws DbException;
    /**
     * 根据指定的insert语句执行插入操作
     *
     * @param sqltext  insert语句
     * @param vParameters    sqltext所用到的参数
     * @return 返回执行插入操作后插入记录的条数
     * @throws DbException
     */
    int insert(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    /**
     * 根据定的SQL执行插入操作，args为动态生成SQL语句的参数，返回插入单条记录的自增主键id
     *
     * @param sqltext insert语句
     * @param vParameters  sqltext所用到的参数
     * @return 返回插入单条记录的自增主键id
     * @throws DbException
     */
    long insertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException;

    /**
     * 更新操作，根据指定的SQL语句数组执行批量更新操作,每个数组里的SQL语句对应参数数组（vParametersArray）相应参数Map。
     *
     * @param sqltxts sql语句数组
     * @param vParametersArray  参数数组
     * @return  sqltxts数组里每条SQL语句返回的条数构成数组
     * @throws DbException
     */
    int[] update(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException;

    /**
     * 批量插入操作，根据指定的SQL语句数组执行批量更新操作,每个数组里的SQL语句对应参数数组（vParametersArray）相应参数Map。
     *
     * @param sqltxts sql语句数组
     * @param vParametersArray   参数数组
     * @return sqltxts数组里每条SQL语句返回的插入条数构成数组
     * @throws DbException
     */
    int[] insert(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException;

    /**
     * 根据指定的单个SQL执行批量更新操作，指定的sql语句会以列表里的每个Map参数依次执行，最终返回对应的每条语句执行更新的条数。
     *
     * @param sqltxt sql语句
     * @param vParametersList    Map参数列表
     * @return 返回执行更新操作后插入记录的条数
     * @throws DbException
     */
    int[] update(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException;
    /**
     * 根据指定的单个SQL执行批量插入操作，指定的sql语句会以列表里的每个Map参数依次执行，最终返回对应的每条语句执行插入的条数。
     *
     * @param sqltxt sql语句
     * @param vParametersList    Map参数列表
     * @return 返回执行插入操作后插入记录的条数
     * @throws DbException
     */
    int[] insert(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException;
    /**
     * 更新操作，根据指定的SQL语句列表执行批量更新操作。
     *
     * @param sqltxts update语句列表
     * @return  sqltxts列表里每条SQL语句返回的条数构成数组
     * @throws DbException
     */
    int[] update(ArrayList<String> sqltxts) throws DbException;
    /**
     * 插入操作，根据指定的SQL语句列表执行批量插入操作。
     *
     * @param sqltxts insert语句列表
     * @return sqltxts列表里每条SQL语句插入的条数构成数组
     * @throws DbException
     */
    int[] insert(ArrayList<String> sqltxts) throws DbException;


    /**
     * 执行脚本
     *
     * @param reader       sql脚本输入reader
     * @param throwWarning 脚本执行时如果出现warning，是否退出并回滚
     * @param args         参数
     * @return 返回执行成功的结果，出错返回异常
     * @throws DbException
     */
    String exeScript(Reader reader, boolean throwWarning, Map<String, Object> args) throws DbException;


    /**
     * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过DataBase.
     * setAutoCommit()方法 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBase.close()方法关闭数据库连接
     *
     * @param b 是否设置为自动提交，如果设置为false表示不自动提交，即开启了事务。
     * @throws DbException
     */
    void setAutoCommit(boolean b) throws DbException;


    /**
     * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过DataBase.setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBase.close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    boolean getAutoCommit() throws DbException;

    /**
     * 用于事务性操作的回滚。
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
     * 设置保存点
     *
     * @param savepointName 保存点名称
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
     * @throws DbException
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
     * 事务性操作的事务的提交，当 {@link #setAutoCommit(boolean)}设为false， 会用到此方法，一般对于事务性操作会用到，如果
     * 事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    void commit() throws DbException;

    /**
     * 关闭数据库连接，释放底层占用资源
     */
    void close();

    /**
     * 返回当前连接，如果force=true，若当前没有连接，则新生成一个连接；force=false，若连接
     * 不存在或已经关闭则会返回null。
     *
     * @param force
     * @return
     */
    Connection getConnection(boolean force);
}