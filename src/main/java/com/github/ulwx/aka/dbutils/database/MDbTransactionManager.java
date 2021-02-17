package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public  class MDbTransactionManager {
    private static Logger log = LoggerFactory.getLogger(MDbTransactionManager.class);
    public static String _children_transaction = "_children_transaction";
    public static String _transaction_start = "_transaction_start";

    private static void start() {
        start(false);
    }
    private static void startNew()  {
        start(true);
    }
    public static <R, T> R executeNew(ServiceLogicHasReturnValue<R, T> serviceLogic, T argObj)
            throws DbException {
        try {
           startNew();
            R ret = serviceLogic.call(argObj);
            commit();
            return ret;

        } catch (Exception e) {
            rollback();
            throw new DbException(e);
        } finally {
            end();
        }
    }
    public static <T> void executeNew(ServiceLogic serviceLogic)
            throws DbException {
        try {
            startNew();
             serviceLogic.call();
            commit();

        } catch (Exception e) {
            rollback();
            throw new DbException(e);
        } finally {
            end();
        }
    }
    public static <R, T> R execute(ServiceLogicHasReturnValue<R, T> serviceLogic, T argObj)
            throws DbException {
        try {
            start();
            R ret = serviceLogic.call(argObj);
            commit();
            return ret;

        } catch (Exception e) {
            rollback();
            throw new DbException(e);
        } finally {
            end();
        }
    }

    public static  void execute(ServiceLogic serviceLogic)
            throws DbException {
        try {
            start();
            serviceLogic.call();
            commit();
        } catch (Exception e) {
            rollback();
            throw new DbException(e);
        } finally {
            end();
        }
    }

    /**
     *
     * @param newTransaction: 是否开启一个新事务。如果为true，则开启一个新事务，若为false：如果有父事务，加入到父事务，否则新开启一个事务
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
        int level=0;
        curContext.put(_transaction_start, new TransactionDataBaseTrace(callMethodInfo,0));
        if (newTransaction) {
            level=0;
        } else {

            if (parentContext != null) {
                int parentLevel=0;
                if(parentContext.get(_children_transaction)!=null) {
                    parentLevel=((TransactionDataBaseTrace)parentContext.get(_children_transaction)).getLevel();
                }else {//说明是顶级
                    parentLevel=0;
                }
                level=parentLevel+1;
                curContext.put(_children_transaction, new TransactionDataBaseTrace(callMethodInfo,level));// 子事务标识
            } else {
                // 说明是新事务
                level=0;
            }

        }
        log.debug(Thread.currentThread().getId() + ":trans-start:context："
                + ObjectUtils.toJsonString(curContext.keySet())+":level="+level);

    }

    private static void commit()  {

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

        if (curContext.get(_children_transaction) != null) {// 如果为子事务不能提交
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
                + ObjectUtils.toJsonString(curContext.keySet()));
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
        if (curContext.get(_children_transaction) != null) {
            log.debug(Thread.currentThread().getId() + ":child trans:no-rollback");
            return;
        }
        //如果为顶级上下文
        for (String key : curContext.keySet()) {
            if ( key.equals(_transaction_start)) {
                continue;
            }
            DataBaseDecorator db = curContext.get(key);
            db.getContentDataBase().rollback();

        }
        log.debug(Thread.currentThread().getId() + ":trans-rollback !");
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

        if (curContext.get(_children_transaction) != null) {//如果是子事务上下文
            if (stack.size() <= 1) {
                throw new DbException("事务上下文栈必须存在顶级事务上下文！");
            }
            curContext = stack.pop();// 弹出
            String popKeys = ObjectUtils.toJsonString(curContext.keySet());
            Map<String, DataBaseDecorator> parentContext = DbContext.getTransactionContextStackTopContext(stack);
            int curlLevel=0;
            TransactionDataBaseTrace cld1=(TransactionDataBaseTrace)curContext.remove(_children_transaction);
            TransactionDataBaseTrace cld2=(TransactionDataBaseTrace)curContext.remove(_transaction_start);
            if(cld1!=null) {
                curlLevel=cld1.getLevel();
            }else {
                curlLevel=cld2.getLevel();
            }
            parentContext.putAll(curContext);

            log.debug(Thread.currentThread().getId() + ":trans-end:context:" + popKeys + ":level="+curlLevel+",pop up!");
            curContext.clear();
            return;

        } else {// 说明是顶级事务上下文
            int curlLevel=0;
            for (String key : curContext.keySet()) {
                try {
                    if (key.equals(_transaction_start)) {
                        TransactionDataBaseTrace cld=(TransactionDataBaseTrace)curContext.get(_transaction_start);
                        curlLevel=cld.getLevel();
                        continue;
                    }
                    if(key.equals(_children_transaction)){//顶级上下文不可能存在子事务标识
                        throw new DbException("顶级事务上下文中不能存在子事务标识！");
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
            log.debug(Thread.currentThread().getId() + ":trans-end:context:" + popKeys+":level="+curlLevel);
            curContext.clear();
        }
    }
}