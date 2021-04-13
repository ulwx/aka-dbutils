##  为什么选择aka-dbutils?

aka-dbutils是一个**轻量级的**、**强大的**数据库访问工具类。aka-dbutils的设计的初衷就是为了使事情更简单，你只需要30分钟就可以完全掌握aka-dbutils的所有用法。如果你不了解aka-dbutils，你可以把它想象成类似于mybatis，但aka-dbutils能覆盖所有mybatis的功能，但比mybatis更强大，并使用起来更简单和高效，你不需要在mapper xml里写烦人的xml，也不需要为保持命名空间的一致性而苦恼。aka-dbutils提供了大量的对象操作方法，针对这些方法，你不需要编写和传递任何sql语句，因为 aka-dbutils会帮你生成，这些对象操作方法有点类似于hibernate，但aka-dbutils使用起来更简单，它不会要求你在类上使用任何注解。

aka-dbutils就跟它的名字一样，它是一个访问数据库的工具库，它专注于处理数据库的访问。掌握aka-dbutils你不需要了解任何其它技术，，你只需要了解SQL和基本的java语法，它特别适合那些喜欢用一个很简单的访问数据库工具类库来进行数据库操作的朋友。你要明白，aka-dbutils虽然是一个工具类库，易于使用，但它很强大，同时它非常的稳定和可靠，它经历过无数项目的考验，不管从企业应用，互联网项目，还是金融项目，它都工作的很好。

aka-dbutils功能如下：

- 单对象的增删改查不需要SQL，动态通过反射生成SQL语句。
- 天然支持分页查询，多种分页策略可供选择，支持的数据库有

  MYSQL，MS_SQL_SERVER，ORACLE，DB2，H2，HSQL，POSTGRE，SYBASE，SQLITE，INFOMIX，DERBY

- 针对复杂的SQL语句，可为相应的dao方法编写对应md文件，md文件里编写SQL语句的规则非常简洁。同时md文件会转换为java文件并实时编译成java  class文件，所以可以在md文件访问任何java类、对象和方法，非常的强大。
- 提供对象查询的一对一、一对多关联映射。
- 支持数据库事务和事务传播，事务传播类似于spring的事务传播机制。
- 你可以把它集成到spring、spingboot，这通过aka-dbutils-spring，或aka-dbutils-spring-starter。
- 支持多种数据库，如mysql、microsoft sql server、oracle、db2、h2、hsql、postgre、sybase、sqlite。
- 支持主从数据库，事务内的语句和更新的语句在主库上执行，非事务性查询语句在从库执行。
- 集成tomcat-jdbc连接池。
- 支持savepoint操作方法，从而支持局部回滚
- 内置强大的生成java bean的工具。可以从数据库的表生成对应的java bean。
- 良好的日志输出，可以看到每条语句执行的时间，日志输出的SQL语句可以直接到数据库工具上执行，方便调试。
- 支持执行SQL脚本的方法。
- 支持设置拦截器，从而可以拦截数据库操作的执行，你可以取消当前的操作。
- 支持设置监听器，从而可以实时监听执行的SQL语句。

aka-dbutils会具有如下优势：

- 数百个测试用例用来保证aka-dbutils的稳定可靠。
- 经过无数项目的考验，具有12年的发展历程，借鉴了众多开源软件的思想。
- aka-dbutils所有新增功能都必须向前兼容，从而aka-dbutils老版本到新版本升级不会有太多压力。
- aka-dbutils会终身得到维护，如出现bug，我们会第一时间来免费支持。

## 入门例子

