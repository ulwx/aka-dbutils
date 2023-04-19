package com.github.ulwx.aka.dbutils.database.transaction;

import com.github.ulwx.aka.dbutils.database.DataBase;
import com.github.ulwx.aka.dbutils.database.DataBaseDecorator;
import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * 本地事务管理器，主要针对本地事务。如果存在多个数据源，则使用弱事务处理。弱事务无法保证①处成功执行后，出现网络异常而②还没有执行时的
 * 事务一致性，因为mdb1.commit()提交成功导致无法回滚，而mdb2却正常执行了回滚。
 * 弱事务伪代码如下：<pre>
     MDataBase mdb1 = null;
     MDataBase mdb1 = null;
     try {
         mdb1 = MDbManager.getDataBase(dbPoolName1);
         mdb2 = MDbManager.getDataBase(dbPoolName2);
         ...
         ...
         mdb1.commit(); ①
         mdb2.commit(); ②
     }catch(Exception e){
         if(mdb1!=null){
           mdb1.rollback();
         }
         if(mdb2!=null){
           mdb2.rollback();
         }
     }finally {
         if (mdb1 != null) {
            mdb1.close();
         }
         if (mdb2 != null) {
            mdb2.close();
         }
     }</pre>
 *
 */
public class MDbTransactionManager implements AKaTransactionManager {

    private static Logger log = LoggerFactory.getLogger(MDbTransactionManager.class);

    private static final MDbTransactionManager instance=new MDbTransactionManager();
    public final static String _transaction_start = "_transaction_start";

    private String dbPoolXmlFileName;

    @Override
    public String getDbPoolXmlFileName() {
        return dbPoolXmlFileName;
    }

    public void setDbPoolXmlFileName(String dbPoolXmlFileName) {
        this.dbPoolXmlFileName = dbPoolXmlFileName;
    }
    public  MDbTransactionManager() {

    }
    public MDbTransactionManager(String dbPoolXmlFileName) {
        this.dbPoolXmlFileName=dbPoolXmlFileName;
    }

    public static MDbTransactionManager getInstance(){
        return instance;
    }

    @Override
    public  void begin(AkaPropagationType propagationType) {
        Stack<Map<String, TransactionContextElem>> stack = DbContext.getTransactionContextStack();
        Map<String, TransactionContextElem> parentContext = DbContext.getTransactionContextStackTopContext(stack);
        Map<String, TransactionContextElem> curContext = new LinkedHashMap<>();
        // 生成新的事务上下文
        stack.add(curContext);
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[2];
        int level = 0;//当前处于栈的第几层
        int nestLevel = -1;//-1表示没有嵌套事务
        TransactionContextInfo parentStart = null;
        if (propagationType == AkaPropagationType.REQUIRES_NEW) {
            level = 0;
            nestLevel = -1;
        } else if (propagationType == AkaPropagationType.REQUIRED || propagationType == AkaPropagationType.NESTED) {
            if (parentContext != null) {
                parentStart = ((TransactionContextInfo) parentContext.get(_transaction_start));
                Assert.notNull(parentStart);
                level = parentStart.getLevel() + 1;
                if (propagationType == AkaPropagationType.REQUIRED) {
                    if (parentStart.getNestedLevel() >= 0) {
                        nestLevel = parentStart.getNestedLevel() + 1;
                    }
                }
            } else {
                level = 0;
                // 说明是新事务
                if (propagationType == AkaPropagationType.REQUIRED) {
                    nestLevel = -1;
                }
            }

        }
        TransactionContextInfo transactionStart = new TransactionContextInfo(callMethodInfo, level);
        transactionStart.setAkaPropagationType(propagationType);
        transactionStart.setMdbTransactionManager(this);
        if (propagationType == AkaPropagationType.NESTED) {
            transactionStart.setNestedStart(true);//表明是嵌套事务
            transactionStart.setNestedLevel(0);//嵌套事务开始时，nestLevel=0；
            transactionStart.setNestedStartSavepointName(RandomUtils.genUUID());
        } else {
            transactionStart.setNestedLevel(nestLevel);
        }
        curContext.put(_transaction_start, transactionStart);
        String startStr = getStartStr((TransactionContextInfo) curContext.get(_transaction_start));
        log.debug("trans-start:context："
                + ObjectUtils.toJsonString(curContext.keySet()) + ":start[" + startStr + "]:level=" + level);

    }

