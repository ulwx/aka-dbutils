package com.github.ulwx.aka.dbutils.database;

public class SelOp {
    private String select = null;
    private String orderBy = null;
    private Integer limit = null;

    /**
     * 设置select部分有哪些字段需要查询，如果不指定则默认为select * 。
     * 例如指定为："sum(count_data) as countData, ab_cd as abCd"，as后面的别名对应javaBean里的属性，
     * 既countData、abCd都为javaBean的属性，as前面的为数据库里的字段。
     * 或者"countData,abCd"
     *
     * @return
     */
    public SelOp select(String selectStr) {
        this.select = selectStr;
        return this;
    }

    public String select() {
        return select;
    }

    /**
     * 设置order by的属性名[或表列名称]，以英文逗号分隔。例如指定"abCd asc,mnXy desc"，
     * 这里的abCd,mnXy都为javaBean的属性名称，也可以指定 "ab_cd asc, mn_xy desc"，其中ab_cd和
     * mn_xy为列名
     *
     * @param orderByStr 排序列字符串， 例如指定"abCd asc,mnXy desc"
     * @return
     */
    public SelOp orderBy(String orderByStr) {

        this.orderBy = orderByStr;
        return this;
    }

    public String orderBy() {
        return orderBy;
    }

    /**
     * 可以指定limit，但对对象分页查询不能调用此方法，如：
     * List&lt;T&gt; queryListBy(T selectObject, int page, int perPage, PageBean pb)方法的调用时，不能调用
     * selectObject#selectOptions()#limit()方法，否则会报错！
     *
     * @param n
     * @return
     */
    public SelOp limit(int n) {
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