入门例子工程名为[aka-dbutils-demo](https://github.com/ulwx/aka-dbutils-demo)，你可以在[https://github.com/ulwx/aka-dbutils-demo](https://github.com/ulwx/aka-dbutils-demo)下载。你需要准备一个mysql数据库，并且需要在src/db目录下找到dbutils_demo.sql文件，并通过mysql数据库客户端工具（如SQLyog)执行从而生成dbutils_demo数据库。

### pom.xml里添加maven依赖

```xml
...
<dependency>
  <groupId>com.github.ulwx</groupId>
  <artifactId>aka-dbutils</artifactId>
  <version>最新版本</version>
</dependency>
...
```

### 在类路径下添加dbpool.xml

**示例—dbpool.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<dbpool-config>
  <setting>
    <!--
     全局设置的数据库表名与javaBean类名的转换规则，有三种转换规则，分别如下：
     underline_to_camel：  表示下划线转驼峰，
     normal：  为数据库表名和javaBean类名一致
     first_letter_upcase： 为数据库表名转为javaBean类名时第一个字母大写
    -->
    <table-name-rule>underline_to_camel</table-name-rule>    <!--  ②-1   -->
    <!--
    全局设置的表字段与javaBean属性名的转换规则，有两种转换规则，分别如下：
     underline_to_camel：  下划线转驼峰
     normal：   为数据库表名和javabean属性一致
    -->
    <table-colum-rule>underline_to_camel</table-colum-rule>    <!--   ③-1   -->
  </setting>
  <!--
  每个<dbpool>元素配置对应一个数据库连接池
  type属性：现在只能指定tomcatdbpool，表示是使用的tomcat jdbc数据库连接池
  table-name-rule属性：指定本数据源对应数据库的表名到javaBean类名的转换规则，覆盖setting里的全局规则
  table-colum-rule属性：指定本数据源对应数据库里表字段与javaBean属性名的转换规则，覆盖setting里的全局规则
  driverClassName：数据库的驱动名称
  url： 连接字符串
  username：用户名
  password：用户密码,如果encrypt=1,则password值是通过aes算法加密后的，既通过DBPoolFactory.aesEncrypt(dbpassword)加密得到
  checkoutTimeout：当数据库满时，等待从连接池里取出connection的最长时间，毫秒为单位
  maxIdleTime ：最长空闲时间，以秒为单位，多于连接池里可容纳的最小connection数（由minPoolSize指定）的连接如果超过此时间就会被释放
  maxPoolSize ：连接池可容纳连接connection最大数量
  minPoolSize： 连接池可容纳的的connection最小数量
  idleConnectionTestPeriod: 每次检测空闲连接的时间间隔（秒），如果检测时发现某空闲连接超过最长空闲时间（maxIdleTime）则清除，但连接池里的连接数会
  会保留到minPoolSize指定的数量。此参数以秒为单位。
  maxStatements：缓存的statement数
  encrypt : password属性是否加密，0：不加密 1：加密（采用的是aes加密，通过DBPoolFactory.aesEncrypt(dbpassword)加密)
  -->
  <dbpool name="dbutils-demo"                     <!--   ①  -->
          type="tomcatdbpool"  
          table-name-rule="underline_to_camel"   <!--   ②   -->
          table-colum-rule="underline_to_camel">   <!--  ③   -->
    <property name="driverClassName">com.mysql.jdbc.Driver</property>
    <property name="url">jdbc:mysql://localhost:3306/dbutils_demo?useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=GMT%2B8&amp;useSSL=false</property>
    <property name="username">root</property>
    <property name="password">abcd</property>
    <property name="encrypt">0</property><!--password属性是否加密，0：不加密 1：加密-->
    <property name="checkoutTimeout">60000</property>
    <property name="idleConnectionTestPeriod">30</property>
    <property name="maxIdleTime">60</property>
    <property name="maxPoolSize">30</property>
    <property name="minPoolSize">2</property>
    <property name="maxStatements">20</property>
  </dbpool>
</dbpool-config>
```

### 从数据库生成javaBean

由于数据库的表，对应的javaBean对象，自己手工编写非常麻烦，可以通过 aka-dbutils提供的工具方法SqlUtils.exportTables()来生成。exportTables()方法签名如下：

```java
public static void exportTables(
    String pool,  //连接池的名称，对应dbpool.xml里的 <dbpool>的 name属性
    String schema, //对应数据库的名称
    String toFolder, //javaBean对应的java文件生成到哪个文件夹里
    String toPackage, //生成的javaBean所属的java包名称，例如com.github.ulwx.aka.dbutils.demo.dao
    String remarkEncoding, //生成javaBean对应java文件的格式
    boolean propertyLowcaseFirstChar //是否属性的第一个字母小写
) 
```

**举例如下**：

```java
SqlUtils.exportTables("dbutils-demo", "dbutils_demo", "c:/dbutils_demo",
                                                                            "com.github.ulwx.aka.dbutils.demo.dao","utf-8",true);
