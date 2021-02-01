# ulwx-dbutils

 ulwx-dbutils是一个**轻量级的**、**强大的**数据库访问工具类。ulwx-dbutils的设计的初衷就是为了使事情更简单，你只需要30分钟就可以完全掌握ulwx-dbutils的所有用法。如果你不了解ulwx-dbutils，你可以把它想象成类似于mybatis，但ulwx-dbutils能覆盖所有mybatis的功能，但却比mybatis使用起来更简单和高效，你不需要在mapper xml里写烦人的xml，也不需要为保持命名空间的一致性而苦恼。甚至针对于单对象的增删改查操作，你不需要写任何sql语句，因为 ulwx-dbutils天然就支持对单对象增删改查操作方法。

ulwx-dbutils就跟它的名字一样，它是一个访问数据库的工具库，它专注于处理数据库的访问。掌握ulwx-dbutils你不需要了解任何其它技术，，你只需要了解SQL和基本的java语法，它特别适合喜欢轻量级集成方案的朋友。

ulwx-dbutils功能点如下：

- 单对象的增删改查不需要SQL，动态通过反射生成SQL语句。
- 天然支持分页查询，多种分页策略可供选择。
- 针对复杂的SQL语句，可为相应的dao方法编写对应md文件，md文件里编写SQL语句的规则非常简洁。同时md文件会转换为java文件并实时编译成java  class文件，所以可以在md文件访问任何java类、对象和方法，非常的强大。
- 提供对象查询的一对一和一对多映射。
- 天然支持事务管理机制，如事务传播，类似于spring。
- 你可以把它集成到spring、spingboot，这通过ulwx-dbutils-spring，或ulwx-dbutils-spring-starter。
- 支持多种数据库，如mysql、microsoft sql server、oracle、db2、h2、hsql、postgre、sybase、sqlite。
- 支持主从数据库，事务内的语句和更新的语句在主库上执行，非事务性查询语句在从库执行。
- 集成tomcat-jdbc连接池。
- 内置强大的生成java bean的工具。可以从数据库的表生成对应的java bean。
- 良好的日志输出，可以看到每条语句执行的时间，日志输出的SQL语句可以直接到数据库工具上执行，方便调试。
- 支持SQL脚本执行方法。

## 入门例子

### 添加maven依赖

		<dependency>
			<groupId>com.github.ulwx</groupId>
			<artifactId>ulwx-dbutils</artifactId>
			<version>1.0.0</version>
		</dependency>

### 在类路径下添加dbpool.xml

**//dbpool.xml**

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
    <table-name-rule>underline_to_camel</table-name-rule>     ②-1
    <!--
    全局设置的表字段与javaBean属性名的转换规则，有两种转换规则，分别如下：
     underline_to_camel：  下划线转驼峰
     normal：   为数据库表名和javabean属性一致
    -->
    <table-colum-rule>underline_to_camel</table-colum-rule>     ③-1
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

  <dbpool name="dbutils-demo"     ①
          type="tomcatdbpool"  
          table-name-rule="underline_to_camel"    ②
          table-colum-rule="underline_to_camel">  ③
    <property name="driverClassName">com.mysql.jdbc.Driver</property>
    <property name="url">jdbc:mysql://localhost:3306/dbutils_demo?useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=GMT%2B8&amp;useSSL=false</property>
    <property name="username">root</property>
    <property name="password">scj_123123</property>
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

由于数据库的表，对应的javaBean对象，自己手工编写非常麻烦，可以通过 ulwx-dbutils提供的工具方法SqlUtils.exportTables()来生成。

```java
public static void exportTables(
    String pool,  //连接池的名称，对应dbpool.xml里的 <dbpool>的 name属性
    String schema, //对应数据库的名称
    String toFolder, //javaBean对应的java文件生成到哪个文件夹里
    String toPackage, //生成的javaBean所属的java包名称，例如com.hithub.ulwx.demo.dao
    String remarkEncoding, //生成javaBean对应java文件的格式
    boolean propertyLowcaseFirstChar //是否属性的第一个字母小写
) 
```

举例如下：

```java
SqlUtils.exportTables("dbutils-demo", "dbutils_demo", "c:/dbutils_demo",
                                                                            "com.hithub.ulwx.demo.dao","utf-8",true);
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

**//Course.java**

```java
package com.hithub.ulwx.demo.domian;
import java.time.LocalDateTime;

