package com.github.ulwx.aka.dbutils.database;

public interface TransactionContextElem {
    public static enum ElemType{
        DataBaseDecoratorElem,TransactionContextInfoElem
    }
    default public ElemType getElemType(){
        return ElemType.DataBaseDecoratorElem;
    }
}
