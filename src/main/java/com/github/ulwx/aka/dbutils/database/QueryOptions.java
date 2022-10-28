package com.github.ulwx.aka.dbutils.database;

public class QueryOptions {
    private boolean isLimitOne;
    private QueryHint queryHint;
    public boolean isLimitOne() {
        return isLimitOne;
    }

    public void setLimitOne(boolean limitOne) {
        isLimitOne = limitOne;
    }

    public QueryHint getQueryHint() {
        return queryHint;
    }

    public void setQueryHint(QueryHint queryHint) {
        this.queryHint = queryHint;
    }
}
