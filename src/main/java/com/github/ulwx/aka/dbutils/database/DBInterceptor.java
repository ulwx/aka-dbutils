package com.github.ulwx.aka.dbutils.database;

import java.lang.reflect.Method;

public interface DBInterceptor {
    /**
     * 每个数据库操作前执行此方法（对应到dataBase中的一个方法，如queryList()），针对每个数据库操作方法的监听，
     * 可能会多次回调beforeDbOperationExeute()方法，比如一个分页查询的数据库操作（queryList()）可能会生成
     * 两个子查询操作，即一个是计算总数的SQL语句的查询，一个是根据总数计算生成的分页查询的SQL语句的查询操作，
     * 所以会调用两次beforeDbOperationExeute()方法。还有一种情况是执行脚本的情况（exeScript()方法）和
     * 批量操作的情况（如insert(String[] sqltxts）方法等,脚本里的每条语句执行之前都会调用此方法。
     *
     * @param dataBase 数据库操作实例
     * @param inBatch  执行的SQL语句是否是处于批量操作中，如在脚本中或batch操作中
     * @param debugSql 执行的debug sql，
     * @return 是否继续执行，true:拦截数据库操作继续执行，false：拦截的数据库操作不执行
     */
    default public boolean beforeDbOperationExeute(DataBase dataBase, boolean inBatch, String debugSql) {
        return true;
    }

    /**
     * 每个数据库操作后执行此方法，result返回的是执行的结果，exception为执行的异常，一般情况下出现异常时，执行结果
     * (result）为空
     *
     * @param dataBase          据库操作实例
     * @param interceptedMethod
     * @param result            数据库操作执行的结果；如果出现异常时，可能为null。
     * @param exception         存放数据库操作执行中发生的异常，如果数据库操作执行成功，则为null。
     * @param lastDebugSql      本次数据库操作中（对应dataBase中的一个方法，如queryList()）执行的debug sql的最后一条SQL，
     *                          比如分页的queryList()方法默认会先生成计算总数的SQL语句，然后生成分页查询的SQL语句，debugSql传入的是
     *                          最后的分页查询的SQL语句
     */
    public void postDbOperationExeute(DataBase dataBase, Method interceptedMethod,
                                      Object result,
                                      Exception exception,
                                      String lastDebugSql);
}