    private static String getStartStr(TransactionContextInfo dataBaseTrace) {
        Map map = new HashMap();
        map.put("needRollBack", dataBaseTrace.isNeedRollBack());
        map.put("nestedStart", dataBaseTrace.isNestedStart());
        map.put("nestedLevel", dataBaseTrace.getNestedLevel());
        map.put("nestedStartSavepointName", dataBaseTrace.getNestedStartSavepointName());
        return ObjectUtils.toString(map);
    }

    @Override
    public  void commit() throws Exception {

        Stack<Map<String, TransactionContextElem>> stack = DbContext.getTransactionContextStack();
        //获取事务上下文栈的当前事务上下文
        Map<String, TransactionContextElem> curContext = DbContext.getTransactionContextStackTopContext(stack);
        if (curContext == null) {
            throw new DbException("当前事务栈里事务上下文为空！");
        }
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[1];
        TransactionContextInfo transactonStartDbTrace = (TransactionContextInfo) curContext
                .get(_transaction_start);

        if (transactonStartDbTrace.getLevel() > 0) {//////////////////
            if (transactonStartDbTrace.isNestedStart() &&
                    transactonStartDbTrace.isNeedRollBack()) {
                throw new DbException(transactonStartDbTrace.getNeedRollBackForException());
            } else {
                log.debug(Thread.currentThread().getId() + ":child transaction not committed:context:"
                        + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + transactonStartDbTrace.getLevel());
            }
            return;
        }
        //如果为顶级上下文
        TransactionContextInfo transactonStart = (TransactionContextInfo) curContext.get(_transaction_start);
        if (transactonStart.isNeedRollBack()) {
            throw new DbException(transactonStart.getNeedRollBackForException());
        }
        log.debug("context:" + ObjectUtils.toJsonString(curContext.keySet()) + ":level="
                + transactonStartDbTrace.getLevel() + " ready to commmit ...");
        hand(curContext, (DataBase db) -> {
            db.commit();
        });
        log.debug("trans-commit-finished:context:"
                + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + transactonStartDbTrace.getLevel());
    }

    @Override
    public  void rollback(Throwable forExcpetion) {
        Stack<Map<String, TransactionContextElem>> stack = DbContext.getTransactionContextStack();
        Map<String, TransactionContextElem> curContext = DbContext.getTransactionContextStackTopContext(stack);
        if (curContext == null) {
            throw new DbException("当前栈里事务上下文为空！");
        }
        TransactionContextInfo transactonStartDbTrace = (TransactionContextInfo) curContext
                .get(_transaction_start);

        if (transactonStartDbTrace.getLevel() > 0) {

            if (transactonStartDbTrace.isNestedStart()) {//嵌套事务
                //回滚到保存点
                String savePointName = transactonStartDbTrace.getNestedStartSavepointName();
                log.error("rollback to " + savePointName + " for exception:" + forExcpetion, forExcpetion);
                hand(curContext, (DataBase db) -> {
                    db.rollbackToSavepoint(savePointName);
                });

                transactonStartDbTrace.setNeedRollBack(false);
                transactonStartDbTrace.setNeedRollBackForException(null);
                log.debug(Thread.currentThread().getId()
                        + ":context" + ObjectUtils.toJsonString(curContext.keySet()) +
                        ":child nested transaction rollback to savepoint:level=" + transactonStartDbTrace.getLevel());
            } else {
                transactonStartDbTrace.setNeedRollBack(true);
                transactonStartDbTrace.setNeedRollBackForException(forExcpetion);
                log.debug(Thread.currentThread().getId() +
                        ":context" + ObjectUtils.toJsonString(curContext.keySet())
                        + ":child transaction rollback-delayed:level=" + transactonStartDbTrace.getLevel());
            }
            return;
        }
        //如果为顶级上下文
        log.error("rollback to for exception:" + forExcpetion, forExcpetion);
        log.debug("context:" + ObjectUtils.toJsonString(curContext.keySet())
                + ":level=" + transactonStartDbTrace.getLevel() + " ready to rollback ...");
        hand(curContext, (DataBase db) -> {
            db.rollback();
        });
        log.debug(Thread.currentThread().getId() + ":trans-rollback-finished:context:"
                + ObjectUtils.toJsonString(curContext.keySet()) + ":level="
                + transactonStartDbTrace.getLevel());
    }

