package com.github.ulwx.database.spring;

public class DBTransInfo {

    private MDataBaseFactory mDataBaseFactory;
    private Object mDataBaseTemplate;

    public MDataBaseFactory getmDataBaseFactory() {
        return mDataBaseFactory;
    }

    public void setmDataBaseFactory(MDataBaseFactory mDataBaseFactory) {
        this.mDataBaseFactory = mDataBaseFactory;
    }

    public Object getmDataBaseTemplate() {
        return mDataBaseTemplate;
    }

    public void setmDataBaseTemplate(Object mDataBaseTemplate) {
        this.mDataBaseTemplate = mDataBaseTemplate;
    }
}
