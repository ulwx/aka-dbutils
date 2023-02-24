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


import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.api.OrderRepository;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TAccount;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrder;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OrderDao implements OrderRepository {
    
    private  String poolName;
    
    public OrderDao(String  poolName) {
        this.poolName = poolName;
    }

    @Override
    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS t_order (order_id BIGINT NOT NULL AUTO_INCREMENT," +
                " user_id INT NOT NULL, address_id BIGINT NOT NULL, " +
                "status VARCHAR(50), PRIMARY KEY (order_id))";
        MDbUtils.update(poolName,"sql:"+sql, (Map)null);

    }

    @Override
    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS t_order";
        MDbUtils.update(this.poolName,"sql:"+sql,(Map)null);
    }

    @Override
    public void truncateTable() throws SQLException {
        String sql = "TRUNCATE TABLE t_order";
        MDbUtils.update(this.poolName,"sql:"+sql,(Map)null);
    }

    @Override
    public Long insert(final TOrder order) throws SQLException {
        String sql = "INSERT INTO t_order (user_id, address_id, status) " +
                "VALUES (#{user_id}, #{address_id}, #{status})";
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",order.getUserId());
        map.put("address_id",order.getAddressId());
        map.put("status",order.getStatus());
        long orderId=MDbUtils.insertReturnKey(this.poolName,
                "sql:"+sql,map);
        return orderId;
    }
    public Long insertOrder(final TOrder order) throws SQLException {
        long orderId=MDbUtils.insertReturnKeyBy(this.poolName,order);
        return orderId;
    }
    @Override
    public void delete(final Long orderId) throws SQLException {
        String sql = "DELETE FROM t_order WHERE order_id=#{order_id}";
        Map<String,Object> map = new HashMap<>();
        map.put("order_id",orderId);
        MDbUtils.del(this.poolName, "sql:"+sql,map);
    }

    @Override
    public List<TOrder> selectAll() throws SQLException {
        String sql = "SELECT * FROM t_order order by user_id asc";
        return getOrders(sql);
    }

    public List<TOrder> getOrders(final String sql) throws SQLException {
        List<TOrder> result = new LinkedList<>();
        result=MDbUtils.queryList(poolName, TOrder.class,"sql:"+sql,(Map) null);
        return result;

    }
}
