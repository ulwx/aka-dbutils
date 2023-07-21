package com.github.ulwx.aka.dbutils.database.transaction;

import com.github.ulwx.aka.dbutils.database.DbException;
import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.core.event.EventBus;
import io.seata.core.event.GuavaEventBus;
import io.seata.core.exception.TmTransactionException;
import io.seata.core.exception.TransactionExceptionCode;
import io.seata.spring.event.DegradeCheckEvent;
import io.seata.tm.api.*;
import io.seata.tm.api.transaction.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.seata.tm.api.GlobalTransactionRole.Participant;

public class GlobalTransactionTemplate {
    private static final Logger log = LoggerFactory.getLogger(GlobalTransactionTemplate.class);
    private static final TransactionalTemplate transactionalTemplate = new TransactionalTemplate();
    private static final int DEFAULT_GLOBAL_TRANSACTION_TIMEOUT = 60000;
    private static final FailureHandler failureHandler = new DefaultFailureHandlerImpl();
    private static final EventBus EVENT_BUS = new GuavaEventBus("degradeCheckEventBus", true);
    private static final AtomicBoolean ATOMIC_DEGRADE_CHECK = new AtomicBoolean(false);
    public static void execute(String dbPoolXmlFileName,
                                ServiceLogic serviceLogic){
        execute(dbPoolXmlFileName,new ServiceLogicHasReturnValue(){
            @Override
            public Object call() throws Exception {
                serviceLogic.call();
                return null;
            }
        },AkaPropagationType.REQUIRED);
    }

    public static <R> R  executeTest(
            String dbPoolXmlFileName,
            ServiceLogicHasReturnValue<R> serviceLogic,
            AkaPropagationType propagationType,boolean async)throws Throwable{
        SeataAtAkaDistributedTransactionManager manager=
                (SeataAtAkaDistributedTransactionManager)TransactionManagerFactory.getTransactionManager(dbPoolXmlFileName,
                        AkaTransactionType.SEATA_AT);
        try{
            manager.begin(manager.getGlobalTXTimeout(),AkaPropagationType.REQUIRED);
            R ret=serviceLogic.call();
            manager.commit();
            return ret;
        }catch (Throwable e){
            manager.rollback(e);
            throw e;
        }finally {
            manager.end();
        }
    }
    public static <R> R execute(String dbPoolXmlFileName,
                                ServiceLogicHasReturnValue<R> serviceLogic){
        return execute(dbPoolXmlFileName,serviceLogic,AkaPropagationType.REQUIRED);
    }
    public static <R> R execute(String dbPoolXmlFileName,
                                ServiceLogicHasReturnValue<R> serviceLogic,
                                AkaPropagationType propagationType){
        SeataAtAkaDistributedTransactionManager manager=
                (SeataAtAkaDistributedTransactionManager)TransactionManagerFactory.getTransactionManager(dbPoolXmlFileName,
                AkaTransactionType.SEATA_AT);
        try {
            AkaTransactionManagerHolder.set(manager);
            return handleGlobalTransaction(serviceLogic,manager,
                    "default",propagationType);
        }catch (Throwable e){
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        } finally {
            AkaTransactionManagerHolder.clear();
        }
    }
    private static final TransactionHook TRANSACTION_HOOK=new TransactionHook(){
        @Override
        public void beforeBegin() {

        }

        @Override
        public void afterBegin() {
        }

        @Override
        public void beforeCommit() {

        }

        @Override
        public void afterCommit() {

        }

        @Override
        public void beforeRollback() {

        }

        @Override
        public void afterRollback() {

        }

        @Override
        public void afterCompletion() {
        }
    };
    public static <R> R handleGlobalTransaction(ServiceLogicHasReturnValue<R> serviceLogic,
                                                SeataAtAkaDistributedTransactionManager manager,
                                                String transactionName,
                                                AkaPropagationType propagationType
                                                )throws Throwable {
        boolean succeed = true;
        try {
            TransactionHookManager.registerHook(TRANSACTION_HOOK);
            return (R) transactionalTemplate.execute(new TransactionalExecutor() {
                @Override
                public Object execute() throws Throwable {
                    return serviceLogic.call();
                }

                @Override
                public TransactionInfo getTransactionInfo() {
                    // reset the value of timeout
                    int timeout = manager.getGlobalTXTimeout();
                    if (timeout <= 0 || timeout == DEFAULT_GLOBAL_TRANSACTION_TIMEOUT) {
                        timeout = DEFAULT_GLOBAL_TRANSACTION_TIMEOUT;
                    }

                    TransactionInfo transactionInfo = new TransactionInfo();
                    transactionInfo.setTimeOut(timeout);
                    transactionInfo.setName(transactionName);
                    if(propagationType==AkaPropagationType.REQUIRED){
                        transactionInfo.setPropagation(Propagation.REQUIRED);
                    }else  if(propagationType==AkaPropagationType.REQUIRES_NEW){
                        transactionInfo.setPropagation(Propagation.REQUIRES_NEW);
                    } else{
                        throw new DbException("事务传播类型："+propagationType+"暂时不支持！");
                    }

                    Set<RollbackRule> rollbackRules = new LinkedHashSet<>();
                    rollbackRules.add(new RollbackRule(Throwable.class));

                    transactionInfo.setRollbackRules(rollbackRules);
                    return transactionInfo;
                }


            });
        } catch (TransactionalExecutor.ExecutionException e) {
            GlobalTransaction globalTransaction = e.getTransaction();

            if (globalTransaction.getGlobalTransactionRole() == Participant) {
                throw e.getOriginalException();
            }
            TransactionalExecutor.Code code = e.getCode();
            Throwable cause = e.getCause();
            boolean timeout = isTimeoutException(cause);
            switch (code) {
                case RollbackDone:
                    if (timeout) {
                        throw cause;
                    } else {
                        throw e.getOriginalException();
                    }
                case BeginFailure:
                    succeed = false;
                    failureHandler.onBeginFailure(globalTransaction, cause);
                    throw cause;
                case CommitFailure:
                    succeed = false;
                    failureHandler.onCommitFailure(globalTransaction, cause);
                    throw cause;
                case RollbackFailure:
                    failureHandler.onRollbackFailure(globalTransaction, e.getOriginalException());
                    throw e.getOriginalException();
                case Rollbacking:
                    failureHandler.onRollbacking(globalTransaction, e.getOriginalException());
                    if (timeout) {
                        throw cause;
                    } else {
                        throw e.getOriginalException();
                    }
                default:
                    throw new ShouldNeverHappenException(String.format("Unknown TransactionalExecutor.Code: %s", code));
            }
        } finally {
            if (ATOMIC_DEGRADE_CHECK.get()) {
                EVENT_BUS.post(new DegradeCheckEvent(succeed));
            }
        }
    }
    private static boolean isTimeoutException(Throwable th) {
        if (null == th) {
            return false;
        }
        if (th instanceof TmTransactionException) {
            TmTransactionException exx = (TmTransactionException)th;
            if (TransactionExceptionCode.TransactionTimeout == exx.getCode()) {
                return true;
            }
        }
        return false;
    }

}
