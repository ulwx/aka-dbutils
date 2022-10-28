package com.github.ulwx.aka.dbutils.database;

/**
 * 用于生成单表的查询sql语句
 */
public class QueryHint {
    private String select ;
    private String groupBy ;
    private String having;
    private String orderBy ;
    private Integer limit ;
    private Object arg;

    public QueryHint groupBy(String groupBy){
        this.groupBy=groupBy;
        return this;
    }
    public String groupBy(){
        return groupBy;
    }

    public String having(){
        return having;
    }

    public QueryHint having(String having){
        this.having=having;
        return this;
    }

    public QueryHint arg(Object arg){
        this.arg=arg;
        return this;
    }
    public Object arg(){
        return arg;
    }


    public QueryHint select(String select) {
        this.select = select;
        return this;
    }

    public String select() {
        return select;
    }


    public QueryHint orderBy(String orderByStr) {

        this.orderBy = orderByStr;
        return this;
    }

    public String orderBy() {
        return orderBy;
    }


    public QueryHint limit(int n) {
        this.limit = n;
        return this;
    }

    /**
     * 返回设置的limit值
     *
     * @return
     */
    public Integer limit() {
        return limit;
    }

}
