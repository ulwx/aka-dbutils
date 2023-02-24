package com.github.ulwx.aka.dbutils.database.transaction;

import com.github.ulwx.aka.dbutils.database.DbException;

public class TransactionTemplate {

    /**
     * 根据propagationType指定PropagationType.REQUIRED事务传播级别。如果是多个不能数据源，则为采用弱事务。
     * @param serviceLogic 务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @throws DbException
     */
    public static void execute(ServiceLogic serviceLogic)
            throws DbException {
        execute(AkaPropagationType.REQUIRED,serviceLogic);
    }
    /**
     * 根据propagationType指定的事务传播级别，来控制事务传播的语义。如果是多个不能数据源，则为采用弱事务。
     * @param propagationType  事务传播级别
     * @param serviceLogic 务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @throws DbException
     */
    public static void execute(
                               AkaPropagationType propagationType,
                               ServiceLogic serviceLogic)
            throws DbException {
        execute(propagationType,new ServiceLogicHasReturnValue(){
            @Override
            public Object call() throws Exception {
                serviceLogic.call();
                return null;
            }
        });
    }


    /**
     * 根据propagationType指定的事务传播级别，来控制事务传播的语义。如果是多个不能数据源，则为采用弱事务。
     * @param propagationType  事务传播级别
     * @param serviceLogic 带有返回值的业务逻辑接口，外部调用通过lambda表达式传入执行数据库操作代码
     * @param <R>
     * @return
     * @throws DbException
     */
    public static <R> R execute(AkaPropagationType propagationType,
                                ServiceLogicHasReturnValue<R> serviceLogic)
            throws DbException {
        MDbTransactionManager mDbTransactionManager= MDbTransactionManager.getInstance();
        try {
            AkaTransactionManagerHolder.set(mDbTransactionManager);
            mDbTransactionManager.begin(propagationType);
            R ret = serviceLogic.call();
            mDbTransactionManager.commit();
            return ret;

        } catch (Throwable e) {
            mDbTransactionManager.rollback(e);
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            try {
                mDbTransactionManager.end();
            }finally {
                AkaTransactionManagerHolder.clear();
            }

        }
    }
}
