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
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ShardingHintRawExample {


    @Before
    public void setup() {
        Utils.initSql();
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }
    private void test(OrderServiceImpl exampleService) throws Exception{


        List<Long> orderIds=exampleService.insertData();
        TResult2<List<TOrder>,List<TOrderItem>> result2=exampleService.printData();


        System.out.println("---------------------------- Print Order Data -------------------");
        for (Object each : result2.getFirstValue()) {
            System.out.println(each);
        }
        System.out.println("---------------------------- Print OrderItem Data -------------------");
        for (Object each : result2.getSecondValue()) {
            System.out.println(each);
        }
        Assert.equal(result2.getFirstValue().size(),10);
        Assert.equal(result2.getFirstValue().get(0).getUserId(),1);
        Assert.equal(result2.getFirstValue().get(9).getUserId(),10);

        Assert.equal(result2.getSecondValue().size(),10);
        Assert.equal(result2.getSecondValue().get(0).getUserId(),1);
        Assert.equal(result2.getSecondValue().get(9).getUserId(),10);
        exampleService.deleteData(orderIds);
        result2= exampleService.printData();
        Assert.equal(result2.getFirstValue().size(),0);
        Assert.equal(result2.getSecondValue().size(),0);

    }
    @Test
    public void test_sharding_hint_databases_only() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_hint_databases_only";
        OrderServiceImpl exampleService =  new OrderServiceImpl(dbpoolName);;
        exampleService.cleanEnvironment();
        exampleService.initEnvironment();

        try (HintManager hintManager = HintManager.getInstance();) {
            hintManager.setDatabaseShardingValue(1L);
            test(exampleService);


        }

    }
    @Test
    public void test_sharding_hint_databases_tables() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_hint_databases_tables";
        OrderServiceImpl exampleService =  new OrderServiceImpl(dbpoolName);;
        exampleService.cleanEnvironment();
        exampleService.initEnvironment();
        try (HintManager hintManager = HintManager.getInstance();) {
            hintManager.addDatabaseShardingValue("t_order", 2L);
            hintManager.addTableShardingValue("t_order", 1L);
            test(exampleService);

        }
    }


}

