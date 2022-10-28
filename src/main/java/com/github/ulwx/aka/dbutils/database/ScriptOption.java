package com.github.ulwx.aka.dbutils.database;


public class ScriptOption {
    /**
     * 如果fromMDMethod=true则为md文件方法路径，否则为"file:" 或 "classpath:" 路径
     */
    private String source;
    private boolean fromMDMethod = true;

    public boolean isFromMDMethod() {
        return fromMDMethod;
    }

    public void setFromMDMethod(boolean fromMDMethod) {
        this.fromMDMethod = fromMDMethod;
    }

    /**
     * 如果fromMDMethod=true则为md文件方法路径，否则为"file:" 或 "classpath:" 路径
     * @return
     */
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
