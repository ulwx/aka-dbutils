package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.dialect.DBMS;
import com.github.ulwx.aka.dbutils.database.nsql.MDTemplate;
import com.github.ulwx.aka.dbutils.database.nsql.NSQL;
import com.github.ulwx.aka.dbutils.tool.PageBean;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDataBaseImpl implements  MDataBase {

    private DataBase dataBase;
    public MDataBaseImpl() {

    }
    public MDataBaseImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }
    @Override
    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public DBMS getDataBaseType() {
        return this.dataBase.getDataBaseType();
    }

    /**
     * @param packageFullName :sql脚本所在都包，例如com.xx.yy
     * @param sqlFileName     ：sql脚本的文件名，例如 db.sql
     * @param throwWarning 脚本执行时如果出现warning，是否退出并回滚
     * @param vParameters 脚本里用到的参数
     * @return 执行成功的结果 ，否则抛出异常
     * @throws DbException
     */
    @Override
    public String exeScript(String packageFullName, String sqlFileName, boolean throwWarning) throws DbException {

        packageFullName = packageFullName.replace(".", "/");
        String source="/" + packageFullName + "/" + sqlFileName;
        InputStream in = this.getClass().getResourceAsStream(source);
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            Map<String, Object> args=new HashMap<>();
            ScriptOption scriptOption=new ScriptOption();
            scriptOption.setSource(source);
            scriptOption.setFromMDMethod(false);
            args.put(ScriptOption.class.getName(),scriptOption);
            return this.dataBase.exeScript(bufReader, throwWarning,args);
        } catch (Exception e) {
            if(e instanceof DbException){
                throw (DbException)e;
            }
            throw new DbException(e);
        }

    }



    @Override
    public String exeScript(String mdFullMethodName,String delimiters, Map<String, Object> args) throws DbException {
        String[] strs = mdFullMethodName.split(":");
        String sql=null;
        try {
            sql = MDTemplate.getResultString(strs[0], strs[1], args);
            sql= sql.replace(delimiters, ";\n");

        } catch (Exception e) {
            if(e instanceof DbException){
                throw (DbException)e;
            }
            throw new DbException(e);
        }

        StringReader sr = new StringReader(sql);
        ScriptOption scriptOption=new ScriptOption();
        scriptOption.setSource(mdFullMethodName);
        scriptOption.setFromMDMethod(true);
        args.put(ScriptOption.class.getName(),scriptOption);
        return this.dataBase.exeScript(sr,false,args);

    }

    private static String getCountSql(String countSqlMdFullMethodName, Map<String, Object> args) {
        String countSql = null;
        countSqlMdFullMethodName=StringUtils.trim(countSqlMdFullMethodName);
        if (StringUtils.hasText(countSqlMdFullMethodName)) {
            if (StringUtils.isNumber(countSqlMdFullMethodName)
                    || countSqlMdFullMethodName.equals("-1")) {
                countSql = countSqlMdFullMethodName;
            } else {
                NSQL cnsql = NSQL.getNSQL(countSqlMdFullMethodName, args);
                countSql = cnsql.getExeSql();
            }
        }
        return countSql;
    }

    @Override
    public DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                         PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryForResultSet(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, countSql);

    }

    @Override
    public DataBaseSet queryForResultSet(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryForResultSet(nsql.getExeSql(), nsql.getArgs());

    }


    @Override
    public <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, int page, int perPage, PageBean pageBean,
                                 RowMapper<T> rowMapper, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryList(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, rowMapper, countSql);
    }


    @Override
    public List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args, int page, int perPage,
                                              PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryMap(nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, countSql);
    }


    @Override
    public <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryList(clazz, nsql.getExeSql(), nsql.getArgs());
    }

    @Override
    public <T> T queryOne(Class<T> clazz, String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryOne(clazz, nsql.getExeSql(), nsql.getArgs());
    }


    @Override
    public <T> List<T> queryListByOne2One(Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> args,
                                 QueryMapNestOne2One[] queryMapNestList) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryListByOne2One(clazz, sqlPrefix, nsql.getExeSql(), nsql.getArgs(), queryMapNestList);
    }


    @Override
    public <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page,
                                 int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryList(clazz, nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, countSql);
    }


    @Override
    public <T> List<T> queryListByOne2One(Class<T> clazz, String sqlPrefix, String mdFullMethodName,
                                 Map<String, Object> args, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                                 PageBean pageBean, String countSqlMdFullMethodName) throws DbException {

        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryListByOne2One(clazz, sqlPrefix, nsql.getExeSql(), nsql.getArgs(), queryMapNestList, page, perPage, pageBean, countSql);
    }


    @Override
    public <T> List<T> queryListByOne2Many(Class<T> clazz, String sqlPrefix, String[] parentBeanKeys, String mdFullMethodName,
                                 Map<String, Object> args, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryListByOne2Many(clazz, sqlPrefix, parentBeanKeys, nsql.getExeSql(), nsql.getArgs(), queryMapNestList);
    }


    @Override
    public <T> List<T> queryList(String mdFullMethodName, Map<String, Object> args, RowMapper<T> rowMapper) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryList(nsql.getExeSql(), nsql.getArgs(), rowMapper);

    }

    /**
     * 分页查询，返回的一页结果为Map列表
     *
     * @param mdFullMethodName 在md文件里定位sql查询语句的方法名，格式为： {@code com.github.ulwx.database.test.SysRightDao.md:getDataCount}，
     *                         其中 com.github.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
     *                         冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
     * @param args             参数
     * @return
     * @throws DbException
     */
    @Override
    public List<Map<String, Object>> queryMap(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);

        return this.dataBase.queryMap(nsql.getExeSql(), nsql.getArgs());

    }

    @Override
    public int del(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.del(nsql.getExeSql(), nsql.getArgs());
    }


    @Override
    public int update(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.update(nsql.getExeSql(), nsql.getArgs());
    }

    /**
     * 调用存储过程，用法如下：
     * <code><pre>
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
     * }</pre></code>
     *
     * @param mdFullMethodName   在md文件里定位sql查询语句的方法名，格式为： {@code com.github.ulwx.database.test.SysRightDao.md:getDataCount}，
     *                           其中 com.github.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
     *                           冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
     * @param parms              输入参数
     * @param outPramsValues     存放输出参数的返回值，根据parms(输入法参数)里的out,inout对应，如果输入参数为上面的例子所示，那么outPramsValues可能输入如下：
     *
     * @param returnDataBaseSets 需返回值的结果集
     * @return
     * @throws DbException
     */

    @Override
    public void callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
                             List<DataBaseSet> returnDataBaseSets) throws DbException {

        NSQL nsql = NSQL.getNSQL(mdFullMethodName, parms, true);
        Map<Integer, Object> args = nsql.getArgs();
        Map<String, Object> argsNew = new HashMap<String, Object>();
        for (Integer key : args.keySet()) {
            TResult2<String, Object> val = (TResult2<String, Object>) args.get(key);
            argsNew.put(key +":"+ val.getFirstValue(), val.getSecondValue());
        }
        Map<Integer, Object> outPramsValuesNew = new HashMap<Integer, Object>();
        this.dataBase.callStoredPro(nsql.getExeSql(), argsNew, outPramsValuesNew, returnDataBaseSets);
        Map<Integer, String> argsToKey = nsql.getArgsToKey();
        for (Integer key : outPramsValuesNew.keySet()) {
            Object val = outPramsValuesNew.get(key);
            outPramsValues.put(argsToKey.get(key), val);
        }


    }


    @Override
    public int insert(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.insert(nsql.getExeSql(), nsql.getArgs());
    }

    @Override
    public long insertReturnKey(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.insertReturnKey(nsql.getExeSql(), nsql.getArgs());
    }


    @Override
    public int[] update(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (int i = 0; i < mdFullMethodNameList.length; i++) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodNameList[i], vParametersArray[i]);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.update(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));
    }

    @Override
    public int[] insert(String[] mdFullMethodNameList, Map<String, Object>[] vParametersArray) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (int i = 0; i < mdFullMethodNameList.length; i++) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodNameList[i], vParametersArray[i]);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.insert(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));
    }


    @Override
    public int[] update(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (Map<String, Object> arg : vParametersList) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodName, arg);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.update(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));

    }

    @Override
    public int[] insert(String mdFullMethodName, List<Map<String, Object>> vParametersList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<Map<Integer, Object>> varList = new ArrayList<Map<Integer, Object>>();
        for (Map<String, Object> arg : vParametersList) {
            NSQL nsql = NSQL.getNSQL(mdFullMethodName, arg);
            sqlList.add(nsql.getExeSql());
            varList.add(nsql.getArgs());
        }
        return this.dataBase.insert(sqlList.toArray(new String[0]),
                varList.toArray(new HashMap[0]));
    }


    @Override
    public int[] update(ArrayList<String> mdFullMethodNameList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        for (String mdMethod : mdFullMethodNameList) {
            NSQL nsql = NSQL.getNSQL(mdMethod, null);
            sqlList.add(nsql.getExeSql());
        }
        return this.dataBase.update(sqlList);
    }

    @Override
    public int[] insert(ArrayList<String> mdFullMethodNameList) throws DbException {
        ArrayList<String> sqlList = new ArrayList<String>();
        for (String mdMethod : mdFullMethodNameList) {
            NSQL nsql = NSQL.getNSQL(mdMethod, null);
            sqlList.add(nsql.getExeSql());
        }
        return this.dataBase.insert(sqlList);
    }


    @Override
    public Connection getConnection() {
        return this.dataBase.getConnection();
    }

    @Override
    public <T> int insertBy(T insertObject) throws DbException {

        return this.dataBase.insertBy(insertObject);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject) throws DbException {

        return this.dataBase.insertReturnKeyBy(insertObject);
    }

    @Override
    public <T> int[] insertBy(T[] objs) throws DbException {

        return this.dataBase.insertBy(objs);
    }


    @Override
    public <T> List<T> queryListBy(T selectObject) throws DbException {

        return this.dataBase.queryListBy(selectObject);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException {

        return this.dataBase.queryListBy(selectObject, page, perPage, pb);
    }

    @Override
    public <T> T queryOneBy(T selectObject) throws DbException {
        return this.dataBase.queryOneBy(selectObject);
    }



    /**
     * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBaseMd里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    @Override
    public void setAutoCommit(boolean b) throws DbException {
        this.dataBase.setAutoCommit(b);
    }



    /**
     * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    @Override
    public boolean getAutoCommit() throws DbException {
        return this.dataBase.getAutoCommit();
    }


    /**
     * 用于事务性操作的回滚，如果事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    @Override
    public void rollback() throws DbException {
        this.dataBase.rollback();
    }



    /**
     * 判断资源和底层数据库连接是否关闭
     *
     * @return
     * @throws DbException
     */
    @Override
    public boolean isColsed() throws DbException {
        return this.dataBase.isColsed();
    }


    /**
     * 事务性操作的事务的提交，当 {@link #setAutoCommit(boolean)}设为false，
     * 会用到此方法，一般对于事务性操作会用到，如果 事务为分布式事务，则为空操作。
     *
     * @throws DbException
     */
    @Override
    public void commit() throws DbException {
        this.dataBase.commit();
    }


    /**
     * 关闭数据库连接，释放底层占用资源
     */
    @Override
    public void close() {
        this.dataBase.close();
    }

    @Override
    public <T> int insertBy(T insertObject, boolean includeNull) throws DbException {
        return dataBase.insertBy(insertObject, includeNull);
    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties) throws DbException {
        return dataBase.insertBy(insertObject, insertProperties);
    }

    @Override
    public <T> int insertBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        return dataBase.insertBy(insertObject, insertProperties, includeNull);
    }

    @Override
    public <T> int[] insertBy(T[] objs, boolean includeNull) throws DbException {
        return dataBase.insertBy(objs, includeNull);
    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties) throws DbException {
        return dataBase.insertBy(objs, insertProperties);
    }

    @Override
    public <T> int[] insertBy(T[] objs, Object[] insertProperties, boolean includeNull) throws DbException {
        return dataBase.insertBy(objs, insertProperties, includeNull);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, boolean includeNull) throws DbException {
        return dataBase.insertReturnKeyBy(insertObject, includeNull);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties) throws DbException {
        return dataBase.insertReturnKeyBy(insertObject, insertProperties);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, Object[] insertProperties, boolean includeNull) throws DbException {
        return dataBase.insertReturnKeyBy(insertObject, insertProperties, includeNull);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis) throws DbException {
        return dataBase.updateBy(updateObject, whereProperteis);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObject, whereProperties, includeNull);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperteis, Object[] updateProperties) throws DbException {
        return dataBase.updateBy(updateObject, whereProperteis, updateProperties);
    }

    @Override
    public <T> int updateBy(T updateObject, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObject, whereProperties, updateProperties, includeNull);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis, Object[] updateProperties) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperteis, updateProperties);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, Object[] updateProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperties, updateProperties, includeNull);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperteis) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperteis);
    }

    @Override
    public <T> int[] updateBy(T[] updateObjects, Object[] whereProperties, boolean includeNull) throws DbException {
        return dataBase.updateBy(updateObjects, whereProperties, includeNull);
    }

    @Override
    public <T> T queryOneBy(T selectObject, Object[] whereProperteis) throws DbException {
        return dataBase.queryOneBy(selectObject, whereProperteis);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis) throws DbException {
        return dataBase.queryListBy(selectObject, whereProperteis);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, Object[] whereProperteis, int page, int perPage, PageBean pb) throws DbException {
        return dataBase.queryListBy(selectObject, whereProperteis, page, perPage, pb);
    }

    @Override
    public <T> int delBy(T deleteObject, Object[] whereProperteis) throws DbException {
        return dataBase.delBy(deleteObject, whereProperteis);
    }

    @Override
    public <T> int[] delBy(T[] deleteObjects, Object[] whereProperteis) throws DbException {
        return dataBase.delBy(deleteObjects, whereProperteis);
    }
}
