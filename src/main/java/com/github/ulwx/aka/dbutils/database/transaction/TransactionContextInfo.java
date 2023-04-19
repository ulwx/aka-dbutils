package com.github.ulwx.aka.dbutils.database.transaction;

public class TransactionContextInfo implements TransactionContextElem {

    private StackTraceElement stackTraceElement;
    private int level = 0;//处于事务栈的第几层
    private Throwable needRollBackForException = null;
    private boolean needRollBack = false;//是否整个事务回滚
    private boolean nestedStart = false;//是否是嵌套事务开始，如果是，则nestedLevel=0
    private String nestedStartSavepointName = null;
    private int nestedLevel = -1;//-1表示没有嵌套事务，如果有嵌套事务，则nestedLevel>=0
    private AkaPropagationType akaPropagationType;
    private MDbTransactionManager mdbTransactionManager;

    public MDbTransactionManager getMdbTransactionManager() {
        return mdbTransactionManager;
    }

    public void setMdbTransactionManager(MDbTransactionManager mdbTransactionManager) {
        this.mdbTransactionManager = mdbTransactionManager;
    }

    public AkaPropagationType getAkaPropagationType() {
        return akaPropagationType;
    }

    public void setAkaPropagationType(AkaPropagationType akaPropagationType) {
        this.akaPropagationType = akaPropagationType;
    }

    public boolean isNeedRollBack() {
        return needRollBack;
    }

    public Throwable getNeedRollBackForException() {
        return needRollBackForException;
    }

    public void setNeedRollBackForException(Throwable needRollBackForException) {
        this.needRollBackForException = needRollBackForException;
    }

    public int getNestedLevel() {
        return nestedLevel;
    }

    public void setNestedLevel(int nestedLevel) {
        this.nestedLevel = nestedLevel;
    }

    public boolean isNestedStart() {
        return nestedStart;
    }

    public void setNestedStart(boolean nestedStart) {
        this.nestedStart = nestedStart;
    }

    public void setNeedRollBack(boolean needRollBack) {
        this.needRollBack = needRollBack;
    }

    public String getNestedStartSavepointName() {
        return nestedStartSavepointName;
    }

    public void setNestedStartSavepointName(String nestedStartSavepointName) {
        this.nestedStartSavepointName = nestedStartSavepointName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public TransactionContextInfo(StackTraceElement stackTraceElement, int level) {
        this.stackTraceElement = stackTraceElement;
        this.level = level;
    }

    public StackTraceElement getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }


}
