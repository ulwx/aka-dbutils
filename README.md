aka-dbutils是一个**轻量级的**、**强大的**数据库访问工具类。aka-dbutils的设计的初衷就是为了使事情更简单，你只需要30分钟就可以完全掌握aka-dbutils的所有用法。如果你不了解aka-dbutils，你可以把它想象成类似于mybatis，但aka-dbutils能覆盖所有mybatis的功能，但却比mybatis使用起来更简单和高效，你不需要在mapper xml里写烦人的xml，也不需要为保持命名空间的一致性而苦恼。甚至针对于单对象的增删改查操作，你不需要写任何sql语句，因为 aka-dbutils天然就支持对单对象增删改查操作方法。

aka-dbutils就跟它的名字一样，它是一个访问数据库的工具库，它专注于处理数据库的访问。掌握aka-dbutils你不需要了解任何其它技术，，你只需要了解SQL和基本的java语法，它特别适合喜欢轻量级集成方案的朋友。

aka-dbutils功能点如下：

- 单对象的增删改查不需要SQL，动态通过反射生成SQL语句。
- 天然支持分页查询，多种分页策略可供选择。
- 针对复杂的SQL语句，可为相应的dao方法编写对应md文件，md文件里编写SQL语句的规则非常简洁。同时md文件会转换为java文件并实时编译成java  class文件，所以可以在md文件访问任何java类、对象和方法，非常的强大。
- 提供对象查询的一对一和一对多映射。
- 天然支持事务管理机制，如事务传播，类似于spring。
- 你可以把它集成到spring、spingboot，这通过aka-dbutils-spring，或aka-dbutils-spring-starter。
- 支持多种数据库，如mysql、microsoft sql server、oracle、db2、h2、hsql、postgre、sybase、sqlite。
- 支持主从数据库，事务内的语句和更新的语句在主库上执行，非事务性查询语句在从库执行。
- 集成tomcat-jdbc连接池。
- 内置强大的生成java bean的工具。可以从数据库的表生成对应的java bean。
- 良好的日志输出，可以看到每条语句执行的时间，日志输出的SQL语句可以直接到数据库工具上执行，方便调试。
- 支持SQL脚本执行方法。
- 后续将会支持分布式事务和分库分表分区功能

## 入门例子

### 添加maven依赖