    private static void hand(Map<String, TransactionContextElem> curContext, Consumer<DataBase> consumer) {
        List<DataBaseDecorator> listDB = new ArrayList<>();
        for (String key : curContext.keySet()) {
            if (key.equals(_transaction_start)) {
                continue;
            }
            DataBaseDecorator db = (DataBaseDecorator) curContext.get(key);
            listDB.add(db);
        }
        for (int i = listDB.size() - 1; i >= 0; i--) {
            consumer.accept(listDB.get(i).getContainedDataBase());
        }
    }

    @Override
    public  void end() {
        Stack<Map<String, TransactionContextElem>> stack = DbContext.getTransactionContextStack();
        Map<String, TransactionContextElem> curContext = DbContext.getTransactionContextStackTopContext(stack);
        if (curContext == null) {
            throw new DbException("当前栈里事务上下文为空！");
        }
        StackTraceElement[] StackTraceElements = (new Throwable()).getStackTrace();
        StackTraceElement callMethodInfo = StackTraceElements[1];
        TransactionContextInfo transactonStartDbTrace = (TransactionContextInfo) curContext
                .get(_transaction_start);

        if (transactonStartDbTrace.getLevel() > 0) {//如果是子事务上下文
            if (stack.size() <= 1) {
                throw new DbException("事务上下文栈必须存在顶级事务上下文！");
            }

            curContext = stack.pop();// 弹出
            String popKeys = ObjectUtils.toJsonString(curContext.keySet());
            Map<String, TransactionContextElem> parentContext = DbContext.getTransactionContextStackTopContext(stack);
            int curlLevel = 0;
            TransactionContextInfo trasactionStart = (TransactionContextInfo) curContext.remove(_transaction_start);
            if (trasactionStart != null) {
                curlLevel = trasactionStart.getLevel();
                boolean needRollBack = transactonStartDbTrace.isNeedRollBack();
                Exception forException = new DbException(transactonStartDbTrace.getNeedRollBackForException());
                TransactionContextInfo parentTrasactionStart =
                        (TransactionContextInfo) parentContext.get(_transaction_start);
                if (needRollBack) {
                    parentTrasactionStart.setNeedRollBackForException(forException);
                    parentTrasactionStart.setNeedRollBack(needRollBack);
                }
            }
            parentContext.putAll(curContext);
            String startStr = getStartStr(trasactionStart);
            log.debug(Thread.currentThread().getId() + ":trans-end:context:" + popKeys + ":start[" + startStr + "]"
                    + ":level=" + curlLevel + ",pop up!");
            curContext.clear();
            return;

        } else {// 说明是顶级事务上下文
            int curlLevel = 0;
            String startStr = getStartStr((TransactionContextInfo) curContext.get(_transaction_start));
            log.debug("context:" + ObjectUtils.toJsonString(curContext.keySet()) + ":level=" + curlLevel + " ready to close ...");

            hand(curContext, (DataBase db) -> {
                db.setAutoCommit(true);
                db.close();
            });
            curContext = stack.pop();// 弹出
            String popKeys = ObjectUtils.toJsonString(curContext.keySet());

            log.debug("trans-end-closed:context:" + popKeys + ":start[" + startStr + "]" + ":level=" + curlLevel);
            curContext.clear();
        }
    }

    @Override
    public AkaTransactionType getTransactionType() {
        return AkaTransactionType.LOCAL;
    }
}