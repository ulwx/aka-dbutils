package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class MDbTransactionManager {
    public enum PROPAGATION {
        REQUIRED, REQUIRES_NEW, NESTED
    }

    private static Logger log = LoggerFactory.getLogger(MDbTransactionManager.class);
    public static String _transaction_start = "_transaction_start";

    /**
     * 总是新开启一个事务执行ServiceLogicHasReturnValue#call()的逻辑。如果当前上下文里存在事务则挂起，
     * 执行当前新建事务完成以后，上下文事务恢复再执行。如果运行新建事务时抛出异常，新建的事务会回滚，并且会再次
     * 抛出异常，此异常如果在外部不被捕获并处理的话，会引发外部的事务回滚。
     *
     * @param serviceLogic 带有返回值的业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @param <R>
     * @return
     * @throws DbException
     */
    public static <R> R executeNew(ServiceLogicHasReturnValue<R> serviceLogic)
            throws DbException {
        try {
            start(PROPAGATION.REQUIRES_NEW);
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
     * @param serviceLogic 业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @throws DbException
     */
    public static void execute(ServiceLogic serviceLogic)
            throws DbException {
        try {
            start(PROPAGATION.REQUIRED);
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
     * 总是新开启一个事务执行ServiceLogic#call()的逻辑。如果当前上下文里存在事务则挂起，
     * 执行当前新建事务完成以后，上下文事务恢复再执行。如果运行新建事务时抛出异常，新建的事务会回滚，并且会再次抛出异常，
     * 此异常如果在外部不被捕获并处理的话，会引发外部的事务回滚。
     *
     * @param serviceLogic 业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @throws DbException
     */
    public static void execute(PROPAGATION propagation,ServiceLogic serviceLogic)
            throws DbException {
        try {
            start(propagation);
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
     *
     * @param serviceLogic 带有返回值的业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @param <R>
     * @return
     * @throws DbException
     */
    public static <R> R execute(PROPAGATION propagation,ServiceLogicHasReturnValue<R> serviceLogic)
            throws DbException {
        try {
            start(propagation);
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
     * @param newTransaction: 是否开启一个新事务。如果为true，则开启一个新事务，若为false：
     *                        如果有父事务，加入到父事务，否则新开启一个事务
     * @throws Exception
     */
    private static void start(PROPAGATION propagation) {

        Stack<Map<String, DataBaseDecorator>> stack = DbContext.getTransactionContextStack();
        Map<String, DataBaseDecorator> parentContext = DbContext.getTransactionContextStackTopContext(stack);
        Map<String, DataBaseDecorator> curContext = new LinkedHashMap<>();
        // 生成新的事务上下文
        stack.add(curContext);
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[2];
        int level = 0;
        int nestLevel=-1;
        if (propagation == PROPAGATION.REQUIRES_NEW) {
            level = 0;
            nestLevel=-1;
        } else if (propagation == PROPAGATION.REQUIRED || propagation == PROPAGATION.NESTED) {
            if (parentContext != null) {
                TransactionDataBaseTrace parentStart= ((TransactionDataBaseTrace) parentContext.get(_transaction_start));
                Assert.notNull(parentStart);
                level = parentStart.getInfo().getLevel()+1;
                if(propagation == PROPAGATION.NESTED){
                    nestLevel=0;
                }else if(propagation == PROPAGATION.REQUIRED){
                    if(parentStart.getInfo().getNestedLevel()>=0) {
                        nestLevel = parentStart.getInfo().getNestedLevel() + 1;
                    }
                }
            } else {
                level = 0;
                // 说明是新事务
                if(propagation == PROPAGATION.REQUIRED ) {
                    nestLevel = -1;
                }else if(propagation == PROPAGATION.NESTED){
                    nestLevel =  0 ;
                }
            }

        }
        TransactionDataBaseTrace transactionStart = new TransactionDataBaseTrace(callMethodInfo, level);
        if (propagation == PROPAGATION.NESTED) {
            transactionStart.getInfo().setNestedStart(true);//表明是嵌套事务
            transactionStart.getInfo().setNestedLevel(0);//嵌套事务开始时，nestLevel=0；
            transactionStart.getInfo().setNestedStartSavepointName(RandomUtils.genUUID());
        }else{
            transactionStart.getInfo().setNestedLevel(nestLevel);
        }
        curContext.put(_transaction_start, transactionStart);

        log.debug("trans-start:context："
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

        if (transactonStartDbTrace.getInfo().getLevel() > 0) {//////////////////
            if(transactonStartDbTrace.getInfo().isNestedStart() &&
                    transactonStartDbTrace.getInfo().isNeedRollBack()){
                throw new DbException("nested transaction need rollback to savepoint...");
            }else {
                log.debug(Thread.currentThread().getId() + ":child transaction not committed:context:"
                        + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + transactonStartDbTrace.getInfo().getLevel());
            }
            return;
        }
        //如果为顶级上下文
        TransactionDataBaseTrace transactonStart = (TransactionDataBaseTrace) curContext.get(_transaction_start);
        if (transactonStart.getInfo().isNeedRollBack()) {
            throw new DbException("transaction need rollback...");
        }
        log.debug("context:" + ObjectUtils.toJsonString(curContext.keySet()) + ":level="
                + transactonStartDbTrace.getInfo().getLevel() + " ready to commmit ...");
        hand(curContext,(DataBase db)->{
            db.commit();
        });
        log.debug("trans-commit-finished:context:"
                + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + transactonStartDbTrace.getInfo().getLevel());
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

        if (transactonStartDbTrace.getInfo().getLevel() > 0) {

            if(transactonStartDbTrace.getInfo().isNestedStart()){//嵌套事务
                //回滚到保存点
                hand(curContext,(DataBase db)->{
                    db.rollbackToSavepoint(transactonStartDbTrace.getInfo().
                            getNestedStartSavepointName());
                });
                transactonStartDbTrace.getInfo().setNeedRollBack(false);
                log.debug(Thread.currentThread().getId()
                        + ":context" +ObjectUtils.toJsonString(curContext.keySet())+
                        ":child nested transaction rollback to savepoint:level=" + transactonStartDbTrace.getInfo().getLevel());
            }else{
                transactonStartDbTrace.getInfo().setNeedRollBack(true);
                log.debug(Thread.currentThread().getId()+
                        ":context" +ObjectUtils.toJsonString(curContext.keySet())
                        + ":child transaction not rollback:level=" + transactonStartDbTrace.getInfo().getLevel());
            }
            return;
        }
        //如果为顶级上下文
        log.debug("context:" + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + transactonStartDbTrace.getInfo().getLevel() + " ready to rollback ...");
        hand(curContext,(DataBase db)->{
            db.rollback();
        });
        log.debug(Thread.currentThread().getId() + ":trans-rollback-finished:context:"
                + ObjectUtils.toJsonString(curContext.keySet()) + ":level="
                + transactonStartDbTrace.getInfo().getLevel());
    }

    private static void hand(Map<String, DataBaseDecorator> curContext, Consumer<DataBase> consumer){
        List<DataBaseDecorator> listDB = new ArrayList<>();
        for (String key : curContext.keySet()) {
            if (key.equals(_transaction_start)) {
                continue;
            }
            DataBaseDecorator db = curContext.get(key);
            listDB.add(db);
        }
        for (int i = listDB.size() - 1; i >= 0; i--) {
            consumer.accept(listDB.get(i).getContentDataBase());
        }
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

        if (transactonStartDbTrace.getInfo().getLevel() > 0) {//如果是子事务上下文
            if (stack.size() <= 1) {
                throw new DbException("事务上下文栈必须存在顶级事务上下文！");
            }

            curContext = stack.pop();// 弹出
            String popKeys = ObjectUtils.toJsonString(curContext.keySet());
            Map<String, DataBaseDecorator> parentContext = DbContext.getTransactionContextStackTopContext(stack);
            int curlLevel = 0;
            TransactionDataBaseTrace trasactionStart = (TransactionDataBaseTrace) curContext.remove(_transaction_start);
            if (trasactionStart != null) {
                curlLevel = trasactionStart.getInfo().getLevel();
                boolean needRollBack = transactonStartDbTrace.getInfo().isNeedRollBack();
                TransactionDataBaseTrace parentTrasactionStart =
                        (TransactionDataBaseTrace) parentContext.get(_transaction_start);
                parentTrasactionStart.getInfo().setNeedRollBack(needRollBack);
            }
            parentContext.putAll(curContext);

            log.debug(Thread.currentThread().getId() + ":trans-end:context:" + popKeys + ":level=" + curlLevel + ",pop up!");
            curContext.clear();
            return;

        } else {// 说明是顶级事务上下文
            int curlLevel = 0;
            log.debug("context:" + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + curlLevel + " ready to close ...");

            hand(curContext,(DataBase db)->{
                    db.setAutoCommit(true);
                    db.close();
            });
            curContext = stack.pop();// 弹出
            String popKeys = ObjectUtils.toJsonString(curContext.keySet());
            log.debug("trans-end-closed:context:" + popKeys + ":level=" + curlLevel);
            curContext.clear();
        }
    }
}