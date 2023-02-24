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

import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderItem;

import java.sql.SQLException;
import java.util.List;

public final class RangeOrderItemDao extends OrderItemDao {
    
    public RangeOrderItemDao(final String poolName) {
        super(poolName);
    }
    
    @Override
    public List<TOrderItem> selectAll() throws SQLException {
        String sql = "SELECT i.* FROM t_order o, t_order_item i " +
                "WHERE o.order_id = i.order_id AND o.user_id BETWEEN 1 AND 5 order by i.user_id asc";
        return getOrderItems(sql);
    }
}
