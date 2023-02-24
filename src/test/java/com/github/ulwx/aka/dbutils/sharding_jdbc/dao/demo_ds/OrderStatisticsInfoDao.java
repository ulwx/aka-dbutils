package com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds;

import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.api.OrderStatisticsInfoRepository;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderStatisticsInfo;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OrderStatisticsInfoDao implements OrderStatisticsInfoRepository {

    private  String poolName;

    public OrderStatisticsInfoDao(String  poolName) {
        this.poolName = poolName;
    }

    @Override
    public void createTableIfNotExists() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS t_order_statistics_info " +
                "(id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT " +
                "NOT NULL, order_date DATE NOT NULL, order_num INT, PRIMARY KEY (id))";

        MDbUtils.update(poolName,"sql:"+sql,(Map)null);
    }

    @Override
    public void dropTable() throws Exception {
        String sql = "DROP TABLE IF EXISTS t_order_statistics_info";
        MDbUtils.update(poolName,"sql:"+sql,(Map)null);
    }

    @Override
    public void truncateTable() throws Exception {
        String sql = "TRUNCATE TABLE t_order_statistics_info";
        MDbUtils.update(poolName,"sql:"+sql,(Map)null);
    }

    @Override
    public Long insert(final TOrderStatisticsInfo orderStatisticsInfo) throws Exception {
        String sql = "INSERT INTO t_order_statistics_info (user_id, order_date, order_num) VALUES (#{user_id}, " +
                "#{order_date}, #{order_num})";
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",orderStatisticsInfo.getUserId());
        map.put("order_date", orderStatisticsInfo.getOrderDate());
        map.put("order_num",orderStatisticsInfo.getOrderNum());
        long id=MDbUtils.insertReturnKey(this.poolName,
                "sql:"+sql,map);
        orderStatisticsInfo.setId(id);
        return id;
    }

    @Override
    public void delete(final Long id) throws Exception {
        String sql = "DELETE FROM t_order_statistics_info WHERE id=#{id}";
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        MDbUtils.update(poolName,"sql:"+sql,map);

    }

    @Override
    public List<TOrderStatisticsInfo> selectAll() throws Exception {
        String sql = "SELECT * FROM t_order_statistics_info order by user_id";
        return getOrderStatisticsInfos(sql);
    }

    protected List<TOrderStatisticsInfo> getOrderStatisticsInfos(final String sql) throws Exception {
        List<TOrderStatisticsInfo> result = new LinkedList<>();
        result=MDbUtils.queryList(poolName, TOrderStatisticsInfo.class,"sql:"+sql,(Map) null);
        return result;
    }
}
