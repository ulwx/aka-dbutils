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
import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.*;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TAccount;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrder;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderItem;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.AccountServiceImpl;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.ExampleExecuteTemplate;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.OrderServiceImpl;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/*
 * Please make sure primary replica data replication sync on MySQL is running correctly. Otherwise this example will query empty data from replica.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ShardingRawYamlRangeConfigurationExample {

    public static void main(final String[] args) throws SQLException, IOException {
    }


    @Before
    public void setup() {
        Utils.initSql();
    }

    @After
    public void after() {
        DbContext.removeDebugSQLListener();
    }

    private void test_databases_range(String dbpoolName) throws Exception{

        OrderDao orderDao;
        OrderItemDao orderItemDao;
        AddressDao addressDao;
        orderDao = new RangeOrderDao(dbpoolName);
        orderItemDao = new RangeOrderItemDao(dbpoolName);
        addressDao = new AddressDao(dbpoolName);
        OrderServiceImpl exampleService=  new OrderServiceImpl(orderDao,orderItemDao,addressDao);
        ExampleExecuteTemplate.run(exampleService);

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

        Assert.equal(result2.getSecondValue().size(),2);
        Assert.equal(result2.getSecondValue().get(0).getUserId(),2);
        Assert.equal(result2.getSecondValue().get(1).getUserId(),4);

        exampleService.deleteData(orderIds);
        result2= exampleService.printData();
        Assert.equal(result2.getFirstValue().size(),0);
        Assert.equal(result2.getSecondValue().size(),0);
        /////
        AccountServiceImpl accountService= new AccountServiceImpl(dbpoolName);
        ExampleExecuteTemplate.run(accountService);
        List<Long> accountIds = accountService.insertData();
        List<TAccount>  accountList=accountService.printData();

        System.out.println("---------------------------- Print Account Data -----------------------");
        for (Object each : accountList) {
            System.out.println(each);
        }
        Assert.equal(accountList.size(),10);
        Assert.equal(accountList.get(0).getUserId(),1);
        Assert.equal(accountList.get(9).getUserId(),10);

        accountService.deleteData(accountIds);
        accountList=accountService.printData();
        Assert.equal(accountList.size(),0);
    }

    private void test_tables_range(String dbpoolName) throws Exception{

        OrderDao orderDao;
        OrderItemDao orderItemDao;
        AddressDao addressDao;
        orderDao = new RangeOrderDao(dbpoolName);
        orderItemDao = new RangeOrderItemDao(dbpoolName);
        addressDao = new AddressDao(dbpoolName);
        OrderServiceImpl exampleService=  new OrderServiceImpl(orderDao,orderItemDao,addressDao);
        ExampleExecuteTemplate.run(exampleService);

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

        Assert.equal(result2.getFirstValue().size(),5);
        Assert.equal(result2.getFirstValue().get(0).getUserId(),1);
        Assert.equal(result2.getFirstValue().get(4).getUserId(),9);

        Assert.equal(result2.getSecondValue().size(),5);
        Assert.equal(result2.getSecondValue().get(0).getUserId(),1);
        Assert.equal(result2.getSecondValue().get(4).getUserId(),5);

        exampleService.deleteData(orderIds);
        result2= exampleService.printData();
        Assert.equal(result2.getFirstValue().size(),0);
        Assert.equal(result2.getSecondValue().size(),0);
        /////
        AccountServiceImpl accountService= new AccountServiceImpl(dbpoolName);
        ExampleExecuteTemplate.run(accountService);
        List<Long> accountIds = accountService.insertData();
        List<TAccount>  accountList=accountService.printData();

        System.out.println("---------------------------- Print Account Data -----------------------");
        for (Object each : accountList) {
            System.out.println(each);
        }
        Assert.equal(accountList.size(),10);
        Assert.equal(accountList.get(0).getUserId(),1);
        Assert.equal(accountList.get(9).getUserId(),10);

        accountService.deleteData(accountIds);
        accountList=accountService.printData();
        Assert.equal(accountList.size(),0);
    }


    private void test_databases_tables_range(String dbpoolName) throws Exception{

        OrderDao orderDao;
        OrderItemDao orderItemDao;
        AddressDao addressDao;
        orderDao = new RangeOrderDao(dbpoolName);
        orderItemDao = new RangeOrderItemDao(dbpoolName);
        addressDao = new AddressDao(dbpoolName);
        OrderServiceImpl exampleService=  new OrderServiceImpl(orderDao,orderItemDao,addressDao);
        ExampleExecuteTemplate.run(exampleService);

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

        Assert.equal(result2.getFirstValue().size(),5);
        Assert.equal(result2.getFirstValue().get(0).getUserId(),1);
        Assert.equal(result2.getFirstValue().get(4).getUserId(),9);

        Assert.equal(result2.getSecondValue().size(),2);
        Assert.equal(result2.getSecondValue().get(0).getUserId(),2);
        Assert.equal(result2.getSecondValue().get(1).getUserId(),4);

        exampleService.deleteData(orderIds);
        result2= exampleService.printData();
        Assert.equal(result2.getFirstValue().size(),0);
        Assert.equal(result2.getSecondValue().size(),0);
        /////
        AccountServiceImpl accountService= new AccountServiceImpl(dbpoolName);
        ExampleExecuteTemplate.run(accountService);
        List<Long> accountIds = accountService.insertData();
        List<TAccount>  accountList=accountService.printData();

        System.out.println("---------------------------- Print Account Data -----------------------");
        for (Object each : accountList) {
            System.out.println(each);
        }
        Assert.equal(accountList.size(),10);
        Assert.equal(accountList.get(0).getUserId(),1);
        Assert.equal(accountList.get(9).getUserId(),10);

        accountService.deleteData(accountIds);
        accountList=accountService.printData();
        Assert.equal(accountList.size(),0);
    }
    @Test
    public void test_sharding_databases_range() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_databases_range";
        test_databases_range(dbpoolName);
    }

    @Test
    public void test_sharding_tables_range() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_tables_range";
        test_tables_range(dbpoolName);
    }

    @Test
    public void test_sharding_databases_tables_range() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_databases_tables_range";
        test_databases_tables_range(dbpoolName);
    }


}