```

假设数据库里存在course表（假设为mysql）

```sql
CREATE TABLE `course` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '课程id',
  `name` varchar(20) DEFAULT '' COMMENT '课程名称',
  `class_hours` int(11) DEFAULT '0' COMMENT '学时',
  `creatime` datetime DEFAULT NULL COMMENT '建立时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='课程'
```

生成的javaBean文件内容如下：

**示例—Course.java**

```java
package com.github.ulwx.aka.dbutils.demo.domian;
import java.time.LocalDateTime;

/*********************************************
课程
***********************************************/
public class Course extends com.github.ulwx.aka.dbutils.database.MdbOptions implements java.io.Serializable {

	private Integer id;/*课程id;len:10*/
	private String name;/*课程名称;len:20*/
	private Integer classHours;/*学时;len:10*/
	private LocalDateTime creatime;/*建立时间;len:19*/
	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return id;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void setClassHours(Integer classHours){
		this.classHours = classHours;
	}
	public Integer getClassHours(){
		return classHours;
	}
	public void setCreatime(LocalDateTime creatime){
		this.creatime = creatime;
	}
	public LocalDateTime getCreatime(){
		return creatime;
	}
	private static final long serialVersionUID =408811385L;

}
```

生成的javaBean Course类继承了com.github.ulwx.aka.dbutils.database.MdbOptions类，用于对单对象查询的提供支持。

### 编写Dao类实现增删改查

下面是针对course表的数据访问类CourseDao，通过MDbUtils的相关方法实现了基本的增删改查逻辑。

**示例—CourseDao.java**

```java
package com.github.ulwx.aka.dbutils.demo.dao;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.demo.domian.Course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDao {
    public static String DbPoolName="dbutils-demo";
    
    public void delAll(){         //①-1  对应md方法名
        MDbUtils.del(DbPoolName, MD.md(), null);    // ①  用到了CourseDao.md文件
    }
    public int addAndReturnKey(Course course)  {  
        return (int)MDbUtils.insertReturnKeyBy(DbPoolName, course);    // ②  直接通过对象反射生成sql语句
    }
    public void update(Course course)throws  DbException{
         MDbUtils.updateBy(DbPoolName, course, MD.of( course::getId)); // ③  直接通过对象反射生成sql语句
        //MDbUtils.updateBy(DbPoolName, course, MD.of( "id"));    // ③-1  和 ③处的等效
    }
    public Course queryOne(String name, int classHours) {
        Course course=new Course();
        course.setName(name);
        course.setClassHours(classHours);
        return MDbUtils.queryOneBy(DbPoolName, course);  // ④  直接通过对象反射生成sql语句
    }

    public  List<Course> queryListFromMdFile(String name, int classHours) {
        Map<String,Object>map=new HashMap<>();
        map.put("myName",name);
        map.put("myClassHours",classHours);
        return MDbUtils.queryList(DbPoolName, Course.class, MD.md(), map);  // ⑤  用到了CourseDao.md文件
    }
}

```

CourseDao的delAll()和queryListFromMdFile()方法需要在md文件里编写SQL语句。md文件要存放在和CourseDao同级目录下（即com.github.ulwx.aka.dbutils.demo.dao），md文件的名称的.md前面部分需与CourseDao类名一致。下面是上面示例⑤处使用的CourseDao.md文件，它是通过MD.md()方法生成的md文件地址来引用，MD.md()返回的为：com.github.ulwx.aka.dbutils.demo.dao.CourseDao.md

**示例—CourseDao.md：**

```sql
delAll    
====
delete from course   

queryListFromMdFile    
====
select * from course where 1=1  
@if( $$:myName ){
 and name like #{myName%}
@}
@if( (Integer)$$.myClassHours > 0 ){
 and class_hours > #{myClassHours}
@}
```

> aka-dbutils在执行数据库操作时会把md文件实时转换成java类，并在内存里实时编译成class并加载到jvm。

如果你想进一步学习和了解aka-dbutils，请查看我们为你准备的《[aka-dbutils详解](https://ulwx.github.io/aka-dbutils/index.html)》，你甚至可以下载[PDF版本](https://github.com/ulwx/aka-dbutils/blob/master/docs/content.pdf)