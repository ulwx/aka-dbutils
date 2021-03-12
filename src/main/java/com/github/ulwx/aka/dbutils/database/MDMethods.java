package com.github.ulwx.aka.dbutils.database;

import com.github.ulwx.aka.dbutils.tool.PageBean;

public class MDMethods {

    public static abstract class MapNestOptions {
        public abstract boolean isOne2One();
    }
    public static class One2ManyMapNestOptions extends MapNestOptions {
        private String sqlPrefix;
        private String[] parentBeanKeys;
        private QueryMapNestOne2Many[] QueryMapNestOne2Manys;

        public String getSqlPrefix() {
            return sqlPrefix;
        }

        /**
         * 指定一个前缀，用于限定sql语句里的哪些列映射主类的属性
         * @param sqlPrefix
         */
        public void setSqlPrefix(String sqlPrefix) {
            this.sqlPrefix = sqlPrefix;
        }

        public String[] getParentBeanKeys() {
            return parentBeanKeys;
        }

        /**
         * 指定主类里的哪些属性共同对应到表里一个唯一键，可能是主键
         * @param parentBeanKeys
         */
        public void setParentBeanKeys(String[] parentBeanKeys) {
            this.parentBeanKeys = parentBeanKeys;
        }

        public QueryMapNestOne2Many[] getQueryMapNestOne2Manys() {
            return QueryMapNestOne2Manys;
        }

        /**
         * 可以设置多个关联属性，每个QueryMapNestOne2Many对象代表一个一对多关联属性配置
         * @param queryMapNestOne2Manys
         */
        public void setQueryMapNestOne2Manys(QueryMapNestOne2Many[] queryMapNestOne2Manys) {
            QueryMapNestOne2Manys = queryMapNestOne2Manys;
        }

        /**
         * 是否是一对一关联，否则是一对多关联
         * @return
         */
        @Override
        public boolean isOne2One() {
            return false;
        }
    }

    public static class One2OneMapNestOptions extends MapNestOptions {
        private String sqlPrefix;
        private QueryMapNestOne2One[] queryMapNestOne2Ones;

        public String getSqlPrefix() {
            return sqlPrefix;
        }

        /**
         * 指定一个前缀，用于限定sql语句里的哪些列映射主类的属性
         * @param sqlPrefix
         */
        public void setSqlPrefix(String sqlPrefix) {
            this.sqlPrefix = sqlPrefix;
        }

        /**
         *
         * @return
         */
        public QueryMapNestOne2One[] getQueryMapNestOne2Ones() {
            return queryMapNestOne2Ones;
        }

        /**
         * 可以设置多个关联属性配置，每个QueryMapNestOne2Many对象代表一个一对一关联属性配置
         * @param queryMapNestOne2Ones
         */
        public void setQueryMapNestOne2Ones(QueryMapNestOne2One[] queryMapNestOne2Ones) {
            this.queryMapNestOne2Ones = queryMapNestOne2Ones;
        }
        @Override
        public boolean isOne2One() {
            return true;
        }
    }

    public static class PageOptions {
        private int page;
        private int perPage;
        private PageBean pageBean = new PageBean();
        private String mdFullMethodNameForCountSql = "";

        public int getPage() {
            return page;
        }

        /**
         * 设置页码 从第1页开始
         * @param page
         */
        public void setPage(int page) {
            this.page = page;
        }

        /**
         * 每页的记录条数
         * @return
         */
        public int getPerPage() {
            return perPage;
        }

        /**
         * 设置每页的记录条数
         * @param perPage
         */
        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        /**
         * 返回pageBean对象
         * @return
         */
        public PageBean getPageBean() {
            return pageBean;
        }

        /**
         * 设置PageBean对象，用于返回分页信息，用于前端UI显示
         * @param pageBean
         */
        public void setPageBean(PageBean pageBean) {
            this.pageBean = pageBean;
        }

        public String getMdFullMethodNameForCountSql() {
            return mdFullMethodNameForCountSql;
        }

        /**
         *
         * @param mdFullMethodNameForCountSql  可以指定四种类型的参数，<br/>
         *                                  null或""：则ak-dbutils会自动帮您生成计算count的select语句；<br/>
         *                                  数字：则表明以指定的数字为总数，用于计算分页信息；<br/>
         *                                  md方法地址：表示计算总数的SQL的md方法地址<br/>
         *                                  -1 ：表示总数未知，此时ak-dbutils不会自动生成计算count的select语句
         */
        public void setMdFullMethodNameForCountSql(String mdFullMethodNameForCountSql) {
            this.mdFullMethodNameForCountSql = mdFullMethodNameForCountSql;
        }

        public static class InsertOptions{
            public static enum ReturnFlag{
                AutoKey,UpdatedNum
            }
            private ReturnFlag returnFlag=ReturnFlag.UpdatedNum;

            public ReturnFlag getReturnFlag() {
                return returnFlag;
            }

            public void setReturnFlag(ReturnFlag returnFlag) {
                this.returnFlag = returnFlag;
            }
        }
    }
}
