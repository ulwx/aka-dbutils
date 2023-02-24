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


import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.AddressDao;
import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.OrderDao;
import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.OrderItemDao;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TAddress;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrder;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrderItem;
import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;

import java.util.ArrayList;
import java.util.List;

public final class OrderServiceImpl implements ExampleService {
    
    private final OrderDao orderDao;
    
    private final OrderItemDao orderItemDao;
    
    private final AddressDao addressDao;

    public OrderDao getOrderDao() {
        return orderDao;
    }

    public OrderItemDao getOrderItemDao() {
        return orderItemDao;
    }

    public AddressDao getAddressDao() {
        return addressDao;
    }

    public OrderServiceImpl(final String poolName) {
        orderDao = new OrderDao(poolName);
        orderItemDao = new OrderItemDao(poolName);
        addressDao = new AddressDao(poolName);
    }
    
    public OrderServiceImpl(final OrderDao orderDao, final OrderItemDao orderItemDao,
                            final AddressDao addressDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.addressDao = addressDao;
    }
    
    @Override
    public void initEnvironment() throws Exception {
        orderDao.createTableIfNotExists();
        orderItemDao.createTableIfNotExists();
        orderDao.truncateTable();
        orderItemDao.truncateTable();
        initAddressTable();
    }
    
    private void initAddressTable() throws Exception {
        addressDao.createTableIfNotExists();
        addressDao.truncateTable();
        initAddressData();
    }
    
    private void initAddressData() throws Exception {
        for (int i = 0; i < 10; i++) {
            insertAddress(i);
        }
    }
    
    private void insertAddress(final int i) throws Exception {
        TAddress address = new TAddress();
        address.setAddressId((long) i);
        address.setAddressName("address_" + i);
        addressDao.insert(address);
    }
    
    @Override
    public void cleanEnvironment() throws Exception {
        orderDao.dropTable();
        orderItemDao.dropTable();
        addressDao.dropTable();
    }

    public void processSuccess() throws Exception {
        System.out.println("-------------- Process Success Begin ---------------");
        List<Long> orderIds = insertData();
        printData();
        deleteData(orderIds);
        printData();
        System.out.println("-------------- Process Success Finish --------------");
    }
    

    public void processFailure() throws Exception {
        System.out.println("-------------- Process Failure Begin ---------------");
        insertData();
        System.out.println("-------------- Process Failure Finish --------------");
        throw new RuntimeException("Exception occur for transaction test.");
    }
    
    public List<Long> insertData() throws Exception {
        System.out.println("---------------------------- Insert Data ----------------------------");
        List<Long> result = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            TOrder order = insertOrder(i);
            insertOrderItem(i, order);
            result.add(order.getOrderId());
        }
        return result;
    }

    public TOrder insertOrder(final int i) throws Exception {
        TOrder order = new TOrder();
        order.setUserId(i);
        order.setAddressId(Long.valueOf(i));
        order.setStatus("INSERT_TEST");
        long orderid=orderDao.insert(order);
        order.setOrderId(orderid);
        return order;
    }

    public void insertOrderItem(final int i, final TOrder order) throws Exception {
        TOrderItem item = new TOrderItem();
        item.setOrderId(order.getOrderId());
        item.setUserId(i);
        item.setStatus("INSERT_TEST");
        orderItemDao.insert(item);
    }

    public void deleteData(final List<Long> orderIds) throws Exception {
        System.out.println("---------------------------- Delete Data ----------------------------");
        for (Long each : orderIds) {
            orderDao.delete(each);
            orderItemDao.delete(each);
        }
    }
    

    public TResult2<List<TOrder>,List<TOrderItem>> printData() throws Exception {
        TResult2<List<TOrder>,List<TOrderItem>> listListTResult2 = new TResult2<>();
        listListTResult2.setFirstValue(orderDao.selectAll());
        listListTResult2.setSecondValue(orderItemDao.selectAll());

        return listListTResult2;
    }


}
