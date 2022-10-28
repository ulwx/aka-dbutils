package com.github.ulwx.aka.dbutils.database;

public class GenerateID {

    private SequenceInfo sequenceInfo;
    private String idName;
    private Object idValue;
    public String getIdName() {
        return idName;
    }

    public SequenceInfo getSequenceInfo() {
        return sequenceInfo;
    }

    public void setSequenceInfo(SequenceInfo sequenceInfo) {
        this.sequenceInfo = sequenceInfo;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public Object getIdValue() {
        return idValue;
    }

    public void setIdValue(Object idValue) {
        this.idValue = idValue;
    }


}