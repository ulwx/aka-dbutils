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

package com.github.ulwx.aka.dbutils.database.transaction;

import io.seata.tm.api.GlobalTransaction;

import java.util.Stack;


/**
 * Seata transaction holder.
 */

final class AkaSeataTransactionHolder {
    
    private static final ThreadLocal<Stack<GlobalTransaction>> CONTEXT =
            new ThreadLocal<Stack<GlobalTransaction>>() {
                @Override
                protected Stack<GlobalTransaction> initialValue() {
                    return new Stack<GlobalTransaction>();
                }
            };
    
    /**
     * Set seata global transaction.
     *
     * @param transaction global transaction context
     */
    static void set(final GlobalTransaction transaction) {
        CONTEXT.get().push(transaction);
    }
    
    /**
     * Get seata global transaction.
     *
     * @return global transaction
     */
    static GlobalTransaction get() {
        if(CONTEXT.get().size()>0) {
            return CONTEXT.get().peek();
        }else {
            return null;
        }
    }
    
    /**
     * Clear global transaction.
     */
    static void clear() {
        if(CONTEXT.get().size()>0) {
            CONTEXT.get().pop();
        }else{

        }
    }

}
