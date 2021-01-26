package com.ulwx.database;

import com.ulwx.database.nsql.NSQL;
import com.ulwx.tool.MD;
import com.ulwx.tool.PageBean;
import com.ulwx.tool.support.StringUtils;
import com.ulwx.tool.support.type.TResult2;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDataBase implements NoSqlOperation,AutoCloseable { 

	private DataBase dataBase;
	public MDataBase(){

	}
	public  boolean isExternalControlConClose(){
		return this.dataBase.isExternalControlConClose();
	}
	public MDataBase(DataBase dataBase){
		this.dataBase=dataBase;
	}
	public DataBase getDataBase() {
		return dataBase;
	}

	public void setDataBase(DataBase dataBase) {
		this.dataBase = dataBase;
	}
	

	/**
	 * 是否自动重连
	 * @return
	 */
	public boolean isAutoReconnect(){
		return this.dataBase.isAutoReconnect(); 
	};

	public void setAutoReconnect(boolean autoReconnect)throws DbException{
		this.dataBase.setAutoCommit(autoReconnect);
	};

	public boolean isMainSlaveMode(){
		return this.dataBase.isMainSlaveMode();
	};

	public void setMainSlaveMode(boolean mainSlaveMode){
		this.dataBase.setMainSlaveMode(mainSlaveMode);
	};


	public boolean getInternalConnectionAutoCommit() throws DbException{
		return this.dataBase.getInternalConnectionAutoCommit();
	};

	/**
	 * 如果数据库是主从式模式，则语句为查询语句并且是非事务性的时候，则选择从库查询
	 */
	public void selectSlaveDb() throws DbException{
		this.dataBase.selectSlaveDb();
	};

	public String getDataBaseType(){
		return this.dataBase.getDataBaseType();
	};

	
	/**
	 * 
	 * @param packageFullName :sql脚本所在都包，例如com.xx.yy
	 * @param sqlFileName ：sql脚本的文件名，例如 db.sql
	 * @return 执行成功的结果 ，否则抛出异常
	 * @throws DbException
	 */
	public String exeScript(String packageFullName,String sqlFileName) throws DbException{
		return this.exeScript(packageFullName, sqlFileName, null);
		
	}
	/**
	 * 
	 * @param packageFullName :sql脚本所在都包，例如com.xx.yy
	 * @param sqlFileName ：sql脚本的文件名，例如 db.sql
	 * @param logWriter ：日志的输出
	 * @return 执行成功的结果 ，否则抛出异常
	 * @throws DbException
	 */
	public String exeScript(String packageFullName,String sqlFileName,PrintWriter logWriter) throws DbException{
		packageFullName=packageFullName.replace(".", "/");
		InputStream in = this.getClass().getResourceAsStream("/" + packageFullName+"/"+sqlFileName);
		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			if(logWriter==null) {
				return this.dataBase.exeScript(bufReader);
			}else {
				return this.dataBase.exeScript(bufReader,logWriter);
			}
		} catch (UnsupportedEncodingException e) {
			throw new DbException(e.getMessage(),e);
		}

	}
	
	
	/**
	 * 
	 * @param mdFullMethodName：定位md文件里的方法，格式为：{@code com.ulwx.database.test.SysRightDao.md:getDataCount} ,
	 *   表示定位到com/ulwx/database/test/SysRightDao.md文件里的{@code codegetDataCount}方法
	 * @param vParameters :参数，在md文件中，只能用${xx},不能用#{xx}
	 * @return 执行成功的结果 ，否则抛出异常
	 * @throws DbException
	 */
	public String exeScript(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		StringReader sr=new StringReader(nsql.getExeSql());
		return this.dataBase.exeScript(sr);
		
	}
	
	/**
	 * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用DataBaseMd.close()方法关闭
	 * 
	 * @param mdFullMethodName  定位md文件里的方法，格式为：{@code com.ulwx.database.test.SysRightDao.md:getDataCount} ,
	 *   表示定位到com/ulwx/database/test/SysRightDao.md文件里的{@code codegetDataCount}方法
	 * @param vParameters 参数
	 * @return 返回查询到的结果集
	 * @throws DbException
	 */
	public DataBaseSet doCachedQuery(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		return this.dataBase.doCachedQuery(nsql.getExeSql(), nsql.getArgs());
	};

	
	/**
	 * 此方法返回查询到的离线结果集，操作完成后，会默认自动关闭底层连接，不需要调用DataBaseMd.close()方法关闭 连接
	 * 
	 * @param mdFullMethodName  定位md文件里的方法，格式为：{@code com.ulwx.database.test.SysRightDao.md:getDataCount} ,
	 *   表示定位到com/ulwx/database/test/SysRightDao.md文件里的{@code codegetDataCount}方法
	 * @param vParameters
	 * @param page
	 *            当前页
	 * @param perPage
	 *            每页多少行
	 * @param pageUtils
	 *            返回的分页信息
	 * @param countSqlMdFullMethodName 格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}
	 *            在md文件里定位查询总数的sql语句，此sql语句查询记录的总数，根据此算页数；
	 *            <p>countSqlMdFullMethodName也可以为一个整数字符串，用于指定总数；
	 *            <p>countSqlMdFullMethodName如果指定null或""字符串，那么系统会根据查询语句自动生成查询总数的sql语句
	 * @return
	 * @throws DbException
	 */
	public DataBaseSet doCachedPageQuery(String mdFullMethodName, Map<String, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		String countSql=null;
		if(StringUtils.hasText(countSqlMdFullMethodName)){
			if(StringUtils.isNumber(countSqlMdFullMethodName)){
				countSql=countSqlMdFullMethodName;
			}else{
				NSQL cnsql=NSQL.getNSQL(countSqlMdFullMethodName,vParameters);
				countSql=cnsql.getExeSql();
			}
		}
		return dataBase.doCachedPageQuery(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageUtils, countSql);
	};
	
	public DataBaseSet query(String mdFullMethodName, Map<String, Object> vParameters, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		
		return this.doCachedPageQuery(mdFullMethodName, vParameters, page, perPage, pageUtils, countSqlMdFullMethodName);
				
	}
	public DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		
		return this.doCachedQuery(mdFullMethodName, vParameters);
				
	}
	/**
	 * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBaseMd.close()方法关闭 连接
	 * 
	 * @param <T>
	 *            需映射的类
	 * @param mdFullMethodName
	 *            在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，其中
	 *            com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，:后面的
	 *            getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param args
	 *            sql查询语句里的参数
	 * @param page
	 *            当前页，从第一页开始
	 * @param perPage
	 *            每页多少行
	 * @param pageUtils
	 *            返回分页信息
	 * @param rowMapper
	 *            映射接口，用户可以通过此接口的回调函数来执行映射
	 * @param countSqlMdFullMethodName 格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}
	 *            在md文件里定位查询总数的sql语句，此sql语句查询记录的总数，根据此算页数；
	 *            <p>countSqlMdFullMethodName也可以为一个整数字符串，用于指定总数；
	 *            <p>countSqlMdFullMethodName如果指定null或""字符串，那么系统会根据查询语句自动生成查询总数的sql语句
	 * @return
	 * @throws DbException
	 */
	public <T> List<T> doPageQueryObject(String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageUtils,
			RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,args);
		String countSql=null;
		if(StringUtils.hasText(countSqlMdFullMethodName)){
			if(StringUtils.isNumber(countSqlMdFullMethodName)){
				countSql=countSqlMdFullMethodName;
			}else{
				NSQL cnsql=NSQL.getNSQL(countSqlMdFullMethodName,args);
				countSql=cnsql.getExeSql();
			}
		}
		return this.dataBase.doPageQueryObject(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageUtils, rowMapper, countSql);
		
	};
	
	public <T> List<T> query(String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageUtils,
			RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException{
		
		return this.doPageQueryObject(mdFullMethodName, args, page, perPage, pageUtils, rowMapper, countSqlMdFullMethodName);
	}

	/**
	 * 查询的结果返回一个Map列表
	 * @param mdFullMethodName
	 *            在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，其中
	 *            com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，:后面的
	 *            getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param args
	 * @param page
	 * @param perPage
	 * @param pageUtils
	 * @param countSqlMdFullMethodName 格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}
	 *            在md文件里定位查询总数的sql语句，此sql语句查询记录的总数，根据此算页数；
	 *            <p>countSqlMdFullMethodName也可以为一个整数字符串，用于指定总数；
	 *            <p>countSqlMdFullMethodName如果指定null或""字符串，那么系统会根据查询语句自动生成查询总数的sql语句
	 * @return
	 * @throws DbException
	 */
	public List<Map<String, Object>> doPageQueryMap(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,args);
		String countSql=null;
		if(StringUtils.hasText(countSqlMdFullMethodName)){
			if(StringUtils.isNumber(countSqlMdFullMethodName)){
				countSql=countSqlMdFullMethodName;
			}else{
				NSQL cnsql=NSQL.getNSQL(countSqlMdFullMethodName,args);
				countSql=cnsql.getExeSql();
			}
		}
		return this.dataBase.doPageQueryMap(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageUtils, countSql);
	};
	public List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		return this.doPageQueryMap(mdFullMethodName, args, page, perPage, pageUtils, countSqlMdFullMethodName);
	}
	

	/**
	 * 调用此方法会默认自动关闭底层数据库连接，返回查询到的T对象列表
	 * @param clazz
	 *            需映射的类
	 * @param mdFullMethodName
	 *            在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，其中
	 *            com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，:后面的
	 *            getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param args
	 *            参数
	 * @return
	 */
	public <T> List<T> doQueryClass(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,args);
		return this.dataBase.doQueryClass(clazz, nsql.getExeSql(), nsql.getArgs());
	};

	public <T> List<T> query(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException{
		return this.doQueryClass(clazz, mdFullMethodName, args);
	}
	public <T> T doQueryClassOne(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,args);
		return this.dataBase.doQueryClassOne(clazz, nsql.getExeSql(), nsql.getArgs());
		
		
	};
	public <T> T queryOne(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException{
		return this.doQueryClassOne(clazz, mdFullMethodName, args);
	}
	public <T> List<T> doQueryClassNoSql(Class<T> clazz, T selectObject, String selectProperties) throws DbException{
		return this.dataBase.doQueryClassNoSql(clazz, selectObject, selectProperties);
	};

	public <T> List<T> doQueryClassNoSql(T selectObject) throws DbException{
		return this.dataBase.doQueryClassNoSql(selectObject);
	};

	public <T> List<T> doQueryClassNoSql(T selectObject, String selectProperties) throws DbException{
		return this.dataBase.doQueryClassNoSql(selectObject, selectProperties);
	};
	// RowMapper rowMapper

	/**
	 * 一到一关联映射查询，调用此方法会默认自动关闭底层数据库连接，
	 * @param <T>
	 *            需映射的主类的Class对象
	 * @param clazz
	 *            需映射到类的Class对象
	 * @param sqlPrefix  sql前缀
	 *  <p>
	 * <blockquote>
	 *	 String sql = &quot;select n.*,p.*,a.* from news n left outer join photos p&quot;
	 * 	+ &quot; on   n.id=p.newsid  left outer join archives a &quot;
	 * 	+ &quot; on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc&quot;;
	 * 
	 * </blockquote>
	 *  <p> 
	 *  如果主类对应于n表(news)，那么sql前缀为"n."
	 * @param mdFullMethodName
	 *            在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，其中
	 *            com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，:后面的
	 *            getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param vParameters
	 *            参数
	 * @param queryMapNestList
	 *            一到一映射关系，可以有多个映射关系，一个映射关系对应一个QueryMapNestOne2One对象
	 * @return 返回查询到的列表
	 * @throws DbException
	 */
	public <T> List<T> doQueryClassOne2One(Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList) throws DbException{
		
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		return this.dataBase.doQueryClassOne2One(clazz, sqlPrefix, nsql.getExeSql(), nsql.getArgs(), queryMapNestList);
		
		
	};

	public <T> List<T> query(Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> vParameters,
			QueryMapNestOne2One[] queryMapNestList) throws DbException{
		return this.doQueryClassOne2One(clazz, sqlPrefix, mdFullMethodName, vParameters, queryMapNestList);
	}
	/**
	 * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBaseMd.close()方法关闭 连接
	 * 
	 * @param clazz
	 *            需映射类的Class对象
	 * @param mdFullMethodName
	 *            在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，其中
	 *            com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，:后面的
	 *            getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param vParameters
	 *            参数
	 * @param page
	 *            当前页，从第一页开始
	 * @param perPage
	 *            每页多少行
	 * @param pageUtils
	 *            返回分页信息的类
	 * @param countSqlMdFullMethodName 格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}
	 *            在md文件里定位查询总数的sql语句，此sql语句查询记录的总数，根据此算页数；
	 *            <p>countSqlMdFullMethodName也可以为一个整数字符串，用于指定总数；
	 *            <p>countSqlMdFullMethodName如果指定null或""字符串，那么系统会根据查询语句自动生成查询总数的sql语句
	 * @return
	 * @throws DbException
	 */
	public <T> List<T> doPageQueryClass(Class<T> clazz, String mdFullMethodName, Map<String, Object> vParameters, int page,
			int perPage, PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		String countSql=null;
		if(StringUtils.hasText(countSqlMdFullMethodName)){
			if(StringUtils.isNumber(countSqlMdFullMethodName)){
				countSql=countSqlMdFullMethodName;
			}else{
				NSQL cnsql=NSQL.getNSQL(countSqlMdFullMethodName,vParameters);
				countSql=cnsql.getExeSql();
			}
		}
		
		return this.dataBase.doPageQueryClass(clazz, nsql.getExeSql(), nsql.getArgs(), page, perPage, pageUtils, countSql);
		
	};
	
	public <T> List<T> query(Class<T> clazz, String mdFullMethodName, Map<String, Object> vParameters, int page,
			int perPage, PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		return this.doPageQueryClass(clazz, mdFullMethodName, vParameters, page, perPage, pageUtils, countSqlMdFullMethodName);
	}


	/**
	 * 此方法操作完成后，会默认自动关闭底层连接，不需要调用DataBaseMd.close()方法关闭 连接
	 * 
	 * @param <T>
	 * @param clazz
	 *            需映射主类的Class对象
	 * @param sqlPrefix
	 *            主类对应的sql前缀
	 * @param mdFullMethodName
	 *            在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，其中
	 *            com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，:后面的
	 *            getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param vParameters
	 *            参数
	 * @param queryMapNestList
	 *            一到一映射关系
	 * @param page
	 *            当前页，从第一页开始
	 * @param perPage
	 *            每页多少行
	 * @param pageUtils
	 *            返回分页信息的类
	 * @param countSqlMdFullMethodName 格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}
	 *            在md文件里定位查询总数的sql语句，此sql语句查询记录的总数，根据此算页数；
	 *            <p>countSqlMdFullMethodName也可以为一个整数字符串，用于指定总数；
	 *            <p>countSqlMdFullMethodName如果指定null或""字符串，那么系统会根据查询语句自动生成查询总数的sql语句
	 * @return
	 * @throws DbException
	 */
	public <T> List<T> doPageQueryClassOne2One(Class<T> clazz, String sqlPrefix, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		String countSql=null;
		if(StringUtils.hasText(countSqlMdFullMethodName)){
			if(StringUtils.isNumber(countSqlMdFullMethodName)){
				countSql=countSqlMdFullMethodName;
			}else{
				NSQL cnsql=NSQL.getNSQL(countSqlMdFullMethodName,vParameters);
				countSql=cnsql.getExeSql();
			}
		}
		
		return this.dataBase.doPageQueryClassOne2One(clazz, sqlPrefix, nsql.getExeSql(), nsql.getArgs(), queryMapNestList, page, perPage, pageUtils, countSql);
	};
	
	public <T> List<T> query(Class<T> clazz, String sqlPrefix, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
			PageBean pageUtils, String countSqlMdFullMethodName) throws DbException{
		return this.doPageQueryClassOne2One(clazz, sqlPrefix, mdFullMethodName, vParameters, queryMapNestList, page, perPage, pageUtils, countSqlMdFullMethodName);
	}

	/**
	 * 一到多关联映射查询。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param clazz
	 *            需映射的主类Class对象
	 * @param sqlPrefix
	 *            sql前缀
	 * @param beanKey
	 *            为主类对应的主键属性名， 如果主键为复合主键，以英文逗号隔开。
	 * @param mdFullMethodName
	 *            在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，其中
	 *            com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，冒号（:）后面的
	 *            getDataCount为方法名，此方法名称下方为sql模板语句
	 * <p>
	 * <blockquote>
	 * <pre>
	 * String sql = &quot;select n.*,p.*,a.* from news n left outer join photos p&quot;
	 * 		+ &quot; on   n.id=p.newsid  left outer join archives a &quot;
	 * 		+ &quot; on n.id=a.newsid  where n.type=?  and n.audi=1 order by n.id desc&quot;;
	 * </pre>
	 * </blockquote>
	 * 如果主类对应于n表(news)，那么sql前缀为"n."
	 * @param vParameters
	 *            参数
	 * @param queryMapNestList
	 *            关联子类关系
	 * @return 返回查询结果
	 * @throws DbException
	 */
	public <T> List<T> doQueryClassOne2Many(Class<T> clazz, String sqlPrefix, String beanKey, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		return this.dataBase.doQueryClassOne2Many(clazz, sqlPrefix, beanKey, nsql.getExeSql(), nsql.getArgs(), queryMapNestList);
	};
	
	
	public <T> List<T> query(Class<T> clazz, String sqlPrefix, String beanKey, String mdFullMethodName,
			Map<String, Object> vParameters, QueryMapNestOne2Many[] queryMapNestList) throws DbException{
		return this.doQueryClassOne2Many(clazz, sqlPrefix, beanKey, mdFullMethodName, vParameters, queryMapNestList);
	}

	/**
	 * 自定义映射查询
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 *            需映射的类
	 * @param mdFullMethodName
	 *       在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，
	 *       其中 com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
	 *       冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param args
	 *            参数
	 * @param rowMapper
	 *            映射接口，用户可以通过此接口的回调函数来执行映射
	 * @return
	 * @throws DbException
	 */
	public <T> List<T> doQueryObject(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,args);
		return this.dataBase.doQueryObject(nsql.getExeSql(), nsql.getArgs(), rowMapper);
	};

	
	public <T> List<T> query(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException{
		return this.doQueryObject(mdFullMethodName, args, rowMapper);
		
	}
	/**
	 * 分页查询，返回的一页结果为Map列表
	 * @param mdFullMethodName
	 *       在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，
	 *       其中 com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
	 *       冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param args 
	 * 			 参数
	 * @return
	 * @throws DbException
	 */
	public List<Map<String, Object>> doQueryMap(String mdFullMethodName, Map<String, Object> args) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,args);
		return this.dataBase.doQueryMap(nsql.getExeSql(), nsql.getArgs());
		
	};
	
	public List<Map<String, Object>> query(String mdFullMethodName, Map<String, Object> args) throws DbException{
		return this.doQueryMap(mdFullMethodName, args);
	}

	/**
	 * 删除
	 * 
	 * @param mdFullMethodName
	 *       在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，
	 *       其中 com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
	 *       冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param vParameters
	 *            参数
	 * @return
	 * @throws DbException
	 */
	public int executeBindDelete(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		return this.dataBase.executeBindDelete(nsql.getExeSql(), nsql.getArgs());
	};
	
	public int del(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		return this.executeBindDelete(mdFullMethodName, vParameters);
	}

	/**
	 * 更新操作（包括删除）
	 * @param mdFullMethodName
	 *       在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，
	 *       其中 com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
	 *       冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param vParameters
	 *            参数
	 * @return
	 * @throws DbException
	 */
	public int executeBindUpdate(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		return this.dataBase.executeBindUpdate(nsql.getExeSql(), nsql.getArgs());
	};
	
	public int update(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		return this.executeBindUpdate(mdFullMethodName, vParameters);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param mdFullMethodName
	 *       在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，
	 *       其中 com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
	 *       冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
	 * @param parms
	 * 用法举例如下：
	 * <code>
	 * <pre>
	 * 用法：
	 * parms.put("name","中");//默认为in类型 
	 * parms.put("sex:in","男");
	 * parms.put("age:in",26);
	 * parms.put("total:out",int.class);
	 * parms.put("createTime:out",java.util.date.class);
	 * parms.put("num:inout",new Long(44));
	 * </pre>
	 *</code>
	 * @param outPramsValues
	 *     存放输出参数的返回值，根据parms(输入法参数)里的out,inout对应，如果输入参数为上面的例子所示，那么outPramsValues可能输入如下：
	 * <pre>
	 *   {
	 *     total:45556,
	 *     createTime:"2015-09-23 12:34:56"
	 *     num:34456
	 *    }
	 * </pre>      
	 * @param returnDataBaseSets
	 *            需返回值的结果集
	 * @return
	 * @throws DbException
	 */

	public int executeStoredProcedure(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
			List<DataBaseSet> returnDataBaseSets) throws DbException{
		
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,parms,true);
		Map<Integer, Object> args=nsql.getArgs();
		Map<String,Object> argsNew=new HashMap<String,Object>();
		for(Integer key:args.keySet()){
			TResult2<String,Object> val=(TResult2<String,Object>)args.get(key);
			argsNew.put(key+val.getFirstValue(), val.getSecondValue());
		}
		Map<Integer, Object> outPramsValuesNew=new HashMap<Integer,Object>();
		int ret= this.dataBase.executeStoredProcedure(nsql.getExeSql(), argsNew, outPramsValuesNew, returnDataBaseSets);
		Map<Integer, String> argsToKey=nsql.getArgsToKey();
		for(Integer key:outPramsValuesNew.keySet()){
			Object val=outPramsValuesNew.get(key);
			outPramsValues.put(argsToKey.get(key), val);
		}
		
		return ret;
		
	};
	
	public int callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
			List<DataBaseSet> returnDataBaseSets) throws DbException{
		return this.executeStoredProcedure(mdFullMethodName, parms, outPramsValues, returnDataBaseSets);
	}

	/**
	 * 返回插入记录的行数
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param mdFullMethodName
	 *       在md文件里定位sql查询语句的方法名，格式为： {@code com.ulwx.database.test.SysRightDao.md:getDataCount}，
	 *       其中 com.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
	 *       冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
	 *            sql语句
	 * @param vParameters
	 *            参数
	 * @return
	 */
	public int executeBindInsert(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethodName,vParameters);
		return this.dataBase.executeBindInsert(nsql.getExeSql(), nsql.getArgs());
	};
	
	
	public int insert(String mdFullMethodName, Map<String, Object> vParameters) throws DbException{
		return this.executeBindInsert(mdFullMethodName, vParameters);
	}

	/**
	 * 使insertObject对象的所有属性插入到数据库，不会忽略为NULL的属性。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 *            要插入的对象
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteInsertWholeClass(T insertObject) throws DbException{
		return this.dataBase.excuteInsertWholeClass(insertObject);
	};

	/**
	 * 使insertObject对象的所有属性插入到数据库，如果insertObject的对象某个属性值为null，
	 * 那么会忽略此属性，不会插入空值到数据库。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 *            要插入的对象
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteInsertClass(T insertObject) throws DbException{
		return this.dataBase.excuteInsertClass(insertObject);
	};

	/**
	 * 向数据库插入在insertObject里properties数组指定的属性，如果在properties中的某
	 * 个属性对应insertObject属性值为空，那么会忽略此属性，不会插入空值到数据库。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @param properties
	 *            指定insertObject里需插入的属性，如果properties指定为空， 则插入insertObject对象所有属性
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteInsertClass(T insertObject, String[] properties) throws DbException{
		return this.dataBase.excuteInsertClass(insertObject, properties);
	};

	/**
	 * 向数据库插入在insertObject里properties数组指定的属性，如果属性的值为空，也会插入到数据库。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @param properties
	 *            指定insertObject里需插入的属性，不会忽略为NULL的属性
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteInsertWholeClass(T insertObject, String[] properties) throws DbException{
		return this.dataBase.excuteInsertWholeClass(insertObject, properties);
	};

	/**
	 * 把某对象的指定的属性插入到数据库到数据库，并返回主键，有些数据库的驱动程序不会返回主键，所以 要根据具体数据库而言，mysql数据可以返回主键。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @return 返回插入的主键
	 * @throws DbException
	 */
	public <T> long excuteInsertClassReturnKey(T insertObject, String[] properties) throws DbException{
		return this.dataBase.excuteInsertClassReturnKey(insertObject, properties);
	};

	/**
	 * 把某对象的指定的属性插入到数据库到数据库，并返回主键，有些数据库的驱动程序不会返回主键，所以 要根据具体数据库而言，mysql数据可以返回主键。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 * @param properties :插入指定的属性，不会忽略为null的属性
	 * @return 返回插入的主键
	 * @throws DbException
	 */
	public <T> long excuteInsertWholeClassReturnKey(T insertObject, String[] properties) throws DbException{
		return this.dataBase.excuteInsertWholeClassReturnKey(insertObject, properties);
	};

	/**
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObject
	 *            要插入的对象
	 * @return
	 * @throws DbException
	 */
	public <T> long excuteInsertClassReturnKey(T insertObject) throws DbException{
		return this.dataBase.excuteInsertClassReturnKey(insertObject);
	};

	/**
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法，不会忽略为null的属性
	 * 
	 * @param <T>
	 * @param insertObject
	 *            要插入的对象
	 * @return
	 * @throws DbException
	 */
	public <T> long excuteInsertWholeClassReturnKey(T insertObject) throws DbException{
		return this.dataBase.excuteInsertWholeClassReturnKey(insertObject);
	};

	/**
	 * 插入多个对象的指定属性,此方法必须保证insertObjects是相同的类型
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObjects
	 *            待插入的对象
	 * @param properties
	 *            对象的属性
	 * @return
	 * @throws DbException
	 */
	public <T> int[] excuteInsertClass(T[] insertObjects, String[] properties) throws DbException{
		return this.dataBase.excuteInsertClass(insertObjects, properties);
	};

	/**
	 * 插入多个对象的指定属性,此方法必须保证insertObjects是相同的类型,并且不会忽略为NULL的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param insertObjects
	 *            待插入的对象
	 * @param properties
	 *            对象的属性，即待插入对象的哪些属性需要插入
	 * @return
	 * @throws DbException
	 */
	public <T> int[] excuteInsertWholeClass(T[] insertObjects, String[] properties) throws DbException{
		return this.dataBase.excuteInsertWholeClass(insertObjects, properties);
	};

	/**
	 * 插入多个对象指定的属性到数据库，insertObjects里的每个对象对应一个属性数组，所以为二维数组，每个对象里的属性值为null的会忽略
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param insertObjects：待插入的对象数组
	 * @param properties：属性数组，每个待插入对象对应一个属性数组，表示要插入此对象的哪些属性
	 * @return
	 * @throws DbException
	 */
	public int[] excuteInsertObjects(Object[] insertObjects, String[][] properties) throws DbException{
		return this.dataBase.excuteInsertObjects(insertObjects, properties);
	};

	/**
	 * 插入多个对象指定的属性到数据库，insertObjects里的每个对象对应一个属性数组，所以为二维数组，不忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param insertObjects：待插入数据库的对象数组
	 * @param properties：属性数组，用于指定对象的哪些属性需要插入数据库；每个对象对应一个属性数组
	 * @return
	 * @throws DbException
	 */
	public int[] excuteInsertWholeObjects(Object[] insertObjects, String[][] properties) throws DbException{
		return this.dataBase.excuteInsertWholeObjects(insertObjects, properties);
	};

	/**
	 * 插入多个对象的所有属性到数据库，如果对象里某个属性为空，会忽略此属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param insertObjects
	 *            待插入的对象，可以为不同类型的类
	 * @return
	 * @throws DbException
	 */
	public int[] excuteInsertWholeObjects(Object[] insertObjects) throws DbException{
		return this.dataBase.excuteInsertWholeObjects(insertObjects);
	};

	/**
	 * 插入多个对象的所有属性到数据库，不忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param insertObjects
	 *            待插入的对象，可以为不同类型的类
	 * @return
	 * @throws DbException
	 */
	public int[] excuteInsertObjects(Object[] insertObjects) throws DbException{
		return this.dataBase.excuteInsertObjects(insertObjects);
	};

	/**
	 * 插入多个对象到数据库，如果对象里某个属性为空，会忽略此属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param objs
	 *            待插入的对象，所有对象的类型必须相同
	 * @return
	 * @throws DbException
	 */
	public <T> int[] excuteInsertClass(T[] objs) throws DbException{
		return this.dataBase.excuteInsertClass(objs);
	};

	/**
	 * 插入多个对象到数据库，不会忽略为NULL的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param objs
	 *            待插入的对象，所有对象的类型必须相同
	 * @return
	 * @throws DbException
	 */
	public <T> int[] excuteInsertWholeClass(T[] objs) throws DbException{
		return this.dataBase.excuteInsertWholeClass(objs);
	};

	/**
	 * 更新对象
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param updateObject
	 *            待更新的对象
	 * @param beanKey
	 *            待更新对象的主键属性名称，复合主键属性用逗号隔开
	 * @param properties
	 *           待插入的属性， 如果指定的属性在upateObject对象里的值为null，则忽略
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteUpdateClass(T updateObject, String beanKey, String[] properties) throws DbException{
		return this.dataBase.excuteUpdateClass(updateObject, beanKey,properties);
	};

	/**
	 * 更新对象
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param updateObject
	 *            待更新的对象
	 * @param beanKey
	 *            待更新对象的主键属性名称，复合主键属性用逗号隔开
	 * @param properties
	 *            待插入的属性，不会忽略为NULL的属性
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteUpdateWholeClass(T updateObject, String beanKey, String[] properties) throws DbException{
		return this.dataBase.excuteUpdateWholeClass(updateObject, beanKey,properties);
	};

	/**
	 * 删除多个对象，所有对象都以deleteProperteis里指定的属性的值来定位删除
	 * 
	 * @param <T>
	 * @param deleteObject
	 * 
	 * @param deleteProperteis
	 *            复合属性（即以英文逗号隔开的属性） 待删除对象根据deleteProperteis字符串里的属性来定位删除。
	 *            deleteProperteis可以为多个属性，以英文逗号隔开
	 *            ，例如deleteProperteis为"name,id,age"，将会生产
	 *            "where name=? and id=? and age=?"条件。
	 * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。
	 *         如果返回为null，表示执行失败
	 * @throws DbException
	 */
	public <T> int[] excuteDeleteClass(T[] deleteObject, String deleteProperteis) throws DbException{
		return this.dataBase.excuteDeleteClass(deleteObject, deleteProperteis);
	};
	
	/**
	 * 删除一个对象，待删除对象根据有值的属性来生成where的删除条件
	 * 
	 * @param <T>
	 * @param deleteObject
	 *            待删除的对象
	 *            待删除对象根据有值的属性来生成where的删除条件。
	 * @return 返回实际更新的行数
	 * @throws DbDbException
	 */
