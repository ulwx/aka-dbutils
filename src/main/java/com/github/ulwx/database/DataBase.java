package com.github.ulwx.database;

import com.github.ulwx.tool.PageBean;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DataBase {

	public static enum SQLType {
		INSERT, UPDATE, SELECT, STORE_DPROCEDURE
	}
	public static class DataBaseType {
		public static String MYSQL = "mysql";
		public static String MS_SQL_SERVER = "microsoft sql server";
		public static String MS_SQL_SERVER_2005 = "microsoft sql server 2005";
		public static String ORACLE = "oracle";
		public static String DB2 = "db2";
		public static String H2 = "h2";
		public static String HSQL = "hsql";
		public static String POSTGRE = "postgre";
		public static String SYBASE = "sybase";
		public static String SQLITE = "sqlite";
		public static String OTHER = "other";
	}
	public static enum MainSlaveModeConnectMode {
		Try_Connect_MainServer, // //
								// 如果是主从模式，并且是非事务性操作，尝试获取主库连接，如果发现执行的sql语句是select则从库连接
		Connect_MainServer, // 如果是主从模式，并且是非事务性操作,如果是这种模式，则只去获取主库链接
		Connect_SlaveServer // 如果是主从模式，并且是非事务性操作，如果是这种模式，则只去获取从库链接
	}
	public MainSlaveModeConnectMode getMainSlaveModeConnectMode();
	boolean isAutoReconnect();

	void setAutoReconnect(boolean autoReconnect) throws DbException;

	String getDbPoolName();

	boolean isMainSlaveMode();

	void setMainSlaveMode(boolean mainSlaveMode);

	boolean getInternalConnectionAutoCommit() throws DbException;

	/**
	 * 如果数据库是主从式模式，则语句为查询语句并且是非事务性的时候，则选择从库查询
	 */
	void selectSlaveDb() throws DbException;

	String getDataBaseType();

	default void connectDb(DataSource dataSource){
	 };

	void connectDb(String dbPoolName) throws DbException;
	 DataBaseImpl.ConnectType getConnectionType() ;

	default  void connectDb(Connection connection,boolean externalControlConClose){
	}
	default  boolean isExternalControlConClose(){
		return false;
	}
	/**
	 * 从dbpool.xml里设置的连接池获得连接
	 * 
	 * @param dbPoolName 对应于dbpool.xml里的元素dbpool的name属性值
	 * @throws DbException
	 */
	void connectDb(String dbPoolName, MainSlaveModeConnectMode mainSlaveModeConnectMode) throws DbException;

	/**
	 * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭
	 * 
	 * @param sqlQuery    sql语句
	 * @param vParameters 参数
	 * @return 返回查询到的结果集
	 * @throws DbException
	 */
	DataBaseSet doCachedQuery(String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

	/**
	 * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
	 * 
	 * @param sqlQuery
	 * @param vParameters
	 * @param page        当前页
	 * @param perPage     每页多少行
	 * @param pageUtils   返回的分页信息
	 * @param countSql    查询总数的sql语句，根据它查询总数；也可以指定一个整数字符串，用于指定总数；
	 *                    如果指定null或""字符串，那么系统会根据sqlQuery自动生成查询总数sql语句
	 * @return
	 * @throws DbException
	 */
	DataBaseSet doCachedPageQuery(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException;

	DataBaseSet query(String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage, PageBean pageUtils,
			String countSql) throws DbException;

	/**
	 * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
	 * 
	 * @param <T>       需映射的类
	 * @param sqlQuery  sql查询语句
	 * @param args      sql查询语句里的参数
	 * @param page      当前页，从第一页开始
	 * @param perPage   每页多少行
	 * @param pageUtils 返回分页信息
	 * @param rowMapper 映射接口，用户可以通过此接口的回调函数来执行映射
	 * @param countSql  查询总数的sql语句，根据它查询总数；也可以指定一个整数字符串，用于指定总数；
	 *                  如果指定null或""字符串，那么系统会根据sqlQuery自动生成查询总数sql语句
	 * @return
	 * @throws DbException
	 */
	<T> List<T> doPageQueryObject(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageUtils,
			RowMapper<T> rowMapper, String countSql) throws DbException;

	<T> List<T> query(String sqlQuery, Map<Integer, Object> args, int page, int perPage, PageBean pageUtils,
			RowMapper<T> rowMapper, String countSql) throws DbException;

	List<Map<String, Object>> doPageQueryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException;

	List<Map<String, Object>> queryMap(String sqlQuery, Map<Integer, Object> args, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException;

	/**
	 * 调用此方法会默认自动关闭底层数据库连接，返回查询到的T对象列表
	 * 
	 * @param clazz       需映射的类
	 * @param sqlQuery    sql语句
	 * @param vParameters 参数
	 * @return
	 */
	<T> List<T> doQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

	<T> List<T> query(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

	<T> T doQueryClassOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

	<T> T queryOne(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters) throws DbException;

	<T> List<T> doQueryClassNoSql(Class<T> clazz, T selectObject, String selectProperties) throws DbException;

	<T> List<T> doQueryClassNoSql(T selectObject) throws DbException;

	<T> List<T> doQueryClassNoSql(T selectObject, String selectProperties) throws DbException;
	// RowMapper rowMapper

	/**
	 * 一到一关联映射查询，调用此方法会默认自动关闭底层数据库连接，
	 * 
	 * @param <T>              需映射的主类的Class对象
	 * @param clazz            需映射到类的Class对象
	 * @param sqlPrefix        sql前缀
	 *                         <p>
	 *                         <blockquote>
	 * 
	 *                         <pre>
	 *                         String sql = &quot;select n.*,p.*,a.* from news n left outer join photos p&quot;
	 *                         		+ &quot; on   n.id=p.newsid  left outer join archives a &quot;
	 *                         		+ &quot; on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc&quot;;
	 *                         </pre>
	 * 
	 *                         </blockquote>
	 *                         </p>
	 *                         <br/>
	 *                         如果主类对应于n表(news)，那么sql前缀为"n."
	 * @param sqlQuery         sql语句
	 * @param vParameters      参数
	 * @param queryMapNestList 一到一映射关系，可以有多个映射关系，一个映射关系对应一个QueryMapNestOne2One对象
	 * @return 返回查询到的列表
	 * @throws DbException
	 */
	<T> List<T> doQueryClassOne2One(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList) throws DbException;

	<T> List<T> query(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList) throws DbException;

	/**
	 * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
	 * 
	 * @param clazz       需映射类的Class对象
	 * @param sqlQuery    sql语句
	 * @param vParameters 参数
	 * @param page        当前页，从第一页开始
	 * @param perPage     每页多少行
	 * @param pageUtils   返回分页信息的类
	 * @param countSql    总行数的查询语句，也可以为一个整数字符串，表明总行数是多少，
	 *                    如果为null或""，系统为自动根据sqlQuery字符串生产总行数的查询语句
	 * @return
	 * @throws DbException
	 */
	<T> List<T> doPageQueryClass(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page,
			int perPage, PageBean pageUtils, String countSql) throws DbException;

	<T> List<T> query(Class<T> clazz, String sqlQuery, Map<Integer, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException;

	<T> List<T> query(Class<T> clazz, String sqlPrefix, String sqlQuery, Map<Integer, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList, int page, int perPage, PageBean pageUtils, String countSql)
			throws DbException;

	/**
	 * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBase.close()方法关闭 连接
	 * 
	 * @param <T>
	 * @param clazz            需映射主类的Class对象
	 * @param sqlPrefix        主类对应的sql前缀
	 * @param sqlQuery         sql语句
	 * @param vParameters      参数
	 * @param queryMapNestList 一到一映射关系
	 * @param page             当前页，从第一页开始
	 * @param perPage          每页多少行
	 * @param pageUtils        返回分页信息的类
	 * @param countSql         总行数的查询语句，也可以为一个整数字符串，表明总行数是多少，
	 *                         如果为null或""，系统为自动根据sqlQuery字符串生产总行数的查询语句
	 * @return
	 * @throws DbException
	 */
	<T> List<T> doPageQueryClassOne2One(Class<T> clazz, String sqlPrefix, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
			PageBean pageUtils, String countSql) throws DbException;

	/**
	 * 一到多关联映射查询。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param clazz            需映射的主类Class对象
	 * @param sqlPrefix        sql前缀
	 * @param beanKey          为主类对应的主键属性名， 如果主键为复合主键，以英文逗号隔开。
	 * @param sqlQuery         sql语句
	 *                         <p>
	 *                         <blockquote>
	 * 
	 *                         <pre>
	 *                         String sql = &quot;select n.*,p.*,a.* from news n left outer join photos p&quot;
	 *                         		+ &quot; on   n.id=p.newsid  left outer join archives a &quot;
	 *                         		+ &quot; on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc&quot;;
	 *                         </pre>
	 * 
	 *                         </blockquote>
	 *                         </p>
	 *                         <br/>
	 *                         如果主类对应于n表(news)，那么sql前缀为"n."
	 * @param vParameters      参数
	 * @param queryMapNestList 关联子类关系
	 * @return 返回查询结果
	 * @throws DbException
	 */
	<T> List<T> doQueryClassOne2Many(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException;

	<T> List<T> query(Class<T> clazz, String sqlPrefix, String beanKey, String sqlQuery,
			Map<Integer, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException;

	/**
	 * 自定义映射查询
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要调用DataBase.close()方法
	 * 
	 * @param <T>       需映射的类
	 * @param sqlQuery  sql语句
	 * @param args      参数
	 * @param rowMapper 映射接口，用户可以通过此接口的回调函数来执行映射
	 * @return
	 * @throws DbException
	 */
	<T> List<T> doQueryObject(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException;

	<T> List<T> query(String sqlQuery, Map<Integer, Object> args, RowMapper<T> rowMapper) throws DbException;

	List<Map<String, Object>> doQueryMap(String sqlQuery, Map<Integer, Object> args) throws DbException;

	List<Map<String, Object>> query(String sqlQuery, Map<Integer, Object> args) throws DbException;

	/**
	 * 删除
	 * 
	 * @param sqltext     sql语句
	 * @param vParameters 参数
	 * @return
	 * @throws DbException
	 */
	int executeBindDelete(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	int del(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	/**
	 * 更新操作（包括删除）
	 * 
	 * @param sqltext     sql语句
	 * @param vParameters 参数
	 * @return
	 * @throws DbException
	 */
	int executeBindUpdate(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	int update(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	/**
	 * 调用存储过程
	 * 
	 * @param sqltext            sql语句
	 * @param parms              用法举例如下： <code>
	 * <pre>
	 * 用法：
	 * parms.put("1","中");//默认为in类型 
	 * parms.put("2:in","孙");
	 * parms.put("3:in",new Integer(3));
	 * parms.put("4:out",int.class);
	 * parms.put("5:out",java.util.data.class);
	 * parms.put("6:inout",new Long(44));
	 * </pre>
	 *</code>
	 * @param outPramsValues     存放输出参数的返回值，根据parms(输入法参数)里的out,inout对应，如果输入参数为上面的例子所示，那么outPramsValues可能输入如下：
	 * 
	 *                           <pre>
	 *   {
	 *     4:45556,
	 *     5:"2015-09-23 12:34:56"
	 *     6:34456
	 *    }
	 *                           </pre>
	 * 
	 * @param returnDataBaseSets 需返回值的结果集
	 * @return
	 * @throws DbException
	 */

	int executeStoredProcedure(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
			List<DataBaseSet> returnDataBaseSets) throws DbException;

	int callStoredPro(String sqltext, Map<String, Object> parms, Map<Integer, Object> outPramsValues,
			List<DataBaseSet> returnDataBaseSets) throws DbException;

	/**
	 * 返回插入记录的行数
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param sqltext     sql语句
	 * @param vParameters 参数
	 * @return
	 */
	int executeBindInsert(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	int insert(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	/**
	 * 使insertObject对象的所有属性插入到数据库，不会忽略为NULL的属性。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObject 要插入的对象
	 * @return
	 * @throws DbException
	 */
	<T> int excuteInsertWholeClass(T insertObject) throws DbException;

	/**
	 * 使insertObject对象的所有属性插入到数据库，如果insertObject的对象某个属性值为null， 那么会忽略此属性，不会插入空值到数据库。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObject 要插入的对象
	 * @return
	 * @throws DbException
	 */
	<T> int excuteInsertClass(T insertObject) throws DbException;

	/**
	 * 向数据库插入在insertObject里properties数组指定的属性，如果在properties中的某
	 * 个属性对应insertObject属性值为空，那么会忽略此属性，不会插入空值到数据库。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @param properties   指定insertObject里需插入的属性，如果properties指定为空，
	 *                     则插入insertObject对象所有属性
	 * @return
	 * @throws DbException
	 */
	<T> int excuteInsertClass(T insertObject, String[] properties) throws DbException;

	/**
	 * 向数据库插入在insertObject里properties数组指定的属性，如果属性的值为空，也会插入到数据库。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @param properties   指定insertObject里需插入的属性，不会忽略为NULL的属性
	 * @return
	 * @throws DbException
	 */
	<T> int excuteInsertWholeClass(T insertObject, String[] properties) throws DbException;

	/**
	 * 把某对象的指定的属性插入到数据库到数据库，并返回主键，有些数据库的驱动程序不会返回主键，所以 要根据具体数据库而言，mysql数据可以返回主键。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @return 返回插入的主键
	 * @throws DbException
	 */
	<T> long excuteInsertClassReturnKey(T insertObject, String[] properties) throws DbException;

	/**
	 * 把某对象的指定的属性插入到数据库到数据库，并返回主键，有些数据库的驱动程序不会返回主键，所以 要根据具体数据库而言，mysql数据可以返回主键。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @param properties   :插入指定的属性，不会忽略为null的属性
	 * @return 返回插入的主键
	 * @throws DbException
	 */
	<T> long excuteInsertWholeClassReturnKey(T insertObject, String[] properties) throws DbException;

	/**
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObject 要插入的对象
	 * @return
	 * @throws DbException
	 */
	<T> long excuteInsertClassReturnKey(T insertObject) throws DbException;

	/**
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法，不会忽略为null的属性
	 * 
	 * @param <T>
	 * @param insertObject 要插入的对象
	 * @return
	 * @throws DbException
	 */
	<T> long excuteInsertWholeClassReturnKey(T insertObject) throws DbException;

	/**
	 * 插入多个对象的指定属性,此方法必须保证insertObjects是相同的类型
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObjects 待插入的对象
	 * @param properties    对象的属性
	 * @return
	 * @throws DbException
	 */
	<T> int[] excuteInsertClass(T[] insertObjects, String[] properties) throws DbException;

	/**
	 * 插入多个对象的指定属性,此方法必须保证insertObjects是相同的类型,并且不会忽略为NULL的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param insertObjects 待插入的对象
	 * @param properties    对象的属性，即待插入对象的哪些属性需要插入
	 * @return
	 * @throws DbException
	 */
	<T> int[] excuteInsertWholeClass(T[] insertObjects, String[] properties) throws DbException;

	/**
	 * 插入多个对象指定的属性到数据库，insertObjects里的每个对象对应一个属性数组，所以为二维数组，每个对象里的属性值为null的会忽略
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param insertObjects：待插入的对象数组
	 * @param properties：属性数组，每个待插入对象对应一个属性数组，表示要插入此对象的哪些属性
	 * @return
	 * @throws DbException
	 */
	int[] excuteInsertObjects(Object[] insertObjects, String[][] properties) throws DbException;

	/**
	 * 插入多个对象指定的属性到数据库，insertObjects里的每个对象对应一个属性数组，所以为二维数组，不忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param insertObjects：待插入数据库的对象数组
	 * @param properties：属性数组，用于指定对象的哪些属性需要插入数据库；每个对象对应一个属性数组
	 * @return
	 * @throws DbException
	 */
	int[] excuteInsertWholeObjects(Object[] insertObjects, String[][] properties) throws DbException;

	/**
	 * 插入多个对象的所有属性到数据库，如果对象里某个属性为空，会忽略此属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param insertObjects 待插入的对象，可以为不同类型的类
	 * @return
	 * @throws DbException
	 */
	int[] excuteInsertWholeObjects(Object[] insertObjects) throws DbException;

	/**
	 * 插入多个对象的所有属性到数据库，不忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param insertObjects 待插入的对象，可以为不同类型的类
	 * @return
	 * @throws DbException
	 */
	int[] excuteInsertObjects(Object[] insertObjects) throws DbException;

	/**
	 * 插入多个对象到数据库，如果对象里某个属性为空，会忽略此属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param objs 待插入的对象，所有对象的类型必须相同
	 * @return
	 * @throws DbException
	 */
	<T> int[] excuteInsertClass(T[] objs) throws DbException;

	/**
	 * 插入多个对象到数据库，不会忽略为NULL的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param objs 待插入的对象，所有对象的类型必须相同
	 * @return
	 * @throws DbException
	 */
	<T> int[] excuteInsertWholeClass(T[] objs) throws DbException;

	/**
	 * 更新对象
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param updateObject 待更新的对象
	 * @param beanKey      待更新对象的主键属性名称，复合主键属性用逗号隔开
	 * @param properties   待插入的属性， 如果指定的属性在upateObject对象里的值为null，则忽略
	 * @return
	 * @throws DbException
	 */
	<T> int excuteUpdateClass(T updateObject, String beanKey, String[] properties) throws DbException;

	/**
	 * 更新对象
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param updateObject 待更新的对象
	 * @param beanKey      待更新对象的主键属性名称，复合主键属性用逗号隔开
	 * @param properties   待插入的属性，不会忽略为NULL的属性
	 * @return
	 * @throws DbException
	 */
	<T> int excuteUpdateWholeClass(T updateObject, String beanKey, String[] properties) throws DbException;

	/**
	 * 删除多个对象，所有对象都以deleteProperteis里指定的属性的值来定位删除
	 * 
	 * @param <T>
	 * @param deleteObject
	 * 
	 * @param deleteProperteis 复合属性（即以英文逗号隔开的属性）
	 *                         待删除对象根据deleteProperteis字符串里的属性来定位删除。
	 *                         deleteProperteis可以为多个属性，以英文逗号隔开
	 *                         ，例如deleteProperteis为"name,id,age"，将会生产 "where name=?
	 *                         and id=? and age=?"条件。
	 * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。 如果返回为null，表示执行失败
	 * @throws DbException
	 */
	<T> int[] excuteDeleteClass(T[] deleteObject, String deleteProperteis) throws DbException;

	/**
	 * 删除一个对象，所有对象都以deleteProperteis属性的值来定位删除
	 * 
	 * @param <T>
	 * @param deleteObject     待删除的对象
	 * @param deleteProperteis 待删除对象根据deleteProperteis里的属性来定位删除。
	 *                         deleteProperteis可以为多个属性，以英文逗号隔开
	 *                         ，例如deleteProperteis为"name,id,age"，将会生产 "where name=?
	 *                         and id=? and age=?"条件。
	 * @return 返回实际更新的行数
	 * @throws DbException
	 */
	<T> int excuteDeleteClass(T deleteObject, String deleteProperteis) throws DbException;

	/**
	 * 把对象所有属性更新到数据库，如果某个属性值为null，则忽略
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param updateObject 待更新到数据库的对象
	 * @param beanKey      updateObject对象的主键属性，复合主键属性以英文逗号隔开
	 * @return
	 * @throws DbException
	 */
	<T> int excuteUpdateClass(T updateObject, String beanKey) throws DbException;

	/**
	 * 把对象所有属性更新到数据库，不会忽略为null的属性
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param updateObject 待更新到数据库的对象
	 * @param beanKey      updateObject对象的主键属性，复合主键属性以英文逗号隔开
	 * @return
	 * @throws DbException
	 */
	<T> int excuteUpdateWholeClass(T updateObject, String beanKey) throws DbException;

	<T> int[] excuteUpdateClass(T[] updateObject, String beanKey) throws DbException;

	<T> int[] excuteUpdateWholeClass(T[] updateObject, String beanKey) throws DbException;

	/**
	 * 把对象指定属性更新到数据库
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param objects    待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组
	 * @param beanKey    objects对象的主键属性，复合主键属性以英文逗号隔开
	 * @param properties 指定需更新的属性，如果指定的某个属性在对应对象里值为null，则忽略
	 * @return
	 * @throws DbException
	 */
	<T> int[] excuteUpdateClass(T[] objects, String beanKey, String[] properties) throws DbException;

	/**
	 * 把对象指定属性更新到数据库,不会忽略为NULL的属性
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param <T>
	 * @param objects    待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组
	 * @param beanKey    objects对象的主键属性，复合主键属性以英文逗号隔开
	 * @param properties 指定需更新的属性，不会忽略为NULL的属性
	 * @return
	 * @throws DbException
	 */
	<T> int[] excuteUpdateWholeClass(T[] objects, String beanKey, String[] properties) throws DbException;

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样,忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param objects  待更新到数据库的多个对象
	 * @param beanKeys 每个对象分别对应主键属性名
	 * @return
	 * @throws DbException
	 */
	int[] excuteUpdateObjects(Object[] objects, String[] beanKeys) throws DbException;

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param objects    待更新到数据库的多个对象
	 * @param beanKeys   每个对象分别对应主键属性名
	 * @param properties 每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
	 *                   如果指定的某个属性在对应对象里值为null，则忽略。
	 * @return
	 * @throws DbException
	 */
	int[] excuteUpdateObjects(Object[] objects, String[] beanKeys, String[][] properties) throws DbException;

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param objects    待更新到数据库的多个对象
	 * @param beanKeys   每个对象分别对应主键属性名
	 * @param properties 每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
	 *                   不忽略为null的属性。
	 * @return
	 * @throws DbException
	 */
	int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys, String[][] properties) throws DbException;

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样，不会忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param objects  待更新到数据库的多个对象
	 * @param beanKeys 每个对象分别对应主键属性名
	 * @return
	 * @throws DbException
	 */
	int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys) throws DbException;

	/**
	 * 删除多个对象
	 * 
	 * @param deleteObjects         待删除的多个对象，数组里的每个对象类型可以不相同
	 * @param deletePropertiesArray 每个对象对应一个复合属性，每个对象根据一个复合属性，来生产sql删除语句，每个复合属性以英文逗号隔开。
	 * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。 如果返回为null，表示执行失败
	 * @throws DbException
	 */
	int[] excuteDeleteObjects(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException;

	/**
	 * 如果insert一条语句用此函数，并返回插入数据库后返回此记录的主键
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param sqltext
	 * @param vParameters
	 * @return
	 */
	long executeBindInsertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	long insertReturnKey(String sqltext, Map<Integer, Object> vParameters) throws DbException;

	/**
	 * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过DataBase.
	 * setAutoCommit()方法 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
	 * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBase.close()方法关闭数据库连接
	 * 
	 * @return
	 * @throws DbException
	 */
	void setAutoCommit(boolean b) throws DbException;

	/**
	 * <pre>
	 *   重新连接数据库，当数据库连接关闭时，可以调用此方法进行重连。如果嫌每次重连太麻烦，你也可以通过设置
	 *   {@link #setAutoReconnect(boolean)}方法，通过传入参数为true，来让操作关闭后自动重连。
	 * 
	 * </pre>
	 * 
	 * @throws DbException
	 */
	void reConnectDb() throws DbException;

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
	 * 用于事务性操作的回滚，如果事务为分布式事务，则为空操作。
	 * 
	 * @throws DbException
	 */
	void rollback() throws DbException;

	/**
	 * 回滚并且关闭连接
	 * 
	 * @throws DbException
	 */
	void rollbackAndClose() throws DbException;

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

	void commitAndClose() throws DbException;

	/**
	 * 返回的数组里每个值对应返回对应sql语句执行后更新的行数，如果为null表明执行失败，内部会自动回滚。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param sqltxts
	 * @param vParametersArray
	 * @return 如果为null表明执行失败
	 * @throws DbException
	 */
	int[] executeBindBatch(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException;

	int[] update(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException;

	int[] insert(String[] sqltxts, Map<Integer, Object>[] vParametersArray) throws DbException;

	/**
	 * 批量更新操作（增，删，改），返回每条语句更新记录的行数
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBase.close()方法
	 * 
	 * @param sqltxt          执行的sql语句
	 * @param vParametersList 每条语句所携带的参数，每条语句对应一个Map，每个Map存放相应语句的参数
	 * @return 返回每条语句更新记录的行数，执行错误，会抛出异常
	 * @throws DbException
	 */
	int[] executeBindBatch(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException;

	int[] update(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException;

	int[] insert(String sqltxt, List<Map<Integer, Object>> vParametersList) throws DbException;

	/**
	 * 不带参数的批量处理
	 * 
	 * @param sqltxt
	 * @return
	 * @throws DbException
	 */
	int[] executeBatch(ArrayList<String> sqltxt) throws DbException;

	int[] update(ArrayList<String> sqltxts) throws DbException;

	int[] insert(ArrayList<String> sqltxts) throws DbException;

	/**
	 * 关闭底层资源，但不关闭数据库连接
	 */
	void closer() throws DbException;

	/**
	 * 关闭数据库连接，释放底层占用资源
	 */
	void close();

	Connection getConnection();

	<T> int insert(T insertObject) throws DbException;

	<T> long insertReturnKey(T insertObject) throws DbException;

	<T> int insert(T insertObject, String[] properties) throws DbException;

	<T> long insertReturnKey(T insertObject, String[] properties) throws DbException;

	<T> int[] insert(T[] objs) throws DbException;

	<T> int[] insert(T[] objs, String[] properties) throws DbException;

	<T> int update(T updateObject, String beanKey) throws DbException;

	<T> int update(T updateObject, String beanKey, String[] properties) throws DbException;

	<T> int[] update(T[] objects, String beanKey, String[] properties) throws DbException;

	<T> int[] update(T[] objects, String beanKey) throws DbException;

	<T> int[] update(Object[] objects, String[] beanKeys, String[][] properties) throws DbException;

	<T> T queryOne(T selectObject, String selectProperties) throws DbException;

	<T> List<T> query(T selectObject, String selectProperties) throws DbException;

	<T> List<T> query(T selectObject, String selectProperties, int page, int perPage, PageBean pb) throws DbException;

	<T> List<T> query(T selectObject, int page, int perPage, PageBean pb) throws DbException;

	<T> List<T> query(T selectObject) throws DbException;

	<T> T queryOne(T selectObject) throws DbException;

	<T> int del(T deleteObject, String deleteProperteis) throws DbException;


	<T> int[] del(T[] deleteObjects, String deleteProperteis) throws DbException;

	<T> int[] del(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException;

	<T> int insertWhole(T insertObject) throws DbException;

	<T> long insertWholeReturnKey(T insertObject) throws DbException;

	<T> int insertWhole(T insertObject, String[] properties) throws DbException;

	<T> long insertWholeReturnKey(T insertObject, String[] properties) throws DbException;

	<T> int[] insertWhole(T[] objs) throws DbException;

	<T> int[] insertWhole(T[] objs, String[] properties) throws DbException;

	<T> int updateWhole(T updateObject, String beanKey) throws DbException;

	<T> int updateWhole(T updateObject, String beanKey, String[] properties) throws DbException;

	<T> int[] updateWhole(T[] objects, String beanKey, String[] properties) throws DbException;

	<T> int[] updateWhole(T[] objects, String beanKey) throws DbException;

	<T> int[] updateWhole(Object[] objects, String[] beanKeys, String[][] properties) throws DbException;
	/**
	 * 
	 * @param reader sql脚本输入reader
	 * @param logWriter 日志打印writer
	 * @return  返回执行成功的结果，出错返回异常
	 * @throws DbException
	 */
	String exeScript(Reader reader,PrintWriter logWriter) throws DbException;
	/**
	 * 
	 * @param reader sql脚本输入reader
	 * @return  返回执行成功的结果，出错返回异常
	 * @throws DbException
	 */
	String exeScript(Reader reader) throws DbException;

}