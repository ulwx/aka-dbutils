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

import com.github.ulwx.aka.dbutils.database.DataBase;
import com.github.ulwx.aka.dbutils.database.DataBaseFactory;
import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.OrderDao;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TOrder;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import org.apache.shardingsphere.transaction.api.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;

import java.sql.SQLException;
import java.util.List;

/**
 * Order service.
 *
 */
public class SeataATOrderServiceImpl implements ExampleService{

    private String poolName;
    private OrderDao orderDao;
    public SeataATOrderServiceImpl(final String poolName) {
        this.poolName=poolName;
        orderDao=new OrderDao(poolName);
    }
    @Override
    public void initEnvironment() throws Exception {
        orderDao.createTableIfNotExists();
        orderDao.truncateTable();
        MDbUtils.exeScript(poolName,
                "com.github.ulwx.aka.dbutils.sharding_jdbc",
                "undo_log.sql", false,true,null);

    }

    @Override
    public void cleanEnvironment() throws Exception {
        orderDao.dropTable();
    }


    
    /**
     * Execute XA.
     *
     * @throws SQLException SQL exception
     */
    public void insert() throws Exception {
        TransactionTypeHolder.set(TransactionType.BASE);
        try{
            doInsert();
        }finally {
            TransactionTypeHolder.clear();
        }

    }
    
    /**
     * Execute XA with exception.
     *
     * @throws SQLException SQL exception
     */
    public void insertFailed() throws Exception {
        try(DataBase db = DataBaseFactory.getDataBase(poolName)) {
            db.setAutoCommit(false);
            for (int i = 0; i < 2; i++) {
                insertOrder(db,i);
            }
            db.rollback();
        }
    }

    public void doInsert() throws Exception {
        try(DataBase db = DataBaseFactory.getDataBase(poolName)) {
            try {
                db.setAutoCommit(false);
                for (int i = 0; i < 3; i++) {
                    insertOrder(db, i);
                }
                db.commit();
            }catch (Exception e){
                e.printStackTrace();
                db.rollback();

            }
        }
    }
    public TOrder insertOrder(DataBase db, int userId) throws Exception {
        TOrder order = new TOrder();
        order.setUserId(userId);
        order.setStatus("init");
        order.setAddressId(12345678l);
        db.insertReturnKeyBy(order);
        return order;

    }
    /**
     * Select all.
     *
     * @return record count
     */
    public List<TOrder> selectAll() throws SQLException {
        return  orderDao.selectAll();
    }
}