//	public <T> int excuteDeleteClass(T deleteObject) throws DbException{
//		return this.dataBase.excuteDeleteClass(deleteObject);
//	};

	/**
	 * 删除一个对象，所有对象都以deleteProperteis属性的值来定位删除
	 * 
	 * @param <T>
	 * @param deleteObject
	 *            待删除的对象
	 * @param deleteProperteis
	 *            待删除对象根据deleteProperteis里的属性来定位删除。
	 *            deleteProperteis可以为多个属性，以英文逗号隔开
	 *            ，例如deleteProperteis为"name,id,age"，将会生产
	 *            "where name=? and id=? and age=?"条件。
	 * @return 返回实际更新的行数
	 * @throws DbException
	 */
	public <T> int excuteDeleteClass(T deleteObject, String deleteProperteis) throws DbException{
		return this.dataBase.excuteDeleteClass(deleteObject, deleteProperteis);
	};

	/**
	 * 把对象所有属性更新到数据库，如果某个属性值为null，则忽略
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param updateObject
	 *            待更新到数据库的对象
	 * @param beanKey
	 *            updateObject对象的主键属性，复合主键属性以英文逗号隔开
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteUpdateClass(T updateObject, String beanKey) throws DbException{
		return this.dataBase.excuteUpdateClass(updateObject, beanKey);
	};

	/**
	 * 把对象所有属性更新到数据库，不会忽略为null的属性
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param updateObject
	 *            待更新到数据库的对象
	 * @param beanKey
	 *            updateObject对象的主键属性，复合主键属性以英文逗号隔开
	 * @return
	 * @throws DbException
	 */
	public <T> int excuteUpdateWholeClass(T updateObject, String beanKey) throws DbException{
		return this.dataBase.excuteUpdateWholeClass(updateObject, beanKey);
	};

	public <T> int[] excuteUpdateClass(T[] updateObject, String beanKey) throws DbException{
		return this.dataBase.excuteUpdateClass(updateObject, beanKey);
	};

	public <T> int[] excuteUpdateWholeClass(T[] updateObject, String beanKey) throws DbException{
		return this.dataBase.excuteUpdateWholeClass(updateObject, beanKey);
	};

	/**
	 * 把对象指定属性更新到数据库
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param objects
	 *            待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组
	 * @param beanKey
	 *            objects对象的主键属性，复合主键属性以英文逗号隔开
	 * @param properties
	 *            指定需更新的属性，如果指定的某个属性在对应对象里值为null，则忽略
	 * @return
	 * @throws DbException
	 */
	public <T> int[] excuteUpdateClass(T[] objects, String beanKey, String[] properties) throws DbException{
		return this.dataBase.excuteUpdateClass(objects, beanKey, properties);
	};

	/**
	 * 把对象指定属性更新到数据库,不会忽略为NULL的属性
	 * <p>
	 * beanKey为对象主键属性（复合主键属性以逗号分开）
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param <T>
	 * @param objects
	 *            待更新到数据库的对象，对象数组里的类型必须一致，每个对象对应相同的properties数组
	 * @param beanKey
	 *            objects对象的主键属性，复合主键属性以英文逗号隔开
	 * @param properties
	 *            指定需更新的属性，不会忽略为NULL的属性
	 * @return
	 * @throws DbException
	 */
	public <T> int[] excuteUpdateWholeClass(T[] objects, String beanKey, String[] properties) throws DbException{
		return this.dataBase.excuteUpdateWholeClass(objects, beanKey, properties);
	};

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样,忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param objects
	 *            待更新到数据库的多个对象
	 * @param beanKeys
	 *            每个对象分别对应主键属性名
	 * @return
	 * @throws DbException
	 */
	public int[] excuteUpdateObjects(Object[] objects, String[] beanKeys) throws DbException{
		return this.dataBase.excuteUpdateObjects(objects, beanKeys);
	};

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param objects
	 *            待更新到数据库的多个对象
	 * @param beanKeys
	 *            每个对象分别对应主键属性名
	 * @param properties
	 *            每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
	 *            如果指定的某个属性在对应对象里值为null，则忽略。
	 * @return
	 * @throws DbException
	 */
	public int[] excuteUpdateObjects(Object[] objects, String[] beanKeys, String[][] properties) throws DbException{
		return this.dataBase.excuteUpdateObjects(objects, beanKeys, properties);
	};

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param objects
	 *            待更新到数据库的多个对象
	 * @param beanKeys
	 *            每个对象分别对应主键属性名
	 * @param properties
	 *            每个对象分别对应的待更新的属性，是个二维数组，每个对象对应一个数组，表明此对象需要更新的属性，
	 *            不忽略为null的属性。
	 * @return
	 * @throws DbException
	 */
	public int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys, String[][] properties) throws DbException{
		return this.dataBase.excuteUpdateWholeObjects(objects, beanKeys, properties);
	};

	/**
	 * 把多个对象插入数据库，各个对象的类型可以不一样，不会忽略为null的属性
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param objects
	 *            待更新到数据库的多个对象
	 * @param beanKeys
	 *            每个对象分别对应主键属性名
	 * @return
	 * @throws DbException
	 */
	public int[] excuteUpdateWholeObjects(Object[] objects, String[] beanKeys) throws DbException{
		return this.dataBase.excuteUpdateWholeObjects(objects, beanKeys);
	};

	/**
	 * 删除多个对象
	 * 
	 * @param deleteObjects
	 *            待删除的多个对象，数组里的每个对象类型可以不相同
	 * @param deletePropertiesArray
	 *            每个对象对应一个复合属性，每个对象根据一个复合属性，来生产sql删除语句，每个复合属性以英文逗号隔开。
	 * @return 返回int数组里每个元素值对应于删除每个对象时实际删除的行数， 如果数组元素的值为-1表示删除失败。
	 *         如果返回为null，表示执行失败
	 * @throws DbException
	 */
	public int[] excuteDeleteObjects(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException{
		return this.dataBase.excuteDeleteObjects(deleteObjects, deletePropertiesArray);
	};

	/**
	 * 如果insert一条语句用此函数，并返回插入数据库后返回此记录的主键
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param mdFullMethod 
	 * @param vParameters
	 * @return
	 */
	public long executeBindInsertReturnKey(String mdFullMethod, Map<String, Object> vParameters) throws DbException{
		NSQL nsql=NSQL.getNSQL(mdFullMethod,vParameters);
		return this.dataBase.executeBindInsertReturnKey(nsql.getExeSql(), nsql.getArgs());
	};
	
	public long insertReturnKey(String mdFullMethod, Map<String, Object> vParameters) throws DbException{
		return this.executeBindInsertReturnKey(mdFullMethod, vParameters);
	}

	/**
	 * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过DataBaseMd.setAutoCommit()方法
	 * 可以设置是否为事务操作，如果为事物操作，那么DataBaseMd里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
	 * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBaseMd.close()方法关闭数据库连接
	 * 
	 * @return
	 * @throws DbException
	 */
	public void setAutoCommit(boolean b) throws DbException{
		this.dataBase.setAutoCommit(b);
	};


	/**
	 * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过DataBaseMd.setAutoCommit()方法
	 * 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
	 * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBaseMd.close()方法关闭数据库连接
	 * 
	 * @return
	 * @throws DbException
	 */
	public boolean getAutoCommit() throws DbException{
		return this.dataBase.getAutoCommit();
	};

	/**
	 * 用于事务性操作的回滚，如果事务为分布式事务，则为空操作。
	 * 
	 * @throws DbException
	 */
	public void rollback() throws DbException{
		this.dataBase.rollback();
	};

	/**
	 * 回滚并且关闭连接
	 * 
	 * @throws DbException
	 */
	public void rollbackAndClose() throws DbException{
		this.dataBase.rollbackAndClose();
	};

	/**
	 * 判断资源和底层数据库连接是否关闭
	 * 
	 * @return
	 * @throws DbException
	 */
	public boolean isColsed() throws DbException{
		return this.dataBase.isColsed();
	};

	/**
	 * 事务性操作的事务的提交，当 {@link #setAutoCommit(boolean)}设为false，
	 * 会用到此方法，一般对于事务性操作会用到，如果 事务为分布式事务，则为空操作。
	 * 
	 * @throws DbException
	 */
	public void commit() throws DbException{
		this.dataBase.commit();
	};

	public void commitAndClose() throws DbException{
		this.dataBase.commitAndClose();
	};

	/**
	 * 返回的数组里每个值对应返回对应sql语句执行后更新的行数，如果为null表明执行失败，内部会自动回滚。
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param mdFullMethodNameList  为md文件里的方法名称数组，例如某一条方法名称格式为： com.ulwx.database.test.SysRightDao.md:getDataCount,
	 *   表示在com/ulwx/database/test/SysRightDao.md文件里查找getDataCount的方法
	 * @param vParametersArray  为参数数组
	 * @return 如果为null表明执行失败
	 * @throws DbException
	 */
	public int[] executeBindBatch(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException{
		ArrayList<String> sqlList=new ArrayList<String>();
		ArrayList<Map<Integer, Object>> varList=new ArrayList<Map<Integer, Object>>();
		for(int i=0; i<mdFullMethodNameList.length; i++){
			NSQL nsql=NSQL.getNSQL(mdFullMethodNameList[i],vParametersArray[i]);
			sqlList.add(nsql.getExeSql());
			varList.add(nsql.getArgs());
		}
		return this.dataBase.executeBindBatch(sqlList.toArray(new String[0]), 
				varList.toArray(new HashMap[0]));
	};
	
	public int[] update(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException{
		return this.executeBindBatch(mdFullMethodNameList, vParametersArray);
	}
	
	public int[] insert(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException{
		return this.executeBindBatch(mdFullMethodNameList, vParametersArray);
	}

	/**
	 * 批量更新操作（增，删，改），返回每条语句更新记录的行数
	 * <p>
	 * 此方法会默认自动关闭底层数据库连接，所以不需要 调用DataBaseMd.close()方法
	 * 
	 * @param mdFullMethodName
	 *            md文件里的方法名，格式为： com.ulwx.database.test.SysRightDao.md:getDataCount
	 * @param vParametersList
	 *            每条语句所携带的参数，每条语句对应一个Map，每个Map存放相应语句的参数
	 * @return 返回每条语句更新记录的行数，执行错误，会抛出异常
	 * @throws DbException
	 */
	public int[] executeBindBatch(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException{
		ArrayList<String> sqlList=new ArrayList<String>();
		ArrayList<Map<Integer, Object>> varList=new ArrayList<Map<Integer, Object>>();
		for(Map<String, Object> arg:vParametersList){
			NSQL nsql=NSQL.getNSQL(mdFullMethodName,arg);
			sqlList.add(nsql.getExeSql());
			varList.add(nsql.getArgs());
		}
		return this.dataBase.executeBindBatch(sqlList.toArray(new String[0]), 
				varList.toArray(new HashMap[0]));
	};
	
	public int[] update(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException{
		return this.executeBindBatch(mdFullMethodName, vParametersList);
	}
	
	public int[] insert(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException{
		return this.executeBindBatch(mdFullMethodName, vParametersList);
	}

	/**
	 * 批量更新操作（增，删，改），返回每条语句更新记录的行数
	 * @param mdFullMethodNameList :md文件里的方法名称列表，例如某一条方法名称格式为： com.ulwx.database.test.SysRightDao.md:getDataCount,
	 *   表示在com/ulwx/database/test/SysRightDao.md文件里查找getDataCount的方法
	 * @return
	 * @throws DbException
	 */
	public int[] executeBatch(ArrayList<String> mdFullMethodNameList) throws DbException{
		ArrayList<String> sqlList=new ArrayList<String>();
		for(String mdMethod:mdFullMethodNameList){
			NSQL nsql=NSQL.getNSQL(mdMethod,null);
			sqlList.add(nsql.getExeSql());
		}
		
		return this.dataBase.executeBatch(sqlList);
	};
	
	public int[] update(ArrayList<String> mdFullMethodNameList) throws DbException{
		return this.executeBatch(mdFullMethodNameList);
	}
	
	public int[] insert(ArrayList<String> mdFullMethodNameList) throws DbException{
		return this.executeBatch(mdFullMethodNameList);
	}

	/**
	 * 关闭底层资源，但不关闭数据库连接
	 */
	public void closer() throws DbException{
		this.dataBase.closer();
	};

	/**
	 * 关闭数据库连接，释放底层占用资源
	 */
	public void close(){
		this.dataBase.close();
	};



	public Connection getConnection(){
		return this.dataBase.getConnection();
	};

	@Override
	public <T> int insert(T insertObject) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insert(insertObject);
	}
	@Override
	public <T> long insertReturnKey(T insertObject) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertReturnKey(insertObject);
	}
	@Override
	public <T> int insert(T insertObject, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insert(insertObject, properties);
	}
	@Override
	public <T> long insertReturnKey(T insertObject, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertReturnKey(insertObject, properties);
	}
	@Override
	public <T> int[] insert(T[] objs) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insert(objs);
	}
	@Override
	public <T> int[] insert(T[] objs, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insert(objs, properties);
	}
	@Override
	public <T> int update(T updateObject, String beanKey) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.update(updateObject, beanKey);
	}
	@Override
	public <T> int update(T updateObject, String beanKey, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.update(updateObject, beanKey, properties);
	}
	@Override
	public <T> int[] update(T[] objects, String beanKey, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.update(objects, beanKey, properties);
	}
	@Override
	public <T> int[] update(T[] objects, String beanKey) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.update(objects, beanKey);
	}
	@Override
	public <T> int[] update(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.update(objects, beanKeys, properties);
	}
	@Override
	public <T> T queryOne(T selectObject, String selectProperties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.queryOne(selectObject, selectProperties);
	}
	@Override
	public <T> List<T> query(T selectObject, String selectProperties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.query(selectObject, selectProperties);
	}
	@Override
	public <T> List<T> query(T selectObject) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.query(selectObject);
	}
	

	
	@Override
	public <T> List<T> query(T selectObject, String selectProperties, int page, int perPage, PageBean pb)
			throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.query(selectObject, selectProperties, page, perPage, pb);
	}
	@Override
	public <T> List<T> query(T selectObject, int page, int perPage, PageBean pb) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.query(selectObject, page, perPage, pb);
	}
	@Override
	public <T> T queryOne(T selectObject) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.queryOne(selectObject);
	}
	@Override
	public <T> int del(T deleteObject, String deleteProperteis) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.del(deleteObject, deleteProperteis);
	}
	@Override
	public <T> int[] del(T[] deleteObjects, String deleteProperteis) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.del(deleteObjects, deleteProperteis);
	}
	@Override
	public <T> int[] del(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.del(deleteObjects, deletePropertiesArray);
	}
	@Override
	public <T> int insertWhole(T insertObject) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertWhole(insertObject);
	}
	@Override
	public <T> long insertWholeReturnKey(T insertObject) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertWholeReturnKey(insertObject);
	}
	@Override
	public <T> int insertWhole(T insertObject, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertWhole(insertObject, properties);
	}
	@Override
	public <T> long insertWholeReturnKey(T insertObject, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertWholeReturnKey(insertObject, properties);
	}
	@Override
	public <T> int[] insertWhole(T[] objs) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertWhole(objs);
	}
	@Override
	public <T> int[] insertWhole(T[] objs, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.insertWhole(objs, properties);
	}
	@Override
	public <T> int updateWhole(T updateObject, String beanKey) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.updateWhole(updateObject, beanKey);
	}
	@Override
	public <T> int updateWhole(T updateObject, String beanKey, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.updateWhole(updateObject, beanKey, properties);
	}
	@Override
	public <T> int[] updateWhole(T[] objects, String beanKey, String[] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.updateWhole(objects, beanKey, properties);
	}
	@Override
	public <T> int[] updateWhole(T[] objects, String beanKey) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.updateWhole(objects, beanKey);
	}
	@Override
	public <T> int[] updateWhole(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {
		// TODO Auto-generated method stub
		return this.dataBase.updateWhole(objects, beanKeys, properties);
	}

	
	public static String md(Class daoClass, String method) {
		return MD.md(daoClass, method);
	}
	
	
}
