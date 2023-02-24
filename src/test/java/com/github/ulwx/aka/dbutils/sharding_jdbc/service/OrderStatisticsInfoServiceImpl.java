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

package com.github.ulwx.aka.dbutils.sharding_jdbc.service;


import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.OrderStatisticsInfoDao;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderStatisticsInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class OrderStatisticsInfoServiceImpl implements ExampleService {
    
    private final OrderStatisticsInfoDao orderStatisticsInfoDao;
    
    public OrderStatisticsInfoServiceImpl(final String  dbpoolName) {
        orderStatisticsInfoDao = new OrderStatisticsInfoDao(dbpoolName);
    }
    
    @Override
    public void initEnvironment() throws Exception {
        orderStatisticsInfoDao.createTableIfNotExists();
        orderStatisticsInfoDao.truncateTable();
    }
    
    @Override
    public void cleanEnvironment() throws Exception {
        orderStatisticsInfoDao.dropTable();
    }
    

    public void processSuccess() throws Exception {
        System.out.println("-------------- Process Success Begin ---------------");
        Collection<Long> ids = insertData();
        printData();
        deleteData(ids);
        printData();
        System.out.println("-------------- Process Success Finish --------------");
    }


    public void processFailure() throws Exception {
        System.out.println("-------------- Process Failure Begin ---------------");
        insertData();
        System.out.println("-------------- Process Failure Finish --------------");
        throw new RuntimeException("Exception occur for transaction test.");
    }

    public Collection<Long> insertData() throws Exception {
        System.out.println("------------------- Insert Data --------------------");
        Collection<Long> result = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            TOrderStatisticsInfo orderStatisticsInfo = insertOrderStatisticsInfo(i);
            result.add(orderStatisticsInfo.getId());
        }
        return result;
    }

    public TOrderStatisticsInfo insertOrderStatisticsInfo(final int i) throws Exception {
        TOrderStatisticsInfo result = new TOrderStatisticsInfo();
        result.setUserId((long) i);
        if (0 == i % 3) {
            result.setOrderDate(LocalDate.now().plusYears(0));
        }else if (1 == i % 3) {
            result.setOrderDate(LocalDate.now().plusYears(1));
        }else if (2 == i % 3) {
            result.setOrderDate(LocalDate.now().plusYears(2));
        } else {
            result.setOrderDate(LocalDate.now());
        }
        result.setOrderNum(i * 10);
        orderStatisticsInfoDao.insert(result);
        return result;
    }

    public void deleteData(final Collection<Long> ids) throws Exception {
        System.out.println("-------------------- Delete Data -------------------");
        for (Long each : ids) {
            orderStatisticsInfoDao.delete(each);
        }
    }

    public List<TOrderStatisticsInfo> printData() throws Exception {
        return orderStatisticsInfoDao.selectAll();
    }
}
