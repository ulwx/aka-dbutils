package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MDbTransactionManager {
    private static Logger log = LoggerFactory.getLogger(MDbTransactionManager.class);
    public static String _transaction_start = "_transaction_start";

    private static void start() {
        start(false);
    }

    private static void startNew() {
        start(true);
    }

    /**
     * 总是新开启一个事务执行ServiceLogicHasReturnValue#call()的逻辑。如果当前上下文里存在事务则挂起，
     * 执行当前新建事务完成以后，上下文事务恢复再执行。如果运行新建事务时抛出异常，新建的事务会回滚，并且会再次
     * 抛出异常，此异常如果在外部不被捕获并处理的话，会引发外部的事务回滚。
     * @param serviceLogic 带有返回值的业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @param <R>
     * @return
     * @throws DbException
     */
    public static <R> R executeNew(ServiceLogicHasReturnValue<R> serviceLogic)
            throws DbException {
        try {
            startNew();
            R ret = serviceLogic.call();
            commit();
            return ret;

        } catch (Exception e) {
            rollback();
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            end();
        }
    }

    /**
     * 总是新开启一个事务执行ServiceLogic#call()的逻辑。如果当前上下文里存在事务则挂起，
     * 执行当前新建事务完成以后，上下文事务恢复再执行。如果运行新建事务时抛出异常，新建的事务会回滚，并且会再次抛出异常，
     * 此异常如果在外部不被捕获并处理的话，会引发外部的事务回滚。
     * @param serviceLogic  业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @throws DbException
     */
    public static void executeNew(ServiceLogic serviceLogic)
            throws DbException {
        try {
            startNew();
            serviceLogic.call();
            commit();

        } catch (Exception e) {
            rollback();
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            end();
        }
    }

    /**
     * 如果当前上下文没有事务，则新开启一个事务执行ServiceLogicHasReturnValue#call()逻辑，
     * 否则把ServiceLogicHasReturnValue#call()加入到当前上下文事务里执行。
     * @param serviceLogic  带有返回值的业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @param <R>
     * @return
     * @throws DbException
     */
    public static <R> R execute(ServiceLogicHasReturnValue<R> serviceLogic)
            throws DbException {
        try {
            start();
            R ret = serviceLogic.call();
            commit();
            return ret;

        } catch (Exception e) {
            rollback();
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            end();
        }
    }

    /**
     * 如果当前上下文没有事务，则新开启一个事务执行serviceLogic#call()逻辑，否则把serviceLogic#call()加入到
     * 当前上下文事务里执行。
     *
     * @param serviceLogic  业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @throws DbException
     */
    public static void execute(ServiceLogic serviceLogic)
            throws DbException {
        try {
            start();
            serviceLogic.call();
            commit();
        } catch (Exception e) {
            rollback();
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            end();
        }
    }

    /**
     * @param newTransaction: 是否开启一个新事务。如果为true，则开启一个新事务，若为false：
     *                      如果有父事务，加入到父事务，否则新开启一个事务
     * @throws Exception
     */
    private static void start(boolean newTransaction) {

        Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();
        Map<String, DataBaseDecorator> parentContext = DbContext.getTransactionContextStackTopContext(stack);
        Map<String, DataBaseDecorator> curContext = new HashMap<>();
        // 生成新的事务上下文
        stack.add(curContext);
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[2];
        int level = 0;

        if (newTransaction) {
            level = 0;
        } else {
            if (parentContext != null) {
                int parentLevel = 0;
                if (parentContext.get(_transaction_start) != null) {
                    parentLevel = ((TransactionDataBaseTrace) parentContext.get(_transaction_start)).getLevel();
                } else {//说明是顶级
                    parentLevel = -1;
                }
                level = parentLevel + 1;
            } else {
                // 说明是新事务
                level = 0;
            }

        }
        curContext.put(_transaction_start, new TransactionDataBaseTrace(callMethodInfo, level));

        log.debug(Thread.currentThread().getId() + ":trans-start:context："
                + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + level);

    }

    private static void commit() {

        Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();

        Map<String, DataBaseDecorator> curContext = DbContext.getTransactionContextStackTopContext(stack);
        if (curContext == null) {
            throw new DbException("当前事务栈里事务上下文为空！");
        }
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[1];
        TransactionDataBaseTrace transactonStartDbTrace = (TransactionDataBaseTrace) curContext
                .get(_transaction_start);
        if (transactonStartDbTrace == null || !transactonStartDbTrace.isSameMethodCall(callMethodInfo)) {
            throw new DbException("Transaction.commit()和Transaction.start()方法必须要在同一方法里！");
        }

        if(transactonStartDbTrace.getLevel()>0){
                log.debug(Thread.currentThread().getId() + ":child trans- not committed:context:"
                        + ObjectUtils.toJsonString(curContext.keySet()));
                return;
        }
        //如果为顶级上下文
        for (String key : curContext.keySet()) {
            if (key.equals(_transaction_start)) {
                continue;
            }
            DataBaseDecorator db = curContext.get(key);
            db.getContentDataBase().commit();

        }
        log.debug(Thread.currentThread().getId() + ":trans-commit:context:"
                + ObjectUtils.toJsonString(curContext.keySet())+":level="+transactonStartDbTrace.getLevel());
    }

    private static void rollback() {
        Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();
        Map<String, DataBaseDecorator> curContext = DbContext.getTransactionContextStackTopContext(stack);

        if (curContext == null) {
            throw new DbException("当前栈里事务上下文为空！");
        }
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[1];
        TransactionDataBaseTrace transactonStartDbTrace = (TransactionDataBaseTrace) curContext
                .get(_transaction_start);
        if (transactonStartDbTrace == null || !transactonStartDbTrace.isSameMethodCall(callMethodInfo)) {
            throw new DbException("Transaction.rollback()和Transaction.start()方法必须在同一方法里！");
        }

        if(transactonStartDbTrace.getLevel()>0){
            log.debug(Thread.currentThread().getId() + ":child trans:no-rollback");
            return;
        }
        //如果为顶级上下文
        for (String key : curContext.keySet()) {
            if (key.equals(_transaction_start)) {
                continue;
            }
            DataBaseDecorator db = curContext.get(key);
            db.getContentDataBase().rollback();

        }
        log.debug(Thread.currentThread().getId() + ":trans-rollback:context:"
                + ObjectUtils.toJsonString(curContext.keySet())+":level="+transactonStartDbTrace.getLevel());
    }

    private static void end() {
        Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();
        Map<String, DataBaseDecorator> curContext = DbContext.getTransactionContextStackTopContext(stack);
        if (curContext == null) {
            throw new DbException("当前栈里事务上下文为空！");
        }
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[1];
        TransactionDataBaseTrace transactonStartDbTrace = (TransactionDataBaseTrace) curContext
                .get(_transaction_start);
        if (transactonStartDbTrace == null || !transactonStartDbTrace.isSameMethodCall(callMethodInfo)) {
            throw new DbException("Transaction.end()和Transaction.start()必须在同一方法里！");
        }

        if (transactonStartDbTrace.getLevel()>0) {//如果是子事务上下文
            if (stack.size() <= 1) {
                throw new DbException("事务上下文栈必须存在顶级事务上下文！");
            }
            curContext = stack.pop();// 弹出
            String popKeys = ObjectUtils.toJsonString(curContext.keySet());
            Map<String, DataBaseDecorator> parentContext = DbContext.getTransactionContextStackTopContext(stack);
            int curlLevel = 0;
            TransactionDataBaseTrace trasactionStart = (TransactionDataBaseTrace) curContext.remove(_transaction_start);
            if (trasactionStart != null) {
                curlLevel = trasactionStart.getLevel();
            }
            parentContext.putAll(curContext);

            log.debug(Thread.currentThread().getId() + ":trans-end:context:" + popKeys + ":level=" + curlLevel + ",pop up!");
            curContext.clear();
            return;

        } else {// 说明是顶级事务上下文
            int curlLevel = 0;
            for (String key : curContext.keySet()) {
                try {
                    if (key.equals(_transaction_start)) {
                        TransactionDataBaseTrace cld = (TransactionDataBaseTrace) curContext.get(_transaction_start);
                        curlLevel = cld.getLevel();
                        continue;
                    }
                    DataBaseDecorator db = curContext.get(key);
                    db.getContentDataBase().setAutoCommit(true);
                    db.getContentDataBase().close();
                } catch (Exception e) {
                    log.error("" + e, e);
                    throw e;
                }

            }
            curContext = stack.pop();// 弹出
            String popKeys = ObjectUtils.toJsonString(curContext.keySet());
            log.debug(Thread.currentThread().getId() + ":trans-end:context:" + popKeys + ":level=" + curlLevel);
            curContext.clear();
        }
    }
}