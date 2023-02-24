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


import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.api.AccountRepository;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TAccount;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AccountDao  implements AccountRepository {
    
    private final String poolName;
    
    public AccountDao(final String poolName) {
        this.poolName = poolName;
    }

    @Override
    public void createTableIfNotExists() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS t_account " +
                "(account_id BIGINT NOT NULL AUTO_INCREMENT, " +
                "user_id INT NOT NULL, " +
                "status VARCHAR(50), " +
                "PRIMARY KEY (account_id))";
        MDbUtils.update(poolName,"sql:"+sql, (Map)null);
    }

    @Override
    public void dropTable() throws SQLException {
        String sql = " DROP TABLE IF EXISTS t_account";
        MDbUtils.update(poolName,"sql:"+sql, (Map)null);
    }

    @Override
    public void truncateTable() throws SQLException {
        String sql = "TRUNCATE TABLE t_account";
        MDbUtils.update(poolName,"sql:"+sql, (Map)null);
    }

    @Override
    public Long insert( TAccount account) throws Exception {
        String sql = "INSERT INTO t_account (user_id, status) VALUES (#{user_id}, #{status})";

        Map<String,Object> map = new HashMap<>();
        map.put("user_id",account.getUserId());
        map.put("status",account.getStatus());
        long acid=MDbUtils.insertReturnKey(this.poolName,
                "sql:"+sql,map);
        return acid;

    }

    @Override
    public void delete(final Long accountId) throws Exception {
        String sql = "DELETE FROM t_account WHERE account_id=#{account_id}";

        Map<String,Object> map = new HashMap<>();
        map.put("account_id",accountId);
        MDbUtils.del(this.poolName,
                "sql:"+sql,map);
    }

    @Override
    public List<TAccount> selectAll() throws Exception {
        String sql = "SELECT * FROM t_account order by user_id asc";
        return getAccounts(sql);
    }


    protected List<TAccount> getAccounts(final String sql) throws Exception {
        List<TAccount> result = new LinkedList<>();
        result=MDbUtils.queryList(poolName,TAccount.class,"sql:"+sql,(Map) null);
        return result;
    }
}
