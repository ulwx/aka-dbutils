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


import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrder;

import java.sql.SQLException;
import java.util.List;

public final class RangeOrderDao extends OrderDao {
    
    public RangeOrderDao(final String poolName) {
        super(poolName);
    }
    
    @Override
    public List<TOrder> selectAll() throws SQLException {
        String sql = "SELECT * FROM t_order WHERE order_id " +
                "BETWEEN 800000000000000000 AND 990000000000000000 order by user_id";
        return getOrders(sql);
    }
}