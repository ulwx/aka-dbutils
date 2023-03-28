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

package com.github.ulwx.aka.dbutils.sharding_jdbc.test.seata;


import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.transaction.GlobalTransactionTemplate;
import com.github.ulwx.aka.dbutils.sharding_jdbc.Utils;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrder;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.SeataATOrderServiceImpl;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import io.seata.core.rpc.netty.RmNettyRemotingClient;
import io.seata.core.rpc.netty.TmNettyRemotingClient;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class TransactionBaseSeataRawExample {
    @Before
    public void setup() {
        Utils.initSql();
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                RmNettyRemotingClient.getInstance().destroy();
                TmNettyRemotingClient.getInstance().destroy();
            }
        }));
    }

    @Test
    public void test() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_transaction_sharding_databases_and_tables";
        SeataATOrderServiceImpl exampleService =  new SeataATOrderServiceImpl(dbpoolName);;
        exampleService.cleanEnvironment();
        exampleService.initEnvironment();

        exampleService.insert();
        List<TOrder> orderList = exampleService.selectAll();

        System.out.println("---------------------------- Print Order Data -------------------");
        for (Object each : orderList) {
            System.out.println(each);
        }
        Thread.sleep(10000);
        Assert.equal(orderList.size(),2);

    }
    @Test
    public void testTemplateExe()throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_transaction_sharding_databases_and_tables";
        SeataATOrderServiceImpl exampleService =  new SeataATOrderServiceImpl(dbpoolName);;
        exampleService.cleanEnvironment();
        exampleService.initEnvironment();

        GlobalTransactionTemplate.execute("sharding_jdbc/dbpool.xml",()->{
            exampleService.insert();
        });
        List<TOrder> orderList = exampleService.selectAll();

        System.out.println("---------------------------- Print Order Data -------------------");
        for (Object each : orderList) {
            System.out.println(each);
        }
        Thread.sleep(10000);
        Assert.equal(orderList.size(),2);
    }

}
