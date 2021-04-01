package com.github.ulwx.aka.dbutils.database;

public abstract class MdbOptions {

    private SelOp selectOptions = null;

    public SelOp selectOptions() {
        if (selectOptions == null) {
            selectOptions = new SelOp();
        }
        return selectOptions;
    }

}
