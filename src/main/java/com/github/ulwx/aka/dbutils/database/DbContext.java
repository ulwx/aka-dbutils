package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.database.DataBase.MainSlaveModeConnectMode;
import com.github.ulwx.aka.dbutils.database.spring.DBTransInfo;
import com.github.ulwx.aka.dbutils.database.transaction.MDbTransactionManager;
import com.github.ulwx.aka.dbutils.database.transaction.TransactionContextElem;
import com.github.ulwx.aka.dbutils.database.transaction.TransactionContextInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

public class DbContext implements Serializable {

    private Map<String, Object> contextMap = new HashMap<String, Object>();

    private static final long serialVersionUID = -2348853553489025507L;

    private static volatile ThreadLocal<DbContext> localDbContext = new ThreadLocal<DbContext>() {
        @Override
        protected DbContext initialValue() {
            return new DbContext();
        }
    };

    private static String key_query_hint = "key_query_hint";
    private static String key_reflectclazz = "key_reflectclazz";
    private static String key_transaction_context_stack = "key_transaction_context_stack";
    private static String key_db_trans_info = "key_db_trans_info";
    public static String main_slave_mode_connectmode = "main_slave_mode_connectmode";
    public static String key_debug_log = "key_debug_log";
    public static String key_debug_sql_listener = "key_debug_sql_listener";

    public static String key_insert_generate_id = "key_insert_generate_id";
    public static String key_db_interceptor = "key_db_interceptor";
    public static String key_db_interceptor_data_info = "key_db_interceptor_data_info";


    /**
     * 同一线程的维度里是否允许打印debug日志，如果设置为true，则允许打印debug日志，否则不打印debug日志，注意：需要考虑到
     * 日志框架（如log4j）设置日志级别设置，如果设置为为error以上级别，则也是不会打印日志。
     *
     * @param permit
     */
    public static void permitDebugLog(boolean permit) {
        localDbContext.get().contextMap.put(key_debug_log, permit);
    }

    public static DBInterceptorInfo getDBInterceptorInfo() {
        DBInterceptorInfo ret = (DBInterceptorInfo) localDbContext.get().contextMap.get(key_db_interceptor_data_info);
        return ret;

    }

    public static void removeDBInterceptorInfo() {
        localDbContext.get().contextMap.remove(key_db_interceptor_data_info);
    }

    public static void setDBInterceptorInfo(DBInterceptorInfo dbInterceptorInfo) {
        localDbContext.get().contextMap.put(key_db_interceptor_data_info, dbInterceptorInfo);
    }

    /**
     * 获取设置的数据库操作拦截器
     *
     * @return
     */
    public static DBInterceptor getDBInterceptor() {
        DBInterceptor ret = (DBInterceptor) localDbContext.get().contextMap.get(key_db_interceptor);
        return ret;

    }

    /**
     * 设置拦截器，拦截数据库操作的执行前和执行后
     *
     * @param dbInterceptor
     */
    public static void setDBInterceptor(DBInterceptor dbInterceptor) {
        localDbContext.get().contextMap.put(key_db_interceptor, dbInterceptor);
    }

    /**
     * 删除拦截器
     */
    public static void removeDBInterceptor() {
        localDbContext.get().contextMap.remove(key_db_interceptor);
    }