```xml
	<dependency>
		<groupId>com.github.ulwx</groupId>
		<artifactId>aka-dbutils</artifactId>
		<version>1.0.0</version>
	</dependency>
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

CourseDao的delAll()和queryListFromMdFile()方法需要在md文件里编写SQL语句。md文件要存放于和CourseDao同级目录下（com.github.ulwx.aka.dbutils.demo.dao），md文件的名称的.md前面部分需与CourseDao类名一致。

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

下面针对上面【示例—CourseDao.java】里标记序号分别进行解释。

**①  MDbUtils.del(DbPoolName, MD.md(), null)**：删除操作，需要使用md文件，对应的SQL语句存放在md文件里。

> MDbUtils#del()的第一个参数需传入一个数据源名称，表明从哪个数据源执行删除操作。这里传入变量DbPoolName的值为"dbutils-demo"，它对应**dbpool.xml**（前面提到的）文件里的①处里的name属性值（<dbpool name="dbutils-demo">）。后面你会看到MDbUtils里的所有方法都得指定数据源。
>
> 本例的MDbUtils#del()方法需要用到md文件，并在里面编写SQL语句，你需要在调用MDbUtils#del()方法的CourseDao的同级包下，定义一个CourseDao.md文件，在里面编写如下内容：
>
> ```sql
> delAll    
> ====
> delete from course   
> ```
>
> 上面md文件里编写的内容通过====分隔开，=\=\=\=的上方指定了调用MDbUtils#del()所在的Dao方法（CourseDao#dellAll）的名称delAll（称为md方法名），下方指定了SQL语句（称作md方法体）。md方法名、\=\=\=\=和md方法体三者组成了md方法。aka-dbutils在运行时会把整个md文件在内存中转换成一个java类，并在内存中编译成class并加载到jvm，每个md方法会对应到这个java类的方法，如本例中转化的java类如下：
>
> ```java
> public class CourseDaoMd {
> 	public static String delAll(Map<String, Object> args)throws Exception{
> 		String retString="";
>      	MDMehtodOptions options = new MDMehtodOptions();
> 		options.setSource(trimRight("CourseDaoMd",2)+".md:delAll");
> 		retString=retString+" delete from course";
> 		return retString;
> 	}
> 	public static String queryListFromMdFile(Map<String, Object> args)throws Exception{
> 		String retString="";
>      	MDMehtodOptions options = new MDMehtodOptions();
> 		options.setSource(trimRight("CourseDaoMd",2)+".md:queryListFromMdFile");
> 		retString=retString+" select * from course where 1=1";
> 		if(  NFunction.isNotEmpty(args.get("myName"))  ){
> 			retString=retString+" and name like #{myName%}";
> 		}
> 		if( (Integer)args.get("myClassHours")  > 0 ){
> 			retString=retString+" and class_hours > #{myClassHours}";
> 		}
> 		return retString;
> 	}
> }
> ```
>
> 转化的java类的类名为CourseDaoMd，即CourseDao加Md后缀，在其内部的delAll()方法对应到上述的md方法dellAll，可以看出CourseDaoMd类里的每个方法签名里含有一个Map<String, Object>类型的args参数，它其实是MDbUtils#del()方法最后一个参数的引用，用于生成SQL的逻辑。
>
> MDbUtils#del()的第二个参数使用了Md.md()工具方法，它会生成"com.github.ulwx.aka.dbutils.demo.dao.CourseDao.md:delAll"，称为**md方法地址**，aka-dbutils会把它转换成CourseDaoMd#delAll()的方法调用，最终得到方法执行返回的SQL语句。
>
> MDbUtils#del()的第三个参数为Map<String, Object> 类型的参数，用于传递md方法体所需的参数，这里传null，表明CourseDao.md:delAll方法体不需要参数。

**②  MDbUtils.insertReturnKeyBy(DbPoolName, course)** ：插入对象到数据表并返回自增id

> MDbUtils#insertReturnKeyBy()方法的第一个参数为数据源名称，对应【示例—dbpool.xml】里的①处（<dbpool name="dbutils-demo">），指定在哪个数据源上执行操作。
>
> MDbUtils#insertReturnKeyBy()方法的第二个参数传入了一个对象course，aka-dbutils会根据对象生成insert语句，这里只会考虑对象不为空的属性，为null的属性会忽略掉从而不会作为生成SQL语句的部分。例如：
>
> ```java
> Course course1=new Course();
> course1.setName("course1");
> course1.setClassHours(11);
> course1.setCreatime(LocalDateTime.now());
> courseDao.add(course1);
> ```
>
> 上面的程序片段传入了course1对象，course1对象只在name、classHours、creatime这三个属性上被赋了非空的值，而id属性值默认为空，所以生成SQL的时候不会考虑，最终生成的SQL如下：
> `insert into course (name,creatime,class_hours) values('course1','2021-01-30 23:25:41',11)`
>
> 可以看出生成的SQL语句里并没有包含id属性对应的表id字段。insert语句的表名course是根据对象course1的类型Course转换而成，具体转换规则由dbpool.xml里的<dbpool>元素的table-name-rule属性值决定（【示例—dbpool.xml】里的②处）。如果<dbpool>没有指定table-name-rule属性，则由<setting>里的子元素 <table-name-rule>决定。本例中<dbpool>的table-name-rule属性值指定为**underline_to_camel**，表示数据库表名与javaBean类名映射为下划线转驼峰，如：hello_world —>HelloWorld。table-name-rule属性还可以指定其它几种转换规则：first_letter_upcase、normal，你可以在<setting>里找到它们的解释。insert语句的字段名是根据对象course1的属性转换二来，具体转换规则由dbpool.xml里的<dbpool>元素的 table-colum-rule属性值决定（dbpool.xml里的②处）。如果<dbpool>没有指定table-colum-rule属性，则由<setting>里的子元素 <table-colum-rule>决定。本例中<dbpool>的 table-colum-rule属性值指定为**underline_to_camel**，表示表字段与javaBean对象的属性映射为下划线转驼峰，如：class_hours—>classHours。还有其它规则，解释见dbpool.xml里的<setting>元素下的子元素<table-colum-rule>
>
> MDbUtils#insertReturnKeyBy()方法返回自增id，您也可以使用不返回自增id的MDbUtils#insertBy()方法，它返回成功插入记录的条数（为1），失败返回-1，方法具体签名如下：
>
> ```
> public static <T> int insertBy(String pollName, T insertObject) 
> ```
>
> MDbUtils#insertReturnKeyBy()和 MDbUtils#insertBy()这两个方法名都是以By为后缀，所有**MDbUtils.XXXBy()**模式的方法都称作**对象操作方法**，**对象操作方法**不需要在md文件里编写SQL语句，这些方法会根据传入的对象反射生成SQL语句，这非常的方便，一般一个项目很大一部分操作都是简单的对象增伤改查操作。MDbUtils里的XXXBy()方法给你提供了极大的便利。

③  **MDbUtils.updateBy(DbPoolName, course, MD.of( course::getId))**：通过对象来更新表记录。

> MDbUtils#updateBy()方法的第一个参数为数据源名称。第二个参数为course对象，它的非空属性将会更新到对应表（course）的记录中，更新哪些记录的查找条件是根据第三个参数所指定的属性来决定。course对象类名到表名的映射规则是根据②里介绍的规则，我们知道它为**underline_to_camel**，即根据course对象类名（Course）通过**underline_to_camel**规则转换成对应的表（course）。第三个参数是个数组类型，它传入的属性构成了update的where条件部分，可以传入对象的多个属性，他们是组成的条件是and关系。MD.of()是一个工具类，方便你去传入多个属性，有两种传入属性的形式，你可以通过方法引用的形式（如：course::getId）传递属性，也可以通过传递属性名称的形式，例如MD.of( "id")，指定了id属性，MD.of()最终会转换成一个数组。下面的程序片段展示了其用法：
>
> ```java
> package com.github.ulwx.aka.dbutils.demo.dao;
> ......
> public class CourseDao {
> public static String DbPoolName="dbutils-demo";
> ......
> public void update(Course course)throws  DbException{
>   MDbUtils.updateBy(DbPoolName, course, MD.of( course::getId));//③ 直接通过对象反射生成sql语句
>   //MDbUtils.updateBy(DbPoolName, course, MD.of( "id")); //另一种形式，和上面运行结果等效
> }
> ......
> }
> 
> ```
>
> 由于③处的第三个参数传入了id属性（course::getId），这表示要根据id查询记录并进行更新，它组成了"where id=24"部分。
>
> ```java
> Course courseForUpdate=new Course();
> courseForUpdate.setId(24);
> courseForUpdate.setName("course33");
> courseDao.update(courseForUpdate);
> ```
>
> CourseDao#update()传入了courseForUpdate对象，courseForUpdate对象不为空的属性（要去除组成where条件部分的属性，这里时id属性）组成了update的set部分。上面的程序片段生成的SQL语句为：
> `update course  set name='course33' where id=24`
>
> 需要强调的是MDbUtils#updateBy()第三个参数如果传入字符串数组的形式，则数组里的每个元素字符串必须对应的是对象的java属性名，而不是对应表（course）字段的名称，虽然在本例中它们是一样的。MDbUtils#updateBy()方法最终生成的update语句里的表名到javaBean对象类名、字段名到javaBean属性名的映射转换规则在②中已经介绍。

**④ MDbUtils.queryOneBy(DbPoolName, course)** ：根据对象里的非空属性作为查询条件查询一条记录并填充到一个对象并返回。

> MDbUtils#queryOneBy()方法由于以By结尾，它不需要使用md文件来编写SQL，而是它会根据传入的对象通过反射生成SQL查询语句。方法的第一个参数为数据源名称，第二个参数为传入的对象。
>
> ```java
> Course course=new Course();
> course.setName("course33");
> course.setClassHours(13);
> Course course= MDbUtils.queryOneBy(DbPoolName, course);
> ```
>
> 上面的程序片段首先new一个Course对象，并只对name和classHours属性赋值，其它属性默认为null，aka-dbutils会把非null属性构成where查询条件，最终生成的SQL语句如下：
>
> ```sql
> select * from course  where name='course33' and class_hours=13 
> ```
>
> 各个非null属性构成的where条件中各个条件为and关系。

**⑤ MDbUtils.queryList(DbPoolName, Course.class, MD.md(), map)**：根据md文件里的SQL语句进行查询，并返回对象列表。

> 方法的第二个参数为表记录映射到的对象的类型，aka-dbutils当从数据库查询的记录后，会为每条记录根据传入的类型生成一个对象并填充记录到对象里，最终返回一个对象列表。
>
> 展示MDbUtils#queryList()使用，如例1-1所示
>
> **【例1】展示MDbUtils#queryList()使用**
>
> ```java
> //CourseDao.java
> package com.github.ulwx.aka.dbutils.demo.dao;
> ......
> public class CourseDao {
> public static String DbPoolName="dbutils-demo";
> ......
> public  List<Course> queryListFromMdFile(String name, int classHours) {
>   Map<String,Object>map=new HashMap<>();
>   map.put("myName",name);
>   map.put("myClassHours",classHours);
>   return MDbUtils.queryList(DbPoolName, Course.class, MD.md(), map);    // ①
> }
> ......
> }
> //CourseService.java
> package com.github.ulwx.aka.dbutils.demo.service;
> ......
> public class CourseService {
> private CourseDao courseDao=new CourseDao();
> public void testBasicUsage(){
>   ......
>   List<Course> list= courseDao.queryListFromMdFile("course", 10);
>   System.out.println(ObjectUtils.toString(list));
> }
> 
> public static void main(String[] args) throws Exception{
>   CourseService courseService=new CourseService();
>   courseService.testBasicUsage();
> }
> 
> ```
>
> CourseDao#queryListFromMdFile()里调用了MDbUtils#queryList()方法，传入的MD.md()生成了md方法地址com.github.ulwx.aka.dbutils.demo.dao.CourseDao.md:queryListFromMdFile。MDbUtils#queryList()的第三个参数传入了一个Map对象，可以在Map对象里添加参数（称作**Map参数**），这些参数会在md方法（这里是queryListFromMdFile）里动态拼装SQL语句时用到，md方法queryListFromMdFile的内容，如【例1】所示：
>
> **【例2】md方法queryListFromMdFile内容**
>
> ```sql
> queryListFromMdFile    
> ====
> select * from course where 1=1  
> @if( $$:myName ){
> and name like #{myName%}
> @}
> @if( (Integer)$$.myClassHours > 0 ){
> and class_hours > #{myClassHours}
> @}
> ```
>
> 上面的md方法的方法体使用了动态拼装SQL的技术，所有@前缀的行都是java代码，里面使用了一个特殊的$$符号，代表传入的Map对象本身，它代表下方的【例3】里①处的args，\$\$.myClassHours等同于args.get("myClassHours") ，即获取Map对象里的**Map参数**myClassHours的值 ，而\$\$:myName 等同于NFunction.isNotEmpty(args.get("myName")) ，即·对Map参数myName进行了非空判断。在非@前缀的部分为SQL语句，在SQL语句里含有#{XXX}的语法，XXX为Map参数名称，#{XXX}本质上是占位符，类似于mybatis，aka-dbutils会对SQL语句含有#{XXX}的部分进行处理从而替换成?，从而通过jdbc的PreprareStatement进行预处理，防止注入式攻击。#{XXX}可以支持%，如name like #{myName%}，表示Map参数myName值的前缀匹配。#{XXX}中XXX参数可以为数组，如：roles in(#{roles})，aka-dbutils在对SQL语句处理时会判断参数是否为数组，从而替换成形如 roles in(3,4,5,6)的形式。
>
> md方法queryListFromMdFile最终转换成的java类CourseDaoMd#queryListFromMdFile()方法，如【例3】所示：
>
> **【例3】CourseDaoMd#queryListFromMdFile()源代码**
>
> ```java
> public class CourseDaoMd {
> 	......
> 	public static String queryListFromMdFile(Map<String, Object> args)  // ①
>           throws Exception{
> 		String retString="";
> 		MDMehtodOptions options = new MDMehtodOptions();
> 		options.setSource(trimRight("CourseDaoMd",2)+".md:queryListFromMdFile");
> 		retString=retString+" select * from course where 1=1";
> 		if(  NFunction.isNotEmpty(args.get("myName"))  ){
> 			retString=retString+" and name like #{myName%}";
> 		}
> 		if( (Integer)args.get("myClassHours")  > 0 ){
> 			retString=retString+" and class_hours > #{myClassHours}";
> 		}
> 		return retString;
> 	}
> }
> 
> ```
>
> 上面的转换后的java类CourseDaoMd的每个方法为static的，而且每个方法都含有Map<String, Object> args参数，它由外部调用传递过来，如【例1】的①处。
>
> 可以看出aka-dbutils的md文件的本质是把程序员以往在java代码里动态拼装SQL语句的逻辑搬移到md文件里，但却变得更精简高效并可以配置，由于最终是java代码执行SQL语句的拼装，不像mybatis通过xml去解析，所以会更高效。

至此你已经学会了aka-dbutils的常用的用法，下一步你将通过《aka-dbutils详解》会了解更多关于aka-dbutils的稍微高级一点的用法，如：分页查询，批量更新，一对一以及一对多关联映射，脚本执行，事务管理及传播，主从用法，md文件的详细语法，以及与Spring框架集成。