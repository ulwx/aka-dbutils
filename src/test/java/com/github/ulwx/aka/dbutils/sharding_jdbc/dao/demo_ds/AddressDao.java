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


import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.api.AddressRepository;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TAddress;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AddressDao implements AddressRepository {

    private final String poolName;
    
    public AddressDao(String poolName) {
        this.poolName = poolName;
    }
    
    @Override
    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS t_address "
            + "(address_id BIGINT NOT NULL," +
                " address_name VARCHAR(100) NOT NULL," +
                " PRIMARY KEY (address_id))";
        MDbUtils.update(this.poolName,"sql:"+sql,(Map)null);
    }
    
    @Override
    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS t_address";

        MDbUtils.update(this.poolName,"sql:"+sql,(Map)null);
    }
    
    @Override
    public void truncateTable() throws SQLException {
        String sql = "TRUNCATE TABLE t_address";

        MDbUtils.update(this.poolName,"sql:"+sql,(Map)null);
    }
    
    @Override
    public Long insert(final TAddress entity) throws SQLException {
        String sql = "INSERT INTO t_address (address_id, address_name) " +
                "VALUES (#{address_id}, #{address_name})";

        Map<String,Object> map = new HashMap<>();
        map.put("address_id",entity.getAddressId());
        map.put("address_name",entity.getAddressName());
        long addressId=MDbUtils.insertReturnKey(this.poolName,
                "sql:"+sql,map);
        return addressId;
    }
    
    @Override
    public void delete(final Long primaryKey) throws SQLException {
        String sql = "DELETE FROM t_address WHERE address_id=#{address_id}";

        Map<String,Object> map = new HashMap<>();
        map.put("address_id",primaryKey);
        MDbUtils.del(this.poolName,
                "sql:"+sql,map);
    }
    
    @Override
    public List<TAddress> selectAll() throws SQLException {
        String sql = "SELECT * FROM t_address";
        return getAddress(sql);
    }
    
    private List<TAddress> getAddress(final String sql) throws SQLException {

        return MDbUtils.queryList(this.poolName,TAddress.class,
                "sql:"+sql,(Map<String, Object>) null);
    }
}
