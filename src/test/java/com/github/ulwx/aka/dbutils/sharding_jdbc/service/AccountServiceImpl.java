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

import com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.AccountDao;
import com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds.TAccount;

import java.util.ArrayList;
import java.util.List;

public final class AccountServiceImpl implements ExampleService {
    
    private final AccountDao accountDao;
    
    public AccountServiceImpl(final String poolName) {
        accountDao = new AccountDao(poolName);
    }
    
    public AccountServiceImpl(final AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    
    @Override
    public void initEnvironment() throws Exception {
        accountDao.createTableIfNotExists();
        accountDao.truncateTable();
    }
    
    @Override
    public void cleanEnvironment() throws Exception {
        accountDao.dropTable();
    }
    

    public void processSuccess() throws Exception {
        System.out.println("-------------- Process Success Begin ---------------");
        List<Long> accountIds = insertData();
        printData();
        deleteData(accountIds);
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
            TAccount account = insertAccounts(i);
            result.add(account.getAccountId());
        }
        return result;
    }

    public TAccount insertAccounts(final int i) throws Exception {
        TAccount account = new TAccount();
        account.setUserId(i);
        account.setStatus("INSERT_TEST");
        long id=accountDao.insert(account);
        account.setAccountId(id);
        return account;
    }

    public void deleteData(final List<Long> accountIds) throws Exception {
        System.out.println("---------------------------- Delete Data ----------------------------");
        for (Long each : accountIds) {
            accountDao.delete(each);
        }
    }

    public List<TAccount>  printData() throws Exception {

        return accountDao.selectAll();

    }
}
