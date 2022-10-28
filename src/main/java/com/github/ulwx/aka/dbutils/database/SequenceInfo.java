package com.github.ulwx.aka.dbutils.database;

public class SequenceInfo {
    private String idName;
    private String sequenceName;
    private Boolean callBeforeInsert;
    private GenerateID generateID;

    public void setGenerateID(GenerateID generateID) {
        this.generateID = generateID;
    }

    public GenerateID getGenerateID() {
        return generateID;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Boolean getCallBeforeInsert() {
        return callBeforeInsert;
    }

    public void setCallBeforeInsert(Boolean callBeforeInsert) {
        this.callBeforeInsert = callBeforeInsert;
    }
}
