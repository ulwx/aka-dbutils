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
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderStatisticsInfo;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.ExampleExecuteTemplate;
import com.github.ulwx.aka.dbutils.sharding_jdbc.service.OrderStatisticsInfoServiceImpl;
import com.github.ulwx.aka.dbutils.tool.support.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ShardingRawYamlIntervalConfigurationExample {
    

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

    private void test(String dbpoolName) throws Exception{

        OrderStatisticsInfoServiceImpl exampleService=  new OrderStatisticsInfoServiceImpl(dbpoolName);
        ExampleExecuteTemplate.run(exampleService);

        Collection<Long> ids=exampleService.insertData();
        List<TOrderStatisticsInfo> result=exampleService.printData();

        System.out.println("---------------------------- Print Order Data -------------------");
        for (Object each : result) {
            System.out.println(each);
        }

        Assert.equal(result.size(),10);
        Assert.equal(result.get(0).getUserId(),1);
        Assert.equal(result.get(9).getUserId(),10);

        exampleService.deleteData(ids);
        result= exampleService.printData();
        System.out.println("---------------------------- Print Order Data -------------------");
        for (Object each : result) {
            System.out.println(each);
        }

    }
    @Test
    public void test_sharding_databases_and_tables() throws Exception{
        String dbpoolName="sharding_jdbc/dbpool.xml#demo_ds_for_sharding_databases_interval";
        test(dbpoolName);

    }


}
