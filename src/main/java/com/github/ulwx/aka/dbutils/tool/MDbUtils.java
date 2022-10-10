package com.github.ulwx.aka.dbutils.tool;

import com.github.ulwx.aka.dbutils.database.*;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MDbUtils extends BaseDao {

    private static <R> R mdbExecute(Function<MDataBase, R> function, String dbpoolName) {
        MDataBase mdb = null;
        try {
            mdb = MDbManager.getDataBase(dbpoolName);
            return function.apply(mdb);
        } finally {
            if (mdb != null) {
                mdb.close();
            }
        }

    }

    /**
     * 执行sql脚本，packageFullName指定SQL脚本所在的包（全路径），sqlFileName为脚本文件的名称，脚本文件里存放的是SQL脚本，
     * 整个脚本的执行在一个事务里，如果执行过程中出错则抛出异常并回滚。可以指定脚本在执行过程中如果出现警告是否抛出异常并回滚，
     * 脚本是按每个语句依次执行，脚本里每个语句的分界是根据英文分号和回车换行共同判定，即 ";\r\n"
     *
     * @param dbpoolName      对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                        如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param packageFullName 指定SQL脚本所在的包（全路径）
     * @param sqlFileName     为脚本文件的名称，脚本文件里存放的是SQL脚本
     * @param throwWarning    脚本执行时如果出现warning时是否抛出异常并回滚
     * @return 返回执行脚本的结果
     * @throws DbException
     */
    public static String exeScript(String dbpoolName, String packageFullName, String sqlFileName,
                                   Boolean throwWarning) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.exeScript(packageFullName, sqlFileName, throwWarning);
        }, dbpoolName);
    }

    /**
     * 执行md方法地址指定的脚本，并且可以传入参数,脚本里执行时按每个SQL语句执行，执行的时候利用的是jdbc的PrepareStatement，能有效防止注入式攻击
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             传入md方法的参数
     * @param delimiters       指定每个SQL语句的分界，例如";"
     * @return 返回脚本执行的结果
     * @throws DbException
     */
    public static String exeScript(String dbpoolName, String mdFullMethodName, String delimiters, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.exeScript(mdFullMethodName, delimiters, args);
        }, dbpoolName);
    }

    /**
     * 根据mdFullMethodName指定的md方法地址所在的SQL从数据库查询记录，aka-dbutils会在内部封装SQL从而形成分页查询的SQL，
     * 最终返回当前页的离线结果集。
     *
     * @param dbpoolName               对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                                 如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName         md方法地址，如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile
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
    public static DataBaseSet queryForResultSet(String dbpoolName, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryForResultSet(mdFullMethodName, args, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    /**
     * 根据mdFullMethodName指定的md方法地址所在的SQL从数据库查询记录，返回离线结果集
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址，如com.hithub.ulwx.demo.dao.CourseDao.md:queryListFromMdFile
     * @param args             md方法里用到的参数
     * @return 返回离线结果集
     * @throws DbException
     */
    public static DataBaseSet queryForResultSet(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryForResultSet(mdFullMethodName, args);
        }, dbpoolName);
    }

    /**
     * 根据md方法指定的SQL语句查询记录，每行记录映射到指定类型的对象。
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param clazz            记录映射到对象的类型
     * @param mdFullMethodName md方法地址
     * @param args             md方法用到的参数
     * @param <T>
     * @return 返回一个List对象，包含行记录映射的对象。
     * @throws DbException
     */
    public static <T> List<T> queryList(String dbpoolName, Class<T> clazz,
                                        String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(clazz, mdFullMethodName, args);
        }, dbpoolName);
    }

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
     * @param dbpoolName               对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                                 如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param clazz                    记录映射到对象的类型
     * @param mdFullMethodName         md方法地址
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
    public static <T> List<T> queryList(String dbpoolName, Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(clazz, mdFullMethodName, args, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    /**
     * 根据md方法指定的SQL语句来查询记录，并通过rowMapper映射到指定类型的对象。
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             md方法所用到的参数
     * @param rowMapper        自定义映射接口，可通过此接口，开发者可以自定义结果集到对象的映射
     * @param <T>
     * @return 返回一个List对象，包含通过rowMapper映射的对象。
     * @throws DbException
     */
    public static <T> List<T> queryList(String dbpoolName, String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(mdFullMethodName, args, rowMapper);
        }, dbpoolName);
    }

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
     * @param dbpoolName               对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                                 如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName         md方法地址
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
    public static <T> List<T> queryList(String dbpoolName, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryList(mdFullMethodName, args, page, perPage, pageBean, rowMapper, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    /**
     * 根据md方法指定的SQL查询记录，每行记录映射到一个Map对象，并返回包含Map对象的List。
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             md方法所用到的参数
     * @return 返回一个含有map的List，每个map对应一行记录，key为表字段名称，value为字段的值
     * @throws DbException
     */
    public static List<Map<String, Object>> queryMap(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryMap(mdFullMethodName, args);
        }, dbpoolName);
    }

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
     * @param dbpoolName               对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                                 如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName         md方法地址
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
    public static List<Map<String, Object>> queryMap(String dbpoolName, String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryMap(mdFullMethodName, args, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

    /**
     * 根据mdFullMethodName指定的SQL查询记录，每条记录映射到一个对象，并返回一个对象。注意，如果指定的SQL语句查询多条记录，aka-dbutils会
     * 内部依然会映射到一个列表，并从列表里取出第一个对象，所以，如果调用此方法，建议指定的SQL里要含有限制取第一个对象，如对mysql，SQL后面加limit 1。
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param clazz            映射的对象的类型
     * @param mdFullMethodName md方法地址
     * @param args             md方法所用到的参数
     * @param <T>
     * @return 返回一个对象
     * @throws DbException
     */
    public static <T> T queryOne(String dbpoolName, Class<T> clazz, String mdFullMethodName,
                                 Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryOne(clazz, mdFullMethodName, args);
        }, dbpoolName);
    }


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
     * @param dbpoolName            对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                              如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param clazz                 映射到的对象所属类型
     * @param mdFullMethodName      md方法地址
     * @param args                  md方法里用到的参数
     * @param one2OneMapNestOptions 关联子对象的映射配置对象。
     * @param <T>
     * @return
     * @throws DbException
     */
    public static <T> List<T> queryListOne2One(String dbpoolName, Class<T> clazz,
                                               String mdFullMethodName, Map<String, Object> args,
                                               One2OneMapNestOptions one2OneMapNestOptions) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryListOne2One(clazz, mdFullMethodName, args, one2OneMapNestOptions);
        }, dbpoolName);
    }

    /**
     * 一对一关联分页查询。是针对一个对象"一对一关联"另一对象，通过在对象的类里定义一个关联属性，包含关联属性的类为主类，
     * 关联属性的类型为子关联类。主类和子关联类分别都对应到数据库表，例如student表，其每个学生只学习一门课程（对应course表一条记录），
     * 那么student表一行学生信息记录就一对一关联course表的一门课程记录，从类的角度来说就是主类Student和子关联类Course具有一对一关联，
     * 并且在Student类里定义了一个名为"course"的关联属性，其类型为Course，它是子关联类型。
     *
     * @param dbpoolName               对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                                 如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param clazz                    映射到的对象所属类型
     * @param mdFullMethodName         md方法地址
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
    public static <T> List<T> queryListOne2One(String dbpoolName, Class<T> clazz,
                                               String mdFullMethodName,
                                               Map<String, Object> args,
                                               One2OneMapNestOptions one2OneMapNestOptions,
                                               int page, int perPage, PageBean pageBean,
                                               String countSqlMdFullMethodName) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryListOne2One(clazz, mdFullMethodName, args, one2OneMapNestOptions, page, perPage, pageBean, countSqlMdFullMethodName);
        }, dbpoolName);
    }

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
     * @param dbpoolName             对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                               如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param clazz                  映射到的对象所属类型
     * @param mdFullMethodName       md方法地址
     * @param args                   md方法里用到的参数
     * @param one2ManyMapNestOptions 指定一对多关联子对象的映射信息，为数组类型，可以指定多个一对多关联映射
     * @param <T>
     * @return
     * @throws DbException
     */
    public static <T> List<T> queryListOne2Many(String dbpoolName, Class<T> clazz,
                                                String mdFullMethodName, Map<String, Object> args,
                                                One2ManyMapNestOptions one2ManyMapNestOptions)
            throws DbException {
        return mdbExecute(mdb -> {
            return mdb.queryListOne2Many(clazz, mdFullMethodName, args, one2ManyMapNestOptions);
        }, dbpoolName);
    }

    /**
     * 执行mdFullMethodName指定的SQL执行删除操作
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             md方法里用到的参数
     * @return 返回删除操作删除的条数
     * @throws DbException
     */
    public static int del(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.del(mdFullMethodName, args);
        }, dbpoolName);
    }


    /**
     * 执行存储过程，可传入参数，得到输出参数和返回的离线结果集，
     * 传入参数的用法如下：
     * <blockquote><pre>
     * //parms参数可以按如下形式添加
     * parms.put("country","U.S.A");//默认为in类型
     * parms.put("province:in","New York");
     * parms.put("count:in",new Integer(3));
     * parms.put("oSumCnt:out",int.class); //①
     * //parms.put("oSumCnt:out",3); //和①处的效果一样，但用的是Integer类型
     * parms.put("oData:out",java.util.date.class);
     * parms.put("ioQuantity:inout",new Long(44));//使用的是Long类型
     *
     * 如果参数是out类型（key里含有:out），则既可以给value里指定一个类型（①处），也可以指定一个具体的值（aka-dbutils可以根据值获取输出的类型）
     * 如果参数是inout类型（key里含有:inout)，则可以指定具体值
     *
     * //outPramsValues存放输出参数的返回值，与parms(输入参数)里的out和inout类型对应，
     * //上面的例子产生的输出参数如下：
     * {
     *   oSumCnt:45556,
     *   oData:"2015-09-23 12:34:56"
     *   ioQuantity:34456
     * }</pre></blockquote>
     *
     * @param dbpoolName         对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                           如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName   md方法地址
     * @param parms              传入的参数
     * @param outPramsValues     输出参数
     * @param returnDataBaseSets 返回的离线结果集
     * @throws DbException
     */
    public static void callStoredPro(String dbpoolName, String mdFullMethodName, Map<String, Object> parms,
                                     Map<String, Object> outPramsValues, List<DataBaseSet> returnDataBaseSets) throws DbException {
        mdbExecute(mdb -> {
            mdb.callStoredPro(mdFullMethodName, parms, outPramsValues, returnDataBaseSets);
            return 1;
        }, dbpoolName);
    }

    /**
     * 执行插入操作，一个md方法地址对应多个参数，同一个md方法会根据不同的参数多次执行，每次执行返回插入的记录数
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             参数数组，会根据数组里的每个参数执行一次md方法，从而生成不同的insert语句
     * @return 每此执行插入操作返回的记录条数存放于int数组相应位置
     * @throws DbException
     */
    public static int[] insert(String dbpoolName, String mdFullMethodName, List<Map<String, Object>> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodName, args);
        }, dbpoolName);
    }

    /**
     * 根据mdFullMethodName指定的SQL执行插入操作，args为动态生成SQL语句的参数
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             为动态生成SQL语句的参数
     * @return 返回执行插入操作后插入记录的条数
     * @throws DbException
     */
    public static int insert(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodName, args);
        }, dbpoolName);
    }

    /**
     * 根据mdFullMethodName指定的SQL执行插入操作，args为动态生成SQL语句的参数，返回插入单条记录的自增主键id
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             为动态生成SQL语句的参数
     * @return 返回插入单条记录的自增主键id
     * @throws DbException
     */
    public static long insertReturnKey(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insertReturnKey(mdFullMethodName, args);
        }, dbpoolName);
    }

    /**
     * 根据多个md方法地址指定的SQL执行插入数据库操作，返回每个md方法执行后插入的记录条数
     *
     * @param dbpoolName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                          如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodNames md方法地址数组，每个元素存放一个md方法地址。mdFullMethodNames可以存放不同的md方法地址。
     * @return 每个md方法地址指定的SQL执行插入操作返回的记录条数存放于int数组相应位置
     * @throws DbException
     */
    public static int[] insert(String dbpoolName, ArrayList<String> mdFullMethodNames) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodNames);
        }, dbpoolName);
    }

    /**
     * 根据多个md方法地址指定的SQL执行插入数据库操作，返回每个md方法执行后插入的记录条数，每个md方法对应一个Map&lt;String, Object&gt;对象，用于传递参数。
     *
     * @param dbpoolName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                          如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodNames md方法地址数组，每个元素存放一个md方法地址。mdFullMethodNames可以存放不同的md方法地址。
     * @param args              mdFullMethodNames数组里每个md方法地址所定义的SQL，可以被提供一个Map&lt;String, Object&gt;对象，用于传递参数。
     * @return 每个md方法地址指定的SQL执行插入操作返回的记录条数存放于int数组相应位置
     * @throws DbException
     */
    public static int[] insert(String dbpoolName, String[] mdFullMethodNames, Map<String, Object>[] args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.insert(mdFullMethodNames, args);
        }, dbpoolName);
    }


    /**
     * 更新操作，一个md方法地址对应多个Map&lt;String, Object&gt;参数对象，从而同一个md方法可以执行多次，每次对应不同的Map&lt;String, Object&gt;对象（用于传达SQL使用的参数）。
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             多个Map&lt;String, Object&gt;对象，对应于每次执行的md方法
     * @return 返回每次执行md方法的SQL语句返回的条数
     * @throws DbException
     */
    public static int[] update(String dbpoolName, String mdFullMethodName, List<Map<String, Object>> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodName, args);
        }, dbpoolName);
    }

    /**
     * 更新操作，根据提供的多个md方法地址指定的SQL执行批量更新操作,每个md方法的SQL可以使用一个Map&lt;String, Object&gt;对象来传递参数
     *
     * @param dbpoolName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                          如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodNames 多个md方法地址
     * @param args              每个md方法的SQL可以使用一个Map&lt;String, Object&gt;对象来传递参数
     * @return 返回每次执行md方法的SQL语句返回的条数
     * @throws DbException
     */
    public static int[] update(String dbpoolName, String[] mdFullMethodNames, Map<String, Object>[] args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodNames, args);
        }, dbpoolName);
    }


    /**
     * 更新操作，根据提供的多个md方法地址指定的SQL执行批量更新操作
     *
     * @param dbpoolName        对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                          如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodNames 多个md方法地址
     * @return 返回每个md方法地址指定的SQL所更新的记录条数
     * @throws DbException
     */
    public static int[] update(String dbpoolName, ArrayList<String> mdFullMethodNames) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodNames);
        }, dbpoolName);
    }

    /**
     * 更新操作，根据md方法地址定义的SQL执行更新，可提供参数给SQL使用
     *
     * @param dbpoolName       对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                         如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param mdFullMethodName md方法地址
     * @param args             md方法用到的参数
     * @return md方法地址指定的SQL执行返回的条数
     * @throws DbException
     */
    public static int update(String dbpoolName, String mdFullMethodName, Map<String, Object> args) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.update(mdFullMethodName, args);
        }, dbpoolName);
    }

    /**
     * 根据接口映射到md文件，接口里定义的方法对应md方法，接口和md文件要放到同一目录，
     * 并且接口名称和md文件的名称相同（不含.md后缀）。
     *
     * @param dbpoolName    对应于dbpool.xml里的元素dbpool的name属性值,格式为：[配置xml文件名称]#[连接池名称]，
     *                      如果为：dbpool.xml#连接池名称，则dbpool.xml#可以省略。
     * @param interfaceType 接口名称
     * @param <T>
     * @return 返回根据接口生成的动态代理
     * @throws DbException
     */
    public static <T> T getMapper(String dbpoolName, Class<T> interfaceType) throws DbException {
        return mdbExecute(mdb -> {
            return mdb.getMapper(interfaceType);
        }, dbpoolName);
    }

}
