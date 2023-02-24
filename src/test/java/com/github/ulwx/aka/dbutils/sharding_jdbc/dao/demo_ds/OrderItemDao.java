/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds;


import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.api.OrderItemRepository;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderItem;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderItemDao implements OrderItemRepository {

    private  String poolName;
    
    public OrderItemDao(String  poolName) {
        this.poolName = poolName;
    }
    
    @Override
    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS t_order_item "
            + "(order_item_id BIGINT NOT NULL AUTO_INCREMENT, " +
                "order_id BIGINT NOT NULL, user_id INT NOT NULL, " +
                "status VARCHAR(50), PRIMARY KEY (order_item_id))";

        MDbUtils.update(poolName,"sql:"+sql, (Map)null);
    }
    
    @Override
    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS t_order_item";
        MDbUtils.update(this.poolName,"sql:"+sql,(Map)null);
    }
    
    @Override
    public void truncateTable() throws SQLException {
        String sql = "TRUNCATE TABLE t_order_item";
        MDbUtils.update(this.poolName,"sql:"+sql,(Map)null);
    }
    
    @Override
    public Long insert(final TOrderItem orderItem) throws SQLException {
        String sql = "INSERT INTO t_order_item (order_id, user_id, status) " +
                "VALUES (#{order_id}, #{user_id}, #{status})";

        Map<String,Object> map = new HashMap<>();
        map.put("order_id",orderItem.getOrderId());
        map.put("user_id",orderItem.getUserId());
        map.put("status",orderItem.getStatus());
        long orderItemId=MDbUtils.insertReturnKey(this.poolName,
                "sql:"+sql,map);
        return orderItemId;
    }
    
    @Override
    public void delete(final Long orderItemId) throws SQLException {
        String sql = "DELETE FROM t_order_item WHERE order_id=#{order_id}";
        Map<String,Object> map = new HashMap<>();
        map.put("order_id",orderItemId);
        MDbUtils.del(this.poolName, "sql:"+sql,map);
    }
    
    @Override
    public List<TOrderItem> selectAll() throws SQLException {
        // TODO Associated query with encrypt may query and decrypt failed. see https://github.com/apache/shardingsphere/issues/3352
        String sql = "SELECT i.* FROM t_order o, t_order_item i " +
                "WHERE o.order_id = i.order_id AND o.user_id order by i.user_id asc";
        return getOrderItems(sql);
    }

    public List<TOrderItem> getOrderItems(final String sql) throws SQLException {

        List<TOrderItem> result=MDbUtils.queryList(poolName, TOrderItem.class,"sql:"+sql,(Map) null);
        return result;
    }
}
