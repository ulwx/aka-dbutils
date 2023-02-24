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

package com.github.ulwx.aka.dbutils.sharding_jdbc.dao.demo_ds.api;

import java.util.List;

public interface CommonRepository<T, P> {
    
    /**
     * Create table if not exist.
     * 
     * @throws Exception SQL exception
     */
    void createTableIfNotExists() throws Exception;
    
    /**
     * Drop table.
     * 
     * @throws Exception SQL exception
     */
    void dropTable() throws Exception;
    
    /**
     * Truncate table.
     * 
     * @throws Exception SQL exception
     */
    void truncateTable() throws Exception;
    
    /**
     * insert data.
     * 
     * @param entity entity
     * @return generated primary key
     * @throws Exception SQL exception
     */
    P insert(T entity) throws Exception;
    
    /**
     * Delete data.
     * 
     * @param primaryKey primaryKey
     * @throws Exception SQL exception
     */
    void delete(P primaryKey) throws Exception;
    
    /**
     * Select all data.
     * 
     * @return all data
     * @throws Exception SQL exception
     */
    List<T> selectAll() throws Exception;
}
