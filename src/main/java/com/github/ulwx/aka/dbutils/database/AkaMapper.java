package com.github.ulwx.aka.dbutils.database;

public abstract class AkaMapper {
    private MDataBase mdDataBase;

    public MDataBase getMdDataBase() {
        return mdDataBase;
    }

    public void setMdDataBase(MDataBase mdDataBase) {
        this.mdDataBase = mdDataBase;
    }
}