    /**
     *
     * @param idName   属性名称
     * @param sequenceName 序列名称和数据库里定义的序列名称一致，会根据指定的序列名称生成SQL语句。如果以"sql:"前缀开始，则直接执行"sql:"的sql语句，不会根据sequenceName自动
     *                     生成SQL语句。
     * @param  callBeforeInsert  <br/>true：表明在插入操作前执行查询序列值（查询序列下一个值），查询的序列值会塞进后续的插入语句里，整个操作返回时
     *                           会把序列值存入到对象里idName指定的属性里，如果是执行的XXReturnKey方法，则同时会把序列值返回。
     *                           <br/>false：表明在插入操作之后才会执行查询序列（即查询序列当前值），查询的序列值存入对象里idName指定的属性里，
     *                           如果是执行的XXReturnKey方法，则在会把序列值返回。注意当为false时，不能用于多对象插入操作方法。
     */
    public static void setGenerateIDForInsert(String idName, String sequenceName,boolean callBeforeInsert) {
        SequenceInfo sequenceInfo=new SequenceInfo();
        sequenceInfo.setIdName(idName);
        sequenceInfo.setSequenceName(sequenceName);
        sequenceInfo.setCallBeforeInsert(callBeforeInsert);
        localDbContext.get().contextMap.put(key_insert_generate_id, sequenceInfo);
    }



    /**
     * 获取设置的调试SQL监听器，回调事件在数据库操作真正执行前发生
     *
     * @return
     */
    public static  SequenceInfo getGenerateIDForInsert() {
        SequenceInfo  ret = ( SequenceInfo ) localDbContext.get().contextMap.get(key_insert_generate_id);
        return ret;

    }
    public static  SequenceInfo removeGenerateIDForInsert() {
        return  (SequenceInfo) localDbContext.get().contextMap.remove(key_insert_generate_id);
    }
    /**
     * 设置监听调试sql的监听器，回调事件在数据库操作真正执行前发生
     *
     * @param listener ：监听器，监听调试sql语句
     */
    public static void setDebugSQLListener(Consumer<String> listener) {
        localDbContext.get().contextMap.put(key_debug_sql_listener, listener);
    }

    /**
     * 获取设置的调试SQL监听器，回调事件在数据库操作真正执行前发生
     *
     * @return
     */
    public static Consumer<String> getDebugSQLListener() {
        Consumer<String> ret = (Consumer<String>) localDbContext.get().contextMap.get(key_debug_sql_listener);
        return ret;

    }

    /**
     * 删除调试SQL监听器
     *
     * @return
     */
    public static Consumer<String> removeDebugSQLListener() {
        return (Consumer<String>) localDbContext.get().contextMap.remove(key_debug_sql_listener);
    }

