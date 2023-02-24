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

package com.github.ulwx.aka.dbutils.sharding_jdbc.test.sharding;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.sharding_jdbc.Utils;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrder;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderItem;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.OrderServiceImpl;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ShardingSQLCommentHintRawExample {
    @Before
    public void setup() {
        Utils.initSql();
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }

    @Test
    public void test_sharding_sql_comment_hint() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_sql_comment_hint";

        OrderServiceImpl exampleService = getExampleService(dbpoolName);
        exampleService.cleanEnvironment();
        exampleService.initEnvironment();
        exampleService.insertData();
        List<TOrder> orders=exampleService.getOrderDao().getOrders("/* ShardingSphere hint: dataSourceName=ds_1 */select * from t_order");
        List<TOrderItem> orderItems=exampleService.getOrderItemDao().getOrderItems("/* ShardingSphere hint: dataSourceName=ds_1 */select * from t_order_item");
        System.out.println("---------------------------- Print Order Data -------------------");
        for (Object each : orders) {
            System.out.println(each);
        }
        System.out.println("---------------------------- Print OrderItem Data -------------------");
        for (Object each : orderItems) {
            System.out.println(each);
        }

        Assert.equal(orders.size(),5);
        Assert.equal(orders.get(0).getUserId(),1);
        Assert.equal(orders.get(4).getUserId(),9);

        Assert.equal(orderItems.size(),5);
        Assert.equal(orderItems.get(0).getUserId(),1);
        Assert.equal(orderItems.get(4).getUserId(),9);
    }
    @Test
    public void test_hint_sql_script() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_sql_comment_hint";
        OrderServiceImpl exampleService = getExampleService(dbpoolName);
        exampleService.cleanEnvironment();
        exampleService.initEnvironment();
        exampleService.insertData();
        MDbUtils.exeScript(dbpoolName,"com.github.ulwx.aka.dbutils.sharding_jdbc",
                "comment_hint.sql",false,";");

    }
    private static OrderServiceImpl getExampleService(final String dbpoolName) {
        return new OrderServiceImpl(dbpoolName);
    }
    

}

