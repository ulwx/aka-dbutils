package com.github.ulwx.database;

import com.github.ulwx.database.nsql.NSQL;
import com.github.ulwx.tool.PageBean;
import com.github.ulwx.tool.support.StringUtils;
import com.github.ulwx.tool.support.type.TResult2;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDataBaseImpl implements  MDataBase {

    private DataBase dataBase;

    public MDataBaseImpl() {

    }

    @Override
    public boolean isExternalControlConClose() {
        return this.dataBase.isExternalControlConClose();
    }

    public MDataBaseImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }


    @Override
    public boolean isMainSlaveMode() {
        return this.dataBase.isMainSlaveMode();
    }


    @Override
    public void setMainSlaveMode(boolean mainSlaveMode) {
        this.dataBase.setMainSlaveMode(mainSlaveMode);
    }

    @Override
    public boolean getInternalConnectionAutoCommit() throws DbException {
        return this.dataBase.getInternalConnectionAutoCommit();
    }

    @Override
    public String getDataBaseType() {
        return this.dataBase.getDataBaseType();
    }

    /**
     * @param packageFullName :sql脚本所在都包，例如com.xx.yy
     * @param sqlFileName     ：sql脚本的文件名，例如 db.sql
     * @return 执行成功的结果 ，否则抛出异常
     * @throws DbException
     */
    @Override
    public String exeScript(String packageFullName, String sqlFileName) throws DbException {
        return this.exeScript(packageFullName, sqlFileName, null);

    }

    /**
     * @param packageFullName :sql脚本所在都包，例如com.xx.yy
     * @param sqlFileName     ：sql脚本的文件名，例如 db.sql
     * @param logWriter       ：日志的输出
     * @return 执行成功的结果 ，否则抛出异常
     * @throws DbException
     */
    @Override
    public String exeScript(String packageFullName, String sqlFileName, PrintWriter logWriter) throws DbException {
        packageFullName = packageFullName.replace(".", "/");
        InputStream in = this.getClass().getResourceAsStream("/" + packageFullName + "/" + sqlFileName);
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            if (logWriter == null) {
                return this.dataBase.exeScript(bufReader);
            } else {
                return this.dataBase.exeScript(bufReader, logWriter);
            }
        } catch (UnsupportedEncodingException e) {
            throw new DbException(e.getMessage(), e);
        }

    }


    /**
     * @param mdFullMethodName：定位md文件里的方法，格式为：{@code com.github.ulwx.database.test.SysRightDao.md:getDataCount} ,
     *                                               表示定位到com/ulwx/database/test/SysRightDao.md文件里的{@code codegetDataCount}方法
     * @param vParameters                            :参数，在md文件中，只能用${xx},不能用#{xx}
     * @return 执行成功的结果 ，否则抛出异常
     * @throws DbException
     */
    @Override
    public String exeScript(String mdFullMethodName, Map<String, Object> args) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        StringReader sr = new StringReader(nsql.getExeSql());
        return this.dataBase.exeScript(sr);

    }

    private static String getCountSql(String countSqlMdFullMethodName, Map<String, Object> args) {
        String countSql = null;
        if (StringUtils.hasText(countSqlMdFullMethodName)) {
            if (StringUtils.isNumber(countSqlMdFullMethodName)) {
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
    public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String mdFullMethodName, Map<String, Object> args,
                                 QueryMapNestOne2One[] queryMapNestList) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryList(clazz, sqlPrefix, nsql.getExeSql(), nsql.getArgs(), queryMapNestList);
    }


    @Override
    public <T> List<T> queryList(Class<T> clazz, String mdFullMethodName, Map<String, Object> args, int page,
                                 int perPage, PageBean pageBean, String countSqlMdFullMethodName) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryList(clazz, nsql.getExeSql(), nsql.getArgs(), page, perPage, pageBean, countSql);
    }


    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String mdFullMethodName,
                                 Map<String, Object> args, QueryMapNestOne2One[] queryMapNestList, int page, int perPage,
                                 PageBean pageBean, String countSqlMdFullMethodName) throws DbException {

        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        String countSql = getCountSql(countSqlMdFullMethodName, args);
        return this.dataBase.queryList(clazz, sqlPrefix, nsql.getExeSql(), nsql.getArgs(), queryMapNestList, page, perPage, pageBean, countSql);
    }


    @Override
    public <T> List<T> queryList(Class<T> clazz, String sqlPrefix, String beanKey, String mdFullMethodName,
                                 Map<String, Object> args, QueryMapNestOne2Many[] queryMapNestList) throws DbException {
        NSQL nsql = NSQL.getNSQL(mdFullMethodName, args);
        return this.dataBase.queryList(clazz, sqlPrefix, beanKey, nsql.getExeSql(), nsql.getArgs(), queryMapNestList);
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
     * 调用存储过程
     *
     * @param mdFullMethodName   在md文件里定位sql查询语句的方法名，格式为： {@code com.github.ulwx.database.test.SysRightDao.md:getDataCount}，
     *                           其中 com.github.ulwx.database.test.SysRightDao.md为包路径名称定位到com/ulwx/database/test/SysRightDao.md文件，
     *                           冒号（:）后面的getDataCount为方法名，此方法名称下方为sql模板语句
     * @param parms              用法举例如下：
     *                           <code>
     *                           <pre>
     *                                                                               用法：
     *                                                                               parms.put("name","中");//默认为in类型
     *                                                                               parms.put("sex:in","男");
     *                                                                               parms.put("age:in",26);
     *                                                                               parms.put("total:out",int.class);
     *                                                                               parms.put("createTime:out",java.util.date.class);
     *                                                                               parms.put("num:inout",new Long(44));
     *                                                                               </pre>
     *                           </code>
     * @param outPramsValues     存放输出参数的返回值，根据parms(输入法参数)里的out,inout对应，如果输入参数为上面的例子所示，那么outPramsValues可能输入如下：
     *                           <pre>
     *                                                                                 {
     *                                                                                   total:45556,
     *                                                                                   createTime:"2015-09-23 12:34:56"
     *                                                                                   num:34456
     *                                                                                  }
     *                                                                               </pre>
     * @param returnDataBaseSets 需返回值的结果集
     * @return
     * @throws DbException
     */

    @Override
    public int callStoredPro(String mdFullMethodName, Map<String, Object> parms, Map<String, Object> outPramsValues,
                             List<DataBaseSet> returnDataBaseSets) throws DbException {

        NSQL nsql = NSQL.getNSQL(mdFullMethodName, parms, true);
        Map<Integer, Object> args = nsql.getArgs();
        Map<String, Object> argsNew = new HashMap<String, Object>();
        for (Integer key : args.keySet()) {
            TResult2<String, Object> val = (TResult2<String, Object>) args.get(key);
            argsNew.put(key + val.getFirstValue(), val.getSecondValue());
        }
        Map<Integer, Object> outPramsValuesNew = new HashMap<Integer, Object>();
        int ret = this.dataBase.callStoredPro(nsql.getExeSql(), argsNew, outPramsValuesNew, returnDataBaseSets);
        Map<Integer, String> argsToKey = nsql.getArgsToKey();
        for (Integer key : outPramsValuesNew.keySet()) {
            Object val = outPramsValuesNew.get(key);
            outPramsValues.put(argsToKey.get(key), val);
        }

        return ret;

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
    public <T> int insertBy(T insertObject, String[] properties) throws DbException {

        return this.dataBase.insertBy(insertObject, properties);
    }

    @Override
    public <T> long insertReturnKeyBy(T insertObject, String[] properties) throws DbException {

        return this.dataBase.insertReturnKeyBy(insertObject, properties);
    }

    @Override
    public <T> int[] insertBy(T[] objs) throws DbException {

        return this.dataBase.insertBy(objs);
    }

    @Override
    public <T> int[] insertBy(T[] objs, String[] properties) throws DbException {

        return this.dataBase.insertBy(objs, properties);
    }

    @Override
    public <T> int updateBy(T updateObject, String beanKey) throws DbException {

        return this.dataBase.updateBy(updateObject, beanKey);
    }

    @Override
    public <T> int updateBy(T updateObject, String beanKey, String[] properties) throws DbException {

        return this.dataBase.updateBy(updateObject, beanKey, properties);
    }

    @Override
    public <T> int[] updateBy(T[] objects, String beanKey, String[] properties) throws DbException {

        return this.dataBase.updateBy(objects, beanKey, properties);
    }

    @Override
    public <T> int[] updateBy(T[] objects, String beanKey) throws DbException {

        return this.dataBase.updateBy(objects, beanKey);
    }

    @Override
    public <T> int[] updateBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {

        return this.dataBase.updateBy(objects, beanKeys, properties);
    }

    @Override
    public <T> T queryOneBy(T selectObject, String selectProperties) throws DbException {

        return this.dataBase.queryOneBy(selectObject, selectProperties);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, String selectProperties) throws DbException {

        return this.dataBase.queryListBy(selectObject, selectProperties);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject) throws DbException {

        return this.dataBase.queryListBy(selectObject);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, String selectProperties, int page, int perPage, PageBean pb)
            throws DbException {

        return this.dataBase.queryListBy(selectObject, selectProperties, page, perPage, pb);
    }

    @Override
    public <T> List<T> queryListBy(T selectObject, int page, int perPage, PageBean pb) throws DbException {

        return this.dataBase.queryListBy(selectObject, page, perPage, pb);
    }

    @Override
    public <T> T queryOneBy(T selectObject) throws DbException {
        return this.dataBase.queryOneBy(selectObject);
    }

    @Override
    public <T> int delBy(T deleteObject, String deleteProperteis) throws DbException {
        return this.dataBase.delBy(deleteObject, deleteProperteis);
    }

    @Override
    public <T> int[] delBy(T[] deleteObjects, String deleteProperteis) throws DbException {
        return this.dataBase.delBy(deleteObjects, deleteProperteis);
    }

    @Override
    public <T> int[] delBy(Object[] deleteObjects, String[] deletePropertiesArray) throws DbException {

        return this.dataBase.delBy(deleteObjects, deletePropertiesArray);
    }

    @Override
    public <T> int insertWholeBy(T insertObject) throws DbException {

        return this.dataBase.insertWholeBy(insertObject);
    }

    @Override
    public <T> long insertWholeReturnKeyBy(T insertObject) throws DbException {

        return this.dataBase.insertWholeReturnKeyBy(insertObject);
    }

    @Override
    public <T> int insertWholeBy(T insertObject, String[] properties) throws DbException {

        return this.dataBase.insertWholeBy(insertObject, properties);
    }

    @Override
    public <T> long insertWholeReturnKeyBy(T insertObject, String[] properties) throws DbException {

        return this.dataBase.insertWholeReturnKeyBy(insertObject, properties);
    }

    @Override
    public <T> int[] insertWholeBy(T[] objs) throws DbException {

        return this.dataBase.insertWholeBy(objs);
    }

    @Override
    public <T> int[] insertWholeBy(T[] objs, String[] properties) throws DbException {

        return this.dataBase.insertWholeBy(objs, properties);
    }

    @Override
    public <T> int updateWholeBy(T updateObject, String beanKey) throws DbException {

        return this.dataBase.updateWholeBy(updateObject, beanKey);
    }

    @Override
    public <T> int updateWholeBy(T updateObject, String beanKey, String[] properties) throws DbException {

        return this.dataBase.updateWholeBy(updateObject, beanKey, properties);
    }

    @Override
    public <T> int[] updateWholeBy(T[] objects, String beanKey, String[] properties) throws DbException {

        return this.dataBase.updateWholeBy(objects, beanKey, properties);
    }

    @Override
    public <T> int[] updateWholeBy(T[] objects, String beanKey) throws DbException {

        return this.dataBase.updateWholeBy(objects, beanKey);
    }

    @Override
    public <T> int[] updateWholeBy(Object[] objects, String[] beanKeys, String[][] properties) throws DbException {

        return this.dataBase.updateWholeBy(objects, beanKeys, properties);
    }

    /**
     * 设置是否为事务操作，false表明为事务操作（事务分为常规事务和分布式事务），事务操作即多个语句功能一个数据库连接。通过DataBaseMd.setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBaseMd里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBaseMd.close()方法关闭数据库连接
     *
     * @return
     * @throws DbException
     */
    @Override
    public void setAutoCommit(boolean b) throws DbException {
        this.dataBase.setAutoCommit(b);
    }



    /**
     * 返回是否为事务操作，false表明为事务操作，事务操作即多个语句功能一个数据库连接。通过DataBaseMd.setAutoCommit()方法
     * 可以设置是否为事务操作，如果为事物操作，那么DataBase里所有默认自动关闭底层数据库连接的方法，都不会自动关闭
     * 底层数据库连接，同一个事务里的所有方法共享一个数据库连接。用户必须手动通过DataBaseMd.close()方法关闭数据库连接
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


}