/*********************************************
课程
***********************************************/
public class Course extends com.github.ulwx.database.MdbOptions implements java.io.Serializable {

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

生成的javaBean Course类继承了com.github.ulwx.database.MdbOptions类，用于对单对象查询的提供支持。

### 编写Dao类实现增删改查

下面是针对course表的数据访问类CourseDao，通过MDbUtils的相关方法实现了基本的增删改查逻辑。

**//CourseDao.java**

```java
package com.hithub.ulwx.demo.dao;
import com.github.ulwx.database.DbException;
import com.github.ulwx.tool.MD;
import com.github.ulwx.tool.MDbUtils;
import com.hithub.ulwx.demo.domian.Course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDao {
    public static String DbPoolName="dbutils-demo";
    
    public void delAll(){         ①-1 //对应md方法名
        MDbUtils.del(DbPoolName, MD.md(), null);    ①  //用到了CourseDao.md文件
    }
    public int addAndReturnKey(Course course) throws DbException {  
        return (int)MDbUtils.insertReturnKeyBy(DbPoolName, course);     ② //直接通过对象反射生成sql语句
    }
    public void update(Course course)throws  DbException{
        MDbUtils.updateBy(DbPoolName, course, "id");    ③//直接通过对象反射生成sql语句
    }
    public Course queryOne(String name, int classHours) throws DbException{
        Course course=new Course();
        course.setName(name);
        course.setClassHours(classHours);
        return MDbUtils.queryOneBy(DbPoolName, course);   ④//直接通过对象反射生成sql语句
    }

    public  List<Course> queryListFromMdFile(String name, int classHours) throws DbException{
        Map<String,Object>map=new HashMap<>();
        map.put("myName",name);
        map.put("myClassHours",classHours);
        return MDbUtils.queryList(DbPoolName, Course.class, MD.md(), map);  ⑤ //用到了CourseDao.md文件
    }
}

```

CourseDao的delAll()和queryListFromMdFile()方法用md文件指定SQL语句。**md文件要存放于和CourseDao同级目录下**（com.hithub.ulwx.demo.dao），即.md前面部分需与CourseDao类名一致，CourseDao.md内容如下：

**//CourseDao.md**

```sql
delAll    ①
====
delete from course   ②

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

> ulwx-dbutils在执行数据库操作时会把md文件实时转换成java类文件，并在内存里实时编译成class并加载到jvm。

下面针对CourseDao.java里标记的序号分别进行解释。

**①  MDbUtils.del(DbPoolName, MD.md(), null)**：删除操作

> MDbUtils.del()的第一个参数需传入一个数据源名称，表明是从哪个数据源执行删除操作。这里传入变量DbPoolName的值为"dbutils-demo"，它对应**dbpool.xml**（前面提到的）文件里的①处里的name属性值（<dbpool name="dbutils-demo">）。后面你会看到MDbUtils里的所有方法都得指定数据源。
>
> MDbUtils.del()的第二个参数指定**md方法地址**，**md方法地址**由**md文件地址**和**md方法名**组成，例如"com.hithub.ulwx.demo.dao.CourseDao.md:delAll"，其中com.hithub.ulwx.demo.dao.CourseDao.md为**md文件地址**，它包含md文件所在的完整包路径com.hithub.ulwx.demo.dao，其中CourseDao.md的文件名.md的前面部分要和数据访问类CourseDao的类名保持一致，这里为"CourseDao"。
>
> md文件的内容由多个**md方法**组成，每个**md方法**由两部分组成，即**md方法名**和**md方法体**，在=\=\==上方定义**md方法名**，在\=\=\=\=下方定义**md方法体**，md方法体里编写相应的SQL语句。如CourseDao.md里的定义了两个方法，分别为delAll方法和queryListFromMdFile方法。**md方法名必须和数据库访问类的方法名一致**，在本例中，CourseDao.md:delAll要和CourseDao.delAll()（CourseDao.java里①-1处）方法名一致，既都为delAll。在本例中在第二个参数处并没有显示传入md方法地址，而是通过MD.md()这是个工具方法，MD.md()内部会识别出哪个类的方法调用了MDbUtils.del()，这里识别出了CourseDao.delAll()方法调用了MDbUtils.del()，MD.md()会根据这些信息（CourseDao.delAll()和CourseDao所在的包）最终生成了**md方法地址**：com.hithub.ulwx.demo.dao.CourseDao.md:delAll。ulwx-dbutils会根据**md方法地址**定位到md文件里md方法体找到相应的SQL。
>
> MDbUtils.del()的第三个参数为Map<String, Object> 类型的参数，用于传递md方法体所需的参数，后面会介绍。这里传null，表明CourseDao.md:delAll方法体（CourseDao.md的②处）不需要参数。

**②  MDbUtils.insertReturnKeyBy(DbPoolName, course)** ：插入对象到数据表并返回自增id

MDbUtils.insertReturnKeyBy()方法的第一个参数为数据源名称，对应dbpool.xml里的①处（<dbpool name="dbutils-demo">），指定数据源表明是在哪个数据源上执行操作。

MDbUtils.insertReturnKeyBy()方法的第二个参数传入了一个对象course，ulwx-dbutils会根据对象生成insert语句，这里只会考虑course对象不为空的属性，例如：

```java
Course course1=new Course();
course1.setName("course1");
course1.setClassHours(11);
course1.setCreatime(LocalDateTime.now());
courseDao.add(course1);
```

上面的程序片段传入了course1对象，course1对象只在name、classHours、creatime这三个属性上赋值，id属性值为空，则最终生成的SQL为：

```sqlite
insert into course (name,creatime,class_hours) values('course1','2021-01-30 23:25:41',11)
```

可以看出生成的SQL语句里并没有id属性，并且插入的表名course根据对象course1的类名Course转换而成，这里具体转换规则由dbpool.xml里的<dbpool>元素的table-name-rule属性决定（dbpool.xml里的②处）。如果<dbpool>没有指定table-name-rule属性，则由<setting>里的子元素 <table-name-rule>决定。可以看到<dbpool name="dbutils-demo">的table-name-rule属性指定为**underline_to_camel**，这表明数据库表名与javaBean类名称为下划线转驼峰，如hello_world -->HelloWorld。table-name-rule属性还可以指定其它几种转换规则：first_letter_upcase、normal，你可以在<setting>里找到它们的解释。

MDbUtils.insertReturnKeyBy()方法返回自增id，您可以使用MDbUtils.insert()方法，此方法不返回自增id，而是返回插入记录的条数，失败返回-1，方法具体签名如下：

```
 public static <T> int insertBy(String pollName, T insertObject) 
```

MDbUtils.insertReturnKeyBy()和 MDbUtils.insertBy()这两个方法名都是以By为后缀，所有**MDbUtils.XXXBy()**模式的方法都是**对象操作方法**，**对象操作方法**不需要传入md文件方法地址，MDbUtils.XXXBy()方法会根据传入的对象反射生成SQL语句，这些方法都非常的方便，让你不用编写任何SQL语句。对于单对象的增删改查操作，MDbUtils里都有其对应的XXXBy()方法。

③  **MDbUtils.updateBy(DbPoolName, course, "id")**：通过对象来更新表记录。

MDbUtils.updateBy()方法的第一个参数为数据源名称。第二个参数为course对象，这表明要更新course对象所属类（Course）对应到的表（course）里的记录。course对象的类名（Course）到表（course）的转换规则，根据上面②里介绍的为 **underline_to_camel**。MDbUtils.updateBy()的第三个参数传入的对象的属性组成了update的where条件，可以传入对象的多个属性，以英文逗号分隔，多个属性组成的where里的条件是and关系。

```java
Course courseForUpdate=new Course();
courseForUpdate.setId(24);
courseForUpdate.setName("course33");
courseDao.update(courseForUpdate);
```

上面的程序片段生成的SQL语句为：

```
update course  set name='course33' where id=24
```

可以看出，由于第三个参数传入了courseForUpdate对象的"id"，表明要根据id查询，既组成了"where id=24"，courseForUpdate对象剩余的不为空的属性组成了update的set部分。需要特别说明的是MDbUtils.updateBy()第三个参数传入的为对象的属性名称，不能为对应的表（course）字段的名称，虽然这里它们是一样的，ulwx-dbutils会在生成SQL语句时把对象的属性名称转换为对应表的字段，转换的规则根据dbpool.xml里的<dbpool name="dbutils-demo">的属性 table-colum-rule，这里是underline_to_camel，既下划线转驼峰，还有其它规则，解释见dbpool.xml里的<setting>元素下的子元素<table-colum-rule>。

**④ MDbUtils.queryOneBy(DbPoolName, course)** ：根据对象里的信息查询一条记录并填充到返回的对象。

MDbUtils.queryOneBy()方法由于以By结尾，可知它不需要传入md方法地址来从外部获取SQL，此方法会根据传入的对象反射生成SQL查询语句。方法的第一个参数为数据源名称，第二个参数为传入的对象。

```java
Course course=new Course();
course.setName("course33");
course.setClassHours(13);
Course course= MDbUtils.queryOneBy(DbPoolName, course);
```

上面的程序片段生成一个Course对象，并只对name和classHours属性赋值，其它属性默认为null。生成的SQL语句如下：

```sql
select * from course  where name='course33' and class_hours=13 
```

MDbUtils.queryOneBy()方法根据不为null的属性生成select语句的where条件部分，属性构成的条件为and关系。