    /**
     * 返回是否允许打印debug日志标志
     *
     * @return
     */
    public static boolean permitDebugLog() {
        Boolean ret = (Boolean) localDbContext.get().contextMap.get(key_debug_log);
        if (ret == null || ret) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置生成SQL语句时使用javaBean的对象（具有父类或接口）的哪个继承层次类的类名转换成表名
     *
     * @param clzz 指定哪个继承层级的类，利用指定类的类名转换成表名
     */
    public static void setReflectClass(Class<?> clzz) {
        localDbContext.get().contextMap.put(key_reflectclazz, clzz);
    }
    public static Class<?> getReflectClass() {
        return (Class<?>) localDbContext.get().contextMap.get(key_reflectclazz);
    }
    public static void clearReflectClass() {
        localDbContext.get().contextMap.remove(key_reflectclazz);
    }

    /**
     * QueryHint只能用于对象查询函数上
     * @param hint
     */
    public static void setQueryHint(QueryHint hint) {
        localDbContext.get().contextMap.put(key_query_hint, hint);
    }
    public static QueryHint getQueryHint() {
        return (QueryHint) localDbContext.get().contextMap.get(key_query_hint);
    }
    public static void clearQueryHint() {
        localDbContext.get().contextMap.remove(key_query_hint);
    }

    public static void setDbTransInfo(DBTransInfo dbt) {
        localDbContext.get().contextMap.put(key_db_trans_info, dbt);
    }
    public static void clearDbTransInfo() {
        localDbContext.get().contextMap.remove(key_db_trans_info);
    }
    public static DBTransInfo getDbTransInfo() {
        return (DBTransInfo) localDbContext.get().contextMap.get(key_db_trans_info);
    }

    /**
     * 获取主从连接模式
     *
     * @return
     */
    public static MainSlaveModeConnectMode getMainSlaveModeConnectMode() {
        MainSlaveModeConnectMode mainSlaveModeConnectMode =
                (MainSlaveModeConnectMode) localDbContext.get().contextMap.get(main_slave_mode_connectmode);
        MainSlaveModeConnectMode useMainSlaveModeConnectMode = null;
        if (mainSlaveModeConnectMode == null) {
            useMainSlaveModeConnectMode = MainSlaveModeConnectMode.Connect_MainServer;
        } else {
            useMainSlaveModeConnectMode = mainSlaveModeConnectMode;
        }
        return useMainSlaveModeConnectMode;
    }

    /**
     * 设置主从连接模式，即是连接主库，从库还是自动识别
     *
     * @param mainSlaveModeConnectMode
     */
    public static void setMainSlaveModeConnectMode(MainSlaveModeConnectMode mainSlaveModeConnectMode) {
        localDbContext.get().contextMap.put(main_slave_mode_connectmode, mainSlaveModeConnectMode);
    }

    /**
     * 删除主从连接模式
     */
    public static void removeMainSlaveModeConnectMode() {
        localDbContext.get().contextMap.remove(main_slave_mode_connectmode);
    }




    public static Stack<Map<String, TransactionContextElem>> getTransactionContextStack() {
        @SuppressWarnings("unchecked")
        Stack<Map<String, TransactionContextElem>> stack = (Stack<Map<String, TransactionContextElem>>) localDbContext.get().contextMap
                .get(key_transaction_context_stack);
        if (stack == null) {
            stack = new Stack<Map<String, TransactionContextElem>>();
            localDbContext.get().contextMap.put(key_transaction_context_stack, stack);
        }
        return stack;
    }

    public static Map<String, TransactionContextElem> getTransactionContextStackTopContext(Stack<Map<String, TransactionContextElem>> stack) {

        try {
            return stack.peek();
        } catch (Exception e) {
            return null;
        }
    }

    public static TransactionContextInfo getTransactionStart(Map<String, TransactionContextElem> context) {
        if (context.get(MDbTransactionManager._transaction_start) != null) {
            return ((TransactionContextInfo) context.get(MDbTransactionManager._transaction_start));
        } else {//说明是顶级

        }
        return null;
    }

    public static Integer getTransactionLevel(Map<String, TransactionContextElem> context) {
        TransactionContextInfo transactionDataBaseTrace = getTransactionStart(context);
        if (transactionDataBaseTrace != null) {
            return transactionDataBaseTrace.getLevel();
        }
        return null;
    }

    public static TransactionContextInfo findNestStartInTransactionContextStack() {
        Stack<Map<String, TransactionContextElem>> stack = DbContext.getTransactionContextStack();

        for (int i = stack.size() - 1; i >= 0; i--) {
            Map<String, TransactionContextElem> tempContext = stack.get(i);
            TransactionContextInfo transactionStart = (TransactionContextInfo) tempContext.get(MDbTransactionManager._transaction_start);
            if (transactionStart.getNestedLevel() == 0) {//
                return transactionStart;
            }

        }
        return null;

    }

    public static DataBaseDecorator findDataBaseInTransactionContextStack(String dbPoolXmlFileNameAndDbPoolName) {
        Stack<Map<String, TransactionContextElem>> stack = DbContext.getTransactionContextStack();

        for (int i = stack.size() - 1; i >= 0; i--) {
            Map<String, TransactionContextElem> tempContext = stack.get(i);
            DataBaseDecorator db = (DataBaseDecorator) tempContext.get(dbPoolXmlFileNameAndDbPoolName);
            TransactionContextInfo transactionStart = (TransactionContextInfo) tempContext.get(MDbTransactionManager._transaction_start);
            if (transactionStart.getLevel() > 0) {//
                if (db != null) {
                    return db;
                } else {
                    //继续向上查找
                }
            } else {  //截止查找
                if (db != null) { //到达顶级事务上下文
                    return db;
                }
                break;
            }

        }
        return null;

    }

}
